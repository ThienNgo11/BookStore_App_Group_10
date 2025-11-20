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
import com.example.bookapp.utils.GridSpacingItemDecoration;
import com.example.bookapp.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvBooks;
    private BookAdapter adapter;
    private List<Book> bookList = new ArrayList<>();
    private List<Book> fullBookList = new ArrayList<>();
    private BookDAO bookDAO;
    private SessionManager sessionManager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bookDAO = new BookDAO(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupRecyclerView();
        setupAdapter();
        loadBooks();
        setupSearch();
        setupNavigation();

        // Xử lý selected tab từ intent
        handleSelectedTabFromIntent();

        String username = sessionManager.getUsername();
        if (username != null) {
            Toast.makeText(this, "Chào mừng: " + username, Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        rvBooks = findViewById(R.id.rvBooks);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
    }

    private void setupRecyclerView() {
        rvBooks.setLayoutManager(new GridLayoutManager(this, 2));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        rvBooks.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true));
    }

    private void setupAdapter() {
        adapter = new BookAdapter(this, bookList, new BookAdapter.OnBookClickListener() {
            @Override
            public void onBookClick(Book book) {
                Intent intent = new Intent(MainActivity.this, BookDetailActivity.class);
                intent.putExtra("BOOK_ID", book.getId());
                startActivity(intent);
            }
        });
        rvBooks.setAdapter(adapter);
    }

    private void setupSearch() {
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.trim().isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, SearchResultsActivity.class);
                    intent.putExtra("SEARCH_QUERY", query);
                    startActivity(intent);
                }
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void setupNavigation() {
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    // Đã ở trang home, chỉ cần load lại sách
                    loadBooks();
                    return true;
                } else if (itemId == R.id.nav_cart) {
                    Intent intent = new Intent(MainActivity.this, CartActivity.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_orders) {
                    Intent intent = new Intent(MainActivity.this, UserOrdersActivity.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }

    private void handleSelectedTabFromIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("SELECTED_TAB")) {
            int selectedTab = intent.getIntExtra("SELECTED_TAB", R.id.nav_home);
            bottomNavigationView.setSelectedItemId(selectedTab);

            // Clear extra để lần sau không bị ảnh hưởng
            getIntent().removeExtra("SELECTED_TAB");
        } else {
            // Mặc định chọn tab Home
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    // Override onNewIntent để xử lý khi activity được resume
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleSelectedTabFromIntent();
    }

    // Xử lý khi quay lại MainActivity từ các activity khác
    @Override
    protected void onResume() {
        super.onResume();
        // Đảm bảo tab Home luôn được chọn khi quay lại
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    private void loadBooks() {
        bookList.clear();
        fullBookList.clear();
        bookList.addAll(bookDAO.getAllBooks());
        fullBookList.addAll(bookList);
        adapter.notifyDataSetChanged();
    }
}