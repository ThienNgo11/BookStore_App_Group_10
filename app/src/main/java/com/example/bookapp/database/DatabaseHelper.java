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

        // ========================================
        // THÊM USERS MẶC ĐỊNH
        // ========================================
        // Password đã hash: admin = admin, user = user
        db.execSQL("INSERT INTO users (username, password, fullname, email, phone, address, role, is_active) VALUES " +
                "('admin', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'Administrator', 'admin@bookstore.com', '0123456789', 'Hà Nội', 'admin', 1)");

        db.execSQL("INSERT INTO users (username, password, fullname, email, phone, address, role, is_active) VALUES " +
                "('user', '04f8996da763b7a969b1028ee3007569eaf3a635486ddab211d512c85b9df8fb', 'Nguyễn Văn A', 'user@gmail.com', '0987654321', 'TP HCM', 'user', 1)");

        db.execSQL("INSERT INTO users (username, password, fullname, email, phone, address, role, is_active) VALUES " +
                "('user2', '04f8996da763b7a969b1028ee3007569eaf3a635486ddab211d512c85b9df8fb', 'Trần Thị B', 'user2@gmail.com', '0909090909', 'Đà Nẵng', 'user', 1)");

        // ========================================
        // THÊM BOOKS MẶC ĐỊNH (15 CUỐN)
        // ========================================
        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('Lập trình Android từ A-Z', 'Nguyễn Văn An', 'Công nghệ', 'Cuốn sách hướng dẫn chi tiết về lập trình Android cho người mới bắt đầu', 250000, 20, 'https://www.sachbaokhang.vn/uploads/files/2023/05/01/van-1.jpg')");

        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('Java Căn bản', 'Trần Minh Tuấn', 'Lập trình', 'Học Java từ cơ bản đến nâng cao với nhiều ví dụ thực tế', 180000, 15, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQD3se4ZhISuAB2hLTBG6PuZWd1yza9rLxdAA&s')");

        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('SQL cho người mới bắt đầu', 'Lê Hoàng Nam', 'Cơ sở dữ liệu', 'Tìm hiểu SQL và quản lý database một cách dễ dàng', 150000, 25, 'https://ischool.vn/wp-content/uploads/2022/12/nhung-cuon-sach-cho-tre-10-tuoi-1.jpg')");

        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('Python cơ bản', 'Phạm Văn Bình', 'Lập trình', 'Khóa học Python từ zero đến hero', 200000, 18, 'https://www.sachbaokhang.vn/uploads/files/2023/05/01/van-1.jpg')");

        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('Web Development 2024', 'Đỗ Thị Mai', 'Web', 'Học làm web với HTML, CSS, JavaScript hiện đại', 280000, 12, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQD3se4ZhISuAB2hLTBG6PuZWd1yza9rLxdAA&s')");

        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('React Native Thực chiến', 'Hoàng Văn Dũng', 'Mobile', 'Xây dựng ứng dụng di động với React Native', 320000, 10, 'https://ischool.vn/wp-content/uploads/2022/12/nhung-cuon-sach-cho-tre-10-tuoi-1.jpg')");

        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('Machine Learning cơ bản', 'Vũ Đức Thắng', 'AI', 'Nhập môn Machine Learning và Deep Learning', 350000, 8, 'https://www.sachbaokhang.vn/uploads/files/2023/05/01/van-1.jpg')");

        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('Clean Code', 'Robert C. Martin', 'Kỹ thuật', 'Nghệ thuật viết code sạch và dễ maintain', 380000, 15, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQD3se4ZhISuAB2hLTBG6PuZWd1yza9rLxdAA&s')");

        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('Design Patterns', 'Gang of Four', 'Kỹ thuật', 'Các mẫu thiết kế phần mềm kinh điển', 420000, 6, 'https://ischool.vn/wp-content/uploads/2022/12/nhung-cuon-sach-cho-tre-10-tuoi-1.jpg')");

        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('Git & GitHub', 'Nguyễn Hữu Phúc', 'Tools', 'Làm chủ Git và quản lý source code hiệu quả', 120000, 30, 'https://www.sachbaokhang.vn/uploads/files/2023/05/01/van-1.jpg')");

        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('Docker & Kubernetes', 'Lê Minh Hoàng', 'DevOps', 'Container hóa ứng dụng với Docker và K8s', 450000, 7, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQD3se4ZhISuAB2hLTBG6PuZWd1yza9rLxdAA&s')");

        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('Node.js Backend', 'Trần Quốc Anh', 'Backend', 'Xây dựng API với Node.js và Express', 290000, 14, 'https://ischool.vn/wp-content/uploads/2022/12/nhung-cuon-sach-cho-tre-10-tuoi-1.jpg')");

        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('Flutter cho mọi người', 'Phạm Thị Lan', 'Mobile', 'Phát triển app đa nền tảng với Flutter', 310000, 11, 'https://www.sachbaokhang.vn/uploads/files/2023/05/01/van-1.jpg')");

        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('C++ Nâng cao', 'Đặng Văn Long', 'Lập trình', 'Chuyên sâu C++ và lập trình hệ thống', 260000, 9, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQD3se4ZhISuAB2hLTBG6PuZWd1yza9rLxdAA&s')");

        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('Blockchain & Crypto', 'Võ Thanh Tùng', 'Blockchain', 'Công nghệ Blockchain và ứng dụng thực tế', 390000, 5, 'https://ischool.vn/wp-content/uploads/2022/12/nhung-cuon-sach-cho-tre-10-tuoi-1.jpg')");

        // ========================================
        // THÊM CART MẶC ĐỊNH
        // ========================================
        // Giỏ hàng cho user_id=2 (user 'user')
        db.execSQL("INSERT INTO cart (user_id, book_id, quantity) VALUES (2, 1, 2)");
        db.execSQL("INSERT INTO cart (user_id, book_id, quantity) VALUES (2, 3, 1)");
        db.execSQL("INSERT INTO cart (user_id, book_id, quantity) VALUES (2, 5, 3)");
        db.execSQL("INSERT INTO cart (user_id, book_id, quantity) VALUES (2, 8, 1)");

        // Giỏ hàng cho user_id=3 (user 'user2')
        db.execSQL("INSERT INTO cart (user_id, book_id, quantity) VALUES (3, 2, 1)");
        db.execSQL("INSERT INTO cart (user_id, book_id, quantity) VALUES (3, 4, 2)");
        db.execSQL("INSERT INTO cart (user_id, book_id, quantity) VALUES (3, 7, 1)");

        // ========================================
        // THÊM ORDERS MẶC ĐỊNH
        // ========================================
        db.execSQL("INSERT INTO orders (user_id, order_date, total_amount, status) VALUES (2, '2025-11-01', 580000, 'Pending')");
        db.execSQL("INSERT INTO orders (user_id, order_date, total_amount, status) VALUES (2, '2025-10-15', 450000, 'Completed')");
        db.execSQL("INSERT INTO orders (user_id, order_date, total_amount, status) VALUES (3, '2025-11-05', 320000, 'Shipped')");

        // ========================================
        // THÊM ORDER ITEMS MẶC ĐỊNH
        // ========================================
        db.execSQL("INSERT INTO order_items (order_id, book_id, quantity, price) VALUES (1, 1, 2, 250000)");
        db.execSQL("INSERT INTO order_items (order_id, book_id, quantity, price) VALUES (1, 2, 1, 180000)");
        db.execSQL("INSERT INTO order_items (order_id, book_id, quantity, price) VALUES (2, 5, 1, 280000)");
        db.execSQL("INSERT INTO order_items (order_id, book_id, quantity, price) VALUES (2, 3, 1, 150000)");
        db.execSQL("INSERT INTO order_items (order_id, book_id, quantity, price) VALUES (3, 6, 1, 320000)");
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