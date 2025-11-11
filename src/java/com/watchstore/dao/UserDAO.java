package com.watchstore.dao;

import com.watchstore.context.DBContext;
import com.watchstore.model.User;
import com.watchstore.util.PasswordUtil;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class UserDAO {

    public boolean registerUser(User user) {
        String query = "INSERT INTO users (username, password, fullname, email, phone, address, role) VALUES (?, ?, ?, ?, ?, ?, 0)";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            // *** BƯỚC QUAN TRỌNG: BĂM MẬT KHẨU TRƯỚC KHI LƯU VÀO CSDL
            String hashedPassword = PasswordUtil.hashPassword(user.getPassword());

            ps.setString(1, user.getUsername());
            ps.setString(2, hashedPassword); // Lưu chuỗi hash
            ps.setString(3, user.getFullname());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getAddress());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Kiểm tra thông tin đăng nhập của người dùng bằng bcrypt.
     *
     * @param username Tên đăng nhập
     * @param plainPassword Mật khẩu thuần túy người dùng nhập
     * @return Đối tượng User nếu đăng nhập thành công, ngược lại trả về null.
     */
    public User checkLogin(String username, String plainPassword) {
        String query = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    // 1. Lấy chuỗi hash đã lưu trong CSDL
                    String hashedPasswordFromDB = rs.getString("password");

                    // 2. BƯỚC QUAN TRỌNG: SO SÁNH mật khẩu plain text với chuỗi hash
                    if (PasswordUtil.checkPassword(plainPassword, hashedPasswordFromDB)) {
                        // Mật khẩu khớp, trả về đối tượng User
                        User user = new User();
                        user.setId(rs.getInt("id"));
                        user.setUsername(rs.getString("username"));
                        user.setFullname(rs.getString("fullname"));
                        user.setEmail(rs.getString("email"));
                        user.setPhone(rs.getString("phone"));
                        user.setAddress(rs.getString("address"));
                        user.setRole(rs.getInt("role"));
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean checkUserExist(String username, String email) {
        String query = "SELECT * FROM users WHERE username = ? OR email = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            ps.setString(2, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Nếu có kết quả, trả về true
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUserProfile(User user) {
        String query = "UPDATE users SET fullname = ?, email = ?, phone = ?, address = ? WHERE id = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, user.getFullname());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getAddress());
            ps.setInt(5, user.getId());

            System.out.println("DEBUG (UserDAO - updateUserProfile): Attempting to update user ID: " + user.getId());
            System.out.println("DEBUG (UserDAO - updateUserProfile): Fullname: " + user.getFullname() + ", Email: " + user.getEmail() + ", Phone: " + user.getPhone() + ", Address: " + user.getAddress());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("DEBUG (UserDAO - updateUserProfile): User profile updated successfully for ID: " + user.getId());
                return true;
            } else {
                System.err.println("WARN (UserDAO - updateUserProfile): No rows affected for user ID: " + user.getId() + ". User not found or no changes made.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("ERROR (UserDAO - updateUserProfile): SQL Exception occurred while updating user profile for ID: " + user.getId());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("ERROR (UserDAO - updateUserProfile): An unexpected error occurred while updating user profile for ID: " + user.getId());
            e.printStackTrace();
        }
        return false;
    }
    


    /**
     * === HÀM MỚI === Lấy thông tin người dùng bằng email. Dùng cho chức năng
     * "Quên Mật khẩu".
     *
     * @param email Email của người dùng
     * @return Đối tượng User nếu tìm thấy, null nếu không
     */
    public User getUserByEmail(String email) {
        String query = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password")); // Trả về cả mật khẩu (đã hash)
                    user.setFullname(rs.getString("fullname"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                    user.setAddress(rs.getString("address"));
                    user.setRole(rs.getInt("role"));
                    return user;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * === HÀM MỚI (Nên có) === Lấy mật khẩu (đã hash) hiện tại của người dùng.
     * Dùng để xác thực khi đổi mật khẩu (check mật khẩu cũ).
     *
     * @param userId ID của người dùng
     * @return Chuỗi mật khẩu đã hash
     */
    public String getCurrentHashedPassword(int userId) {
        String query = "SELECT password FROM users WHERE id = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        String query = "SELECT * FROM users ORDER BY id ASC"; // Tri par ID croissant

        // Utilisation de try-with-resources
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                // NE PAS récupérer le mot de passe pour la sécurité
                // user.setPassword(rs.getString("password"));
                user.setFullname(rs.getString("fullname"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setRole(rs.getInt("role")); // Récupère le rôle
                userList.add(user);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération de la liste des utilisateurs (getAllUsers): " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("DEBUG (Admin): Fetched " + userList.size() + " users."); // Log
        return userList;
    }

  
    public boolean deleteUser(int userId) {
        // Kiểm tra để tránh xóa user có ID = 1 (thường là super admin) hoặc ID đang đăng nhập
        // if (userId == 1 || /* logic kiểm tra ID đang đăng nhập */ ) {
        //     System.err.println("WARN: Attempt to delete critical admin user (ID: " + userId + ") blocked.");
        //     return false;
        // }

        String query = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userId);
            int rowsAffected = ps.executeUpdate();
            System.out.println("DEBUG (Admin): deleteUser for ID " + userId + ". Rows affected: " + rowsAffected); // Log
            return rowsAffected > 0;
        } catch (Exception e) {
            // Có thể lỗi do khóa ngoại (foreign key constraint) nếu user đã có đơn hàng/giỏ hàng
            // Cần xử lý các bảng liên quan trước khi xóa user hoặc set ON DELETE CASCADE/SET NULL
            System.err.println("Lỗi khi xóa người dùng (deleteUser) ID " + userId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * === HÀM MỚI (Cho Admin - Tùy chọn) === Cập nhật vai trò (role) của người
     * dùng.
     *
     * @param userId ID người dùng.
     * @param newRole Vai trò mới (0 = customer, 1 = admin).
     * @return true nếu thành công.
     */
    public boolean updateUserRole(int userId, int newRole) {
        // Thêm kiểm tra không cho hạ quyền super admin hoặc chính mình
        // if (userId == 1 || /* logic kiểm tra ID đang đăng nhập */ ) return false;

        String query = "UPDATE users SET role = ? WHERE id = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, newRole);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    // ⭐ REQUIRED for Forgot Password ⭐




    // ⭐ REQUIRED for Forgot Password (Token Generation) ⭐
    public String generateRandomToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32]; // 32 bytes = 256 bits
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes); // URL-safe base64 encoding
    }
    /**
     * ⭐ HÀM CÒN THIẾU: Cập nhật mật khẩu mới (đã băm) cho người dùng theo ID.
     * Cần cho chức năng đổi mật khẩu trong trang Edit Profile.
     * @param userId ID của người dùng
     * @param newHashedPassword Mật khẩu mới đã được băm
     * @return true nếu cập nhật thành công.
     */
    public boolean updatePassword(int userId, String newHashedPassword) {
        String query = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, newHashedPassword); // Mật khẩu mới đã băm
            ps.setInt(2, userId);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                 System.out.println("DEBUG (UserDAO): Updated password successfully for user ID: " + userId);
                 return true;
            } else {
                 System.err.println("WARN (UserDAO - updatePassword): Failed to update password for user ID (not found?): " + userId);
                 return false;
            }
        } catch (Exception e) {
             System.err.println("ERROR (UserDAO - updatePassword): " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }


    public User findUserByEmail(String email) {
        String query = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, email);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setFullname(rs.getString("fullname"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getInt("role"));
                    user.setAddress(rs.getString("address"));
                    user.setPhone(rs.getString("phone"));
                    return user;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Cập nhật mật khẩu mới (đã băm) cho người dùng dựa trên email.
     * @param email Email của người dùng
     * @param newHashedPassword Mật khẩu mới đã được băm
     * @return true nếu cập nhật thành công.
     */
    public boolean updatePasswordByEmail(String email, String newHashedPassword) {
        String query = "UPDATE users SET password = ? WHERE email = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, newHashedPassword);
            ps.setString(2, email);

            return ps.executeUpdate() > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
