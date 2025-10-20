

package com.watchstore.dao;

import com.watchstore.context.DBContext;
import com.watchstore.model.Category;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections; // Cần thiết để đảo ngược danh sách

public class CategoryDAO {

    /**
     * Lấy tất cả các danh mục từ CSDL, ngoại trừ mục "Tất cả sản phẩm" (ID=1).
     * Sắp xếp ngược thứ tự và chèn mục "Tất cả sản phẩm" (ID=0) vào đầu.
     * @return Danh sách các đối tượng Category đã được sắp xếp.
     */

    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        // Truy vấn tất cả trừ mục ID 1 (Tất cả sản phẩm theo DB)
        String query = "SELECT * FROM categories WHERE id != 1"; 
        
        // ... (khối try-with-resources để lấy danh mục)
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Category c = new Category();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                list.add(c);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        
        // Đảo ngược thứ tự các danh mục còn lại (6, 5, 4)
        Collections.reverse(list); 
        
        // Tạo và chèn mục "Tất cả sản phẩm" vào đầu danh sách (index 0).
        Category allProductsCat = new Category();
        allProductsCat.setId(0); 
        allProductsCat.setName("Tất cả sản phẩm");
        
        list.add(0, allProductsCat); 
        
        return list;
    }
}