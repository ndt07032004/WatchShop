package com.watchstore.controlller;

import com.watchstore.dao.CategoryDAO;
import com.watchstore.dao.PasswordResetDAO; // Import
import com.watchstore.dao.UserDAO;
import com.watchstore.model.Category;
import com.watchstore.model.User;
import com.watchstore.util.EmailUtil; // Import

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ForgotPasswordServlet", urlPatterns = {"/forgot-password"})
public class ForgotPasswordServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        loadCategoriesAndForward(request, response, "forgot.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String email = request.getParameter("email");
        String message = "";
        boolean error = true;

        if (email == null || email.trim().isEmpty()) {
            message = "Vui lòng nhập địa chỉ email của bạn.";
        } else {
            email = email.trim();
            UserDAO userDAO = new UserDAO();
            User user = userDAO.findUserByEmail(email);

            if (user == null) {
                // Security: Don't reveal if email exists or not
                message = "Nếu email của bạn tồn tại trong hệ thống, bạn sẽ nhận được một liên kết đặt lại mật khẩu.";
                error = false; // Pretend success
                System.out.println("DEBUG (ForgotPassword): Request for non-existent email: " + email);
            } else {
                PasswordResetDAO resetDAO = new PasswordResetDAO();
                String token = userDAO.generateRandomToken();

                if (resetDAO.saveToken(email, token)) {
                    // Build reset link dynamically
                    String resetLink = String.format("%s://%s:%d%s/reset-password?token=%s",
                                       request.getScheme(), request.getServerName(), request.getServerPort(),
                                       request.getContextPath(), token);

                    boolean emailSent = EmailUtil.sendPasswordResetEmail(email, user.getFullname(), resetLink);

                    if (emailSent) {
                        message = "Yêu cầu đặt lại mật khẩu đã được gửi. Vui lòng kiểm tra email (cả thư mục Spam).";
                        error = false;
                        System.out.println("DEBUG (ForgotPassword): Email sent to: " + email);
                    } else {
                        message = "Lỗi khi gửi email. Vui lòng thử lại sau.";
                        System.err.println("ERROR (ForgotPassword): Failed to send email to: " + email);
                    }
                } else {
                    message = "Lỗi hệ thống khi xử lý yêu cầu. Vui lòng thử lại sau.";
                    System.err.println("ERROR (ForgotPassword): Failed to save token for: " + email);
                }
            }
        }

        if (error) {
            request.setAttribute("error", message);
        } else {
            request.setAttribute("success", message);
        }

        loadCategoriesAndForward(request, response, "forgot.jsp");
    }

    private void loadCategoriesAndForward(HttpServletRequest request, HttpServletResponse response, String jspPage)
            throws ServletException, IOException {
        // (Implementation provided previously)
         CategoryDAO categoryDAO = new CategoryDAO(); List<Category> categoryList = categoryDAO.getAllCategories(); request.setAttribute("categoryList", categoryList); request.getRequestDispatcher(jspPage).forward(request, response);
    }

     @Override
    public String getServletInfo() {
        return "Handles initial password reset request.";
    }
}