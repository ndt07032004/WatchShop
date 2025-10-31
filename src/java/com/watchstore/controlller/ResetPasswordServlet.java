package com.watchstore.controlller;

import com.watchstore.dao.CategoryDAO;
import com.watchstore.dao.PasswordResetDAO;
import com.watchstore.dao.UserDAO;
import com.watchstore.model.Category;
import com.watchstore.util.PasswordUtil; // Import

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ResetPasswordServlet", urlPatterns = {"/reset-password"})
public class ResetPasswordServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");
        PasswordResetDAO resetDAO = new PasswordResetDAO();
        String message = "";
        boolean validToken = false;

        if (token == null || token.trim().isEmpty()) {
            message = "Token đặt lại mật khẩu không hợp lệ hoặc bị thiếu.";
        } else {
            token = token.trim();
            String email = resetDAO.getEmailByValidToken(token); // Checks expiry too
            if (email != null) {
                request.setAttribute("token", token); // Pass token to form
                validToken = true;
            } else {
                message = "Liên kết đặt lại mật khẩu không hợp lệ hoặc đã hết hạn. Vui lòng yêu cầu lại.";
            }
        }

        if (!validToken) {
            request.setAttribute("error", message);
        }

        loadCategoriesAndForward(request, response, "reset_password.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String token = request.getParameter("token");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        String message = "";
        boolean error = true;

        PasswordResetDAO resetDAO = new PasswordResetDAO();
        UserDAO userDAO = new UserDAO();

        if (token == null || token.trim().isEmpty()) {
            message = "Token không hợp lệ.";
        } else if (newPassword == null || newPassword.isEmpty()) {
             message = "Vui lòng nhập mật khẩu mới.";
        } else if (newPassword.length() < 6) { // Basic strength check
             message = "Mật khẩu mới phải có ít nhất 6 ký tự.";
        } else if (confirmPassword == null || !newPassword.equals(confirmPassword)) {
             message = "Mật khẩu xác nhận không khớp.";
        } else {
            token = token.trim();
            String email = resetDAO.getEmailByValidToken(token); // Re-validate token
            if (email == null) {
                message = "Liên kết đặt lại mật khẩu không hợp lệ hoặc đã hết hạn.";
            } else {
                try {
                    String hashedNewPassword = PasswordUtil.hashPassword(newPassword); // Hash the new password
                    if (userDAO.updatePasswordByEmail(email, hashedNewPassword)) {
                        resetDAO.deleteTokenByEmail(email); // Delete token after successful update
                        message = "Đặt lại mật khẩu thành công! Bạn có thể đăng nhập bằng mật khẩu mới.";
                        error = false;

                        // Redirect to login with success message in session
                        request.getSession().setAttribute("loginMessage", message);
                        response.sendRedirect("login.jsp");
                        return; // IMPORTANT: Stop execution after redirect

                    } else {
                        message = "Lỗi khi cập nhật mật khẩu. Vui lòng thử lại.";
                    }
                } catch (Exception e) {
                     message = "Lỗi hệ thống khi cập nhật mật khẩu.";
                     e.printStackTrace();
                }
            }
        }

        // If error occurred, forward back to the reset form with error message
        request.setAttribute("token", token); // Pass token back to form
        request.setAttribute("error", message);
        loadCategoriesAndForward(request, response, "reset_password.jsp");
    }

    private void loadCategoriesAndForward(HttpServletRequest request, HttpServletResponse response, String jspPage)
            throws ServletException, IOException {
        // (Implementation provided previously)
         CategoryDAO categoryDAO = new CategoryDAO(); List<Category> categoryList = categoryDAO.getAllCategories(); request.setAttribute("categoryList", categoryList); request.getRequestDispatcher(jspPage).forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Handles password reset link validation and password update.";
    }
}