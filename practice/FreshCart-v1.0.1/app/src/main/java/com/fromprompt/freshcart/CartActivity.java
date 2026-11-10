package com.fromprompt.freshcart;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Map;

public class CartActivity extends BaseActivity {
    private final CartManager cart = CartManager.get();

    private LinearLayout listCart;
    private View layoutEmpty;
    private View layoutContent;
    private Button btnCheckout;
    private EditText etCoupon;
    private Button btnApplyCoupon;
    private TextView tvCouponStatus;
    private TextView tvSubtotal;
    private TextView tvDiscount;
    private TextView tvDelivery;
    private TextView tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listCart = findViewById(R.id.list_cart);
        layoutEmpty = findViewById(R.id.layout_empty);
        layoutContent = findViewById(R.id.layout_content);
        btnCheckout = findViewById(R.id.btn_checkout);
        etCoupon = findViewById(R.id.et_coupon);
        btnApplyCoupon = findViewById(R.id.btn_apply_coupon);
        tvCouponStatus = findViewById(R.id.tv_coupon_status);
        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvDiscount = findViewById(R.id.tv_discount);
        tvDelivery = findViewById(R.id.tv_delivery);
        tvTotal = findViewById(R.id.tv_total);

        btnApplyCoupon.setOnClickListener(v -> {
            if (cart.coupon() != null) {
                cart.clearCoupon();
                etCoupon.setText("");
                showCouponStatus("Coupon removed", false);
            } else {
                String error = cart.applyCoupon(etCoupon.getText().toString());
                if (error == null) {
                    showCouponStatus(cart.coupon() + " applied", false);
                } else {
                    showCouponStatus(error, true);
                }
            }
            render();
        });

        btnCheckout.setOnClickListener(v -> startActivity(new Intent(this, CheckoutActivity.class)));
        findViewById(R.id.btn_start_shopping).setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        render();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void render() {
        String couponDropped = cart.revalidateCoupon();
        if (couponDropped != null) {
            etCoupon.setText("");
            showCouponStatus(couponDropped, true);
        }

        boolean empty = cart.isEmpty();
        layoutEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        layoutContent.setVisibility(empty ? View.GONE : View.VISIBLE);
        btnCheckout.setVisibility(empty ? View.GONE : View.VISIBLE);
        if (empty) return;

        listCart.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (Map.Entry<String, Integer> entry : new ArrayList<>(cart.items().entrySet())) {
            Product p = Catalog.byId(entry.getKey());
            if (p == null) continue;
            int qty = entry.getValue();

            View row = inflater.inflate(R.layout.item_cart, listCart, false);
            ((TextView) row.findViewById(R.id.tv_name)).setText(p.name);
            ((TextView) row.findViewById(R.id.tv_line_total)).setText(
                    CartManager.rupees(p.price) + " × " + qty + " = " + CartManager.rupees(p.price * qty));
            ((TextView) row.findViewById(R.id.tv_qty)).setText(String.valueOf(qty));

            View btnMinus = row.findViewById(R.id.btn_minus);
            View btnPlus = row.findViewById(R.id.btn_plus);
            View btnRemove = row.findViewById(R.id.btn_remove);
            btnMinus.setContentDescription("Decrease " + p.name);
            btnPlus.setContentDescription("Increase " + p.name);
            btnRemove.setContentDescription("Remove " + p.name);

            btnMinus.setOnClickListener(v -> {
                cart.setQuantity(p, qty - 1);
                render();
            });
            btnPlus.setOnClickListener(v -> {
                cart.setQuantity(p, qty + 1);
                render();
            });
            btnRemove.setOnClickListener(v -> {
                cart.remove(p.id);
                render();
            });
            listCart.addView(row);
        }

        btnApplyCoupon.setText(cart.coupon() != null ? "Remove" : "Apply");
        etCoupon.setEnabled(cart.coupon() == null);

        tvSubtotal.setText("Subtotal: " + CartManager.rupees(cart.subtotal()));
        int discount = cart.discount();
        tvDiscount.setVisibility(discount > 0 ? View.VISIBLE : View.GONE);
        tvDiscount.setText("Discount (" + cart.coupon() + "): -" + CartManager.rupees(discount));
        int fee = cart.deliveryFee();
        tvDelivery.setText("Delivery: " + (fee == 0 ? "FREE" : CartManager.rupees(fee)));
        tvTotal.setText("Total: " + CartManager.rupees(cart.total()));
    }

    private void showCouponStatus(String message, boolean isError) {
        tvCouponStatus.setVisibility(View.VISIBLE);
        tvCouponStatus.setText(message);
        tvCouponStatus.setTextColor(ContextCompat.getColor(this, isError ? R.color.error : R.color.green_700));
    }
}
