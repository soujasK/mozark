package com.fromprompt.freshcart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class OrderDetailActivity extends AppCompatActivity {
    public static final String EXTRA_INDEX = "order_index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Store store = new Store(this);
        String email = store.sessionEmail();
        int index = getIntent().getIntExtra(EXTRA_INDEX, -1);
        JSONObject order = store.orders(email).optJSONObject(index);
        if (order == null) {
            finish();
            return;
        }

        TextView tvStatus = findViewById(R.id.tv_order_status);
        ((TextView) findViewById(R.id.tv_order_id)).setText("Order " + order.optString("id"));
        tvStatus.setText("Status: " + order.optString("status"));

        JSONArray items = order.optJSONArray("items");
        StringBuilder sb = new StringBuilder();
        if (items != null) {
            for (int i = 0; i < items.length(); i++) {
                sb.append("• ").append(items.optString(i)).append("\n");
            }
        }
        ((TextView) findViewById(R.id.tv_order_items)).setText(sb.toString().trim());
        ((TextView) findViewById(R.id.tv_order_address)).setText("Deliver to: " + order.optString("address")
                + "\nSlot: " + order.optString("slot"));
        ((TextView) findViewById(R.id.tv_order_payment)).setText("Payment: " + order.optString("payment"));
        ((TextView) findViewById(R.id.tv_order_total)).setText("Total: " + CartManager.rupees(order.optInt("total")));

        View btnCancel = findViewById(R.id.btn_cancel_order);
        btnCancel.setVisibility("Placed".equals(order.optString("status")) ? View.VISIBLE : View.GONE);
        btnCancel.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Cancel this order?")
                .setPositiveButton("Yes, cancel", (d, w) -> {
                    store.cancelOrder(email, index);
                    tvStatus.setText("Status: Cancelled");
                    btnCancel.setVisibility(View.GONE);
                    Toast.makeText(this, "Order cancelled", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show());

        findViewById(R.id.btn_reorder).setOnClickListener(v -> {
            if (items != null) {
                for (int i = 0; i < items.length(); i++) {
                    String line = items.optString(i);
                    String[] parts = line.split(" × ");
                    Product p = Catalog.byName(parts[0]);
                    int q = 1;
                    if (parts.length > 1) {
                        try {
                            q = Integer.parseInt(parts[1].trim());
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    if (p != null) CartManager.get().add(p, q);
                }
            }
            Toast.makeText(this, "Items added to cart", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, CartActivity.class));
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
