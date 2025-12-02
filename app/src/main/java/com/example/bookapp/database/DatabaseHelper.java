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
        // THÊM BOOKS MẶC ĐỊNH (15 CUỐN) – SÁCH NỔI TIẾNG VIỆT NAM
        // ========================================
        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('Dám Bị Ghét', 'Koga Fumitake', 'Tâm lý', 'Cuốn sách giúp thay đổi tư duy và sống tự do hơn.', 89000, 20, 'https://thuvienninhthuan.vn/Upload/2021/12/22/Dam%20bi%20ghet.jpg_2021Thg1222_034414130(1).jpg')");

        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('Nhà Giả Kim', 'Paulo Coelho', 'Văn học', 'Hành trình tìm kiếm ước mơ của chàng chăn cừu Santiago.', 99000, 15, 'https://tusachnuocman.com/wp-content/uploads/2023/12/Bia-cuon-sach-nha-gia-kim.jpg')");

        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('Tuổi Trẻ Đáng Giá Bao Nhiêu', 'Rosie Nguyễn', 'Kỹ năng sống', 'Gợi mở cách sống có mục tiêu và định hướng.', 105000, 25, 'https://cdn1.fahasa.com/media/catalog/product/i/m/image_239651.jpg')");

        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('Đắc Nhân Tâm', 'Dale Carnegie', 'Tâm lý – kỹ năng', 'Cuốn sách kinh điển về nghệ thuật giao tiếp.', 120000, 18, 'https://tiki.vn/blog/wp-content/uploads/2023/08/phan-4-dac-nhan-tam-1024x1024.jpg')");

        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('Muôn Kiếp Nhân Sinh', 'Nguyên Phong', 'Tâm linh', 'Giải thích luật nhân quả và bài học từ nhiều kiếp sống.', 150000, 12, 'https://pos.nvncdn.com/fd5775-40602/ps/20240508_aPnKpELWEt.png?v=1715157206')");

        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('Trí Tuệ Do Thái', 'Erik H. Erikson', 'Giáo dục – văn hóa', 'Khám phá bí quyết thành công của người Do Thái.', 180000, 10, 'https://pos.nvncdn.com/fd5775-40602/ps/20241120_qheOYNfxFq.png?v=1732086050')");

        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('Think And Grow Rich – 13 Nguyên Tắc Nghĩ Giàu Làm Giàu', 'Napoleon Hill', 'Kinh doanh – phát triển bản thân', 'Cẩm nang giúp thay đổi tư duy tài chính.', 140000, 15, 'https://pos.nvncdn.com/fd5775-40602/ps/20240507_sM636ASt69.png')");

        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('Tư Duy Nhanh Và Chậm', 'Daniel Kahneman', 'Khoa học – tâm lý', 'Giải thích cách hai hệ thống tư duy chi phối con người.', 180000, 14, 'https://salt.tikicdn.com/cache/w300/ts/product/7f/54/81/29cedc73e248c3bc710166595220a91a.jpg')");

        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('Một Đời Quản Trị', 'Howard Schultz', 'Kinh doanh', 'Bài học từ hành trình xây dựng Starbucks.', 165000, 8, 'https://cdn1.fahasa.com/media/catalog/product/8/9/8934974164623.jpg')");

        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('Chuyện Nghề Marketing', 'Trần Dzĩ Châu', 'Marketing', 'Những câu chuyện và kinh nghiệm marketing thực tế.', 130000, 30, 'https://media.vov.vn/sites/default/files/styles/large/public/2024-05/thanh_kinh_marketing_4.jpg')");

        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('Tôi Thấy Hoa Vàng Trên Cỏ Xanh', 'Nguyễn Nhật Ánh', 'Văn học thiếu nhi', 'Câu chuyện tuổi thơ trong trẻo đầy cảm xúc.', 95000, 20, 'https://media.metaisach.com/2025/05/toi-thay-hoa-vang-tren-co-xanh-ec153f52.jpeg')");

        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('Người Bán Hàng Vĩ Đại Nhất Thế Giới', 'Og Mandino', 'Kinh doanh', 'Bộ nguyên tắc sống và thành công vượt thời gian.', 99000, 18, 'https://pos.nvncdn.com/fd5775-40602/ps/20240507_yn8IoyAoXS.png?v=1715068569')");

        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('Thép Đã Tôi Thế Đấy', 'Nikolai Ostrovsky', 'Văn học', 'Câu chuyện phi thường về nghị lực sống và lý tưởng.', 110000, 11, 'https://nhasachminhthang.vn/UserFiles/files/Vanhocnuocngoai/5125678bb1a9990a3339e59602a2370b.jpg')");

        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('Sapiens: Lược Sử Loài Người', 'Yuval Noah Harari', 'Lịch sử – khoa học', 'Hành trình tiến hóa của nhân loại từ thuở sơ khai.', 199000, 9, 'https://pos.nvncdn.com/fd5775-40602/ps/20210408_3BPrLNJh3UvQBJWG17DkXxVu.png?v=1674520579')");

        db.execSQL("INSERT INTO books (title, author, category, description, price, stock, image) VALUES " +
                "('Cà Phê Cùng Tony', 'Tony Buổi Sáng', 'Kỹ năng sống', 'Bài học sống và tư duy tích cực qua những câu chuyện hài hước.', 115000, 22, 'https://product.hstatic.net/200000612211/product/ca-phe-cung-tony-2_cac0dccf23534a5eadbd63c85c1c9b69_master.jpg')");


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