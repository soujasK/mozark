package com.fromprompt.freshcart;

public class Product {
    public final String id;
    public final String name;
    public final String category;
    public final String unit;
    public final int price; // in rupees
    public final int stock;
    public final String description;
    public final String highlights; // comma separated

    public Product(String id, String name, String category, String unit, int price, int stock, String description, String highlights) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.unit = unit;
        this.price = price;
        this.stock = stock;
        this.description = description;
        this.highlights = highlights;
    }
}
