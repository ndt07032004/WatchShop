package com.watchstore.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<Item> items;

    public Cart() {
        items = new ArrayList<>();
    }

    // Lấy một item trong giỏ hàng bằng id sản phẩm
    public Item getItemById(int id) {
        for (Item i : items) {
            if (i.getProduct().getId() == id) {
                return i;
            }
        }
        return null;
    }

    // Thêm một item vào giỏ. Nếu đã có thì tăng số lượng, nếu chưa có thì thêm mới.
    public void addItem(Item newItem) {
        Item existingItem = getItemById(newItem.getProduct().getId());
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + newItem.getQuantity());
        } else {
            items.add(newItem);
        }
    }

    // Xóa một item khỏi giỏ
    public void removeItem(int id) {
        Item itemToRemove = getItemById(id);
        if (itemToRemove != null) {
            items.remove(itemToRemove);
        }
    }

    // Tính tổng tiền của tất cả các item trong giỏ
    public double getTotalMoney() {
        double total = 0;
        for (Item i : items) {
            total += i.getTotalPrice();
        }
        return total;
    }
    
    // Getter
    public List<Item> getItems() {
        return items;
    }
}