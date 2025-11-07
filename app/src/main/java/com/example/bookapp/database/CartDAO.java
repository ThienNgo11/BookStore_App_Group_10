package com.example.bookapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.bookapp.models.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartDAO {
    private DatabaseHelper dbHelper;

    public CartDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Lấy tất cả items trong giỏ hàng của user (JOIN với bảng books để lấy thông tin đầy đủ)
    public List<CartItem> getCartItems(int userId) {
        List<CartItem> items = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT cart.id, cart.user_id, cart.book_id, cart.quantity, " +
                "books.title, books.image, books.price, books.stock " +
                "FROM cart " +
                "INNER JOIN books ON cart.book_id = books.id " +
                "WHERE cart.user_id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                CartItem item = new CartItem(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("book_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("image")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("quantity")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("stock"))
                );
                items.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return items;
    }
    public CartItem getCartItem(int userId, int bookId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT cart.id, cart.user_id, cart.book_id, cart.quantity, " +
                "books.title, books.image, books.price, books.stock " +
                "FROM cart " +
                "INNER JOIN books ON cart.book_id = books.id " +
                "WHERE cart.user_id = ? AND cart.book_id = ?";

        Cursor cursor = db.rawQuery(query,
                new String[]{String.valueOf(userId), String.valueOf(bookId)});

        CartItem item = null;

        if (cursor.moveToFirst()) {
            item = new CartItem(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("book_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    cursor.getString(cursor.getColumnIndexOrThrow("image")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("quantity")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("stock"))
            );
        }

        cursor.close();
        return item;
    }

    // Thêm sách vào giỏ hàng
    public boolean addToCart(int userId, int bookId, int quantity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Kiểm tra xem sách đã có trong giỏ chưa
        Cursor cursor = db.rawQuery(
                "SELECT id, quantity FROM cart WHERE user_id = ? AND book_id = ?",
                new String[]{String.valueOf(userId), String.valueOf(bookId)}
        );

        if (cursor.moveToFirst()) {
            // Nếu đã có, tăng số lượng
            int cartId = cursor.getInt(0);
            int currentQuantity = cursor.getInt(1);
            cursor.close();
            return updateQuantity(cartId, currentQuantity + quantity);
        } else {
            // Nếu chưa có, thêm mới
            cursor.close();
            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            values.put("book_id", bookId);
            values.put("quantity", quantity);
            long result = db.insert("cart", null, values);
            return result != -1;
        }
    }

    // Cập nhật số lượng
    public boolean updateQuantity(int cartId, int newQuantity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quantity", newQuantity);
        int result = db.update("cart", values, "id = ?",
                new String[]{String.valueOf(cartId)});
        return result > 0;
    }

    // Xóa item khỏi giỏ hàng
    public boolean deleteCartItem(int cartId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("cart", "id = ?",
                new String[]{String.valueOf(cartId)});
        return result > 0;
    }

    // Xóa toàn bộ giỏ hàng của user
    public boolean clearCart(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("cart", "user_id = ?",
                new String[]{String.valueOf(userId)});
        return result > 0;
    }

    // Đếm số lượng items trong giỏ
    public int getCartCount(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(quantity) FROM cart WHERE user_id = ?",
                new String[]{String.valueOf(userId)}
        );

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
}