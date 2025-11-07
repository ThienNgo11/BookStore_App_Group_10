package com.example.bookapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
// MỚI: Thêm import cho SearchView
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookapp.R;
import com.example.bookapp.adapters.BookAdapter;
import com.example.bookapp.database.BookDAO;
import com.example.bookapp.models.Book;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    private RecyclerView rvSearchResults;
    private BookAdapter adapter;
    // NÂNG CẤP: Thay tvNoResults bằng layout
    private View layoutNoResults;
    private TextView tvNoResultsTitle;

    private TextView tvActiveFilters;
    private MaterialButton btnOpenFilter;
    private BookDAO bookDAO;
    private String query;

    // MỚI: Thêm SearchView
    private SearchView searchView;

    // Các biến để lưu trữ trạng thái lọc hiện tại
    private String currentCategory = "Tất cả";
    private String currentPriceRange = "Tất cả";
    private String currentSortOption = "Mặc định";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rvSearchResults = findViewById(R.id.rvSearchResults);
        // NÂNG CẤP: Ánh xạ layout mới
        layoutNoResults = findViewById(R.id.layoutNoResults);
        tvNoResultsTitle = findViewById(R.id.tvNoResultsTitle);

        btnOpenFilter = findViewById(R.id.btnOpenFilter);
        tvActiveFilters = findViewById(R.id.tvActiveFilters);

        // MỚI: Ánh xạ SearchView
        searchView = findViewById(R.id.searchViewResults);

        bookDAO = new BookDAO(this);
        query = getIntent().getStringExtra("SEARCH_QUERY");

        if (query != null && !query.isEmpty()) {
            setTitle("Kết quả cho: '" + query + "'");
            searchView.setQuery(query, false); // Hiển thị query lên SearchView
            loadResults(); // Tải kết quả lần đầu
        } else {
            setTitle("Tìm kiếm");
            layoutNoResults.setVisibility(View.VISIBLE); // Hiển thị nếu không có query
        }

        btnOpenFilter.setOnClickListener(v -> showFilterBottomSheet());

        // MỚI: Thêm logic cho SearchView
        setupSearchView();

        updateActiveFiltersText();
    }

    // MỚI: Tách logic của SearchView ra một hàm riêng
    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newQuery) {
                if (newQuery != null && !newQuery.trim().isEmpty()) {
                    // Khi người dùng tìm kiếm lại, cập nhật query và tải lại kết quả
                    query = newQuery;
                    setTitle("Kết quả cho: '" + query + "'");
                    loadResults(); // Tải lại kết quả với query mới
                }
                searchView.clearFocus(); // Ẩn bàn phím
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Không làm gì khi đang gõ
                return false;
            }
        });
    }

    private void showFilterBottomSheet() {
        // ... (Giữ nguyên code của hàm này như bạn đã có) ...
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View sheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_filter, null);

        ChipGroup chipGroupCategory = sheetView.findViewById(R.id.chipGroupCategory);
        ChipGroup chipGroupPrice = sheetView.findViewById(R.id.chipGroupPrice);
        ChipGroup chipGroupSort = sheetView.findViewById(R.id.chipGroupSort);
        MaterialButton btnApplyFilter = sheetView.findViewById(R.id.btnApplyFilter);
        View ivCloseSheet = sheetView.findViewById(R.id.ivCloseSheet);

        // (Bạn có thể thêm logic để set chip_checked ở đây)

        ivCloseSheet.setOnClickListener(v -> bottomSheetDialog.dismiss());

        btnApplyFilter.setOnClickListener(v -> {
            int selectedCategoryId = chipGroupCategory.getCheckedChipId();
            if (selectedCategoryId != View.NO_ID) {
                Chip selectedChip = sheetView.findViewById(selectedCategoryId);
                currentCategory = selectedChip.getText().toString();
            }

            int selectedPriceId = chipGroupPrice.getCheckedChipId();
            if (selectedPriceId != View.NO_ID) {
                Chip selectedChip = sheetView.findViewById(selectedPriceId);
                currentPriceRange = selectedChip.getText().toString();
            }

            int selectedSortId = chipGroupSort.getCheckedChipId();
            if (selectedSortId != View.NO_ID) {
                Chip selectedChip = sheetView.findViewById(selectedSortId);
                currentSortOption = selectedChip.getText().toString();
            }

            loadResults();
            updateActiveFiltersText();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }


    private void updateActiveFiltersText() {
        // ... (Giê nguyên code của hàm này) ...
        StringBuilder filtersText = new StringBuilder();
        if (!currentCategory.equals("Tất cả")) {
            filtersText.append(currentCategory).append(" | ");
        }
        if (!currentPriceRange.equals("Tất cả")) {
            filtersText.append(currentPriceRange).append(" | ");
        }
        if (!currentSortOption.equals("Mặc định")) {
            filtersText.append(currentSortOption).append(" | ");
        }

        if (filtersText.length() == 0) {
            tvActiveFilters.setText("Tất cả");
        } else {
            tvActiveFilters.setText(filtersText.substring(0, filtersText.length() - 3));
        }
    }

    // NÂNG CẤP: Cập nhật hàm loadResults
    private void loadResults() {
        List<Book> books = bookDAO.searchBooks(query, currentCategory, currentPriceRange, currentSortOption);

        if (books.isEmpty()) {
            // Khi không có sách
            rvSearchResults.setVisibility(View.GONE); // Ẩn danh sách
            layoutNoResults.setVisibility(View.VISIBLE); // Hiện layout thông báo

            // Cập nhật text để thân thiện hơn
            tvNoResultsTitle.setText("Không tìm thấy '" + query + "'");
        } else {
            // Khi có sách
            rvSearchResults.setVisibility(View.VISIBLE); // Hiện danh sách
            layoutNoResults.setVisibility(View.GONE); // Ẩn layout thông báo

            rvSearchResults.setLayoutManager(new GridLayoutManager(this, 2));
            adapter = new BookAdapter(this, books, book -> {
                Intent intent = new Intent(SearchResultsActivity.this, BookDetailActivity.class);
                intent.putExtra("BOOK_ID", book.getId());
                startActivity(intent);
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