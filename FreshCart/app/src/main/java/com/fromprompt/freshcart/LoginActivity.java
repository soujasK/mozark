package com.fromprompt.freshcart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private Store store;
    private EditText etEmail;
    private EditText etPassword;
    private TextView tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        store = new Store(this);

        if (store.sessionEmail() != null) {
            openHome();
            return;
        }

        setContentView(R.layout.activity_login);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        tvError = findViewById(R.id.tv_error);
        Button btnLogin = findViewById(R.id.btn_login);
        Button btnSignup = findViewById(R.id.btn_go_signup);

        btnLogin.setOnClickListener(v -> attemptLogin());
        btnSignup.setOnClickListener(v -> startActivity(new Intent(this, SignupActivity.class)));
        findViewById(R.id.btn_forgot).setOnClickListener(v -> forgotPassword());
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim().toLowerCase();
        String password = etPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter email and password");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Enter a valid email address");
            return;
        }
        if (!store.userExists(email)) {
            showError("No account found for this email");
            return;
        }
        if (store.isLocked(email)) {
            showError("Too many failed attempts. Your account is locked, please try again later.");
            return;
        }
        if (!store.checkPassword(email, password)) {
            store.recordFailedAttempt(email);
            int left = Store.MAX_LOGIN_ATTEMPTS - store.failedAttempts(email);
            showError("Incorrect password" + (left > 0 ? " (" + left + " attempts left)" : ""));
            return;
        }
        store.resetFailedAttempts(email);
        store.login(email);
        openHome();
    }

    private void forgotPassword() {
        String email = etEmail.getText().toString().trim();
        if (!store.userExists(email)) {
            showError("Enter your registered email above, then tap Forgot password");
            return;
        }
        String temp = store.resetPassword(email);
        new AlertDialog.Builder(this)
                .setTitle("Password reset")
                .setMessage("Your temporary password is " + temp + "\nUse it to log in and change it from your profile.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    private void openHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
