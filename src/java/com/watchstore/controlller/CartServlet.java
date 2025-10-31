package com.watchstore.controlller;

import com.watchstore.dao.CartDAO;
import com.watchstore.dao.ProductDAO;
import com.watchstore.model.Cart;
import com.watchstore.model.Item;
import com.watchstore.model.Product;
import com.watchstore.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession(true);
        Cart cart = (Cart) session.getAttribute("cart");

        if (cart == null) {
            cart = new Cart();
        }

        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "view";
        }
        String idParam = request.getParameter("id");
        int productId = 0;
        User user = (User) session.getAttribute("account");
        CartDAO cartDAO = new CartDAO();
        ProductDAO productDAO = new ProductDAO(); // Thêm ProductDAO
        String message = null;
        boolean requiresDbSync = false; // Cờ đánh dấu cần đồng bộ DB

        try {
            // Parse ID nếu cần
            if (idParam != null && !action.equals("view")) {
                 try {
                     productId = Integer.parseInt(idParam);
                 } catch (NumberFormatException e){
                      message = "ID sản phẩm không hợp lệ.";
                      action = "view";
                 }
            }

            System.out.println("DEBUG (CartServlet): Action=" + action + ", ProductID=" + productId + ", User=" + (user != null ? user.getUsername() : "Guest"));

            // --- XỬ LÝ SESSION CART TRƯỚC ---
            switch (action) {
                case "add":
                    if (productId > 0) {
                        Product pAdd = productDAO.getProductById(productId);
                        if (pAdd != null) {
                            if(pAdd.getStock() > 0) {
                                Item newItem = new Item(pAdd, 1); // Dùng constructor mới
                                cart.addItem(newItem);
                                message = "Đã thêm '" + pAdd.getName() + "' vào giỏ.";
                                requiresDbSync = true; // Đánh dấu cần đồng bộ
                            } else { message = "'" + pAdd.getName() + "' đã hết hàng."; }
                        } else { message = "Không tìm thấy sản phẩm."; }
                    } else if (message == null) { message = "Thiếu ID sản phẩm."; }
                    break;

                case "remove":
                     if (productId > 0) {
                        Item removedItem = cart.getItemById(productId); // Kiểm tra xem có item không
                        if (removedItem != null) {
                            cart.removeItem(productId);
                            message = "Đã xóa sản phẩm khỏi giỏ.";
                            requiresDbSync = true; // Đánh dấu cần đồng bộ
                        } else {
                            message = "Sản phẩm không có trong giỏ để xóa.";
                        }
                    } else if (message == null) { message = "Thiếu ID sản phẩm."; }
                    break;

                case "increase":
                    if (productId > 0) {
                        Product pIncrease = productDAO.getProductById(productId);
                        Item itemToIncrease = cart.getItemById(productId);
                        if (pIncrease != null && itemToIncrease != null) {
                            if (itemToIncrease.getQuantity() < pIncrease.getStock()) {
                                itemToIncrease.setQuantity(itemToIncrease.getQuantity() + 1);
                                requiresDbSync = true; // Đánh dấu cần đồng bộ
                            } else { message = "Đã đạt số lượng tồn kho tối đa!"; }
                        } else { message = "Sản phẩm không có trong giỏ.";}
                    } else if (message == null) { message = "Thiếu ID sản phẩm.";}
                    break;

                case "decrease":
                    if (productId > 0) {
                        Item itemToDecrease = cart.getItemById(productId);
                        if (itemToDecrease != null) {
                            if (itemToDecrease.getQuantity() > 1) {
                                itemToDecrease.setQuantity(itemToDecrease.getQuantity() - 1);
                                requiresDbSync = true; // Đánh dấu cần đồng bộ
                            }
                        } else { message = "Sản phẩm không có trong giỏ.";}
                    } else if (message == null) { message = "Thiếu ID sản phẩm.";}
                    break;

                case "view":
                default:
                    // Không làm gì, chỉ hiển thị
                    break;
            }

            // --- LƯU SESSION CART ---
            session.setAttribute("cart", cart); // Lưu trạng thái session cart mới nhất

            // --- ĐỒNG BỘ CSDL NẾU USER ĐĂNG NHẬP VÀ CÓ THAY ĐỔI ---
            // ⭐ SỬA LẠI LOGIC ĐỒNG BỘ ⭐
            if (user != null && requiresDbSync && productId > 0) {
                System.out.println("DEBUG (CartServlet): User logged in, syncing DB for ProductID: " + productId);
                Cart dbCart = cartDAO.getCartByUserId(user.getId()); // Lấy cart ID
                if (dbCart != null) {
                    Item currentSessionItem = cart.getItemById(productId); // Lấy trạng thái item cuối cùng trong session
                    if (currentSessionItem != null) {
                        // Cập nhật hoặc thêm item vào DB với số lượng cuối cùng trong session
                        System.out.println("DEBUG (CartServlet - DB Sync): Updating/Adding item. CartID=" + dbCart.getId() + ", ProdID=" + productId + ", FinalQty=" + currentSessionItem.getQuantity());
                        cartDAO.addItem(dbCart.getId(), productId, currentSessionItem.getQuantity()); // Dùng addItem để Insert/Update
                    } else {
                        // Nếu item không còn trong session (do remove hoặc decrease về 0) thì xóa khỏi DB
                        System.out.println("DEBUG (CartServlet - DB Sync): Removing item. CartID=" + dbCart.getId() + ", ProdID=" + productId);
                        cartDAO.removeItem(dbCart.getId(), productId);
                    }
                     // Cân nhắc: Có nên tải lại toàn bộ cart từ DB vào session sau khi sync không?
                     // Nếu có nhiều thao tác nhanh, việc tải lại liên tục có thể chậm.
                     // Tạm thời chỉ đồng bộ thay đổi lên DB.
                } else {
                    System.err.println("ERROR (CartServlet - DB Sync): Could not get/create DB cart for user ID: " + user.getId());
                    message = (message == null ? "" : message + " ") + "Lỗi đồng bộ giỏ hàng với tài khoản."; // Thêm thông báo lỗi
                }
            } else if (user != null && requiresDbSync) {
                 System.err.println("WARN (CartServlet - DB Sync): Sync required but ProductID is invalid (0). Action was: " + action);
            }

            // Đặt thông báo vào session
            if (message != null) {
                 session.setAttribute("cartMessage", message);
            }

        } catch (Exception e) {
             System.err.println("ERROR (CartServlet): Unhandled exception.");
             e.printStackTrace();
             session.setAttribute("cartMessage", "Đã xảy ra lỗi hệ thống khi xử lý giỏ hàng.");
        }

        // Luôn redirect về trang giỏ hàng
        response.sendRedirect("cart.jsp");
    }

    // (doGet, doPost, getServletInfo giữ nguyên)
     @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { processRequest(request, response); }
     @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { processRequest(request, response); }
     @Override public String getServletInfo() { return "Servlet handling cart actions (session/DB sync)."; }
}