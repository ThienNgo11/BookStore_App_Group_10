package com.example.bookapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.bookapp.R;
import com.example.bookapp.database.BookDAO;
import com.example.bookapp.database.CartDAO;
import com.example.bookapp.models.Book;
import com.example.bookapp.models.CartItem;
import com.example.bookapp.utils.SessionManager;

import java.util.ArrayList;

public class BookDetailActivity extends AppCompatActivity {

    private ImageView ivBookImage;
    private TextView tvTitle, tvAuthor, tvCategory, tvPrice, tvStock, tvDescription;
    private Button btnAddToCart, btnBuyNow;
    private ImageButton btnBack, btnShare, btnFavorite;

    // View số lượng
    private ImageButton btnMinus, btnPlus;
    private TextView tvQuantity;
    private int currentQuantity = 1;

    private BookDAO bookDAO;
    private Book book;
    private int bookId;

    private SessionManager sessionManager;
    private CartDAO cartDAO;

    // Biến kiểm tra xem có phải mở từ GuestActivity không
    private boolean isGuestMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        // 1. Nhận cờ GUEST từ Intent (QUAN TRỌNG)
        // Nếu bên GuestActivity truyền sang true thì biến này sẽ là true
        isGuestMode = getIntent().getBooleanExtra("GUEST", false);

        sessionManager = new SessionManager(this);
        cartDAO = new CartDAO(this);
        bookDAO = new BookDAO(this);

        initViews();

        bookId = getIntent().getIntExtra("BOOK_ID", -1);
        if (bookId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy sách", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadBookDetails();
        setupButtons();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnShare = findViewById(R.id.btnShare);
        btnFavorite = findViewById(R.id.btnFavorite);

        ivBookImage = findViewById(R.id.ivBookImage);
        tvTitle = findViewById(R.id.tvTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvCategory = findViewById(R.id.tvCategory);
        tvPrice = findViewById(R.id.tvPrice);
        tvStock = findViewById(R.id.tvStock);
        tvDescription = findViewById(R.id.tvDescription);

        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        tvQuantity = findViewById(R.id.tvQuantity);

        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBuyNow = findViewById(R.id.btnBuyNow);
    }

    private void loadBookDetails() {
        book = bookDAO.getBookById(bookId);

        if (book == null) {
            Toast.makeText(this, "Không tìm thấy sách", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvTitle.setText(book.getTitle());
        tvAuthor.setText(book.getAuthor());
        tvCategory.setText(book.getCategory());
        tvPrice.setText(String.format("%,.0f đ", book.getPrice()));

        if (book.getStock() > 0) {
            tvStock.setText("Còn hàng: " + book.getStock() + " cuốn");
            tvStock.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            btnAddToCart.setEnabled(true);
            btnBuyNow.setEnabled(true);
        } else {
            tvStock.setText("Hết hàng");
            tvStock.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            btnAddToCart.setEnabled(false);
            btnBuyNow.setEnabled(false);
        }

        tvDescription.setText(book.getDescription() != null ? book.getDescription() : "Chưa có mô tả");

        Glide.with(this)
                .load(book.getImage())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(ivBookImage);
    }

    // Hàm kiểm tra Login tập trung
    private boolean checkLoginRequirement() {
        // 1. Ưu tiên kiểm tra cờ Guest Mode từ Intent trước
        if (isGuestMode) {
            Toast.makeText(this, "Bạn đang xem với tư cách Khách. Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(BookDetailActivity.this, LoginActivity.class);
            startActivity(intent);
            return false; // Chặn lại
        }

        // 2. Kiểm tra Session (để chắc chắn User hợp lệ)
        if (!sessionManager.isLoggedIn() || sessionManager.getUserId() <= 0) {
            Toast.makeText(this, "Vui lòng đăng nhập để thực hiện", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(BookDetailActivity.this, LoginActivity.class);
            startActivity(intent);
            return false; // Chặn lại
        }

        return true; // Cho phép đi tiếp
    }

    private void setupButtons() {
        btnBack.setOnClickListener(v -> finish());

        btnShare.setOnClickListener(v -> Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show());
        btnFavorite.setOnClickListener(v -> Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show());

        btnMinus.setOnClickListener(v -> {
            if (currentQuantity > 1) {
                currentQuantity--;
                tvQuantity.setText(String.valueOf(currentQuantity));
            }
        });

        btnPlus.setOnClickListener(v -> {
            if (book != null && currentQuantity < book.getStock()) {
                currentQuantity++;
                tvQuantity.setText(String.valueOf(currentQuantity));
            } else {
                Toast.makeText(this, "Đã đạt giới hạn tồn kho", Toast.LENGTH_SHORT).show();
            }
        });

        // ============================================
        // NÚT THÊM VÀO GIỎ
        // ============================================
        btnAddToCart.setOnClickListener(v -> {
            // GỌI HÀM KIỂM TRA (Nếu false nghĩa là chưa login -> return luôn)
            if (!checkLoginRequirement()) {
                return;
            }

            // Code chạy xuống đây nghĩa là Đã Login hợp lệ
            int userId = sessionManager.getUserId();

            if (book.getStock() <= 0) {
                Toast.makeText(this, "Sách đã hết hàng", Toast.LENGTH_SHORT).show();
                return;
            }

            CartItem existing = cartDAO.getCartItem(userId, book.getId());

            if (existing != null) {
                int newTotalQuantity = existing.getQuantity() + currentQuantity;
                if (newTotalQuantity > book.getStock()) {
                    Toast.makeText(this, "Tổng số lượng (" + newTotalQuantity + ") vượt quá tồn kho", Toast.LENGTH_SHORT).show();
                    return;
                }
                cartDAO.updateQuantity(existing.getId(), newTotalQuantity);
                Toast.makeText(this, "Đã cập nhật số lượng trong giỏ", Toast.LENGTH_SHORT).show();
            } else {
                boolean ok = cartDAO.addToCart(userId, book.getId(), currentQuantity);
                if (ok) Toast.makeText(this, "Đã thêm " + currentQuantity + " cuốn vào giỏ", Toast.LENGTH_SHORT).show();
                else Toast.makeText(this, "Lỗi khi thêm giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });

        // ==========================================
        // NÚT MUA NGAY
        // ==========================================
        btnBuyNow.setOnClickListener(v -> {
            // GỌI HÀM KIỂM TRA
            if (!checkLoginRequirement()) {
                return;
            }

            int userId = sessionManager.getUserId();

            if (book.getStock() <= 0) {
                Toast.makeText(this, "Sách đã hết hàng", Toast.LENGTH_SHORT).show();
                return;
            }

            CartItem temp = new CartItem(
                    0,
                    userId,
                    book.getId(),
                    book.getTitle(),
                    book.getImage(),
                    book.getPrice(),
                    currentQuantity,
                    book.getStock()
            );

            Intent intent = new Intent(this, CheckoutActivity.class);
            ArrayList<CartItem> list = new ArrayList<>();
            list.add(temp);
            intent.putExtra("SELECTED_ITEMS", list);
            startActivity(intent);
        });
    }
}