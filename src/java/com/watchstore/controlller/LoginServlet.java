package com.watchstore.controlller; // Giữ nguyên package của bạn

import com.watchstore.dao.CartDAO; // <<< THÊM IMPORT MỚI
import com.watchstore.dao.UserDAO;
import com.watchstore.model.Cart;     // <<< THÊM IMPORT MỚI, đảm bảo đã cập nhật Model
import com.watchstore.model.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String userParam = request.getParameter("username");
        String passParam = request.getParameter("password");

        UserDAO userDAO = new UserDAO();
        // Hàm checkLogin NÊN kiểm tra mật khẩu đã hash
        User account = userDAO.checkLogin(userParam, passParam);

        if (account == null) {
            // Đăng nhập thất bại
            request.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng!");
            // Cần lấy lại categoryList để hiển thị menu khi forward
            com.watchstore.dao.CategoryDAO cDao = new com.watchstore.dao.CategoryDAO();
            request.setAttribute("categoryList", cDao.getAllCategories());
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } else {
            // Đăng nhập thành công
            HttpSession session = request.getSession();
            session.setAttribute("account", account);
            session.setMaxInactiveInterval(60 * 60 * 24); // Session 1 ngày

            // --- ⭐ LOGIC MỚI: TẢI VÀ GỘP GIỎ HÀNG ---

            // 1. Lấy giỏ hàng tạm của khách (nếu có) từ session
            Cart sessionCart = (Cart) session.getAttribute("cart");

            // 2. Khởi tạo CartDAO
            CartDAO cartDAO = new CartDAO();

            // 3. Lấy/Tạo giỏ hàng CSDL cho user này
            // Hàm getCartByUserId đã bao gồm việc tạo mới nếu chưa có
            Cart dbCart = cartDAO.getCartByUserId(account.getId());

            // 4. Gộp giỏ hàng session vào giỏ CSDL (nếu giỏ session tồn tại)
            // Hàm mergeSessionCart trong CartDAO sẽ xử lý logic cộng dồn
            if (sessionCart != null && dbCart != null) {
                 cartDAO.mergeSessionCart(dbCart.getId(), sessionCart);
            }

            // 5. Tải lại giỏ hàng cuối cùng từ CSDL (đã bao gồm item vừa gộp)
            // và lưu vào session, thay thế giỏ hàng cũ (nếu có)
            Cart finalCart = cartDAO.getCartByUserId(account.getId());
            session.setAttribute("cart", finalCart);

            // --- KẾT THÚC LOGIC MỚI ---

            // 6. Chuyển hướng dựa trên vai trò
            if (account.getRole() == 1) { // Giả sử role 1 là Admin
                response.sendRedirect(request.getContextPath() + "/admin/manage-orders");
            } else { // Role 0 là User
                response.sendRedirect("home"); // Về trang chủ
            }
        }
    }

    @Override
    public String getServletInfo() {
        return "Handles user login and merges session cart into database cart.";
    }
}