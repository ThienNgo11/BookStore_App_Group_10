package com.example.bookapp.database;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.bookapp.database.DatabaseHelper;

public class OrderDAO {

    private DatabaseHelper dbHelper;

    public OrderDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public int getTotalOrders() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM orders", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public double getTotalRevenue() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(total_amount) FROM orders", null);
        cursor.moveToFirst();
        double revenue = cursor.getDouble(0);
        cursor.close();
        return revenue;
    }
}
