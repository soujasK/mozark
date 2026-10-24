package com.fromprompt.freshcart;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
    private static final int EXPRESS_FEE = 25;
    private static final int[] TIPS = {0, 10, 20, 30};

    private final CartManager cart = CartManager.get();

    private EditText etName;
    private EditText etPhone;
    private EditText etAddress;
    private EditText etPincode;
    private EditText etNotes;
    private EditText etUpi;
    private CheckBox cbSaveAddress;
    private RadioGroup rgSlot;
    private RadioGroup rgTip;
    private RadioGroup rgPayment;
    private TextView tvError;
    private TextView tvAmount;
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
        etNotes = findViewById(R.id.et_notes);
        etUpi = findViewById(R.id.et_upi);
        cbSaveAddress = findViewById(R.id.cb_save_address);
        rgSlot = findViewById(R.id.rg_slot);
        rgTip = findViewById(R.id.rg_tip);
        rgPayment = findViewById(R.id.rg_payment);
        tvError = findViewById(R.id.tv_error);
        tvAmount = findViewById(R.id.tv_amount);
        btnPlaceOrder = findViewById(R.id.btn_place_order);
        progress = findViewById(R.id.progress);

        Store store = new Store(this);
        etName.setText(store.nameOf(store.sessionEmail()));
        String[] saved = store.savedAddress(store.sessionEmail());
        if (saved != null) {
            etPhone.setText(saved[0]);
            etAddress.setText(saved[1]);
            etPincode.setText(saved[2]);
            cbSaveAddress.setChecked(true);
        }

        rgPayment.setOnCheckedChangeListener((group, checkedId) ->
                etUpi.setVisibility(checkedId == R.id.rb_upi ? View.VISIBLE : View.GONE));
        rgSlot.setOnCheckedChangeListener((group, checkedId) -> updateAmount());
        rgTip.setOnCheckedChangeListener((group, checkedId) -> updateAmount());

        updateAmount();
        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private int tip() {
        int checkedId = rgTip.getCheckedRadioButtonId();
        if (checkedId == -1) return 0;
        int index = rgTip.indexOfChild(findViewById(checkedId));
        if (index >= 0 && index < TIPS.length) {
            return TIPS[index];
        }
        return 0;
    }

    private int expressFee() {
        return rgSlot.getCheckedRadioButtonId() == R.id.rb_slot_now ? EXPRESS_FEE : 0;
    }

    private void updateAmount() {
        int payable = Math.max(0, cart.subtotal() - cart.discount() + cart.deliveryFee() + tip() + expressFee());
        tvAmount.setText("Amount payable: " + CartManager.rupees(payable));
    }

    private boolean placingOrder = false;

    private void placeOrder() {
        if (placingOrder) return;
        if (cart.isEmpty()) {
            tvError.setText("Your cart is empty");
            tvError.setVisibility(View.VISIBLE);
            return;
        }

        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String pincode = etPincode.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();
        String upi = etUpi.getText().toString().trim();
        boolean isUpi = rgPayment.getCheckedRadioButtonId() == R.id.rb_upi;

        String error = null;
        if (name.isEmpty() || !name.matches("[A-Za-z '-]+")) {
            error = "Please enter a valid name";
        } else if (!phone.matches("[6-9][0-9]{9}")) {
            error = "Enter a valid 10-digit mobile number";
        } else if (address.length() < 5) {
            error = "Please enter your full address";
        } else if (!pincode.matches("[1-9][0-9]{5}")) {
            error = "Enter a valid 6-digit pincode";
        } else if (rgSlot.getCheckedRadioButtonId() == -1) {
            error = "Please choose a delivery slot";
        } else if (isUpi && !upi.matches("^[a-zA-Z0-9.\\-_]{2,256}@[a-zA-Z]{2,64}$")) {
            error = "Enter a valid UPI ID";
        }

        if (error != null) {
            tvError.setText(error);
            tvError.setVisibility(View.VISIBLE);
            return;
        }
        tvError.setVisibility(View.GONE);

        int checkedSlotId = rgSlot.getCheckedRadioButtonId();
        RadioButton rbSlot = findViewById(checkedSlotId);
        String slot = rbSlot != null ? rbSlot.getText().toString() : "Standard";
        String payment = isUpi ? "UPI (" + upi + ")" : "Cash on delivery";
        String instructions = notes.length() > 50 ? notes.substring(0, 50) : notes;

        Store store = new Store(this);
        if (cbSaveAddress.isChecked()) {
            store.saveAddress(store.sessionEmail(), phone, address, pincode);
        }

        placingOrder = true;
        btnPlaceOrder.setEnabled(false);
        progress.setVisibility(View.VISIBLE);
        btnPlaceOrder.setText("Placing order...");

        // Simulates the order API round-trip.
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            JSONObject order = buildOrder(name, phone, address, pincode, slot, payment, instructions);
            store.addOrder(store.sessionEmail(), order);
            cart.clear();

            Intent intent = new Intent(this, OrderSuccessActivity.class);
            intent.putExtra(OrderSuccessActivity.EXTRA_ORDER, order.toString());
            startActivity(intent);
            finish();
        }, 1500);
    }

    private JSONObject buildOrder(String name, String phone, String address, String pincode,
                                  String slot, String payment, String instructions) {
        JSONObject order = new JSONObject();
        try {
            long now = System.currentTimeMillis();
            order.put("id", "FC" + (now % 900000 + 100000));
            order.put("placedAt", now);
            order.put("name", name);
            order.put("phone", phone);
            order.put("address", address + " - " + pincode);
            order.put("instructions", instructions);
            order.put("slot", slot);
            order.put("payment", payment);
            order.put("subtotal", cart.subtotal());
            order.put("discount", cart.discount());
            order.put("delivery", cart.deliveryFee());
            order.put("tip", tip());
            int finalTotal = Math.max(0, cart.subtotal() - cart.discount() + cart.deliveryFee() + tip() + expressFee());
            order.put("total", finalTotal);
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
