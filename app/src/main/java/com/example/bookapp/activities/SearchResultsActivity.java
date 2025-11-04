package com.example.bookapp.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookapp.R;
import com.example.bookapp.adapters.BookAdapter;
import com.example.bookapp.database.BookDAO;
import com.example.bookapp.models.Book;

import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    private RecyclerView rvSearchResults;
    private BookAdapter adapter;
    private List<Book> searchResultList;
    private BookDAO bookDAO;
    private TextView tvNoResults;
    private Spinner spinnerSort;

    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rvSearchResults = findViewById(R.id.rvSearchResults);
        tvNoResults = findViewById(R.id.tvNoResults);
        spinnerSort = findViewById(R.id.spinnerSort);

        bookDAO = new BookDAO(this);
        query = getIntent().getStringExtra("SEARCH_QUERY");

        if (query != null && !query.isEmpty()) {
            setTitle("Kết quả cho: '" + query + "'");
            loadResults(null); // Lần đầu chưa chọn sắp xếp
        } else {
            setTitle("Tìm kiếm");
            tvNoResults.setVisibility(View.VISIBLE);
        }

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sortOption = null;
                switch (position) {
                    case 1: sortOption = "price_asc"; break;
                    case 2: sortOption = "price_desc"; break;
                    case 3: sortOption = "title_asc"; break;
                }
                loadResults(sortOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadResults(String sortOption) {
        searchResultList = bookDAO.searchBooks(query, sortOption);

        if (searchResultList.isEmpty()) {
            rvSearchResults.setVisibility(View.GONE);
            tvNoResults.setVisibility(View.VISIBLE);
        } else {
            rvSearchResults.setVisibility(View.VISIBLE);
            tvNoResults.setVisibility(View.GONE);
            rvSearchResults.setLayoutManager(new GridLayoutManager(this, 2));

            // SỬA Ở ĐÂY: Thêm listener cho BookAdapter
            adapter = new BookAdapter(this, searchResultList, new BookAdapter.OnBookClickListener() {
                @Override
                public void onBookClick(Book book) {
                    // Trong SearchResultsActivity, có thể hiển thị chi tiết sách
                    // hoặc thêm vào giỏ hàng tùy theo nhu cầu
                    Toast.makeText(SearchResultsActivity.this,
                            "Xem chi tiết: " + book.getTitle(), Toast.LENGTH_SHORT).show();
                    // Có thể mở Activity chi tiết sách ở đây
                }
            });
            rvSearchResults.setAdapter(adapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}