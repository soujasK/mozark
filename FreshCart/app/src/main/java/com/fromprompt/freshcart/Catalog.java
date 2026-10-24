package com.fromprompt.freshcart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Catalog {
    public static final String ALL = "All";
    public static final String[] CATEGORIES = {ALL, "Fruits", "Vegetables", "Dairy", "Bakery"};
    public static final String[] SORT_MODES = {"Relevance", "Price: Low to High", "Price: High to Low", "Name"};

    private static final List<Product> PRODUCTS = Arrays.asList(
            new Product("p1", "Banana", "Fruits", "1 dozen", 60, 20, "Ripe Robusta bananas, sourced fresh every morning.", "Rich in potassium,Naturally sweet"),
            new Product("p2", "Apple Shimla", "Fruits", "1 kg", 180, 12, "Crisp and juicy Shimla apples.", "Hand picked,Crunchy"),
            new Product("p3", "Alphonso Mango", "Fruits", "1 kg", 450, 5, "Premium Ratnagiri Alphonso mangoes.", "GI tagged,Seasonal"),
            new Product("p4", "Pomegranate", "Fruits", "500 g", 120, 8, "Sweet ruby-red pomegranate.", "Antioxidant rich"),
            new Product("p5", "Tomato", "Vegetables", "1 kg", 40, 30, "Farm fresh red tomatoes.", "Farm fresh"),
            new Product("p6", "Onion", "Vegetables", "1 kg", 35, 40, "Nashik red onions.", "Long shelf life"),
            new Product("p7", "Potato", "Vegetables", "1 kg", 30, 40, "Clean, sorted potatoes.", "Sorted,Washed"),
            new Product("p8", "Spinach", "Vegetables", "250 g", 25, 3, "Tender palak leaves, washed and bundled.", "Tender,Washed,Iron rich"),
            new Product("p9", "Milk Toned", "Dairy", "1 L", 56, 25, "Pasteurised toned milk.", "Pasteurised,3% fat"),
            new Product("p10", "Paneer", "Dairy", "200 g", 90, 10, "Soft fresh malai paneer.", "High protein"),
            new Product("p11", "Curd", "Dairy", "400 g", 45, 15, "Thick set curd.", "Probiotic"),
            new Product("p12", "Butter", "Dairy", "100 g", 58, 0, "Salted table butter.", "Salted"),
            new Product("p13", "Whole Wheat Bread", "Bakery", "400 g", 50, 10, "Soft whole wheat sandwich bread.", "No maida"),
            new Product("p14", "Croissant", "Bakery", "2 pcs", 120, 6, "Buttery, flaky croissants baked daily.", "Baked daily"),
            new Product("p15", "Multigrain Cookies", "Bakery", "250 g", 95, 9, "Crunchy multigrain cookies with oats.", "With oats,No palm oil")
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

    public static Product byName(String name) {
        for (Product p : PRODUCTS) {
            if (p.name.equals(name)) return p;
        }
        return null;
    }

    public static List<Product> filter(String category, String query) {
        String q = query == null ? "" : query.trim().toLowerCase();
        List<Product> result = new ArrayList<>();
        for (Product p : PRODUCTS) {
            boolean categoryMatch = ALL.equals(category) || p.category.equalsIgnoreCase(category);
            boolean queryMatch = q.isEmpty() || p.name.toLowerCase().contains(q);
            if (categoryMatch && queryMatch) result.add(p);
        }
        return result;
    }

    public static void sort(List<Product> list, String mode) {
        if ("Price: Low to High".equals(mode)) {
            Collections.sort(list, (a, b) -> Integer.compare(a.price, b.price));
        } else if ("Price: High to Low".equals(mode)) {
            Collections.sort(list, (a, b) -> Integer.compare(b.price, a.price));
        } else if ("Name".equals(mode)) {
            Collections.sort(list, (a, b) -> a.name.compareToIgnoreCase(b.name));
        }
    }
}
