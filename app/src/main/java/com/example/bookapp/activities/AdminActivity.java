package com.example.bookapp.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookapp.R;
import com.example.bookapp.adapters.BookAdapter;
import com.example.bookapp.database.BookDAO;
import com.example.bookapp.database.OrderDAO;
import com.example.bookapp.database.UserDAO;
import com.example.bookapp.models.Book;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView rvBooks;
    private BookAdapter adapter;
    private List<Book> bookList = new ArrayList<>();
    private List<Book> fullBookList = new ArrayList<>();
    private BookDAO bookDAO;
    private UserDAO userDAO;
    private OrderDAO orderDAO;
    private TextView tvTotalBooks, tvTotalUsers, tvTotalOrders, tvTotalRevenue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        bookDAO = new BookDAO(this);
        userDAO = new UserDAO(this);
        orderDAO = new OrderDAO(this);

        // Init views
        tvTotalBooks = findViewById(R.id.tvTotalBooks);
        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvTotalOrders = findViewById(R.id.tvTotalOrders);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        rvBooks = findViewById(R.id.rvBooks);
        rvBooks.setLayoutManager(new GridLayoutManager(this, 2));

        // Khởi tạo adapter chỉ một lần, với override cho long click (sửa/xóa)
        adapter = new BookAdapter(this, bookList) {
            @Override
            public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
                holder.itemView.setOnLongClickListener(v -> {
                    showEditDeleteDialog(bookList.get(position));
                    return true;
                });
            }
        };
        rvBooks.setAdapter(adapter);

        loadStatistics(); // Load thống kê
        loadBooks(); // Load sách

        // Search
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

        // FAB add book
        FloatingActionButton fabAddBook = findViewById(R.id.fabAddBook);
        fabAddBook.setOnClickListener(v -> showAddBookDialog());

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_books) {
                loadBooks(); // Reload sách
                return true;
            } else if (itemId == R.id.nav_users) {
                Toast.makeText(this, "Quản lý User (Chưa implement)", Toast.LENGTH_SHORT).show();
                // Intent to UsersActivity
                return true;
            } else if (itemId == R.id.nav_orders) {
                Toast.makeText(this, "Quản lý Đơn hàng (Chưa implement)", Toast.LENGTH_SHORT).show();
                // Intent to OrdersActivity
                return true;
            } else if (itemId == R.id.nav_profile) {
                Intent intent = new Intent(AdminActivity.this, ProfileActivity.class);
                intent.putExtra("USERNAME", getIntent().getStringExtra("USERNAME")); // Truyền username
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void loadStatistics() {
        tvTotalBooks.setText("Tổng sách: " + bookDAO.getTotalBooks());
        tvTotalUsers.setText("Tổng user: " + userDAO.getTotalUsers());
        tvTotalOrders.setText("Tổng đơn hàng: " + orderDAO.getTotalOrders());
        tvTotalRevenue.setText(String.format("Tổng doanh thu: %,.0f đ", orderDAO.getTotalRevenue()));
    }

    private void loadBooks() {
        bookList.clear();
        fullBookList.clear();
        bookList.addAll(bookDAO.getAllBooks());
        fullBookList.addAll(bookList);
        adapter.notifyDataSetChanged();
        if (bookList.isEmpty()) {
            Toast.makeText(this, "Chưa có sách nào", Toast.LENGTH_SHORT).show();
        }
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

    // Dialog thêm sách
    private void showAddBookDialog() {
        try {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_add_edit_book);
            dialog.setTitle("Thêm sách");

            EditText etTitle = dialog.findViewById(R.id.etTitle);
            EditText etAuthor = dialog.findViewById(R.id.etAuthor);
            EditText etCategory = dialog.findViewById(R.id.etCategory);
            EditText etDescription = dialog.findViewById(R.id.etDescription);
            EditText etPrice = dialog.findViewById(R.id.etPrice);
            EditText etStock = dialog.findViewById(R.id.etStock);
            EditText etImage = dialog.findViewById(R.id.etImage);
            Button btnSave = dialog.findViewById(R.id.btnSave);

            btnSave.setOnClickListener(v -> {
                if (validateInputs(etTitle, etAuthor, etPrice, etStock)) {
                    Book newBook = new Book(0, etTitle.getText().toString(), etAuthor.getText().toString(),
                            etCategory.getText().toString(), etDescription.getText().toString(),
                            Double.parseDouble(etPrice.getText().toString()),
                            Integer.parseInt(etStock.getText().toString()), etImage.getText().toString());
                    bookDAO.insertBook(newBook);
                    Toast.makeText(this, "Thêm sách thành công", Toast.LENGTH_SHORT).show();
                    loadBooks();
                    loadStatistics();
                    dialog.dismiss();
                }
            });

            dialog.show();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi dialog thêm sách: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Dialog edit/delete sách
    private void showEditDeleteDialog(Book book) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Chọn hành động cho: " + book.getTitle())
                .setPositiveButton("Sửa", (dialog, which) -> showEditBookDialog(book))
                .setNegativeButton("Xóa", (dialog, which) -> {
                    bookDAO.deleteBook(book.getId());
                    Toast.makeText(this, "Xóa sách thành công", Toast.LENGTH_SHORT).show();
                    loadBooks();
                    loadStatistics();
                })
                .setNeutralButton("Hủy", null)
                .show();
    }

    private void showEditBookDialog(Book book) {
        try {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_add_edit_book);
            dialog.setTitle("Sửa sách");

            EditText etTitle = dialog.findViewById(R.id.etTitle);
            etTitle.setText(book.getTitle());
            EditText etAuthor = dialog.findViewById(R.id.etAuthor);
            etAuthor.setText(book.getAuthor());
            EditText etCategory = dialog.findViewById(R.id.etCategory);
            etCategory.setText(book.getCategory());
            EditText etDescription = dialog.findViewById(R.id.etDescription);
            etDescription.setText(book.getDescription());
            EditText etPrice = dialog.findViewById(R.id.etPrice);
            etPrice.setText(String.valueOf(book.getPrice()));
            EditText etStock = dialog.findViewById(R.id.etStock);
            etStock.setText(String.valueOf(book.getStock()));
            EditText etImage = dialog.findViewById(R.id.etImage);
            etImage.setText(book.getImage());
            Button btnSave = dialog.findViewById(R.id.btnSave);

            btnSave.setOnClickListener(v -> {
                if (validateInputs(etTitle, etAuthor, etPrice, etStock)) {
                    book.setTitle(etTitle.getText().toString());
                    book.setAuthor(etAuthor.getText().toString());
                    book.setCategory(etCategory.getText().toString());
                    book.setDescription(etDescription.getText().toString());
                    book.setPrice(Double.parseDouble(etPrice.getText().toString()));
                    book.setStock(Integer.parseInt(etStock.getText().toString()));
                    book.setImage(etImage.getText().toString());
                    bookDAO.updateBook(book);
                    Toast.makeText(this, "Sửa sách thành công", Toast.LENGTH_SHORT).show();
                    loadBooks();
                    dialog.dismiss();
                }
            });

            dialog.show();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi dialog sửa sách: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateInputs(EditText... editTexts) {
        for (EditText et : editTexts) {
            if (TextUtils.isEmpty(et.getText().toString())) {
                et.setError("Không được để trống");
                return false;
            }
        }
        return true;
    }
}