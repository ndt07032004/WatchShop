package com.watchstore.dao;

import com.watchstore.context.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * DashboardDAO - các API thống kê cho admin dashboard
 */
public class DashboardDAO {

    public double getYearlyRevenue() {
        double total = 0;
        String sql = "SELECT IFNULL(SUM(total_money),0) FROM orders WHERE YEAR(order_date) = YEAR(CURDATE())";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) total = rs.getDouble(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    public int getPendingOrders() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM orders WHERE status IS NULL OR status = 'PENDING' OR status = 'Chưa xử lý'";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) count = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public int getTotalOrders() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM orders";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) count = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

}