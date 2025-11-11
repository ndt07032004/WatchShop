package com.watchstore.dao; // <-- Dòng 1: Khai báo package

// Các import cần thiết
import com.watchstore.context.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class PasswordResetDAO { // <-- Khai báo class

    // Hằng số thời gian hết hạn (SỬA THÀNH PUBLIC)
    public static final long EXPIRATION_MINUTES = 60;

    /**
     * Lưu token reset mật khẩu vào CSDL.
     * Xóa các token cũ của cùng email trước khi thêm token mới.
     * @param email Email của người dùng
     * @param token Token reset ngẫu nhiên
     * @return true nếu lưu thành công
     */
    public boolean saveToken(String email, String token) {
        String deleteSql = "DELETE FROM password_resets WHERE email = ?";
        String insertSql = "INSERT INTO password_resets (email, token, created_at) VALUES (?, ?, NOW())";

        Connection conn = null;
        PreparedStatement psDelete = null;
        PreparedStatement psInsert = null;
        boolean success = false;

        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            // 1. Xóa token cũ (nếu có)
            psDelete = conn.prepareStatement(deleteSql);
            psDelete.setString(1, email);
            psDelete.executeUpdate();

            // 2. Thêm token mới
            psInsert = conn.prepareStatement(insertSql);
            psInsert.setString(1, email);
            psInsert.setString(2, token);
            int rowsAffected = psInsert.executeUpdate();

            if (rowsAffected > 0) {
                conn.commit(); // Hoàn thành transaction
                success = true;
                System.out.println("DEBUG (PasswordResetDAO): Saved token for " + email);
            } else {
                conn.rollback(); // Hủy nếu insert lỗi
                 System.err.println("ERROR (PasswordResetDAO): Failed to insert token for " + email);
            }

        } catch (Exception e) {
             System.err.println("ERROR (PasswordResetDAO - saveToken): " + e.getMessage());
             e.printStackTrace();
             try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        } finally {
             try { if (psDelete != null) psDelete.close(); } catch (SQLException e) { e.printStackTrace(); }
             try { if (psInsert != null) psInsert.close(); } catch (SQLException e) { e.printStackTrace(); }
             try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException e) { e.printStackTrace(); }
        }
        return success;
    }

    /**
     * Tìm email dựa vào token và kiểm tra xem token còn hạn không.
     * @param token Token cần kiểm tra
     * @return Email nếu token hợp lệ và còn hạn, ngược lại trả về null.
     */
    public String getEmailByValidToken(String token) {
        String sql = "SELECT email, created_at FROM password_resets WHERE token = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    LocalDateTime createdAtLDT = createdAt.toLocalDateTime();
                    LocalDateTime expiryTime = createdAtLDT.plusMinutes(EXPIRATION_MINUTES);

                    // Kiểm tra xem thời gian hiện tại có trước thời gian hết hạn không
                    if (LocalDateTime.now().isBefore(expiryTime)) {
                        System.out.println("DEBUG (PasswordResetDAO): Token valid for " + rs.getString("email"));
                        return rs.getString("email"); // Token hợp lệ
                    } else {
                         System.out.println("DEBUG (PasswordResetDAO): Token expired for " + rs.getString("email"));
                        // (Tùy chọn) Xóa token hết hạn ở đây
                        // deleteToken(token);
                        return null; // Token hết hạn
                    }
                }
            }
        } catch (Exception e) {
             System.err.println("ERROR (PasswordResetDAO - getEmailByValidToken): " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("DEBUG (PasswordResetDAO): Token not found: " + token);
        return null; // Token không tồn tại
    }

    /**
     * Xóa token sau khi đã sử dụng hoặc hết hạn.
     * @param token Token cần xóa
     */
    public void deleteToken(String token) {
        String sql = "DELETE FROM password_resets WHERE token = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            int rowsAffected = ps.executeUpdate();
             if (rowsAffected > 0) {
                 System.out.println("DEBUG (PasswordResetDAO): Deleted token: " + token);
             } else {
                 System.out.println("DEBUG (PasswordResetDAO): Token not found to delete: " + token);
             }
        } catch (Exception e) {
             System.err.println("ERROR (PasswordResetDAO - deleteToken): " + e.getMessage());
            e.printStackTrace();
        }
    }

     /**
     * Xóa token theo email (dùng khi cập nhật MK thành công).
     * @param email Email có token cần xóa
     */
     public void deleteTokenByEmail(String email) {
        String sql = "DELETE FROM password_resets WHERE email = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("DEBUG (PasswordResetDAO): Deleted token(s) for email: " + email);
            }
        } catch (Exception e) {
             System.err.println("ERROR (PasswordResetDAO - deleteTokenByEmail): " + e.getMessage());
            e.printStackTrace();
        }
    }
}