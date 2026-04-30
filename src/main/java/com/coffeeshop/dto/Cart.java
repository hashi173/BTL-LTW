package com.coffeeshop.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class Cart {
    private List<CartItem> items = new ArrayList<>();

    public void addItem(CartItem newItem) {
        // Merge only when the cart line is identical, including custom options.
        for (CartItem item : items) {
            if (isSameItem(item, newItem)) {
                item.setQuantity(item.getQuantity() + newItem.getQuantity());
                return;
            }
        }
        items.add(newItem);
    }

    public void updateQuantity(int index, int quantity) {
        if (index >= 0 && index < items.size()) {
            if (quantity <= 0) {
                items.remove(index);
            } else {
                items.get(index).setQuantity(quantity);
            }
        }
    }

    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }

    public Double getTotalAmount() {
        return items.stream().mapToDouble(CartItem::getTotal).sum();
    }

    public int getTotalItems() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    private boolean isSameItem(CartItem item1, CartItem item2) {
        boolean sameProduct = Objects.equals(item1.getProductId(), item2.getProductId());
        boolean sameSize = Objects.equals(item1.getSizeId(), item2.getSizeId());
        boolean sameToppings = Objects.equals(item1.getToppingIds(), item2.getToppingIds());
        boolean sameAttributes = Objects.equals(item1.getAttributes(), item2.getAttributes());
        boolean sameNote = Objects.equals(item1.getNote(), item2.getNote());

        return sameProduct && sameSize && sameToppings && sameAttributes && sameNote;
    }
}
