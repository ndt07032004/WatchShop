package com.watchstore.controlller; // Giữ nguyên package của bạn

import com.watchstore.dao.CategoryDAO;
import com.watchstore.dao.OrderDAO;
import com.watchstore.model.Category;
import com.watchstore.model.Order;         // Import Order
import com.watchstore.model.OrderDetail;   // Import OrderDetail
import com.watchstore.model.User;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "OrderDetailServlet", urlPatterns = {"/order-detail"}) // URL khớp với link trong JSP
public class OrderDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("account");

        // 1. Yêu cầu đăng nhập
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 2. Lấy orderId từ tham số URL
        String idParam = request.getParameter("id");
        int orderId = 0;
        if (idParam != null) {
            try {
                orderId = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                System.err.println("OrderDetailServlet: Invalid order ID format.");
                response.sendRedirect("order-history"); // Chuyển về trang lịch sử nếu ID lỗi
                return;
            }
        } else {
             response.sendRedirect("order-history"); // Chuyển về trang lịch sử nếu thiếu ID
             return;
        }

        System.out.println("DEBUG: Viewing details for Order ID: " + orderId); // Log

        try {
            OrderDAO orderDAO = new OrderDAO();

            // 3. Lấy thông tin cơ bản của đơn hàng (để kiểm tra user và hiển thị)
            // (Bạn có thể cần thêm hàm getOrderById vào OrderDAO)
            // Order orderInfo = orderDAO.getOrderById(orderId);
            // Tạm thời lấy lại từ danh sách (cách này không tối ưu nhưng dùng được nếu chưa có getOrderById)
            Order orderInfo = null;
            List<Order> userOrders = orderDAO.getOrdersByUserId(user.getId());
            for(Order o : userOrders) {
                if(o.getId() == orderId) {
                    orderInfo = o;
                    break;
                }
            }


            // 4. Kiểm tra xem đơn hàng có thuộc về user đang đăng nhập không
            if (orderInfo == null) { // || orderInfo.getUserId() != user.getId()) { -> bỏ check userId nếu bạn lấy lại từ list userOrders
                System.err.println("WARN: User " + user.getId() + " trying to access order " + orderId + " which does not belong to them or does not exist.");
                response.sendRedirect("order-history"); // Không cho xem đơn hàng của người khác
                return;
            }

            // 5. Lấy danh sách chi tiết sản phẩm của đơn hàng
            List<OrderDetail> orderDetails = orderDAO.getOrderDetailsByOrderId(orderId); // Dùng hàm mới

            // 6. Lấy danh sách category cho menu
            CategoryDAO categoryDAO = new CategoryDAO();
            List<Category> categoryList = categoryDAO.getAllCategories();

            // 7. Gửi dữ liệu sang JSP
            request.setAttribute("orderInfo", orderInfo);       // Thông tin chung của đơn hàng
            request.setAttribute("orderDetails", orderDetails); // Danh sách sản phẩm chi tiết
            request.setAttribute("categoryList", categoryList);
            request.setAttribute("activePage", "profile");      // Giữ active menu Hồ sơ

            // 8. Forward đến trang JSP
            request.getRequestDispatcher("order_detail.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }

    @Override
    public String getServletInfo() {
        return "Fetches and displays the details of a specific order.";
    }
}