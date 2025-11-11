package com.watchstore.dao; // Giữ nguyên package của bạn

import com.watchstore.context.DBContext;
import com.watchstore.model.Cart; // Đảm bảo model Cart đã được cập nhật
import com.watchstore.model.Item; // Đảm bảo tên Item.java khớp
import com.watchstore.model.Order; // Đảm bảo model Order đã được cập nhật
import com.watchstore.model.OrderDetail;
import com.watchstore.model.Product;
import com.watchstore.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public boolean addOrder(User u, Cart cart, String paymentMethod) {
        Connection conn = null;
        PreparedStatement psOrder = null;
        PreparedStatement psDetail = null;
        ResultSet rs = null;
        boolean check = false;

        // 1. Cập nhật SQL: Thêm cột payment_method và status
        String orderSql = "INSERT INTO orders (user_id, total_money, customer_name, customer_address, customer_phone, "
                + "payment_method, status, order_date) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, NOW())"; // Thêm 2 dấu ?

        String detailSql = "INSERT INTO order_details (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";

        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // 2. Thêm tham số cho PreparedStatement của 'orders'
            psOrder = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            psOrder.setInt(1, u.getId());
            psOrder.setDouble(2, cart.getTotalMoney());
            psOrder.setString(3, u.getFullname());    // Lấy từ User
            psOrder.setString(4, u.getAddress());    // Lấy từ User
            psOrder.setString(5, u.getPhone());      // Lấy từ User

            // --- THAM SỐ MỚI ---
            psOrder.setString(6, paymentMethod);         // 6. Phương thức thanh toán
            psOrder.setString(7, "Chưa thanh toán");     // 7. Trạng thái mặc định
            // --- KẾT THÚC ---

            int rowsAffected = psOrder.executeUpdate();

            if (rowsAffected > 0) {
                // Lấy ID của đơn hàng vừa tạo
                rs = psOrder.getGeneratedKeys();
                if (rs.next()) {
                    int orderId = rs.getInt(1);

                    // 3. Thêm vào 'order_details'
                    psDetail = conn.prepareStatement(detailSql);
                    for (Item item : cart.getItems()) { // Sử dụng model Item
                        psDetail.setInt(1, orderId);
                        psDetail.setInt(2, item.getProduct().getId());
                        psDetail.setInt(3, item.getQuantity());
                        psDetail.setDouble(4, item.getProduct().getPrice()); // Lấy giá tại thời điểm đặt hàng
                        psDetail.addBatch(); // Thêm vào lô xử lý
                    }
                    psDetail.executeBatch(); // Thực thi tất cả các lệnh trong lô

                    conn.commit(); // Hoàn tất transaction nếu không có lỗi
                    check = true; // Đánh dấu thành công
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Nếu có lỗi, rollback để hủy bỏ mọi thay đổi
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            // Đóng tất cả kết nối (ResultSet, PreparedStatement, Connection)
            try {
                if (rs != null) {
                    rs.close();
                }
                if (psOrder != null) {
                    psOrder.close();
                }
                if (psDetail != null) {
                    psDetail.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true); // Trả lại chế độ auto-commit
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return check; // Trả về true/false
    }

    /**
     * Lấy danh sách tất cả các đơn hàng của một người dùng.
     *
     * @param userId ID của người dùng
     * @return Danh sách các đối tượng Order
     */
    public List<Order> getOrdersByUserId(int userId) {
        List<Order> list = new ArrayList<>();
        // Lấy các cột mới đã thêm vào CSDL
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();
                    order.setId(rs.getInt("id"));
                    order.setUserId(rs.getInt("user_id"));
                    order.setCustomerName(rs.getString("customer_name"));
                    order.setCustomerAddress(rs.getString("customer_address"));
                    order.setCustomerPhone(rs.getString("customer_phone"));
                    order.setTotalMoney(rs.getDouble("total_money"));
                    order.setOrderDate(rs.getTimestamp("order_date"));

                    order.setPaymentMethod(rs.getString("payment_method"));
                    order.setStatus(rs.getString("status"));

                    list.add(order);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     *
     *
     * /**
     * Lấy danh sách chi tiết (các sản phẩm) của một đơn hàng cụ thể. ⭐ ĐÃ SỬA:
     * JOIN với bảng products để lấy tên, ảnh. ⭐
     *
     * @param orderId ID của đơn hàng cần xem chi tiết
     * @return Danh sách các đối tượng OrderDetail (đã có thông tin Product)
     */
    public List<OrderDetail> getOrderDetailsByOrderId(int orderId) {
        List<OrderDetail> details = new ArrayList<>();
        // ⭐ THÊM JOIN VÀ LẤY CÁC CỘT TỪ BẢNG products (p) ⭐
        String sql = "SELECT od.*, p.name as productName, p.image as productImage "
                + "FROM order_details od "
                + "JOIN products p ON od.product_id = p.id "
                + // <<< JOIN products p
                "WHERE od.order_id = ?";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderDetail detail = new OrderDetail();
                    detail.setId(rs.getInt("id"));
                    detail.setOrderId(rs.getInt("order_id"));
                    detail.setProductId(rs.getInt("product_id"));
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setPrice(rs.getDouble("price")); // Giá bán tại thời điểm mua

                    // ⭐ TẠO ĐỐI TƯỢNG Product VÀ LẤY DỮ LIỆU TỪ JOIN ⭐
                    Product productInfo = new Product();
                    productInfo.setId(rs.getInt("product_id")); // Lấy ID nếu cần
                    productInfo.setName(rs.getString("productName")); // Lấy tên sản phẩm
                    productInfo.setImage(rs.getString("productImage")); // <<< LẤY ĐƯỜNG DẪN ẢNH

                    // ⭐ GÁN ĐỐI TƯỢNG Product VÀO OrderDetail ⭐
                    detail.setProduct(productInfo); // <<< Rất quan trọng!

                    details.add(detail);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy chi tiết đơn hàng (getOrderDetailsByOrderId) ID " + orderId + ": " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("DEBUG: Lấy được " + details.size() + " chi tiết (kèm thông tin SP) cho đơn hàng ID " + orderId); // Log thêm
        return details;
    }

//    public List<Order> getAllOrders() {
//        List<Order> list = new ArrayList<>();
//        String sql = "SELECT * FROM orders ORDER BY id DESC";
//
//        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
//
//            while (rs.next()) {
//                Order order = new Order();
//                order.setId(rs.getInt("id"));
//                order.setUserId(rs.getInt("user_id"));
//                order.setCustomerName(rs.getString("customer_name"));
//                order.setCustomerAddress(rs.getString("customer_address"));
//                order.setCustomerPhone(rs.getString("customer_phone"));
//                order.setTotalMoney(rs.getDouble("total_money"));
//                order.setOrderDate(rs.getTimestamp("order_date"));
//                order.setPaymentMethod(rs.getString("payment_method"));
//                order.setStatus(rs.getString("status"));
//
//                list.add(order);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return list;
//    }

    /**
     * Cập nhật trạng thái của một đơn hàng.
     *
     * @param orderId ID của đơn hàng cần cập nhật
     * @param newStatus Trạng thái mới (vd: "Đang xử lý", "Đang giao hàng", ...)
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateOrderStatus(int orderId, String newStatus) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setInt(2, orderId);

            return ps.executeUpdate() > 0; // Trả về true nếu có dòng được cập nhật
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public double getTotalCostOfGoodsSold() {
        double totalCOGS = 0;
        // JOIN orders, order_details, products
        // Chỉ tính cho đơn hàng 'Đã giao thành công'
        // Tính tổng của (số lượng bán * giá nhập sản phẩm)
        String sql = "SELECT SUM(od.quantity * p.cost_price) AS total_cogs "
                + "FROM orders o "
                + "JOIN order_details od ON o.id = od.order_id "
                + "JOIN products p ON od.product_id = p.id "
                + "WHERE o.status = 'Đã giao thành công'";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                totalCOGS = rs.getDouble("total_cogs"); // Lấy tổng vốn
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tính tổng vốn hàng bán (getTotalCostOfGoodsSold): " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("DEBUG (Statistics): Total COGS = " + totalCOGS); // Log
        return totalCOGS;
    }

    /**
     * === HÀM MỚI (Cho Thống kê) === Tính tổng doanh thu từ các đơn hàng đã
     * giao thành công.
     *
     * @return Tổng doanh thu (double).
     */
    public double getTotalRevenue() {
        double totalRevenue = 0;
        // Calcule seulement la somme pour les commandes avec status = 'Đã giao thành công'
        String sql = "SELECT SUM(total_money) AS total FROM orders WHERE status = 'Đã giao thành công'";

        // Utilisation de try-with-resources
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                totalRevenue = rs.getDouble("total"); // Récupère la somme
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du calcul du revenu total (getTotalRevenue): " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("DEBUG (Statistics): Total Revenue = " + totalRevenue); // Log
        return totalRevenue;
    }

    /**
     * === HÀM MỚI (Cho Thống kê) === Đếm tổng số lượng đơn hàng theo trạng
     * thái.
     *
     * @param status Trạng thái cần đếm (vd: "Đang xử lý", null hoặc "" để đếm
     * tất cả).
     * @return Số lượng đơn hàng.
     */
    public int countOrdersByStatus(String status) {
        int count = 0;
        String sql;
        // Nếu status là null hoặc rỗng, đếm tất cả đơn hàng
        if (status == null || status.trim().isEmpty()) {
            sql = "SELECT COUNT(*) AS count FROM orders";
        } else {
            // Nếu có status cụ thể, đếm theo status đó
            sql = "SELECT COUNT(*) AS count FROM orders WHERE status = ?";
        }

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            // Chỉ set tham số nếu có status cụ thể
            if (status != null && !status.trim().isEmpty()) {
                ps.setString(1, status);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt("count"); // Lấy giá trị đếm từ cột 'count'
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi đếm đơn hàng theo trạng thái (countOrdersByStatus - status: " + status + "): " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("DEBUG (Statistics): Count Orders (Status: " + (status == null ? "ALL" : status) + ") = " + count); // Log
        return count;
    }

    /**
     * === HÀM MỚI (Cho Thống kê) === Lấy tổng số lượng khách hàng (user có
     * role=0). (Hàm này có thể đặt ở UserDAO sẽ hợp lý hơn, nhưng để tạm ở đây
     * cho tiện)
     *
     * @return Tổng số khách hàng.
     */
    public int countCustomers() {
        int count = 0;
        String sql = "SELECT COUNT(*) AS count FROM users WHERE role = 0"; // Đếm user có role=0

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt("count");
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi đếm khách hàng (countCustomers): " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("DEBUG (Statistics): Total Customers = " + count); // Log
        return count;
    }

    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY id DESC";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setUserId(rs.getInt("user_id"));
                order.setCustomerName(rs.getString("customer_name"));
                order.setCustomerAddress(rs.getString("customer_address"));
                order.setCustomerPhone(rs.getString("customer_phone"));
                order.setTotalMoney(rs.getDouble("total_money"));
                order.setOrderDate(rs.getTimestamp("order_date"));
                order.setPaymentMethod(rs.getString("payment_method"));
                order.setStatus(rs.getString("status"));

                // ⚡ Gọi hàm lấy danh sách sản phẩm và gộp thành chuỗi
                String summary = getProductSummaryByOrderId(order.getId());
                order.setProductSummary(summary);

                list.add(order);
            }

        } catch (Exception e) {
            System.err.println("Lỗi getAllOrders(): " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public String getProductSummaryByOrderId(int orderId) {
        StringBuilder summary = new StringBuilder();
        String sql = """
            SELECT p.name, od.quantity
            FROM order_details od
            JOIN products p ON od.product_id = p.id
            WHERE od.order_id = ?
        """;

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (summary.length() > 0) {
                        summary.append(", ");
                    }
                    summary.append(rs.getString("name"))
                            .append(" (x").append(rs.getInt("quantity")).append(")");
                }
            }

        } catch (Exception e) {
            System.err.println("Lỗi getProductSummaryByOrderId(" + orderId + "): " + e.getMessage());
        }

        return summary.toString();
    }

    /**
     * Lấy thông tin một đơn hàng và email của khách hàng.
     * @param orderId ID của đơn hàng
     * @return Đối tượng Order có chứa email và tên khách hàng
     */
    public Order getOrderWithCustomerEmail(int orderId) {
        Order order = null;
        String sql = """
            SELECT o.*, u.email AS customer_email, u.fullname AS customer_fullname
            FROM orders o
            JOIN users u ON o.user_id = u.id
            WHERE o.id = ?
        """;

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    order = new Order();
                    order.setId(rs.getInt("id"));
                    order.setUserId(rs.getInt("user_id"));
                    order.setOrderDate(rs.getTimestamp("order_date"));
                    order.setTotalMoney(rs.getDouble("total_money"));
                    order.setPaymentMethod(rs.getString("payment_method"));
                    order.setStatus(rs.getString("status"));

                    // Lấy thông tin khách hàng từ JOIN
                    User customer = new User();
                    customer.setEmail(rs.getString("customer_email"));
                    customer.setFullname(rs.getString("customer_fullname"));
                    order.setCustomer(customer); // Giả sử bạn có setter này trong model Order
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi getOrderWithCustomerEmail(" + orderId + "): " + e.getMessage());
            e.printStackTrace();
        }
        return order;
    }

    /**
     * [TỐI ƯU HÓA N+1] Lấy tất cả đơn hàng và chi tiết của chúng trong 1 truy vấn.
     * @return Danh sách Order, mỗi Order đã chứa sẵn List<OrderDetail>
     */
    public List<Order> getAllOrdersWithDetails() {
        List<Order> orderList = new ArrayList<>();
        // Sử dụng Map để xử lý N+1, key là Order ID
        java.util.Map<Integer, Order> orderMap = new java.util.LinkedHashMap<>();

        String sql = """
            SELECT
                o.id AS order_id, o.user_id, o.order_date, o.total_money, o.customer_name,
                o.customer_address, o.customer_phone, o.payment_method, o.status,
                od.id AS detail_id, od.quantity, od.price AS price_at_purchase,
                p.id AS product_id, p.name AS product_name, p.image AS product_image
            FROM orders o
            LEFT JOIN order_details od ON o.id = od.order_id
            LEFT JOIN products p ON od.product_id = p.id
            ORDER BY o.order_date DESC, o.id DESC, od.id ASC
        """;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                Order order = orderMap.get(orderId);

                // Nếu chưa có order này trong map, tạo mới và thêm vào
                if (order == null) {
                    order = new Order();
                    order.setId(orderId);
                    order.setUserId(rs.getInt("user_id"));
                    order.setOrderDate(rs.getTimestamp("order_date"));
                    order.setTotalMoney(rs.getDouble("total_money"));
                    order.setCustomerName(rs.getString("customer_name"));
                    order.setCustomerAddress(rs.getString("customer_address"));
                    order.setCustomerPhone(rs.getString("customer_phone"));
                    order.setPaymentMethod(rs.getString("payment_method"));
                    order.setStatus(rs.getString("status"));
                    orderMap.put(orderId, order);
                }

                // Nếu có chi tiết đơn hàng (detail_id không null), tạo và thêm vào order
                if (rs.getInt("detail_id") != 0) {
                    OrderDetail detail = new OrderDetail();
                    detail.setId(rs.getInt("detail_id"));
                    detail.setOrderId(orderId);
                    detail.setProductId(rs.getInt("product_id"));
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setPrice(rs.getDouble("price_at_purchase"));

                    Product product = new Product();
                    product.setId(rs.getInt("product_id"));
                    product.setName(rs.getString("product_name"));
                    product.setImage(rs.getString("product_image"));
                    detail.setProduct(product);

                    order.getDetails().add(detail);
                }
            }
            orderList.addAll(orderMap.values());

        } catch (Exception e) {
            System.err.println("Lỗi getAllOrdersWithDetails(): " + e.getMessage());
            e.printStackTrace();
        }
        return orderList;
    }

    // 🔹 Lấy chi tiết từng đơn (nếu cần dùng ở trang admin)
//    public List<OrderDetail> getOrderDetailsByOrderId(int orderId) {
//        List<OrderDetail> details = new ArrayList<>();
//        String sql = """
//            SELECT od.*, p.name AS productName, p.image AS productImage
//            FROM order_details od
//            JOIN products p ON od.product_id = p.id
//            WHERE od.order_id = ?
//        """;
//
//        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setInt(1, orderId);
//            try (ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) {
//                    OrderDetail detail = new OrderDetail();
//                    detail.setId(rs.getInt("id"));
//                    detail.setOrderId(rs.getInt("order_id"));
//                    detail.setProductId(rs.getInt("product_id"));
//                    detail.setQuantity(rs.getInt("quantity"));
//                    detail.setPrice(rs.getDouble("price"));
//
//                    Product p = new Product();
//                    p.setName(rs.getString("productName"));
//                    p.setImage(rs.getString("productImage"));
//                    detail.setProduct(p);
//
//                    details.add(detail);
//                }
//            }
//
//        } catch (Exception e) {
//            System.err.println("Lỗi getOrderDetailsByOrderId(): " + e.getMessage());
//            e.printStackTrace();
//        }
//
//        return details;
//    }
}
