package com.fromprompt.freshcart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    private Store store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        store = new Store(this);
        ((TextView) findViewById(R.id.tv_version)).setText("FreshCart v" + BuildConfigHelper.versionName(this));

        findViewById(R.id.btn_edit_profile).setOnClickListener(v -> startActivity(new Intent(this, EditProfileActivity.class)));
        findViewById(R.id.btn_my_wishlist).setOnClickListener(v -> startActivity(new Intent(this, WishlistActivity.class)));
        findViewById(R.id.btn_logout).setOnClickListener(v -> {
            CartManager.get().clear();
            store.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finishAffinity();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String email = store.sessionEmail();
        ((TextView) findViewById(R.id.tv_name)).setText(store.nameOf(email));
        ((TextView) findViewById(R.id.tv_email)).setText(email);
        String phone = store.phoneOf(email);
        ((TextView) findViewById(R.id.tv_phone)).setText(phone.isEmpty() ? "No phone number added" : "Phone: " + phone);
        ((TextView) findViewById(R.id.tv_order_count)).setText("Orders placed: " + store.orders(email).length());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
