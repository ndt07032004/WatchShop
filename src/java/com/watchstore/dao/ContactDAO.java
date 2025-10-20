package com.watchstore.dao;

import com.watchstore.context.DBContext;
import com.watchstore.model.Contact;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ContactDAO {

    /**
     * Thêm một liên hệ mới vào CSDL.
     * @param contact Đối tượng Contact chứa thông tin
     * @return true nếu thêm thành công, false nếu thất bại.
     */
    public boolean addContact(Contact contact) {
        String query = "INSERT INTO contacts (name, email, message) VALUES (?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, contact.getName());
            ps.setString(2, contact.getEmail());
            ps.setString(3, contact.getMessage());
            
            return ps.executeUpdate() > 0;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}