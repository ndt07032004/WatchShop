package com.watchstore.dao;

import com.watchstore.context.DBContext;
import com.watchstore.model.Contact;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;   // ✅ Bổ sung import bị thiếu
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lấy tất cả các liên hệ từ CSDL
     */
    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<>();
        String query = "SELECT * FROM contacts ORDER BY created_at DESC";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Contact contact = new Contact();
                contact.setId(rs.getInt("id"));
                contact.setName(rs.getString("name"));
                contact.setEmail(rs.getString("email"));
                contact.setMessage(rs.getString("message"));
                if (hasColumn(rs, "created_at")) {
                    contact.setCreatedAt(rs.getTimestamp("created_at"));
                }
                contactList.add(contact);
            }

            System.out.println("DEBUG (ContactDAO): Retrieved " + contactList.size() + " contacts.");
        } catch (Exception e) {
            System.err.println("ERROR (ContactDAO - getAllContacts): " + e.getMessage());
            e.printStackTrace();
        }
        return contactList;
    }

    // ✅ Kiểm tra cột có tồn tại hay không
    private boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
