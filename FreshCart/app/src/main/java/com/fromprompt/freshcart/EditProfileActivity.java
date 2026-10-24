package com.fromprompt.freshcart;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class EditProfileActivity extends AppCompatActivity {
    private Store store;
    private String email;
    private TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        store = new Store(this);
        email = store.sessionEmail();
        EditText etName = findViewById(R.id.et_name);
        EditText etPhone = findViewById(R.id.et_phone);
        EditText etCurrent = findViewById(R.id.et_current_password);
        EditText etNew = findViewById(R.id.et_new_password);
        tvMessage = findViewById(R.id.tv_message);

        etName.setText(store.nameOf(email));
        etPhone.setText(store.phoneOf(email));

        findViewById(R.id.btn_save_profile).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            if (name.isEmpty()) {
                showMessage("Name cannot be empty", true);
            } else if (!phone.isEmpty() && !phone.matches("\\d{10}")) {
                showMessage("Enter a valid 10-digit mobile number", true);
            } else {
                store.updateProfile(email, name, phone);
                showMessage("Profile updated", false);
            }
        });

        findViewById(R.id.btn_change_password).setOnClickListener(v -> {
            String current = etCurrent.getText().toString();
            String newPassword = etNew.getText().toString();
            if (!store.checkPassword(email, current)) {
                showMessage("Current password is incorrect", true);
            } else if (newPassword.length() < 8) {
                showMessage("New password must be at least 8 characters", true);
            } else {
                store.setPassword(email, newPassword);
                etCurrent.setText("");
                etNew.setText("");
                showMessage("Password updated successfully", false);
            }
        });
    }

    private void showMessage(String message, boolean isError) {
        tvMessage.setVisibility(View.VISIBLE);
        tvMessage.setText(message);
        tvMessage.setTextColor(ContextCompat.getColor(this, isError ? R.color.error : R.color.green_700));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
