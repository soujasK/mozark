package com.fromprompt.freshcart;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrdersActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Store store = new Store(this);
        String email = store.sessionEmail();
        JSONArray orders = store.orders(email);
        LinearLayout list = findViewById(R.id.list_orders);
        list.removeAllViews();
        findViewById(R.id.tv_empty).setVisibility(orders.length() == 0 ? View.VISIBLE : View.GONE);

        SimpleDateFormat fmt = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        LayoutInflater inflater = LayoutInflater.from(this);
        // Newest first
        for (int i = orders.length() - 1; i >= 0; i--) {
            JSONObject o = orders.optJSONObject(i);
            if (o == null) continue;

            View row = inflater.inflate(R.layout.item_order, list, false);
            ((TextView) row.findViewById(R.id.tv_order_id)).setText(
                    "Order " + o.optString("id") + " · " + o.optString("status"));
            long placedAt = o.optLong("placedAt");
            if (placedAt < 10000000000L) placedAt *= 1000;
            ((TextView) row.findViewById(R.id.tv_order_date)).setText(fmt.format(new Date(placedAt)));

            JSONArray items = o.optJSONArray("items");
            StringBuilder sb = new StringBuilder();
            if (items != null) {
                for (int j = 0; j < items.length(); j++) {
                    if (j > 0) sb.append(", ");
                    sb.append(items.optString(j));
                }
            }
            ((TextView) row.findViewById(R.id.tv_order_items)).setText(sb.toString());
            ((TextView) row.findViewById(R.id.tv_order_total)).setText(
                    "Total: " + CartManager.rupees(o.optInt("total")) + " · " + o.optString("payment"));

            final int orderIndex = i;
            row.setOnClickListener(v -> {
                Intent intent = new Intent(this, OrderDetailActivity.class);
                intent.putExtra(OrderDetailActivity.EXTRA_INDEX, orderIndex);
                startActivity(intent);
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
