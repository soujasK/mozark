package com.fromprompt.freshcart;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

/**
 * Hosts every screen below an app-owned toolbar and keeps content clear of the status bar,
 * navigation bar and display cutouts. Android 15+ always draws apps edge-to-edge when they
 * target SDK 35+, so the insets must be applied by the app; enabling edge-to-edge on every
 * version keeps the layout identical on older Android releases.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT), SystemBarStyle.dark(Color.TRANSPARENT));
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        View root = getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout container = root.findViewById(R.id.screen_container);
        LayoutInflater.from(this).inflate(layoutResID, container, true);
        super.setContentView(root);

        MaterialToolbar toolbar = root.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, windowInsets) -> {
            Insets bars = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            Insets ime = windowInsets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(bars.left, bars.top, bars.right, Math.max(bars.bottom, ime.bottom));
            return WindowInsetsCompat.CONSUMED;
        });
    }

    @Override
    public void setContentView(View view) {
        throw new UnsupportedOperationException("Use setContentView(int)");
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        throw new UnsupportedOperationException("Use setContentView(int)");
    }
}
