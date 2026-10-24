package com.fromprompt.freshcart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProductDetailActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "product_id";

    private Product product;
    private Store store;
    private int qty = 1;
    private TextView tvQty;
    private Button btnWishlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        store = new Store(this);
        product = Catalog.byId(getIntent().getStringExtra(EXTRA_ID));
        if (product == null) {
            finish();
            return;
        }
        setTitle(product.name);

        ((TextView) findViewById(R.id.tv_name)).setText(product.name);
        ((TextView) findViewById(R.id.tv_category)).setText(product.category + " · " + product.unit);
        ((TextView) findViewById(R.id.tv_price)).setText(displayPrice(product));
        ((TextView) findViewById(R.id.tv_stock)).setText(product.stock > 0 ? "In stock: " + product.stock : "Out of stock");
        ((TextView) findViewById(R.id.tv_description)).setText(product.description);

        StringBuilder highlights = new StringBuilder();
        if (product.highlights != null && !product.highlights.isEmpty()) {
            for (String h : product.highlights.split(",")) {
                highlights.append("✓ ").append(h.trim()).append("\n");
            }
        }
        ((TextView) findViewById(R.id.tv_highlights)).setText(highlights.toString().trim());

        tvQty = findViewById(R.id.tv_qty);
        findViewById(R.id.btn_minus).setOnClickListener(v -> {
            if (qty > 1) qty--;
            tvQty.setText(String.valueOf(qty));
        });
        findViewById(R.id.btn_plus).setOnClickListener(v -> {
            if (qty < product.stock) qty++;
            tvQty.setText(String.valueOf(qty));
        });

        btnWishlist = findViewById(R.id.btn_wishlist);
        updateWishlistButton();
        btnWishlist.setOnClickListener(v -> {
            if (store.inWishlist(product.id)) {
                store.removeFromWishlist(product.id);
                Toast.makeText(this, "Removed from wishlist", Toast.LENGTH_SHORT).show();
            } else {
                store.addToWishlist(product.id);
                Toast.makeText(this, "Saved to wishlist", Toast.LENGTH_SHORT).show();
            }
            updateWishlistButton();
        });

        Button btnAdd = findViewById(R.id.btn_add_to_cart);
        btnAdd.setEnabled(product.stock > 0);
        btnAdd.setOnClickListener(v -> {
            int added = CartManager.get().add(product, qty);
            if (added == 0) {
                Toast.makeText(this, "You already have the maximum quantity in your cart", Toast.LENGTH_SHORT).show();
            } else if (added < qty) {
                Toast.makeText(this, "Only " + added + " more could be added (stock limit)", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Added " + added + " × " + product.name + " to cart", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btn_go_cart).setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
    }

    private String displayPrice(Product p) {
        return CartManager.rupees(p.price);
    }

    private void updateWishlistButton() {
        btnWishlist.setText(store.inWishlist(product.id) ? "♥ Saved to wishlist" : "♡ Save to wishlist");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
