package com.example.bookapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.bookapp.models.Order;
import com.example.bookapp.models.OrderItem;

import java.util.ArrayList;
import java.util.List;

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
        // Chỉ tính doanh thu của các đơn hàng đã 'Completed' hoặc 'Accepted'
        Cursor cursor = db.rawQuery("SELECT SUM(total_amount) FROM orders WHERE status = 'Completed' OR status = 'Accepted' OR status = 'Shipped'", null);
        cursor.moveToFirst();
        double revenue = cursor.getDouble(0);
        cursor.close();
        return revenue;
    }

    // === MỚI: Lấy tất cả đơn hàng (kèm tên người dùng) ===
    public List<Order> getAllOrders() {
        List<Order> orderList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Sử dụng JOIN để lấy tên người dùng từ bảng 'users'
        String query = "SELECT o.id, o.user_id, u.fullname, o.order_date, o.total_amount, o.status " +
                "FROM orders o " +
                "JOIN users u ON o.user_id = u.id " +
                "ORDER BY o.order_date DESC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
                String userName = cursor.getString(cursor.getColumnIndexOrThrow("fullname"));
                String orderDate = cursor.getString(cursor.getColumnIndexOrThrow("order_date"));
                double totalAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));

                Order order = new Order(id, userId, userName, orderDate, totalAmount, status);
                orderList.add(order);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return orderList;
    }

    // === MỚI: Cập nhật trạng thái đơn hàng ===
    public boolean updateOrderStatus(int orderId, String newStatus) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", newStatus);

        int rowsAffected = db.update("orders", values, "id = ?", new String[]{String.valueOf(orderId)});
        db.close();
        return rowsAffected > 0;
    }
    // Thêm vào OrderDAO.java
    public List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> orderItems = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT oi.*, b.title, b.author, b.image " +
                "FROM order_items oi " +
                "JOIN books b ON oi.book_id = b.id " +
                "WHERE oi.order_id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(orderId)});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                int bookId = cursor.getInt(cursor.getColumnIndexOrThrow("book_id"));
                String bookTitle = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String bookAuthor = cursor.getString(cursor.getColumnIndexOrThrow("author"));
                String bookImage = cursor.getString(cursor.getColumnIndexOrThrow("image"));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));

                OrderItem orderItem = new OrderItem(id, orderId, bookId, bookTitle, bookAuthor, bookImage, quantity, price);
                orderItems.add(orderItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return orderItems;
    }

    public Order getOrderById(int orderId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT o.*, u.fullname, u.phone, u.address " +
                "FROM orders o " +
                "JOIN users u ON o.user_id = u.id " +
                "WHERE o.id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(orderId)});

        Order order = null;
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
            String userName = cursor.getString(cursor.getColumnIndexOrThrow("fullname"));
            String userPhone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            String userAddress = cursor.getString(cursor.getColumnIndexOrThrow("address"));
            String orderDate = cursor.getString(cursor.getColumnIndexOrThrow("order_date"));
            double totalAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount"));
            String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));

            order = new Order(id, userId, userName, orderDate, totalAmount, status);
            order.setUserPhone(userPhone);
            order.setUserAddress(userAddress);
        }
        cursor.close();
        return order;
    }
}