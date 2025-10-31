package com.watchstore.util; // Or your package

import com.watchstore.dao.PasswordResetDAO; // Make sure PasswordResetDAO is accessible
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.io.UnsupportedEncodingException; // Added for exception handling

public class EmailUtil {

    // --- ⭐ CONFIGURE YOUR GMAIL DETAILS HERE ⭐ ---
    private static final String FROM_EMAIL = "your-email@gmail.com"; // Your sending Gmail address
    private static final String APP_PASSWORD = "your_app_password"; // Your 16-digit Gmail App Password
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587"; // TLS Port for Gmail
    // --- END CONFIGURATION ---

    /**
     * Sends the password reset email.
     * @param toEmail Recipient's email
     * @param recipientName Recipient's name
     * @param resetLink The password reset link including the token
     * @return true if sent successfully, false otherwise.
     */
    public static boolean sendPasswordResetEmail(String toEmail, String recipientName, String resetLink) {
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // Use STARTTLS

        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        };

        Session session = Session.getInstance(props, auth);
        // session.setDebug(true); // Uncomment for detailed SMTP logs if needed

        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(FROM_EMAIL, "WatchStore Support")); // Sender display name
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            msg.setSubject("Yêu cầu đặt lại mật khẩu WatchStore", "UTF-8");

            // Build HTML content
            String htmlContent = String.format("""
                <html><body style='font-family: Arial, sans-serif;'>
                <h2>Xin chào %s,</h2>
                <p>Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản WatchStore của bạn.</p>
                <p>Vui lòng nhấp vào liên kết dưới đây để đặt lại mật khẩu:</p>
                <p style='margin: 20px 0;'>
                    <a href='%s' style='background-color: #0d6efd; color: white; padding: 10px 15px; text-decoration: none; border-radius: 5px;'>Đặt Lại Mật Khẩu</a>
                </p>
                <p>Liên kết này sẽ hết hạn sau %d phút.</p>
                <p>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>
                <p>Trân trọng,<br>Đội ngũ WatchStore</p>
                </body></html>
                """, recipientName, resetLink, PasswordResetDAO.EXPIRATION_MINUTES); // Use public constant

            msg.setContent(htmlContent, "text/html; charset=UTF-8");

            Transport.send(msg); // Send the email

            System.out.println("INFO (EmailUtil): Password reset email sent successfully to " + toEmail);
            return true;

        } catch (MessagingException | UnsupportedEncodingException e) {
            System.err.println("ERROR (EmailUtil): Failed to send email to " + toEmail);
            e.printStackTrace();
            return false;
        }
    }
}