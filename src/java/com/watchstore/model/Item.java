package com.watchstore.model; // Hoặc package của bạn

public class Item {
    private Product product;
    private int quantity;

    public Item() {
    }

    public Item(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    // Tính tổng tiền dựa trên giá HIỆN TẠI của sản phẩm
    public double getTotalPrice() {
        // ⭐ THÊM KIỂM TRA NULL CHO product ⭐
        if (product != null) {
            return product.getPrice() * quantity; // Chỉ tính nếu product tồn tại
        }
        // Trả về 0 nếu product không tồn tại để tránh lỗi
        System.err.println("WARN (Item): Attempted to getTotalPrice on an Item with null product!"); // Ghi log cảnh báo
        return 0;
    }

    // Getters and Setters (Giữ nguyên)
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}