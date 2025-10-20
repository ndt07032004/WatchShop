package com.watchstore.dao;

import com.watchstore.context.DBContext;
import com.watchstore.model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // Hàm private để tái sử dụng việc map dữ liệu từ ResultSet sang đối tượng Product
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getDouble("price"));
        p.setImage(rs.getString("image"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setStock(rs.getInt("stock"));
        return p;
    }
    // Trong ProductDAO.java

    /**
     * Lấy tất cả sản phẩm dựa vào Category ID.
     *
     * @param categoryId ID của danh mục sản phẩm (hoặc 0 nếu là Tất cả sản
     * phẩm)
     * @return Danh sách các sản phẩm.
     */
    public List<Product> getProductsByCategoryId(int categoryId) {
        List<Product> list = new ArrayList<>();
        String query;
        if (categoryId == 0 || categoryId == 1) { // Thêm điều kiện categoryId == 1 để xử lý mục DB ID 1
            // Nếu ID là 0 (được set trong DAO) hoặc ID là 1 (Tất cả sản phẩm từ DB), lấy tất cả sản phẩm
            query = "SELECT * FROM products ORDER BY id DESC";
        } else {
            // Lấy sản phẩm theo category_id cụ thể
            query = "SELECT * FROM products WHERE category_id = ? ORDER BY id DESC";
        }

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            if (categoryId != 0 && categoryId != 1) {
                ps.setInt(1, categoryId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProduct(rs));
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Product> getProductsByPriceRange(int categoryId, double minPrice, double maxPrice) {
        List<Product> list = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM products WHERE 1=1 ");

        if (categoryId > 0) {
            queryBuilder.append(" AND category_id = ? ");
        }

        if (minPrice > 0) {
            queryBuilder.append(" AND price >= ? ");
        }
        
        // Chỉ thêm điều kiện maxPrice nếu maxPrice > 0 (tức là có giới hạn trên)
        if (maxPrice > 0) {
            queryBuilder.append(" AND price <= ? ");
        }
        
        queryBuilder.append(" ORDER BY id DESC");

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(queryBuilder.toString())) {
            
            int paramIndex = 1;
            if (categoryId > 0) {
                ps.setInt(paramIndex++, categoryId);
            }
            if (minPrice > 0) {
                ps.setDouble(paramIndex++, minPrice);
            }
            if (maxPrice > 0) {
                ps.setDouble(paramIndex++, maxPrice);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProduct(rs));
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * Lấy tất cả sản phẩm từ CSDL.
     *
     * @return Danh sách các sản phẩm.getProductsByCategoryId
     */
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String query = "SELECT * FROM products ORDER BY id DESC";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy một số lượng giới hạn sản phẩm (dùng cho trang chủ).
     *
     * @param limit Số lượng sản phẩm cần lấy
     * @return Danh sách sản phẩm.
     */
    public List<Product> getTopProducts(int limit) {
        List<Product> list = new ArrayList<>();
        String query = "SELECT * FROM products ORDER BY id DESC LIMIT ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProduct(rs));
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy thông tin một sản phẩm dựa vào ID.
     *
     * @param id ID của sản phẩm
     * @return Đối tượng Product hoặc null nếu không tìm thấy.
     */
    public Product getProductById(int id) {
        String query = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduct(rs);
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Thêm một sản phẩm mới vào CSDL (cho admin).
     *
     * @param p Đối tượng Product cần thêm
     */
    public void addProduct(Product p) {
        String query = "INSERT INTO products(name, description, price, image, category_id, stock) VALUES(?,?,?,?,?,?)";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setDouble(3, p.getPrice());
            ps.setString(4, p.getImage());
            ps.setInt(5, p.getCategoryId());
            ps.setInt(6, p.getStock());
            ps.executeUpdate();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cập nhật thông tin một sản phẩm (cho admin).
     *
     * @param p Đối tượng Product chứa thông tin mới
     */
    public void updateProduct(Product p) {
        String query = "UPDATE products SET name=?, description=?, price=?, image=?, category_id=?, stock=? WHERE id=?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setDouble(3, p.getPrice());
            ps.setString(4, p.getImage());
            ps.setInt(5, p.getCategoryId());
            ps.setInt(6, p.getStock());
            ps.setInt(7, p.getId());
            ps.executeUpdate();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Xóa một sản phẩm khỏi CSDL (cho admin).
     *
     * @param id ID của sản phẩm cần xóa
     */
    public void deleteProduct(int id) {
        String query = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
