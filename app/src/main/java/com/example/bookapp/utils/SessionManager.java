package com.example.bookapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "BookAppSession";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_FULLNAME = "fullname";
    private static final String KEY_ROLE = "role";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    // Lưu thông tin user sau khi login
    public void createLoginSession(int userId, String username, String fullname, String role) {
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_FULLNAME, fullname);
        editor.putString(KEY_ROLE, role);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    // Lấy user ID
    public int getUserId() {
        return preferences.getInt(KEY_USER_ID, -1);
    }

    // Lấy username
    public String getUsername() {
        return preferences.getString(KEY_USERNAME, "");
    }

    // Lấy fullname
    public String getFullname() {
        return preferences.getString(KEY_FULLNAME, "");
    }

    // Lấy role
    public String getRole() {
        return preferences.getString(KEY_ROLE, "");
    }

    // Kiểm tra đã login chưa
    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Kiểm tra có phải guest không
    public boolean isGuest() {
        return "guest".equals(getRole());
    }

    // Kiểm tra có phải admin không
    public boolean isAdmin() {
        return "admin".equals(getRole());
    }

    // Logout - xóa session
    public void logout() {
        editor.clear();
        editor.apply();
    }
}