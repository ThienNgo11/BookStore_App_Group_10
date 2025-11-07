package com.example.bookapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.bookapp.utils.SecurityUtils;

import com.example.bookapp.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private DatabaseHelper dbHelper;

    public UserDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public User authenticateUser(String username, String plainTextPassword) {

        //Băm mật khẩu
        String hashedPassword = SecurityUtils.hashPassword(plainTextPassword);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                "id", "username", "password", "fullname", "email", "phone", "address", "role", "is_active"
        };
        // Chỉ cho phép đăng nhập nếu tài khoản còn active
        String selection = "username = ? AND password = ? AND is_active = 1";
        String[] selectionArgs = {username, hashedPassword};
        Cursor cursor = db.query(
                "users",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow("username")));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("password")));
            user.setFullname(cursor.getString(cursor.getColumnIndexOrThrow("fullname")));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            user.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
            user.setRole(cursor.getString(cursor.getColumnIndexOrThrow("role")));
            user.setActive(cursor.getInt(cursor.getColumnIndexOrThrow("is_active")) == 1);
            cursor.close();
        }
        if (cursor != null) {
            cursor.close();
        }
        return user;
    }

    public boolean insertUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", user.getUsername());
        values.put("password", user.getPassword());
        values.put("fullname", user.getFullname());
        values.put("email", user.getEmail());
        values.put("phone", user.getPhone());
        values.put("address", user.getAddress());
        values.put("role", user.getRole());
        values.put("is_active", user.isActive() ? 1 : 0);
        long result = db.insert("users", null, values);
        return result != -1;
    }

    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM users WHERE username = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public int getTotalUsers() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM users", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
    public User getUserByUsername(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM users WHERE username = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow("username")));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("password")));
            user.setFullname(cursor.getString(cursor.getColumnIndexOrThrow("fullname")));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            user.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
            user.setRole(cursor.getString(cursor.getColumnIndexOrThrow("role")));
            cursor.close();
        }
        return user;
    }

    public void updateUserPassword(int userId, String hashedPassword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", hashedPassword); // Lưu chuỗi Hex 64 ký tự
        db.update("users", values, "id = ?", new String[]{String.valueOf(userId)});
        db.close();
    }

    public void updateUserInfo(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fullname", user.getFullname());
        values.put("email", user.getEmail());
        values.put("phone", user.getPhone());
        values.put("address", user.getAddress());
        db.update("users", values, "id = ?", new String[]{String.valueOf(user.getId())});
    }
    public int getActiveUsersCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM users WHERE is_active = 1", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // Lấy tất cả users (cả active và inactive)
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users ORDER BY id", null);
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow("username")));
                user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("password")));
                user.setFullname(cursor.getString(cursor.getColumnIndexOrThrow("fullname")));
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
                user.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
                user.setRole(cursor.getString(cursor.getColumnIndexOrThrow("role")));
                user.setActive(cursor.getInt(cursor.getColumnIndexOrThrow("is_active")) == 1);
                users.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return users;
    }

    public boolean updateUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", user.getUsername());
        values.put("password", user.getPassword());
        values.put("fullname", user.getFullname());
        values.put("email", user.getEmail());
        values.put("phone", user.getPhone());
        values.put("address", user.getAddress());
        values.put("role", user.getRole());
        values.put("is_active", user.isActive() ? 1 : 0);
        int result = db.update("users", values, "id = ?", new String[]{String.valueOf(user.getId())});
        return result > 0;
    }

    // Soft delete - chỉ set is_active = 0
    public boolean deleteUser(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_active", 0);
        int result = db.update("users", values, "id = ?", new String[]{String.valueOf(userId)});
        return result > 0;
    }

    // Kiểm tra xem user có phải admin không
    public boolean isAdmin(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT role FROM users WHERE id = ?", new String[]{String.valueOf(userId)});
        boolean isAdmin = false;
        if (cursor != null && cursor.moveToFirst()) {
            String role = cursor.getString(cursor.getColumnIndexOrThrow("role"));
            isAdmin = "admin".equals(role);
            cursor.close();
        }
        return isAdmin;
    }

    // Toggle active status
    public boolean toggleUserActive(int userId, boolean isActive) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_active", isActive ? 1 : 0);
        int result = db.update("users", values, "id = ?", new String[]{String.valueOf(userId)});
        return result > 0;
    }
    public User getUserById(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                "id", "username", "password", "fullname", "email", "phone", "address", "role"
        };
        String selection = "id = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        Cursor cursor = db.query(
                "users",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow("username")));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("password")));
            user.setFullname(cursor.getString(cursor.getColumnIndexOrThrow("fullname")));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            user.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
            user.setRole(cursor.getString(cursor.getColumnIndexOrThrow("role")));
            cursor.close();
        }
        return user;
    }
}