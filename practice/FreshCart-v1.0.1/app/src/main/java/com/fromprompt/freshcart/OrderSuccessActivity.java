package com.fromprompt.freshcart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderSuccessActivity extends BaseActivity {
    public static final String EXTRA_ORDER = "order_json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        try {
            JSONObject order = new JSONObject(getIntent().getStringExtra(EXTRA_ORDER));
            ((TextView) findViewById(R.id.tv_order_id)).setText("Order ID: " + order.getString("id"));
            ((TextView) findViewById(R.id.tv_order_total)).setText("Total paid: " + CartManager.rupees(order.getInt("total")));
            ((TextView) findViewById(R.id.tv_order_slot)).setText("Delivery: " + order.getString("slot"));
        } catch (JSONException | NullPointerException e) {
            finish();
            return;
        }

        findViewById(R.id.btn_continue).setOnClickListener(v -> backToHome());
        findViewById(R.id.btn_view_orders).setOnClickListener(v -> {
            startActivity(new Intent(this, OrdersActivity.class));
            finish();
        });
    }

    private void backToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
