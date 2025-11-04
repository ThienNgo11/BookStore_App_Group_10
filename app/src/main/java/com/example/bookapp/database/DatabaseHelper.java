package com.example.bookapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "bookstore.db";
    public static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // === USERS ===
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "fullname TEXT," +
                "email TEXT," +
                "phone TEXT," +
                "address TEXT," +
                "role TEXT DEFAULT 'user'," +
                "is_active INTEGER DEFAULT 1)");

        // === BOOKS ===
        db.execSQL("CREATE TABLE books (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "author TEXT," +
                "category TEXT," +
                "description TEXT," +
                "price REAL NOT NULL," +
                "stock INTEGER DEFAULT 0," +
                "image TEXT)");

        // === ORDERS ===
        db.execSQL("CREATE TABLE orders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "order_date TEXT," +
                "total_amount REAL," +
                "status TEXT DEFAULT 'Pending'," +
                "FOREIGN KEY (user_id) REFERENCES users(id))");

        // === ORDER ITEMS ===
        db.execSQL("CREATE TABLE order_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "order_id INTEGER," +
                "book_id INTEGER," +
                "quantity INTEGER," +
                "price REAL," +
                "FOREIGN KEY (order_id) REFERENCES orders(id)," +
                "FOREIGN KEY (book_id) REFERENCES books(id))");

        // === CART ===
        db.execSQL("CREATE TABLE cart (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "book_id INTEGER," +
                "quantity INTEGER DEFAULT 1," +
                "FOREIGN KEY (user_id) REFERENCES users(id)," +
                "FOREIGN KEY (book_id) REFERENCES books(id))");

        // Thêm users mặc định
        db.execSQL("INSERT INTO users (username, password, fullname, role, is_active) VALUES ('admin', 'admin', 'Admin', 'admin', 1)");
        db.execSQL("INSERT INTO users (username, password, fullname, role, is_active) VALUES ('user', 'user', 'User', 'user', 1)");
        db.execSQL("INSERT INTO users (username, password, fullname, role, is_active) VALUES ('user2', 'user2', 'User 2', 'user', 1)");
        db.execSQL("INSERT INTO users (username, password, fullname, role, is_active) VALUES ('user3', 'user3', 'User 3', 'user', 0)"); // Inactive user

        // Thêm books mặc định
        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('Book 1: Android Programming', 'Author A', 'Tech', 'Description here', 150000, 10, 'https://www.sachbaokhang.vn/uploads/files/2023/05/01/van-1.jpg')");
        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('Book 2: Java Basics', 'Author B', 'Programming', 'Intro to Java', 120000, 5, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQD3se4ZhISuAB2hLTBG6PuZWd1yza9rLxdAA&s')");
        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('Book 3: SQL for Beginners', 'Author C', 'Database', 'Learn SQL', 100000, 15, 'https://ischool.vn/wp-content/uploads/2022/12/nhung-cuon-sach-cho-tre-10-tuoi-1.jpg')");

        // Thêm orders mặc định (giả sử user_id=2 là 'user', order_date format 'YYYY-MM-DD')
        db.execSQL("INSERT INTO orders (user_id, order_date, total_amount, status) VALUES (2, '2025-10-01', 270000, 'Pending')");
        db.execSQL("INSERT INTO orders (user_id, order_date, total_amount, status) VALUES (2, '2025-10-15', 100000, 'Completed')");
        db.execSQL("INSERT INTO orders (user_id, order_date, total_amount, status) VALUES (3, '2025-11-01', 150000, 'Shipped')");

        // Thêm order_items mặc định (liên kết với orders id=1,2,3 và books id=1,2,3)
        db.execSQL("INSERT INTO order_items (order_id, book_id, quantity, price) VALUES (1, 1, 1, 150000)");
        db.execSQL("INSERT INTO order_items (order_id, book_id, quantity, price) VALUES (1, 2, 1, 120000)");
        db.execSQL("INSERT INTO order_items (order_id, book_id, quantity, price) VALUES (2, 3, 1, 100000)");
        db.execSQL("INSERT INTO order_items (order_id, book_id, quantity, price) VALUES (3, 1, 1, 150000)");

        // Thêm cart mặc định (cho user_id=2 và 3, với book_id)
        db.execSQL("INSERT INTO cart (user_id, book_id, quantity) VALUES (2, 1, 2)");
        db.execSQL("INSERT INTO cart (user_id, book_id, quantity) VALUES (2, 2, 1)");
        db.execSQL("INSERT INTO cart (user_id, book_id, quantity) VALUES (3, 2, 3)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa và tạo lại bảng khi nâng cấp
        db.execSQL("DROP TABLE IF EXISTS order_items");
        db.execSQL("DROP TABLE IF EXISTS orders");
        db.execSQL("DROP TABLE IF EXISTS cart");
        db.execSQL("DROP TABLE IF EXISTS books");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }
}