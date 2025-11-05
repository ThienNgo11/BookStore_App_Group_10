package com.example.bookapp.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.bookapp.database.DatabaseHelper;
import com.example.bookapp.models.Book;

import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    private DatabaseHelper dbHelper;

    public BookDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM books", null);
        if (cursor.moveToFirst()) {
            do {
                Book book = new Book(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getDouble(5),
                        cursor.getInt(6),
                        cursor.getString(7)
                );
                books.add(book);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return books;
    }

    // =================================================================
    // === PHƯƠNG THỨC MỚI CHO TÌM KIẾM - THÊM VÀO ĐÂY ===
    // =================================================================
    public List<Book> searchBooks(String keyword, String category, String priceRange, String sortOption) {
        List<Book> books = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuilder sqlQuery = new StringBuilder("SELECT * FROM books WHERE (");
        sqlQuery.append("title LIKE ? COLLATE NOCASE ");
        sqlQuery.append("OR author LIKE ? COLLATE NOCASE ");
        sqlQuery.append("OR category LIKE ? COLLATE NOCASE ");
        sqlQuery.append("OR description LIKE ? COLLATE NOCASE)");

        List<String> argsList = new ArrayList<>();
        String pattern = "%" + keyword + "%";
        for (int i = 0; i < 4; i++) argsList.add(pattern);

        // === Lọc theo category (nếu có) ===
        if (category != null && !category.equalsIgnoreCase("Tất cả")) {
            sqlQuery.append(" AND category = ?");
            argsList.add(category);
        }

        // === Lọc theo khoảng giá (nếu có) ===
        if (priceRange != null) {
            switch (priceRange) {
                case "Dưới 100k":
                    sqlQuery.append(" AND price < 100000");
                    break;
                case "100k - 200k":
                    sqlQuery.append(" AND price BETWEEN 100000 AND 200000");
                    break;
                case "Trên 200k":
                    sqlQuery.append(" AND price > 200000");
                    break;
            }
        }

        // === Sắp xếp ===
        if (sortOption != null) {
            switch (sortOption) {
                case "Giá tăng dần":
                    sqlQuery.append(" ORDER BY price ASC");
                    break;
                case "Giá giảm dần":
                    sqlQuery.append(" ORDER BY price DESC");
                    break;
                case "Tên (A-Z)":
                    sqlQuery.append(" ORDER BY title COLLATE NOCASE ASC");
                    break;
            }
        }

        Cursor cursor = db.rawQuery(sqlQuery.toString(), argsList.toArray(new String[0]));

        if (cursor.moveToFirst()) {
            do {
                Book book = new Book(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("author")),
                        cursor.getString(cursor.getColumnIndexOrThrow("category")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("stock")),
                        cursor.getString(cursor.getColumnIndexOrThrow("image"))
                );
                books.add(book);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return books;
    }



    // ===========================================

    public void insertBook(Book book) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", book.getTitle());
        values.put("author", book.getAuthor());
        values.put("category", book.getCategory());
        values.put("description", book.getDescription());
        values.put("price", book.getPrice());
        values.put("stock", book.getStock());
        values.put("image", book.getImage());
        db.insert("books", null, values);
    }

    public void updateBook(Book book) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", book.getTitle());
        values.put("author", book.getAuthor());
        values.put("category", book.getCategory());
        values.put("description", book.getDescription());
        values.put("price", book.getPrice());
        values.put("stock", book.getStock());
        values.put("image", book.getImage());
        db.update("books", values, "id = ?", new String[]{String.valueOf(book.getId())});
    }

    public void deleteBook(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("books", "id = ?", new String[]{String.valueOf(id)});
    }

    public int getTotalBooks() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM books", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }
}