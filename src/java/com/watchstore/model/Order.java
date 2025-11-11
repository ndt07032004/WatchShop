package com.watchstore.model;

import java.sql.Timestamp;

public class Order {
    private int id;
    private int userId;
    private Timestamp orderDate;
    private double totalMoney;
    private String customerName;
    private String customerAddress;
    private String customerPhone;
    private String paymentMethod;
    private String status;

    // ⭐ Thêm: Tóm tắt sản phẩm trong đơn (hiển thị hoặc xuất Excel)
    private String productSummary;

    // Thêm để chứa thông tin khách hàng khi cần
    private User customer;

    // Thêm để chứa danh sách chi tiết của đơn hàng
    private java.util.List<OrderDetail> details;

    public Order() {
        this.details = new java.util.ArrayList<>(); // Khởi tạo list
    }

    // --- Getters & Setters ---
    public java.util.List<OrderDetail> getDetails() { return details; }
    public void setDetails(java.util.List<OrderDetail> details) { this.details = details; }

    public User getCustomer() { return customer; }
    public void setCustomer(User customer) { this.customer = customer; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Timestamp getOrderDate() { return orderDate; }
    public void setOrderDate(Timestamp orderDate) { this.orderDate = orderDate; }

    public double getTotalMoney() { return totalMoney; }
    public void setTotalMoney(double totalMoney) { this.totalMoney = totalMoney; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerAddress() { return customerAddress; }
    public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getProductSummary() {
        return productSummary != null ? productSummary : "";
    }
    public void setProductSummary(String productSummary) {
        this.productSummary = productSummary;
    }
}
