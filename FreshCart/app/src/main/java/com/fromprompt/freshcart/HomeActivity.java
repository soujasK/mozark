package com.fromprompt.freshcart;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private LinearLayout listProducts;
    private TextView tvEmpty;
    private EditText etSearch;
    private Button btnViewCart;
    private String selectedCategory = Catalog.ALL;
    private String sortMode = Catalog.SORT_MODES[0];

    private TextView tvGreeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Store store = new Store(this);
        String email = store.sessionEmail();
        tvGreeting = findViewById(R.id.tv_greeting);
        if (email != null) {
            tvGreeting.setText("Hi, " + store.nameOf(email) + "!");
        }

        listProducts = findViewById(R.id.list_products);
        tvEmpty = findViewById(R.id.tv_empty);
        etSearch = findViewById(R.id.et_search);
        btnViewCart = findViewById(R.id.btn_view_cart);
        btnViewCart.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));

        ChipGroup chipGroup = findViewById(R.id.chip_group);
        for (String category : Catalog.CATEGORIES) {
            Chip chip = new Chip(this);
            chip.setText(category);
            chip.setCheckable(true);
            chip.setChecked(category.equals(Catalog.ALL));
            chip.setOnClickListener(v -> {
                selectedCategory = category;
                renderProducts();
            });
            chipGroup.addView(chip);
        }

        Spinner spSort = findViewById(R.id.sp_sort);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Catalog.SORT_MODES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSort.setAdapter(adapter);
        spSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortMode = Catalog.SORT_MODES[position];
                renderProducts();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                renderProducts();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Store store = new Store(this);
        String email = store.sessionEmail();
        if (email != null && tvGreeting != null) {
            tvGreeting.setText("Hi, " + store.nameOf(email) + "!");
        }
        renderProducts();
        updateCartButton();
    }

    private void renderProducts() {
        List<Product> products = Catalog.filter(selectedCategory, etSearch.getText().toString());
        Catalog.sort(products, sortMode);
        listProducts.removeAllViews();
        tvEmpty.setVisibility(products.isEmpty() ? View.VISIBLE : View.GONE);

        LayoutInflater inflater = LayoutInflater.from(this);
        for (Product p : products) {
            View row = inflater.inflate(R.layout.item_product, listProducts, false);
            ((TextView) row.findViewById(R.id.tv_name)).setText(p.name);
            ((TextView) row.findViewById(R.id.tv_meta)).setText(p.unit + (p.stock == 0 ? " · Out of stock" : ""));
            ((TextView) row.findViewById(R.id.tv_price)).setText(CartManager.rupees(p.price));

            Button btnAdd = row.findViewById(R.id.btn_add);
            btnAdd.setContentDescription("Add " + p.name);
            btnAdd.setEnabled(p.stock > 0);
            btnAdd.setOnClickListener(v -> {
                int added = CartManager.get().add(p, 1);
                if (added > 0) {
                    Toast.makeText(this, p.name + " added to cart", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Only " + p.stock + " in stock", Toast.LENGTH_SHORT).show();
                }
                updateCartButton();
            });

            row.setOnClickListener(v -> {
                Intent intent = new Intent(this, ProductDetailActivity.class);
                intent.putExtra(ProductDetailActivity.EXTRA_ID, p.id);
                startActivity(intent);
            });
            listProducts.addView(row);
        }
    }

    private void updateCartButton() {
        int count = CartManager.get().itemCount();
        btnViewCart.setText(count == 0 ? "View cart" : "View cart (" + count + ") · " + CartManager.rupees(CartManager.get().subtotal()));
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        menu.findItem(R.id.action_cart).setTitle("Cart (" + CartManager.get().itemCount() + ")");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_cart) {
            startActivity(new Intent(this, CartActivity.class));
            return true;
        } else if (id == R.id.action_orders) {
            startActivity(new Intent(this, OrdersActivity.class));
            return true;
        } else if (id == R.id.action_wishlist) {
            startActivity(new Intent(this, WishlistActivity.class));
            return true;
        } else if (id == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
