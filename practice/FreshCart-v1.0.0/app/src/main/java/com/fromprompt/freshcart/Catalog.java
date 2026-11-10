package com.fromprompt.freshcart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class Catalog {
    public static final String ALL = "All";
    public static final String[] CATEGORIES = {ALL, "Fruits", "Vegetables", "Dairy", "Bakery"};

    private static final List<Product> PRODUCTS = Arrays.asList(
            new Product("p1", "Banana", "Fruits", "1 dozen", 60, 20, "Ripe Robusta bananas, sourced fresh every morning."),
            new Product("p2", "Apple Shimla", "Fruits", "1 kg", 180, 12, "Crisp and juicy Shimla apples."),
            new Product("p3", "Alphonso Mango", "Fruits", "1 kg", 450, 5, "Premium Ratnagiri Alphonso mangoes."),
            new Product("p4", "Pomegranate", "Fruits", "500 g", 120, 8, "Sweet ruby-red pomegranate."),
            new Product("p5", "Tomato", "Vegetables", "1 kg", 40, 30, "Farm fresh red tomatoes."),
            new Product("p6", "Onion", "Vegetables", "1 kg", 35, 40, "Nashik red onions."),
            new Product("p7", "Potato", "Vegetables", "1 kg", 30, 40, "Clean, sorted potatoes."),
            new Product("p8", "Spinach", "Vegetables", "250 g", 25, 3, "Tender palak leaves, washed and bundled."),
            new Product("p9", "Milk Toned", "Dairy", "1 L", 56, 25, "Pasteurised toned milk."),
            new Product("p10", "Paneer", "Dairy", "200 g", 90, 10, "Soft fresh malai paneer."),
            new Product("p11", "Curd", "Dairy", "400 g", 45, 15, "Thick set curd."),
            new Product("p12", "Butter", "Dairy", "100 g", 58, 0, "Salted table butter."),
            new Product("p13", "Whole Wheat Bread", "Bakery", "400 g", 50, 10, "Soft whole wheat sandwich bread."),
            new Product("p14", "Croissant", "Bakery", "2 pcs", 120, 6, "Buttery, flaky croissants baked daily."),
            new Product("p15", "Multigrain Cookies", "Bakery", "250 g", 95, 9, "Crunchy multigrain cookies with oats.")
    );

    private Catalog() {
    }

    public static List<Product> all() {
        return PRODUCTS;
    }

    public static Product byId(String id) {
        for (Product p : PRODUCTS) {
            if (p.id.equals(id)) return p;
        }
        return null;
    }

    public static List<Product> filter(String category, String query) {
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        List<Product> result = new ArrayList<>();
        for (Product p : PRODUCTS) {
            boolean categoryMatch = ALL.equals(category) || p.category.equals(category);
            boolean queryMatch = q.isEmpty() || p.name.toLowerCase(Locale.ROOT).contains(q);
            if (categoryMatch && queryMatch) result.add(p);
        }
        return result;
    }
}
