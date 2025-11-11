package com.watchstore.model; // Hoặc package của bạn

import java.util.ArrayList;
import java.util.List;

public class Cart {

    // --- Các trường liên quan CSDL (nếu bạn lưu giỏ hàng vào DB) ---
    private int id;
    private int userId;
    // --- Kết thúc trường CSDL ---

    private List<Item> items; // Danh sách các sản phẩm trong giỏ

    // Constructor cho giỏ hàng trong session
    public Cart() {
        this.items = new ArrayList<>();
    }

    // Constructor khi tải từ CSDL (nếu có)
    public Cart(int id, int userId) {
        this.id = id;
        this.userId = userId;
        this.items = new ArrayList<>();
    }

    /**
     * Tìm một item trong giỏ hàng dựa vào ID sản phẩm.
     *
     * @param productId ID của sản phẩm cần tìm.
     * @return Item nếu tìm thấy, ngược lại trả về null.
     */
    public Item getItemById(int productId) {
        // ⭐ THÊM KIỂM TRA NULL CHO items ⭐
        if (items == null) {
            System.err.println("WARN (Cart): getItemById called when items list is null!"); // Ghi log cảnh báo
            return null; // Trả về null nếu danh sách không tồn tại
        }

        // Vòng lặp chỉ chạy nếu items không null
        for (Item item : items) {
            if (item.getProduct() != null && item.getProduct().getId() == productId) {
                return item;
            }
        }
        return null; // Không tìm thấy
    }

    /**
     * Thêm một Item mới vào giỏ hàng. Nếu sản phẩm đã tồn tại, tăng số lượng
     * (kiểm tra tồn kho). Nếu là sản phẩm mới, thêm vào danh sách (kiểm tra tồn
     * kho).
     *
     * @param newItem Item cần thêm.
     */
    public void addItem(Item newItem) {
        if (newItem == null || newItem.getProduct() == null) {
            System.err.println("WARN (Cart): Attempted to add null item or item with null product.");
            return; // Không thêm item không hợp lệ
        }

        Item existingItem = getItemById(newItem.getProduct().getId());
        Product product = newItem.getProduct(); // Lấy thông tin sản phẩm

        if (existingItem != null) {
            // Sản phẩm đã có, kiểm tra xem cộng thêm có vượt tồn kho không
            int newQuantity = existingItem.getQuantity() + newItem.getQuantity();
            if (newQuantity <= product.getStock()) {
                existingItem.setQuantity(newQuantity);
                System.out.println("DEBUG (Cart): Updated quantity for product ID " + product.getId() + " to " + newQuantity);
            } else {
                // Nếu vượt quá, chỉ đặt số lượng bằng tồn kho tối đa
                existingItem.setQuantity(product.getStock());
                System.err.println("WARN (Cart): Cannot add more quantity for product ID " + product.getId() + ". Reached stock limit (" + product.getStock() + "). Set quantity to max.");
                // Cần có cơ chế báo lỗi này cho người dùng (ví dụ qua session attribute)
            }
        } else {
            // Sản phẩm mới, kiểm tra số lượng ban đầu có nhỏ hơn hoặc bằng tồn kho không
            if (newItem.getQuantity() <= product.getStock()) {
                items.add(newItem);
                System.out.println("DEBUG (Cart): Added new product ID " + product.getId() + " with quantity " + newItem.getQuantity());
            } else {
                System.err.println("WARN (Cart): Cannot add product ID " + product.getId() + " initially. Requested quantity (" + newItem.getQuantity() + ") exceeds stock (" + product.getStock() + "). Item not added.");
                // Không thêm sản phẩm nếu số lượng yêu cầu ban đầu đã vượt tồn kho
                // Cần có cơ chế báo lỗi này cho người dùng
            }
        }
    }

    /**
     * Xóa một Item khỏi giỏ hàng dựa vào ID sản phẩm.
     *
     * @param productId ID sản phẩm cần xóa.
     */
    public void removeItem(int productId) {
        Item itemToRemove = getItemById(productId);
        if (itemToRemove != null) {
            items.remove(itemToRemove);
            System.out.println("DEBUG (Cart): Removed product ID " + productId);
        } else {
            System.err.println("WARN (Cart): Attempted to remove non-existent product ID " + productId);
        }
    }

    /**
     * Tính tổng tiền của giỏ hàng.
     *
     * @return Tổng tiền (double).
     */
    public double getTotalMoney() {
        double total = 0;
        if (items == null) {
            return 0; // An toàn nếu items là null
        }
        for (Item item : items) {
            total += item.getTotalPrice(); // Dùng hàm tính tiền của từng Item
        }
        return total;
    }

    // --- Getters và Setters ---
    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
