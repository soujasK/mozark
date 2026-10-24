package com.fromprompt.freshcart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;

public class WishlistActivity extends AppCompatActivity {
    private Store store;
    private LinearLayout list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        store = new Store(this);
        list = findViewById(R.id.list_wishlist);
    }

    @Override
    protected void onResume() {
        super.onResume();
        render();
    }

    private void render() {
        JSONArray ids = store.wishlist();
        list.removeAllViews();
        findViewById(R.id.tv_empty).setVisibility(ids.length() == 0 ? View.VISIBLE : View.GONE);

        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < ids.length(); i++) {
            Product p = Catalog.byId(ids.optString(i));
            if (p == null) continue;

            View row = inflater.inflate(R.layout.item_wishlist, list, false);
            ((TextView) row.findViewById(R.id.tv_name)).setText(p.name);
            ((TextView) row.findViewById(R.id.tv_price)).setText(CartManager.rupees(p.price) + " · " + p.unit);
            View btnRemove = row.findViewById(R.id.btn_remove_wish);
            View btnMove = row.findViewById(R.id.btn_move_to_cart);
            btnRemove.setContentDescription("Remove " + p.name + " from wishlist");
            btnMove.setContentDescription("Move " + p.name + " to cart");

            btnRemove.setOnClickListener(v -> {
                store.removeFromWishlist(p.id);
                render();
            });
            btnMove.setOnClickListener(v -> {
                store.removeFromWishlist(p.id);
                CartManager.get().add(p, 1);
                Toast.makeText(this, p.name + " moved to cart", Toast.LENGTH_SHORT).show();
                render();
            });
            list.addView(row);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
