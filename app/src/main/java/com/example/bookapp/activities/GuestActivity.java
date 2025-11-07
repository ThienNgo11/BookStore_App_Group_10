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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class GuestActivity extends AppCompatActivity {

    private RecyclerView rvBooks;
    private BookAdapter adapter;
    private List<Book> bookList = new ArrayList<>();
    private BookDAO bookDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Dùng chung layout với Main
        setContentView(R.layout.activity_main);

        bookDAO = new BookDAO(this);

        rvBooks = findViewById(R.id.rvBooks);
        rvBooks.setLayoutManager(new GridLayoutManager(this, 2));

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        rvBooks.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true));

        setupAdapter();
        loadBooks();

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.trim().isEmpty()) {
                    Intent intent = new Intent(GuestActivity.this, SearchResultsActivity.class);
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

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    loadBooks();
                    return true;
                }

                // Chặn 3 tính năng
                if (id == R.id.nav_cart || id == R.id.nav_orders || id == R.id.nav_profile) {
                    Toast.makeText(GuestActivity.this, "Bạn cần đăng nhập", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(GuestActivity.this, LoginActivity.class));
                    finish();
                    return true;
                }

                return false;
            }
        });
    }

    private void setupAdapter() {
        adapter = new BookAdapter(this, bookList, new BookAdapter.OnBookClickListener() {
            @Override
            public void onBookClick(Book book) {
                Intent intent = new Intent(GuestActivity.this, BookDetailActivity.class);
                intent.putExtra("BOOK_ID", book.getId());
                intent.putExtra("GUEST", true); // gửi flag cho BookDetailActivity
                startActivity(intent);
            }
        });
        rvBooks.setAdapter(adapter);
    }

    private void loadBooks() {
        bookList.clear();
        bookList.addAll(bookDAO.getAllBooks());
        adapter.notifyDataSetChanged();
    }
}
