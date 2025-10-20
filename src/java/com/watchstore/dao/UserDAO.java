package com.watchstore.dao;

import com.watchstore.context.DBContext;
import com.watchstore.model.User;
import com.watchstore.util.PasswordUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public boolean registerUser(User user) {
        String query = "INSERT INTO users (username, password, fullname, email, phone, address, role) VALUES (?, ?, ?, ?, ?, ?, 0)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            // *** BƯỚC QUAN TRỌNG: BĂM MẬT KHẨU TRƯỚC KHI LƯU VÀO CSDL
            String hashedPassword = PasswordUtil.hashPassword(user.getPassword());
            
            ps.setString(1, user.getUsername());
            ps.setString(2, hashedPassword); // Lưu chuỗi hash
            ps.setString(3, user.getFullname());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getAddress());
            
            return ps.executeUpdate() > 0;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Kiểm tra thông tin đăng nhập của người dùng bằng bcrypt.
     * @param username Tên đăng nhập
     * @param plainPassword Mật khẩu thuần túy người dùng nhập
     * @return Đối tượng User nếu đăng nhập thành công, ngược lại trả về null.
     */
    public User checkLogin(String username, String plainPassword) {
        String query = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
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
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean checkUserExist(String username, String email) {
        String query = "SELECT * FROM users WHERE username = ? OR email = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, username);
            ps.setString(2, email);
            
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Nếu có kết quả, trả về true
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Thêm một người dùng mới vào cơ sở dữ liệu.
     * @param user Đối tượng User chứa thông tin đăng ký
     * @return true nếu đăng ký thành công, false nếu thất bại.
     */
   
}