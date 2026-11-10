package com.fromprompt.freshcart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Store store = new Store(this);
        String email = store.sessionEmail();
        ((TextView) findViewById(R.id.tv_name)).setText(store.nameOf(email));
        ((TextView) findViewById(R.id.tv_email)).setText(email);
        ((TextView) findViewById(R.id.tv_order_count)).setText("Orders placed: " + store.orders(email).length());
        ((TextView) findViewById(R.id.tv_version)).setText("FreshCart v" + BuildConfigHelper.versionName(this));

        findViewById(R.id.btn_logout).setOnClickListener(v -> {
            store.logout();
            CartManager.get().clear();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
