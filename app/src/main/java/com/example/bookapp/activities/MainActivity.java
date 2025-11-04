package com.example.bookapp.activities;

import android.content.Intent;
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
    private List<Book> fullBookList = new ArrayList<>();
    private BookDAO bookDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bookDAO = new BookDAO(this);
        rvBooks = findViewById(R.id.rvBooks);
        rvBooks.setLayoutManager(new GridLayoutManager(this, 2));

        // Khởi tạo Adapter ở đây để tránh lỗi NullPointerException khi gọi filterBooks/loadBooks
        setupAdapter();
        loadBooks();

        SearchView searchView = findViewById(R.id.searchView);
        // --- THAY ĐỔI LOGIC TÌM KIẾM Ở ĐÂY ---
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Khi người dùng nhấn Enter hoặc nút tìm kiếm
                if (query != null && !query.trim().isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, SearchResultsActivity.class);
                    intent.putExtra("SEARCH_QUERY", query); // Gửi từ khóa tìm kiếm
                    startActivity(intent);
                }
                searchView.clearFocus(); // Ẩn bàn phím
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Không làm gì khi người dùng đang gõ
                return false;
            }
        });

        String username = getIntent().getStringExtra("USERNAME");
        if (username != null) {
            Toast.makeText(this, "Chào mừng: " + username, Toast.LENGTH_SHORT).show();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    loadBooks();
                    return true;
                } else if (itemId == R.id.nav_cart) {
                    Toast.makeText(MainActivity.this, "Giỏ hàng (Chưa implement)", Toast.LENGTH_SHORT).show();
                    // Intent to CartActivity
                    return true;
                } else if (itemId == R.id.nav_orders) {
                    Toast.makeText(MainActivity.this, "Đơn hàng (Chưa implement)", Toast.LENGTH_SHORT).show();
                    // Intent to OrdersActivity
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    intent.putExtra("USERNAME", getIntent().getStringExtra("USERNAME")); // Truyền username
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }

    private void setupAdapter() {
        // Khởi tạo Adapter với một listener trống hoặc có chức năng mặc định (ví dụ: xem chi tiết)
        adapter = new BookAdapter(this, bookList, new BookAdapter.OnBookClickListener() {
            @Override
            public void onBookClick(Book book) {
                // TODO: Triển khai chức năng cho người dùng/khách hàng
                Toast.makeText(MainActivity.this, "Xem chi tiết sách: " + book.getTitle(), Toast.LENGTH_SHORT).show();
                // Ví dụ: startActivity(new Intent(MainActivity.this, BookDetailActivity.class).putExtra("BOOK_ID", book.getId()));
            }
        });
        rvBooks.setAdapter(adapter);
    }

    private void loadBooks() {
        bookList.clear();
        fullBookList.clear();
        bookList.addAll(bookDAO.getAllBooks());
        fullBookList.addAll(bookList);
        // Không cần khởi tạo lại adapter ở đây.
        adapter.notifyDataSetChanged();
    }

//    private void filterBooks(String query) {
//        bookList.clear();
//        if (query.isEmpty()) {
//            bookList.addAll(fullBookList);
//        } else {
//            for (Book book : fullBookList) {
//                if (book.getTitle().toLowerCase().contains(query.toLowerCase())) {
//                    bookList.add(book);
//                }
//            }
//        }
//        adapter.notifyDataSetChanged();
//    }
}