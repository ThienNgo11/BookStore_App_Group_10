package com.example.bookapp.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.bookapp.database.DatabaseHelper;
import com.example.bookapp.models.User;

public class UserDAO {

    private DatabaseHelper dbHelper;

    public UserDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public User authenticateUser(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                "id", "username", "password", "fullname", "email", "phone", "address", "role"
        };

        String selection = "username = ? AND password = ?";
        String[] selectionArgs = {username, password};

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
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM users WHERE role = 'user'", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    // Thêm vào cuối class UserDAO

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

    public void updateUserPassword(int userId, String newPassword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", newPassword);
        db.update("users", values, "id = ?", new String[]{String.valueOf(userId)});
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
}