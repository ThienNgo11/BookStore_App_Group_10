package com.example.bookapp.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookapp.R;
import com.example.bookapp.adapters.BookAdapter;
import com.example.bookapp.database.BookDAO;
import com.example.bookapp.models.Book;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvBooks;
    private BookAdapter adapter;
    private List<Book> bookList = new ArrayList<>();
    private List<Book> fullBookList = new ArrayList<>(); // Để lưu danh sách gốc cho search
    private BookDAO bookDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bookDAO = new BookDAO(this);
        rvBooks = findViewById(R.id.rvBooks);
        rvBooks.setLayoutManager(new GridLayoutManager(this, 2)); // Grid 2 cột

        loadBooks(); // Load all books

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterBooks(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBooks(newText);
                return false;
            }
        });

        // Nếu cần welcome message, có thể thêm TextView riêng hoặc Toast
        String username = getIntent().getStringExtra("USERNAME");
        if (username != null) {
            Toast.makeText(this, "Chào mừng: " + username, Toast.LENGTH_SHORT).show();
        }

        // Set up Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    // Đã ở home, không làm gì hoặc reload
                    loadBooks();
                    return true;
                } else if (itemId == R.id.nav_cart) {
                    Toast.makeText(MainActivity.this, "Giỏ hàng (Chưa implement)", Toast.LENGTH_SHORT).show();
                    // Intent to CartActivity or switch fragment
                    return true;
                } else if (itemId == R.id.nav_orders) {
                    Toast.makeText(MainActivity.this, "Đơn hàng (Chưa implement)", Toast.LENGTH_SHORT).show();
                    // Intent to OrdersActivity
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    Toast.makeText(MainActivity.this, "Tài khoản (Chưa implement)", Toast.LENGTH_SHORT).show();
                    // Intent to ProfileActivity
                    return true;
                }
                return false;
            }
        });
    }

    private void loadBooks() {
        bookList.clear();
        fullBookList.clear();
        bookList.addAll(bookDAO.getAllBooks());
        fullBookList.addAll(bookList);
        adapter = new BookAdapter(this, bookList);
        rvBooks.setAdapter(adapter);
    }

    private void filterBooks(String query) {
        bookList.clear();
        if (query.isEmpty()) {
            bookList.addAll(fullBookList);
        } else {
            for (Book book : fullBookList) {
                if (book.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    bookList.add(book);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}