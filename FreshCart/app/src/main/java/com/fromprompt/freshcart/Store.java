package com.fromprompt.freshcart;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** Local persistence for accounts, session, wishlist, addresses and order history. */
public final class Store {
    private static final String PREFS = "freshcart";
    private static final String KEY_USERS = "users";
    private static final String KEY_SESSION = "session_email";
    private static final String KEY_WISHLIST = "wishlist";
    public static final int MAX_LOGIN_ATTEMPTS = 3;

    public static final String DEMO_EMAIL = "demo@freshcart.app";
    public static final String DEMO_PASSWORD = "Demo@123";

    private final SharedPreferences prefs;

    public Store(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if (!userExists(DEMO_EMAIL)) {
            register("Demo User", DEMO_EMAIL, DEMO_PASSWORD);
        }
    }

    private JSONObject users() {
        try {
            return new JSONObject(prefs.getString(KEY_USERS, "{}"));
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    private void saveUsers(JSONObject all) {
        prefs.edit().putString(KEY_USERS, all.toString()).apply();
    }

    public boolean userExists(String email) {
        return users().has(email);
    }

    public void register(String name, String email, String password) {
        JSONObject all = users();
        try {
            JSONObject u = new JSONObject();
            u.put("name", name);
            u.put("password", password);
            all.put(email, u);
        } catch (JSONException ignored) {
        }
        saveUsers(all);
    }

    public boolean checkPassword(String email, String password) {
        JSONObject u = users().optJSONObject(email);
        return u != null && u.optString("password").equals(password);
    }

    public void setPassword(String email, String password) {
        JSONObject all = users();
        JSONObject u = all.optJSONObject(email);
        if (u == null) return;
        try {
            u.put("password", password);
            all.put(email, u);
        } catch (JSONException ignored) {
        }
        saveUsers(all);
    }

    /** Generates a temporary password, stores it and returns it for display. */
    public String resetPassword(String email) {
        String temp = "Fresh@" + (1000 + email.length() * 37 % 9000);
        setPassword(email, temp);
        resetFailedAttempts(email);
        return temp;
    }

    public int failedAttempts(String email) {
        return prefs.getInt("failed_" + email.toLowerCase().trim(), 0);
    }

    public void recordFailedAttempt(String email) {
        prefs.edit().putInt("failed_" + email.toLowerCase().trim(), failedAttempts(email) + 1).apply();
    }

    public void resetFailedAttempts(String email) {
        prefs.edit().remove("failed_" + email.toLowerCase().trim()).apply();
    }

    public boolean isLocked(String email) {
        return failedAttempts(email) >= MAX_LOGIN_ATTEMPTS;
    }

    public String nameOf(String email) {
        JSONObject u = users().optJSONObject(email);
        return u == null ? "" : u.optString("name");
    }

    public String phoneOf(String email) {
        JSONObject u = users().optJSONObject(email);
        return u == null ? "" : u.optString("phone");
    }

    public void updateProfile(String email, String name, String phone) {
        JSONObject all = users();
        JSONObject u = all.optJSONObject(email);
        if (u == null) return;
        try {
            u.put("name", name);
            u.put("phone", phone);
            all.put(email, u);
        } catch (JSONException ignored) {
        }
        saveUsers(all);
    }

    public void login(String email) {
        prefs.edit().putString(KEY_SESSION, email).apply();
    }

    public void logout() {
        prefs.edit().remove(KEY_SESSION).apply();
    }

    public String sessionEmail() {
        return prefs.getString(KEY_SESSION, null);
    }

    // ---- Wishlist ----

    private String wishlistKey() {
        String email = sessionEmail();
        return KEY_WISHLIST + "_" + (email == null ? "guest" : email.toLowerCase().trim());
    }

    public JSONArray wishlist() {
        try {
            return new JSONArray(prefs.getString(wishlistKey(), "[]"));
        } catch (JSONException e) {
            return new JSONArray();
        }
    }

    public boolean inWishlist(String productId) {
        JSONArray list = wishlist();
        for (int i = 0; i < list.length(); i++) {
            if (productId.equals(list.optString(i))) return true;
        }
        return false;
    }

    public void addToWishlist(String productId) {
        if (inWishlist(productId)) return;
        JSONArray list = wishlist();
        list.put(productId);
        prefs.edit().putString(wishlistKey(), list.toString()).apply();
    }

    public void removeFromWishlist(String productId) {
        JSONArray list = wishlist();
        int index = -1;
        for (int i = 0; i < list.length(); i++) {
            if (productId.equals(list.optString(i))) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            list.remove(index);
            prefs.edit().putString(wishlistKey(), list.toString()).apply();
        }
    }

    // ---- Saved address ----

    public void saveAddress(String email, String phone, String address, String pincode) {
        JSONObject a = new JSONObject();
        try {
            a.put("phone", phone);
            a.put("address", address);
            a.put("pincode", pincode);
        } catch (JSONException ignored) {
        }
        prefs.edit().putString("address_" + email.toLowerCase().trim(), a.toString()).apply();
    }

    /** Returns {phone, address, pincode} or null. */
    public String[] savedAddress(String email) {
        if (email == null) return null;
        String raw = prefs.getString("address_" + email.toLowerCase().trim(), null);
        if (raw == null) return null;
        try {
            JSONObject a = new JSONObject(raw);
            return new String[]{a.optString("phone"), a.optString("address"), a.optString("pincode")};
        } catch (JSONException e) {
            return null;
        }
    }

    // ---- Orders ----

    public JSONArray orders(String email) {
        if (email == null) return new JSONArray();
        try {
            return new JSONArray(prefs.getString("orders_" + email.toLowerCase().trim(), "[]"));
        } catch (JSONException e) {
            return new JSONArray();
        }
    }

    public void addOrder(String email, JSONObject order) {
        if (email == null) return;
        JSONArray list = orders(email);
        list.put(order);
        prefs.edit().putString("orders_" + email.toLowerCase().trim(), list.toString()).apply();
    }

    public void cancelOrder(String email, int index) {
        if (email == null) return;
        JSONArray list = orders(email);
        JSONObject order = list.optJSONObject(index);
        if (order == null) return;
        try {
            order.put("status", "Cancelled");
            prefs.edit().putString("orders_" + email.toLowerCase().trim(), list.toString()).apply();
        } catch (JSONException ignored) {
        }
    }
}
