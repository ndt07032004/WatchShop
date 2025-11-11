package com.watchstore.controlller;

import com.watchstore.dao.PasswordResetDAO;
import com.watchstore.dao.UserDAO;
import com.watchstore.model.User;
import com.watchstore.util.EmailUtil;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ForgotPasswordServlet", urlPatterns = {"/forgot-password"})
public class ForgotPasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String email = request.getParameter("email");
        
        UserDAO userDAO = new UserDAO();
        PasswordResetDAO passwordResetDAO = new PasswordResetDAO();
        
        User user = userDAO.findUserByEmail(email);

        String successMessage = "Nếu email của bạn tồn tại trong hệ thống, một liên kết đặt lại mật khẩu đã được gửi. Vui lòng kiểm tra hộp thư của bạn (bao gồm cả thư mục spam).";
        request.setAttribute("success", successMessage);

        if (user != null) {
            System.out.println("DEBUG: User found for email: " + email + ". Preparing to send email.");
            try {
                String token = generateRandomToken();
                boolean saved = passwordResetDAO.saveToken(email, token);

                if (saved) {
                    String resetLink = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/reset-password?token=" + token;

                    String subject = "WatchStore - Yêu Cầu Đặt Lại Mật Khẩu";
                    String body = "Xin chào " + user.getFullname() + ",\n\n"
                                + "Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.\n"
                                + "Vui lòng nhấp vào liên kết bên dưới để đặt lại mật khẩu của bạn:\n"
                                + resetLink + "\n\n"
                                + "Liên kết này sẽ hết hạn trong vòng " + PasswordResetDAO.EXPIRATION_MINUTES + " phút.\n"
                                + "Nếu bạn không yêu cầu điều này, vui lòng bỏ qua email này.\n\n"
                                + "Trân trọng,\nĐội ngũ WatchStore";

                    System.out.println("DEBUG: Calling EmailUtil.sendEmail...");
                    EmailUtil.sendEmail(email, subject, body);
                    System.out.println("SUCCESS: EmailUtil.sendEmail call completed without throwing an exception.");

                }
            } catch (Exception e) {
                System.err.println("ERROR: An exception occurred during the email sending process.");
                e.printStackTrace(); 
            }
        } else {
            System.out.println("DEBUG: No user found for email: " + email + ". Not sending email.");
        }
        
        request.getRequestDispatcher("forgot.jsp").forward(request, response);
    }

    private String generateRandomToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32]; // 256 bits
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}