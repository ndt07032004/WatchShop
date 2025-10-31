package com.watchstore.controlller; // Giữ nguyên package của bạn

import com.watchstore.dao.CartDAO;
import com.watchstore.dao.CategoryDAO; // Import CategoryDAO
import com.watchstore.dao.OrderDAO;
import com.watchstore.model.Cart;
import com.watchstore.model.Category; // Import Category model
import com.watchstore.model.User;
import java.io.IOException;
import java.util.List; // Import List
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "CheckoutServlet", urlPatterns = {"/checkout"})
public class CheckoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("cart.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        Cart cart = (Cart) session.getAttribute("cart");
        User user = (User) session.getAttribute("account");

        System.out.println("--- CheckoutServlet doPost START ---"); // LOG START

        // 1. Kiểm tra đăng nhập
        if (user == null) {
            System.err.println("ERROR: User not logged in. Redirecting to login.jsp.");
            response.sendRedirect("login.jsp");
            return;
        }
        System.out.println("User: " + user.getUsername() + " (ID: " + user.getId() + ")");

        // 2. Kiểm tra giỏ hàng
        if (cart != null && cart.getItems() != null && !cart.getItems().isEmpty()) {
            System.out.println("Cart has " + cart.getItems().size() + " items. Total: " + cart.getTotalMoney());

            // 3. Lấy phương thức thanh toán
            String paymentMethod = request.getParameter("paymentMethod");
            if (paymentMethod == null || paymentMethod.isEmpty()) {
                paymentMethod = "COD"; // Mặc định
            }
            System.out.println("Payment Method selected: " + paymentMethod);

            OrderDAO orderDAO = new OrderDAO();
            boolean success = false;
            try {
                // 4. Gọi hàm addOrder MỚI
                System.out.println("Attempting to add order...");
                success = orderDAO.addOrder(user, cart, paymentMethod);
                System.out.println("orderDAO.addOrder result: " + success);
            } catch (Exception e) {
                 System.err.println("ERROR during orderDAO.addOrder execution:");
                 e.printStackTrace(); // In chi tiết lỗi vào log server
                 success = false; // Đảm bảo success là false nếu có exception
            }


            if (success) {
                System.out.println("Order added successfully.");
                // 5. Xóa giỏ hàng CSDL
                CartDAO cartDAO = new CartDAO();
                try {
                    // ⭐ KIỂM TRA CART ID TRƯỚC KHI XÓA ⭐
                    if (cart.getId() > 0) {
                        System.out.println("Attempting to clear cart (ID: " + cart.getId() + ") from database...");
                        cartDAO.clearCart(cart.getId());
                        System.out.println("Database cart cleared.");
                    } else {
                        System.err.println("WARN: Invalid Cart ID (" + cart.getId() + ") found in session. Skipping clearCart.");
                    }
                } catch (Exception e) {
                     System.err.println("ERROR during cartDAO.clearCart execution:");
                     e.printStackTrace();
                     // Có thể tiếp tục dù lỗi xóa giỏ, nhưng cần ghi log
                }


                // 6. Xóa giỏ hàng khỏi session
                session.removeAttribute("cart");
                System.out.println("Cart removed from session.");

                // 7. Chuyển hướng
                String redirectUrl;
                if ("BANK".equals(paymentMethod)) {
                    session.setAttribute("hoTen", user.getFullname());
                    redirectUrl = "bank_payment.jsp";
                } else if ("MOMO".equals(paymentMethod)) {
                    session.setAttribute("hoTen", user.getFullname());
                    redirectUrl = "momo_payment.jsp";
                } else { // COD
                    redirectUrl = "success.jsp";
                }
                System.out.println("Redirecting to: " + redirectUrl);
                response.sendRedirect(redirectUrl);

            } else {
                // 8. Đặt hàng thất bại
                System.err.println("Order failed. Forwarding back to checkout.jsp with error message.");
                request.setAttribute("error", "Đặt hàng thất bại, có lỗi xảy ra khi lưu đơn hàng. Vui lòng thử lại.");

                // Cần lấy lại categoryList để hiển thị menu khi forward
                try {
                    CategoryDAO cDao = new CategoryDAO();
                    List<Category> categoryList = cDao.getAllCategories();
                    request.setAttribute("categoryList", categoryList);
                    System.out.println("Category list loaded for forwarding.");
                } catch (Exception e) {
                     System.err.println("ERROR loading categories for error page:");
                     e.printStackTrace();
                     // Vẫn cố gắng forward nhưng menu có thể bị lỗi
                }

                request.getRequestDispatcher("checkout.jsp").forward(request, response);
            }

        } else {
            // 9. Giỏ hàng trống
            System.err.println("WARN: Cart is empty or null. Redirecting to cart.jsp.");
            response.sendRedirect("cart.jsp");
        }
        System.out.println("--- CheckoutServlet doPost END ---"); // LOG END
    }

    @Override
    public String getServletInfo() {
        return "Handles checkout process including payment method and clearing cart.";
    }
}