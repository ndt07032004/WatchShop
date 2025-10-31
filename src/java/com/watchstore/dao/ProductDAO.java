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

    // Hàm map dữ liệu từ ResultSet sang Product
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getDouble("price"));
        p.setCostPrice(rs.getDouble("cost_price")); // Lấy giá vốn từ CSDL
        p.setImage(rs.getString("image"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setStock(rs.getInt("stock"));
        return p;
    }

    // Lấy tất cả sản phẩm
//    public List<Product> getAllProducts() {
//        List<Product> list = new ArrayList<>();
//        String query = "SELECT * FROM products ORDER BY id DESC";
//        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
//            while (rs.next()) {
//                list.add(mapResultSetToProduct(rs));
//            }
//        } catch (Exception e) {
//            System.err.println("ERROR getting all products: " + e.getMessage());
//            e.printStackTrace();
//        }
//        return list;
//    }

    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String query = """
        SELECT p.*, c.name AS category_name
        FROM products p
        LEFT JOIN categories c ON p.category_id = c.id
        ORDER BY p.id DESC
    """;

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setDescription(rs.getString("description"));
                p.setPrice(rs.getDouble("price"));
                p.setCostPrice(rs.getDouble("cost_price"));
                p.setImage(rs.getString("image"));
                p.setCategoryId(rs.getInt("category_id"));
                p.setStock(rs.getInt("stock"));
                // ✅ Gán tên danh mục
                p.setCategoryName(rs.getString("category_name"));

                list.add(p);
            }
        } catch (Exception e) {
            System.err.println("ERROR getting all products: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // Lấy sản phẩm theo ID
    public Product getProductById(int id) {
        String query = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduct(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR getting product by ID " + id + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // --- CRUD Operations (Đã sửa trả về boolean và thêm cost_price) ---
    /**
     * Thêm sản phẩm mới.
     *
     * @param p Đối tượng Product
     * @return true nếu thành công, false nếu lỗi.
     */
    public boolean addProduct(Product p) {
        String query = "INSERT INTO products(name, description, price, cost_price, image, category_id, stock) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setDouble(3, p.getPrice());
            ps.setDouble(4, p.getCostPrice()); // Set giá vốn
            ps.setString(5, p.getImage());    // Image có thể null
            ps.setInt(6, p.getCategoryId());
            ps.setInt(7, p.getStock());

            int rowsAffected = ps.executeUpdate();
            System.out.println("DEBUG (addProduct): Rows affected = " + rowsAffected);
            return rowsAffected > 0;

        } catch (Exception e) {
            System.err.println("ERROR adding product '" + p.getName() + "': " + e.getMessage());
            e.printStackTrace(); // In lỗi chi tiết
            return false;
        }
    }

    /**
     * Cập nhật sản phẩm.
     *
     * @param p Đối tượng Product
     * @return true nếu thành công, false nếu lỗi.
     */
    public boolean updateProduct(Product p) {
        String query = "UPDATE products SET name=?, description=?, price=?, cost_price=?, image=?, category_id=?, stock=? WHERE id=?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setDouble(3, p.getPrice());
            ps.setDouble(4, p.getCostPrice()); // Set giá vốn
            ps.setString(5, p.getImage());
            ps.setInt(6, p.getCategoryId());
            ps.setInt(7, p.getStock());
            ps.setInt(8, p.getId());

            int rowsAffected = ps.executeUpdate();
            System.out.println("DEBUG (updateProduct): Rows affected = " + rowsAffected);
            return rowsAffected > 0;

        } catch (Exception e) {
            System.err.println("ERROR updating product ID " + p.getId() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa sản phẩm.
     *
     * @param id ID sản phẩm
     * @return true nếu thành công, false nếu lỗi.
     */
    public boolean deleteProduct(int id) {
        String query = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            System.out.println("DEBUG (deleteProduct): Rows affected = " + rowsAffected);
            return rowsAffected > 0;

        } catch (Exception e) { // Bắt lỗi khóa ngoại nếu có
            System.err.println("ERROR deleting product ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Hàm updateStock (Giữ nguyên)
    public boolean updateStock(int productId, int quantityChange) {
        String sql = "UPDATE products SET stock = stock + ? WHERE id = ? AND stock + ? >= 0";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantityChange);
            ps.setInt(2, productId);
            ps.setInt(3, quantityChange);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("DEBUG (Stock Update): Updated stock for product ID " + productId + " by " + quantityChange);
                return true;
            } else {
                System.err.println("WARN: Failed to update stock for product ID " + productId + " by " + quantityChange + ". Maybe insufficient stock or invalid ID?");
                return false;
            }
        } catch (Exception e) {
            System.err.println("ERROR updating stock for product ID " + productId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // (Các hàm khác như getProductsByCategoryId, getTopProducts... nếu có thì giữ nguyên)
    public List<Product> getProductsByCategoryId(int categoryId) {
        List<Product> list = new ArrayList<>();
        String query;
        if (categoryId <= 1) {
            query = "SELECT * FROM products ORDER BY id DESC";
        } else {
            query = "SELECT * FROM products WHERE category_id = ? ORDER BY id DESC";
        }
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            if (categoryId > 1) {
                ps.setInt(1, categoryId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProduct(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Product> getProductsByPriceRange(int categoryId, double minPrice, double maxPrice) {
        List<Product> list = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM products WHERE 1=1 ");
        if (categoryId > 1) {
            queryBuilder.append(" AND category_id = ? ");
        }
        if (minPrice > 0) {
            queryBuilder.append(" AND price >= ? ");
        }
        if (maxPrice > 0) {
            queryBuilder.append(" AND price <= ? ");
        }
        queryBuilder.append(" ORDER BY id DESC");
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(queryBuilder.toString())) {
            int paramIndex = 1;
            if (categoryId > 1) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Hàm tính tổng giá trị tồn kho (cho thống kê)
    public double getTotalInventoryValue() {
        double totalValue = 0;
        // Tính tổng của (giá nhập * số lượng tồn) cho các sản phẩm còn hàng
        String sql = "SELECT SUM(cost_price * stock) AS total_value FROM products WHERE stock > 0";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                totalValue = rs.getDouble("total_value");
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tính tổng giá trị tồn kho (getTotalInventoryValue): " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("DEBUG (Statistics): Total Inventory Value = " + totalValue);
        return totalValue;
    }

}
