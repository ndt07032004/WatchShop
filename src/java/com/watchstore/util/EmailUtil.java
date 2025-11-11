package com.watchstore.util;

import java.util.Properties;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailUtil {

    // --- ⭐ CẤU HÌNH EMAIL CỦA BẠN TẠI ĐÂY ⭐ ---
    // 1. Email Gmail của bạn
        private static final String FROM_EMAIL = "ndthai.dhti16a1hn@sv.uneti.edu.vn"; // THAY "your-email@gmail.com" BẰNG EMAIL CỦA BẠN
        // 2. Mật khẩu ứng dụng (16 ký tự) lấy từ Google
        private static final String APP_PASSWORD = "bynb mnap ekmk esnk"; // THAY "your_16_digit_app_password" BẰNG MẬT KHẨU ỨNG DỤNG CỦA BẠN 
    // --- HẾT CẤU HÌNH ---

    /**
     * Gửi email văn bản đơn giản.
     * @param toEmail Email người nhận
     * @param subject Tiêu đề email
     * @param body Nội dung email
     * @throws MessagingException
     */
    public static void sendEmail(String toEmail, String subject, String body) throws MessagingException {
        
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // SMTP Host
        props.put("mail.smtp.port", "587"); // TLS Port
        props.put("mail.smtp.auth", "true"); // Bật xác thực
        props.put("mail.smtp.starttls.enable", "true"); // Bật STARTTLS

        // BẬT DEBUG MODE
        // props.put("mail.debug", "true");

        // Tạo phiên (Session) với trình xác thực
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        };
        Session session = Session.getInstance(props, auth);

        // Tạo đối tượng MimeMessage
        MimeMessage msg = new MimeMessage(session);
        
        try {
            // Đặt người gửi
            msg.setFrom(new InternetAddress(FROM_EMAIL, "WatchStore Support"));
            
            // Đặt người nhận
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            
            // Đặt tiêu đề (Subject) - hỗ trợ Tiếng Việt
            msg.setSubject(subject, "UTF-8");
            
            // Đặt nội dung (Body) - hỗ trợ Tiếng Việt
            msg.setText(body, "UTF-8");
            
            // Gửi email
            Transport.send(msg);
            
            System.out.println("INFO (EmailUtil): Gửi email thành công đến " + toEmail);
            
        } catch (Exception e) {
            System.err.println("ERROR (EmailUtil): Lỗi khi gửi email đến " + toEmail);
            e.printStackTrace();
            throw new MessagingException("Lỗi khi gửi email", e);
        }
    }
}