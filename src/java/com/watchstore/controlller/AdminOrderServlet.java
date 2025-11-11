package com.watchstore.controlller; // Giữ nguyên package của bạn

import com.watchstore.dao.OrderDAO;
import com.watchstore.dao.ProductDAO; // Import để trừ kho
import com.watchstore.model.Order;
import com.watchstore.model.OrderDetail; // Import để trừ kho
import com.watchstore.model.User;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "AdminOrderServlet", urlPatterns = {"/admin/manage-orders"})
public class AdminOrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        User adminUser = (User) session.getAttribute("account");

        // 1. Kiểm tra quyền Admin
        if (adminUser == null || adminUser.getRole() != 1) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            OrderDAO orderDAO = new OrderDAO();

            // 2. Lấy danh sách tất cả đơn hàng cùng với chi tiết của chúng (ĐÃ TỐI ƯU HÓA N+1)
            List<Order> allOrders = orderDAO.getAllOrdersWithDetails();

            // 3. Gửi dữ liệu sang trang JSP
            request.setAttribute("orderList", allOrders);
            request.setAttribute("pageTitle", "Quản lý Đơn hàng");
            request.setAttribute("activePage", "orders");

            // 4. Forward
            request.getRequestDispatcher("/admin/manage_orders.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("ERROR in AdminOrderServlet doGet:");
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi tải danh sách đơn hàng: " + e.getMessage());
            request.getRequestDispatcher("/admin/error_admin.jsp").forward(request, response);
        }
    }

    // *** THAY THẾ PHƯƠNG THỨC doPost TRONG AdminOrderServlet.java ***
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
         request.setCharacterEncoding("UTF-8");
         HttpSession session = request.getSession();
         User adminUser = (User) session.getAttribute("account");

         // 1. Kiểm tra Admin
         if (adminUser == null || adminUser.getRole() != 1) {
             response.sendRedirect(request.getContextPath() + "/login.jsp");
             return;
         }

         // 2. Lấy tham số
         String orderIdStr = request.getParameter("orderId");
         String newStatus = request.getParameter("newStatus");
         String message = "";
         boolean error = false;
         int orderId = 0;

         System.out.println("\n--- AdminOrderServlet POST Start ---"); // Log bắt đầu
         System.out.println("   Request: OrderID=" + orderIdStr + ", NewStatus=" + newStatus);

         // 3. Xử lý
         if (orderIdStr != null && newStatus != null && !newStatus.isEmpty()) {
             try {
                 orderId = Integer.parseInt(orderIdStr); // Chuyển ID sang số
                 OrderDAO orderDAO = new OrderDAO();

                 // 4. Cập nhật trạng thái đơn hàng trước
                 System.out.println("   Updating order status...");
                 if (orderDAO.updateOrderStatus(orderId, newStatus)) {
                     message = "Cập nhật trạng thái đơn hàng #" + orderId + " thành '" + newStatus + "' thành công!";
                     System.out.println("   >> Order status updated successfully.");

                     // --- GỬI EMAIL THÔNG BÁO CHO KHÁCH HÀNG ---
                     try {
                         Order orderDetails = orderDAO.getOrderWithCustomerEmail(orderId);
                         if (orderDetails != null && orderDetails.getCustomer() != null) {
                             String toEmail = orderDetails.getCustomer().getEmail();
                             String customerName = orderDetails.getCustomer().getFullname();
                             String subject = "";
                             String body = "";

                             // Soạn nội dung email dựa trên trạng thái mới
                             switch (newStatus) {
                                 case "Đang xử lý":
                                     subject = String.format("Đơn hàng #%d của bạn đã được xác nhận", orderId);
                                     body = String.format("Xin chào %s,\n\nĐơn hàng #%d của bạn tại WatchStore đã được xác nhận và đang trong quá trình xử lý.\nChúng tôi sẽ thông báo cho bạn khi đơn hàng bắt đầu được giao.\n\nCảm ơn bạn đã mua sắm!", customerName, orderId);
                                     break;
                                 case "Đang giao hàng":
                                     subject = String.format("Đơn hàng #%d của bạn đang được giao", orderId);
                                     body = String.format("Xin chào %s,\n\nTin vui! Đơn hàng #%d của bạn đã được bàn giao cho đơn vị vận chuyển và đang trên đường đến với bạn.\nVui lòng chuẩn bị để nhận hàng trong thời gian sớm nhất.\n\nCảm ơn bạn đã mua sắm!", customerName, orderId);
                                     break;
                                 case "Đã giao thành công":
                                     subject = String.format("Đơn hàng #%d đã giao thành công", orderId);
                                     body = String.format("Xin chào %s,\n\nWatchStore xác nhận đơn hàng #%d đã được giao thành công đến bạn.\nHy vọng bạn hài lòng với sản phẩm và hẹn gặp lại trong những lần mua sắm tiếp theo!", customerName, orderId);
                                     break;
                                 case "Đã hủy":
                                     subject = String.format("Đơn hàng #%d của bạn đã được hủy", orderId);
                                     body = String.format("Xin chào %s,\n\nChúng tôi rất tiếc phải thông báo rằng đơn hàng #%d của bạn đã được hủy theo yêu cầu hoặc do một số lý do khác.\nNếu có bất kỳ thắc mắc nào, vui lòng liên hệ với chúng tôi.\n\nCảm ơn bạn.", customerName, orderId);
                                     break;
                             }

                             // Chỉ gửi email nếu có nội dung
                             if (!subject.isEmpty() && !body.isEmpty()) {
                                 com.watchstore.util.EmailUtil.sendEmail(toEmail, subject, body);
                                 System.out.println("   >> Notification email sent to " + toEmail);
                             }
                         } else {
                              System.err.println("   WARN: Could not find order details or customer email for order ID " + orderId + " to send notification.");
                         }
                     } catch (Exception e) {
                         System.err.println("   ERROR: Failed to send notification email for order ID " + orderId);
                         e.printStackTrace();
                         // Không để lỗi gửi mail làm hỏng cả quy trình, chỉ ghi log
                     }
                     // --- KẾT THÚC GỬI EMAIL ---

                     // --- ⭐ LOGIC TRỪ KHO (KIỂM TRA KỸ HƠN) ⭐ ---
                     if ("Đã giao thành công".equals(newStatus)) {
                         System.out.println("   Status is 'Đã giao thành công'. Initiating stock update...");

                         // Lấy chi tiết đơn hàng
                         List<OrderDetail> details = orderDAO.getOrderDetailsByOrderId(orderId);

                         // Kiểm tra xem có lấy được chi tiết không
                         if (details != null && !details.isEmpty()) {
                             System.out.println("   Found " + details.size() + " items in order details.");
                             ProductDAO productDAO = new ProductDAO(); // Khởi tạo ProductDAO
                             boolean allStockUpdated = true; // Cờ theo dõi lỗi trừ kho

                             // Lặp qua từng sản phẩm trong đơn hàng
                             for (OrderDetail detail : details) {
                                 int productId = detail.getProductId();
                                 int quantitySold = detail.getQuantity();
                                 System.out.println("      Processing item: ProductID=" + productId + ", QuantitySold=" + quantitySold);

                                 // Gọi hàm trừ kho (truyền số lượng âm)
                                 boolean updateResult = productDAO.updateStock(productId, -quantitySold); // Gọi hàm updateStock

                                 // Kiểm tra kết quả trừ kho cho từng sản phẩm
                                 if (!updateResult) {
                                     allStockUpdated = false; // Đánh dấu có lỗi
                                     String errorMsg = "   ❌ LỖI: Không thể trừ kho cho SP ID " + productId + " (SL: " + quantitySold + "). Có thể hết hàng.";
                                     System.err.println(errorMsg);
                                     message += " " + errorMsg; // Nối lỗi vào thông báo chính
                                 } else {
                                     System.out.println("      ✅ Stock updated for ProductID=" + productId);
                                 }
                             } // Kết thúc vòng lặp for

                             if (!allStockUpdated) {
                                 error = true; // Nếu có ít nhất 1 lỗi trừ kho, đánh dấu lỗi chung
                                 message += " Vui lòng kiểm tra lại tồn kho.";
                             } else {
                                 System.out.println("   >> All stock updated successfully for Order ID " + orderId);
                             }
                         } else { // Không lấy được chi tiết đơn hàng
                              message += " ⚠️ CẢNH BÁO: Không tìm thấy chi tiết đơn hàng #" + orderId + " để trừ kho.";
                              error = true; // Coi như lỗi
                              System.err.println("   WARN: No order details found for Order ID " + orderId + ". Stock not updated.");
                         }
                     } else { // Trạng thái mới không phải 'Đã giao thành công'
                          System.out.println("   New status is not 'Đã giao thành công'. No stock update needed.");
                     }
                     // --- ⭐ KẾT THÚC TRỪ KHO ⭐ ---

                 } else { // Cập nhật trạng thái đơn hàng thất bại
                     message = "Lỗi: Cập nhật trạng thái đơn hàng #" + orderId + " thất bại! (Hàm updateOrderStatus trả về false)";
                     error = true;
                     System.err.println("   ERROR: orderDAO.updateOrderStatus returned false for Order ID " + orderId);
                 }
             } catch (NumberFormatException e) {
                 message = "Lỗi: ID đơn hàng không hợp lệ (" + orderIdStr + ").";
                 error = true;
                 System.err.println("   ERROR: Invalid Order ID format.");
             } catch (Exception e) {
                 message = "Lỗi hệ thống nghiêm trọng khi xử lý yêu cầu. Xem log server.";
                 error = true;
                 System.err.println("   ERROR: Unexpected exception during doPost:");
                 e.printStackTrace();
             }
         } else { // Thiếu tham số
             message = "Lỗi: Yêu cầu không hợp lệ (thiếu ID đơn hàng hoặc trạng thái mới).";
             error = true;
             System.err.println("   ERROR: Missing 'orderId' or 'newStatus' parameter.");
         }

         // 5. Đặt thông báo vào session
         if (error) {
             session.setAttribute("adminOrderError", message);
         } else {
             session.setAttribute("adminOrderSuccess", message);
         }

         // 6. Redirect lại trang quản lý
         System.out.println("--- AdminOrderServlet POST End ---"); // Log kết thúc
         response.sendRedirect(request.getContextPath() + "/admin/manage-orders");
    }

    @Override
    public String getServletInfo() {
        return "Admin servlet for managing customer orders (view list, update status, and update stock).";
    }
}