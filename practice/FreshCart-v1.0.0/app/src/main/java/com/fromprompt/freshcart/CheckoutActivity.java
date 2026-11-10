package com.fromprompt.freshcart;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {
    private final CartManager cart = CartManager.get();

    private EditText etName;
    private EditText etPhone;
    private EditText etAddress;
    private EditText etPincode;
    private EditText etUpi;
    private RadioGroup rgSlot;
    private RadioGroup rgPayment;
    private TextView tvError;
    private Button btnPlaceOrder;
    private View progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        etPincode = findViewById(R.id.et_pincode);
        etUpi = findViewById(R.id.et_upi);
        rgSlot = findViewById(R.id.rg_slot);
        rgPayment = findViewById(R.id.rg_payment);
        tvError = findViewById(R.id.tv_error);
        btnPlaceOrder = findViewById(R.id.btn_place_order);
        progress = findViewById(R.id.progress);

        Store store = new Store(this);
        etName.setText(store.nameOf(store.sessionEmail()));

        rgPayment.setOnCheckedChangeListener((group, checkedId) ->
                etUpi.setVisibility(checkedId == R.id.rb_upi ? View.VISIBLE : View.GONE));

        ((TextView) findViewById(R.id.tv_amount)).setText("Amount payable: " + CartManager.rupees(cart.total()));
        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void placeOrder() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String pincode = etPincode.getText().toString().trim();
        String upi = etUpi.getText().toString().trim();
        boolean isUpi = rgPayment.getCheckedRadioButtonId() == R.id.rb_upi;

        String error = null;
        if (name.isEmpty()) {
            error = "Please enter your name";
        } else if (!phone.matches("[6-9][0-9]{9}")) {
            error = "Enter a valid 10-digit mobile number";
        } else if (address.length() < 10) {
            error = "Please enter your full address";
        } else if (!pincode.matches("[1-9][0-9]{5}")) {
            error = "Enter a valid 6-digit pincode";
        } else if (rgSlot.getCheckedRadioButtonId() == -1) {
            error = "Please choose a delivery slot";
        } else if (isUpi && !upi.matches("[A-Za-z0-9._-]+@[A-Za-z]+")) {
            error = "Enter a valid UPI ID";
        }

        if (error != null) {
            tvError.setText(error);
            tvError.setVisibility(View.VISIBLE);
            return;
        }
        tvError.setVisibility(View.GONE);

        String slot = ((RadioButton) findViewById(rgSlot.getCheckedRadioButtonId())).getText().toString();
        String payment = isUpi ? "UPI (" + upi + ")" : "Cash on delivery";

        progress.setVisibility(View.VISIBLE);
        btnPlaceOrder.setText("Placing order...");

        // Simulates the order API round-trip.
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            JSONObject order = buildOrder(name, phone, address, pincode, slot, payment);
            Store store = new Store(this);
            store.addOrder(store.sessionEmail(), order);
            cart.clear();

            Intent intent = new Intent(this, OrderSuccessActivity.class);
            intent.putExtra(OrderSuccessActivity.EXTRA_ORDER, order.toString());
            startActivity(intent);
            finish();
        }, 1500);
    }

    private JSONObject buildOrder(String name, String phone, String address, String pincode, String slot, String payment) {
        JSONObject order = new JSONObject();
        try {
            long now = System.currentTimeMillis();
            order.put("id", "FC" + (now % 1000000));
            order.put("placedAt", now);
            order.put("name", name);
            order.put("phone", phone);
            order.put("address", address + " - " + pincode);
            order.put("slot", slot);
            order.put("payment", payment);
            order.put("subtotal", cart.subtotal());
            order.put("discount", cart.discount());
            order.put("delivery", cart.deliveryFee());
            order.put("total", cart.total());
            order.put("itemCount", cart.itemCount());

            JSONArray lines = new JSONArray();
            for (Map.Entry<String, Integer> e : cart.items().entrySet()) {
                Product p = Catalog.byId(e.getKey());
                if (p == null) continue;
                lines.put(p.name + " × " + e.getValue());
            }
            order.put("items", lines);
            order.put("status", "Placed");
        } catch (JSONException ignored) {
        }
        return order;
    }
}
