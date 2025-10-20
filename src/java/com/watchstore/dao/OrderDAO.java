package com.watchstore.dao;

import com.watchstore.context.DBContext;
import com.watchstore.model.Cart;
import com.watchstore.model.Item;
import com.watchstore.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OrderDAO {

    /**
     * Thêm một đơn hàng mới vào CSDL, bao gồm cả thông tin đơn hàng và chi tiết các sản phẩm.
     * Sử dụng transaction để đảm bảo cả hai bảng orders và order_details được cập nhật đồng bộ.
     * @param u Người dùng đặt hàng
     * @param cart Giỏ hàng chứa các sản phẩm
     */
    public void addOrder(User u, Cart cart) {
        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            // Tắt chế độ tự động commit để bắt đầu transaction
            conn.setAutoCommit(false);

            // 1. Thêm bản ghi vào bảng 'orders'
            String orderSql = "INSERT INTO orders (user_id, total_money, customer_name, customer_address, customer_phone) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement psOrder = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                
                psOrder.setInt(1, u.getId());
                psOrder.setDouble(2, cart.getTotalMoney());
                psOrder.setString(3, u.getFullname());
                psOrder.setString(4, u.getAddress());
                psOrder.setString(5, u.getPhone());
                psOrder.executeUpdate();
                
                // Lấy order_id vừa được tạo ra
                try (ResultSet generatedKeys = psOrder.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int orderId = generatedKeys.getInt(1);
                        
                        // 2. Thêm các bản ghi vào bảng 'order_details'
                        String detailSql = "INSERT INTO order_details (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement psDetail = conn.prepareStatement(detailSql)) {
                            for (Item item : cart.getItems()) {
                                psDetail.setInt(1, orderId);
                                psDetail.setInt(2, item.getProduct().getId());
                                psDetail.setInt(3, item.getQuantity());
                                psDetail.setDouble(4, item.getProduct().getPrice());
                                psDetail.addBatch(); // Thêm vào lô xử lý
                            }
                            psDetail.executeBatch(); // Thực thi tất cả các lệnh trong lô
                        }
                    }
                }
            }
            // Nếu mọi thứ thành công, commit transaction
            conn.commit();
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
            // Đóng kết nối
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}