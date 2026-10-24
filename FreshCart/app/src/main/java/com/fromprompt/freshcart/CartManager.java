package com.fromprompt.freshcart;

import java.util.LinkedHashMap;
import java.util.Map;

/** In-memory cart shared across screens. All amounts are whole rupees. */
public final class CartManager {
    public static final int DELIVERY_FEE = 40;
    public static final int FREE_DELIVERY_ABOVE = 499;

    private static final CartManager INSTANCE = new CartManager();

    private final Map<String, Integer> items = new LinkedHashMap<>();
    private String coupon;

    private CartManager() {
    }

    public static CartManager get() {
        return INSTANCE;
    }

    public Map<String, Integer> items() {
        return items;
    }

    public int quantityOf(String productId) {
        Integer q = items.get(productId);
        return q == null ? 0 : q;
    }

    /** Adds qty of a product, capped at available stock. Returns the quantity actually added. */
    public int add(Product product, int qty) {
        int current = quantityOf(product.id);
        int allowed = Math.min(qty, product.stock - current);
        if (allowed <= 0) return 0;
        items.put(product.id, current + allowed);
        return allowed;
    }

    public void setQuantity(Product product, int qty) {
        if (qty <= 0) {
            items.remove(product.id);
        } else {
            int capped = Math.min(qty, product.stock);
            items.put(product.id, capped);
        }
    }

    public void remove(String productId) {
        items.remove(productId);
    }

    public int itemCount() {
        int count = 0;
        for (Integer q : items.values()) {
            if (q != null) count += q;
        }
        return count;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int subtotal() {
        int total = 0;
        for (Map.Entry<String, Integer> e : items.entrySet()) {
            Product p = Catalog.byId(e.getKey());
            if (p != null && e.getValue() > 0) total += p.price * e.getValue();
        }
        return total;
    }

    public String coupon() {
        return coupon;
    }

    /** Returns null if applied, otherwise an error message. */
    public String applyCoupon(String code) {
        String c = code == null ? "" : code.trim().toUpperCase();
        if (c.isEmpty()) return "Enter a coupon code";
        if (c.equals("FRESH10")) {
            coupon = c;
            return null;
        }
        if (c.equals("SAVE50")) {
            if (subtotal() < 300) return "SAVE50 needs a minimum order of ₹300";
            coupon = c;
            return null;
        }
        return "Invalid coupon code";
    }

    public void clearCoupon() {
        coupon = null;
    }

    public int discount() {
        if (coupon == null) return 0;
        int sub = subtotal();
        if (coupon.equals("FRESH10")) return Math.min(sub / 10, 100);
        if (coupon.equals("SAVE50")) return sub >= 300 ? 50 : 0;
        return 0;
    }

    public int deliveryFee() {
        if (items.isEmpty()) return 0;
        return subtotal() >= FREE_DELIVERY_ABOVE ? 0 : DELIVERY_FEE;
    }

    public int total() {
        return Math.max(0, subtotal() - discount() + deliveryFee());
    }

    public void clear() {
        items.clear();
        coupon = null;
    }

    public static String rupees(int amount) {
        return "₹" + amount;
    }
}
