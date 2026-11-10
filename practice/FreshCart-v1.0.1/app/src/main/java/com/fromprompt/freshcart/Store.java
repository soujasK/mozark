package com.fromprompt.freshcart;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** Local persistence for accounts, session and order history. */
public final class Store {
    private static final String PREFS = "freshcart";
    private static final String KEY_USERS = "users";
    private static final String KEY_SESSION = "session_email";

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
        prefs.edit().putString(KEY_USERS, all.toString()).apply();
    }

    public boolean checkPassword(String email, String password) {
        JSONObject u = users().optJSONObject(email);
        return u != null && u.optString("password").equals(password);
    }

    public String nameOf(String email) {
        JSONObject u = users().optJSONObject(email);
        return u == null ? "" : u.optString("name");
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

    public JSONArray orders(String email) {
        try {
            return new JSONArray(prefs.getString("orders_" + email, "[]"));
        } catch (JSONException e) {
            return new JSONArray();
        }
    }

    public void addOrder(String email, JSONObject order) {
        JSONArray list = orders(email);
        list.put(order);
        prefs.edit().putString("orders_" + email, list.toString()).apply();
    }
}
