package com.watchstore.dao; // Giữ nguyên package của bạn

import com.watchstore.context.DBContext;
import com.watchstore.model.Cart;
import com.watchstore.model.Item;
import com.watchstore.model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {

    /**
     * Lấy giỏ hàng CSDL của user. Nếu chưa có, tự động tạo mới.
     * Bao gồm cả việc tải các item vào đối tượng Cart.
     */
    public Cart getCartByUserId(int userId) {
        String sqlGet = "SELECT id FROM carts WHERE user_id = ?";
        String sqlCreate = "INSERT INTO carts (user_id) VALUES (?)";
        Cart cart = null;
        Connection conn = null;
        PreparedStatement psGet = null;
        PreparedStatement psCreate = null;
        ResultSet rs = null;
        int cartId = -1;

        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false); // Quản lý transaction

            // 1. Thử lấy ID giỏ hàng đã có
            psGet = conn.prepareStatement(sqlGet);
            psGet.setInt(1, userId);
            rs = psGet.executeQuery();

            if (rs.next()) {
                cartId = rs.getInt("id");
                System.out.println("DEBUG (CartDAO): Found existing cart ID: " + cartId + " for user ID: " + userId);
            } else {
                // 2. Nếu chưa có, tạo giỏ hàng mới
                psCreate = conn.prepareStatement(sqlCreate, Statement.RETURN_GENERATED_KEYS);
                psCreate.setInt(1, userId);
                psCreate.executeUpdate();
                rs = psCreate.getGeneratedKeys();
                if (rs.next()) {
                    cartId = rs.getInt(1);
                    System.out.println("DEBUG (CartDAO): Created new cart ID: " + cartId + " for user ID: " + userId);
                } else {
                     System.err.println("ERROR (CartDAO): Failed to create new cart or retrieve its ID for user ID: " + userId);
                     throw new SQLException("Could not create cart."); // Ném lỗi nếu không tạo được
                }
            }
            conn.commit(); // Commit sau khi lấy hoặc tạo cart ID

            // 3. Nếu có cartId hợp lệ, tạo đối tượng Cart và tải items
            if (cartId > 0) {
                cart = new Cart(cartId, userId); // Dùng constructor mới
                loadCartItems(conn, cart); // Tải các item vào cart
            }

        } catch (Exception e) {
             System.err.println("ERROR (CartDAO - getCartByUserId) for user ID " + userId + ": " + e.getMessage());
             e.printStackTrace();
             try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } // Rollback nếu lỗi
        } finally {
             // Đóng resources
             try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
             try { if (psGet != null) psGet.close(); } catch (SQLException e) { e.printStackTrace(); }
             try { if (psCreate != null) psCreate.close(); } catch (SQLException e) { e.printStackTrace(); }
             try { if (conn != null) conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return cart; // Trả về cart (có thể null nếu lỗi)
    }


    /**
     * Tải các item từ bảng cart_items vào đối tượng Cart đã có.
     * Cần JOIN với products để lấy thông tin sản phẩm.
     */
    private void loadCartItems(Connection conn, Cart cart) throws SQLException {
        if (cart == null || conn == null) return; // Thêm kiểm tra conn
        List<Item> items = new ArrayList<>();
        // ⭐ BỎ `ci.price` KHỎI SELECT, LẤY `p.price` ĐỂ TẠO Product ⭐
        String sql = "SELECT ci.product_id, ci.quantity, " +
                     "p.name, p.description, p.image, p.category_id, p.stock, p.cost_price, p.price as product_price " + // Lấy p.price
                     "FROM cart_items ci JOIN products p ON ci.product_id = p.id " +
                     "WHERE ci.cart_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cart.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Tạo đối tượng Product với giá hiện tại từ bảng products
                    Product product = new Product();
                    product.setId(rs.getInt("product_id"));
                    product.setName(rs.getString("name"));
                    product.setDescription(rs.getString("description"));
                    product.setPrice(rs.getDouble("product_price")); // ⭐ Lấy giá từ products
                    product.setCostPrice(rs.getDouble("cost_price"));
                    product.setImage(rs.getString("image"));
                    product.setCategoryId(rs.getInt("category_id"));
                    product.setStock(rs.getInt("stock"));
                    // Lấy category name nếu cần (cần join thêm categories)
                    // product.setCategoryName(rs.getString("category_name"));

                    // ⭐ Tạo Item bằng constructor mới (chỉ product và quantity) ⭐
                    Item item = new Item(product, rs.getInt("quantity"));
                    items.add(item);
                }
            }
        }
        cart.setItems(items);
        System.out.println("DEBUG (CartDAO): Loaded " + items.size() + " items for cart ID: " + cart.getId());
    }


    /**
     * Thêm hoặc cập nhật item trong CSDL (bảng cart_items).
     * Không còn lưu giá.
     */
    // ⭐ SỬA HÀM NÀY ⭐
    public void addItem(int cartId, int productId, int quantity) { // <<< Bỏ tham số price
        String checkSql = "SELECT quantity FROM cart_items WHERE cart_id = ? AND product_id = ?";
        // ⭐ BỎ cột `price` khỏi INSERT và UPDATE ⭐
        String insertSql = "INSERT INTO cart_items (cart_id, product_id, quantity) VALUES (?, ?, ?)";
        String updateSql = "UPDATE cart_items SET quantity = ? WHERE cart_id = ? AND product_id = ?";

        Connection conn = null;
        PreparedStatement psCheck = null;
        PreparedStatement psInsert = null;
        PreparedStatement psUpdate = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

             // Lấy tồn kho để kiểm tra (vẫn cần thiết)
             ProductDAO productDAO = new ProductDAO();
             Product product = productDAO.getProductById(productId);
             if (product == null) { // Bỏ kiểm tra stock <= 0 vì có thể muốn cập nhật quantity = 0
                  System.err.println("WARN (CartDAO - addItem DB): Product ID " + productId + " not found. Cannot add/update cart item.");
                  conn.rollback();
                  return;
             }
             int maxStock = product.getStock();

            psCheck = conn.prepareStatement(checkSql);
            psCheck.setInt(1, cartId);
            psCheck.setInt(2, productId);
            rs = psCheck.executeQuery();

            if (rs.next()) { // Đã tồn tại -> UPDATE
                int currentQuantity = rs.getInt("quantity");
                int newQuantity = quantity; // Ghi đè số lượng

                if (newQuantity > maxStock) { // Giới hạn bởi tồn kho
                     newQuantity = maxStock;
                     System.err.println("WARN (CartDAO DB - update): Quantity for product ID " + productId + " exceeds stock. Setting to max: " + maxStock);
                }

                if (newQuantity <= 0) { // Nếu số lượng <= 0 thì xóa
                   removeItem(cartId, productId); // Gọi hàm xóa (không cần commit/rollback ở đây)
                   System.out.println("DEBUG (CartDAO DB - update): Quantity <= 0, removing item ID " + productId);
                } else if (newQuantity != currentQuantity) { // Chỉ update nếu số lượng thay đổi
                   psUpdate = conn.prepareStatement(updateSql);
                   psUpdate.setInt(1, newQuantity);
                   // psUpdate.setDouble(2, price); // <<< XÓA DÒNG NÀY
                   psUpdate.setInt(2, cartId);     // <<< SỬA INDEX
                   psUpdate.setInt(3, productId);    // <<< SỬA INDEX
                   psUpdate.executeUpdate();
                   System.out.println("DEBUG (CartDAO DB - update): Updated quantity for product ID " + productId + " to " + newQuantity);
                } else {
                     System.out.println("DEBUG (CartDAO DB - update): Quantity unchanged for product ID " + productId);
                }

            } else { // Chưa tồn tại -> INSERT
                int newQuantity = quantity;
                if (newQuantity > maxStock) { // Giới hạn bởi tồn kho
                     newQuantity = maxStock;
                     System.err.println("WARN (CartDAO DB - insert): Initial quantity for product ID " + productId + " exceeds stock. Setting to max: " + maxStock);
                }

                if (newQuantity > 0) { // Chỉ insert nếu số lượng > 0
                    psInsert = conn.prepareStatement(insertSql);
                    psInsert.setInt(1, cartId);
                    psInsert.setInt(2, productId);
                    psInsert.setInt(3, newQuantity);
                    // psInsert.setDouble(4, price); // <<< XÓA DÒNG NÀY
                    psInsert.executeUpdate();
                    System.out.println("DEBUG (CartDAO DB - insert): Inserted product ID " + productId + " with quantity " + newQuantity);
                } else {
                    System.err.println("WARN (CartDAO DB - insert): Initial quantity for product ID " + productId + " is <= 0. Item not inserted.");
                }
            }

            conn.commit(); // Commit transaction thành công

        } catch (Exception e) {
             System.err.println("ERROR (CartDAO - addItem DB) for cart ID " + cartId + ", product ID " + productId + ": " + e.getMessage());
             e.printStackTrace();
             try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        } finally {
            // Đóng resources...
             try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); } try { if (psCheck != null) psCheck.close(); } catch (SQLException e) { e.printStackTrace(); } try { if (psInsert != null) psInsert.close(); } catch (SQLException e) { e.printStackTrace(); } try { if (psUpdate != null) psUpdate.close(); } catch (SQLException e) { e.printStackTrace(); } try { if (conn != null) conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

  

    /**
     * Xóa một item khỏi giỏ hàng CSDL (bảng cart_items).
     */
    public void removeItem(int cartId, int productId) {
        String sql = "DELETE FROM cart_items WHERE cart_id = ? AND product_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cartId);
            ps.setInt(2, productId);
            int rowsAffected = ps.executeUpdate();
             if (rowsAffected > 0) {
                 System.out.println("DEBUG (CartDAO - removeItem): Removed product ID " + productId + " from cart ID " + cartId);
             } else {
                 System.err.println("WARN (CartDAO - removeItem): Product ID " + productId + " not found in cart ID " + cartId + " to remove.");
             }
        } catch (Exception e) {
            System.err.println("ERROR (CartDAO - removeItem) for cart ID " + cartId + ", product ID " + productId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Xóa SẠCH tất cả item khỏi giỏ hàng (bảng cart_items).
     * Dùng sau khi checkout thành công.
     */
    public void clearCart(int cartId) {
        String sql = "DELETE FROM cart_items WHERE cart_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cartId);
            int rowsAffected = ps.executeUpdate();
            System.out.println("DEBUG (CartDAO - clearCart): Cleared " + rowsAffected + " items from cart ID " + cartId);
        } catch (Exception e) {
            System.err.println("ERROR (CartDAO - clearCart) for cart ID " + cartId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gộp giỏ hàng session của khách vào giỏ hàng CSDL khi đăng nhập.
     */
    public void mergeSessionCart(int dbCartId, Cart sessionCart) {
        if (sessionCart == null || sessionCart.getItems() == null || sessionCart.getItems().isEmpty()) {
            System.out.println("DEBUG (CartDAO - merge): Session cart empty for cart ID " + dbCartId);
            return;
        }
        System.out.println("DEBUG (CartDAO - merge): Merging session cart into DB cart ID " + dbCartId);

        // Lấy giỏ hàng DB hiện tại để biết số lượng đang có
        Cart dbCart = new Cart(dbCartId, 0); // UserID tạm thời = 0
        Connection conn = null;
        try {
            conn = DBContext.getConnection(); // Mở connection 1 lần
             if (conn == null) {
                  System.err.println("ERROR (CartDAO - merge): Cannot get DB connection.");
                  return;
             }
            loadCartItems(conn, dbCart); // Tải items hiện có trong DB vào dbCart

            // Duyệt qua từng item trong session cart
            for (Item sessionItem : sessionCart.getItems()) {
                if (sessionItem.getProduct() != null) {
                    int productId = sessionItem.getProduct().getId();
                    int sessionQuantity = sessionItem.getQuantity();
                    int finalQuantity = sessionQuantity; // Số lượng cuối cùng sẽ lưu vào DB

                    // Tìm xem item này đã có trong giỏ hàng DB chưa
                    Item dbItem = dbCart.getItemById(productId);
                    if (dbItem != null) {
                        // Nếu đã có, cộng dồn số lượng
                        finalQuantity += dbItem.getQuantity();
                        System.out.println("DEBUG (CartDAO - merge): Product ID " + productId + " exists in DB cart. Merged quantity: " + dbItem.getQuantity() + " + " + sessionQuantity + " = " + finalQuantity);
                    } else {
                        System.out.println("DEBUG (CartDAO - merge): Product ID " + productId + " is new to DB cart. Quantity: " + finalQuantity);
                    }

                    // Gọi hàm addItem (đã sửa, không có price) để INSERT hoặc UPDATE vào CSDL
                    // Hàm addItem sẽ tự kiểm tra tồn kho cuối cùng
                    addItem(dbCartId, productId, finalQuantity);
                }
            }
            System.out.println("DEBUG (CartDAO - merge): Merge completed for cart ID " + dbCartId);
        } catch(Exception e) {
             System.err.println("ERROR (CartDAO - merge): " + e.getMessage());
             e.printStackTrace();
             // Không rollback ở đây vì addItem đã tự quản lý transaction
        } finally {
             // Chỉ đóng connection mở ở hàm này
             try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}