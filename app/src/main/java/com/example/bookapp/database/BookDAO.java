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
    public List<Book> searchBooks(String keyword, String sortOption) {
        List<Book> books = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Câu SQL linh hoạt
        String sqlQuery = "SELECT * FROM books WHERE " +
                "title LIKE ? OR author LIKE ? OR category LIKE ? OR description LIKE ?";

        // Thêm phần sắp xếp
        if (sortOption != null) {
            if (sortOption.equals("price_asc")) sqlQuery += " ORDER BY price ASC";
            else if (sortOption.equals("price_desc")) sqlQuery += " ORDER BY price DESC";
            else if (sortOption.equals("title_asc")) sqlQuery += " ORDER BY title ASC";
        }

        String pattern = "%" + keyword + "%";
        String[] args = new String[]{pattern, pattern, pattern, pattern};

        Cursor cursor = db.rawQuery(sqlQuery, args);

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