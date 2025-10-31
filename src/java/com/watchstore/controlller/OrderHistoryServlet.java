package com.watchstore.controlller; // Giữ nguyên package của bạn

import com.watchstore.dao.CategoryDAO;
import com.watchstore.dao.OrderDAO;
import com.watchstore.model.Category;
import com.watchstore.model.Order;
import com.watchstore.model.User;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "OrderHistoryServlet", urlPatterns = {"/order-history"})
public class OrderHistoryServlet extends HttpServlet {

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

        try {
            // 2. Lấy danh sách đơn hàng từ DAO
            OrderDAO orderDAO = new OrderDAO();
            List<Order> orderList = orderDAO.getOrdersByUserId(user.getId()); // Dùng hàm mới đã tạo

            // 3. Lấy danh sách category (cần cho top_menu.jsp)
            CategoryDAO categoryDAO = new CategoryDAO();
            List<Category> categoryList = categoryDAO.getAllCategories();

            // 4. Gửi dữ liệu sang trang JSP
            request.setAttribute("orderList", orderList);
            request.setAttribute("categoryList", categoryList);
            request.setAttribute("activePage", "profile"); // Đánh dấu active cho menu Hồ sơ

            // 5. Forward đến trang JSP để hiển thị
            request.getRequestDispatcher("order_history.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace(); // In lỗi ra console
            response.sendRedirect("error.jsp"); // Chuyển hướng đến trang lỗi chung
        }
    }

     @Override
    public String getServletInfo() {
        return "Fetches and displays the order history for the logged-in user.";
    }
}