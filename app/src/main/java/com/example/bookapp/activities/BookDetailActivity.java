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

    // MỚI: Khai báo các view cho phần số lượng
    private ImageButton btnMinus, btnPlus;
    private TextView tvQuantity;
    private int currentQuantity = 1; // Mặc định là 1

    private BookDAO bookDAO;
    private Book book;
    private int bookId;

    private SessionManager sessionManager;
    private CartDAO cartDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        // Khởi tạo views
        initViews();

        // Nhận BOOK_ID từ Intent
        bookId = getIntent().getIntExtra("BOOK_ID", -1);
        if (bookId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy sách", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        sessionManager = new SessionManager(this);
        cartDAO = new CartDAO(this);
        // Load thông tin sách
        bookDAO = new BookDAO(this);
        loadBookDetails();

        // Setup buttons
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

        // MỚI: Ánh xạ view số lượng
        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        tvQuantity = findViewById(R.id.tvQuantity);

        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBuyNow = findViewById(R.id.btnBuyNow);
    }

    private void loadBookDetails() {
        // Lấy thông tin sách từ database
        book = bookDAO.getBookById(bookId);

        if (book == null) {
            Toast.makeText(this, "Không tìm thấy sách", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hiển thị thông tin
        tvTitle.setText(book.getTitle());
        tvAuthor.setText(book.getAuthor());
        tvCategory.setText(book.getCategory());
        tvPrice.setText(String.format("%,.0f đ", book.getPrice()));

        // Hiển thị tình trạng kho
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

        // Load ảnh
        Glide.with(this)
                .load(book.getImage())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(ivBookImage);
    }

    private void setupButtons() {
        // Nút back
        btnBack.setOnClickListener(v -> finish());

        // Nút share
        btnShare.setOnClickListener(v -> {
            Toast.makeText(this, "Chia sẻ sách (Chưa implement)", Toast.LENGTH_SHORT).show();
            // TODO: Implement share functionality
        });

        // Nút favorite
        btnFavorite.setOnClickListener(v -> {
            Toast.makeText(this, "Thêm vào yêu thích (Chưa implement)", Toast.LENGTH_SHORT).show();
            // TODO: Implement favorite functionality
        });

        // MỚI: Logic nút Giảm (-)
        btnMinus.setOnClickListener(v -> {
            if (currentQuantity > 1) {
                currentQuantity--;
                tvQuantity.setText(String.valueOf(currentQuantity));
            }
        });

        // MỚI: Logic nút Tăng (+)
        btnPlus.setOnClickListener(v -> {
            // Kiểm tra xem có vượt quá tồn kho không
            if (book != null && currentQuantity < book.getStock()) {
                currentQuantity++;
                tvQuantity.setText(String.valueOf(currentQuantity));
            } else {
                Toast.makeText(this, "Đã đạt giới hạn tồn kho", Toast.LENGTH_SHORT).show();
            }
        });

        btnAddToCart.setOnClickListener(v -> {

            int userId = sessionManager.getUserId();
            if (userId == -1) {
                Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                return;
            }

            if (book.getStock() <= 0) {
                Toast.makeText(this, "Sách đã hết hàng", Toast.LENGTH_SHORT).show();
                return;
            }

            CartItem existing = cartDAO.getCartItem(userId, book.getId());

            if (existing != null) {
                // Tính tổng số lượng mới = số lượng đang có trong giỏ + số lượng muốn thêm
                int newTotalQuantity = existing.getQuantity() + currentQuantity;

                // Kiểm tra xem tổng số lượng có vượt quá kho không
                if (newTotalQuantity > book.getStock()) {
                    Toast.makeText(this, "Tổng số lượng (" + newTotalQuantity + ") vượt quá tồn kho", Toast.LENGTH_SHORT).show();
                    return;
                }

                // SỬA: Dùng newTotalQuantity thay vì (existing.getQuantity() + 1)
                cartDAO.updateQuantity(existing.getId(), newTotalQuantity);
                Toast.makeText(this, "Đã thêm " + currentQuantity + " cuốn vào giỏ", Toast.LENGTH_SHORT).show();
            } else {
                // SỬA: Dùng currentQuantity thay vì số 1 cứng
                boolean ok = cartDAO.addToCart(userId, book.getId(), currentQuantity);

                if (ok) Toast.makeText(this, "Đã thêm " + currentQuantity + " cuốn vào giỏ", Toast.LENGTH_SHORT).show();
                else Toast.makeText(this, "Lỗi khi thêm giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });


        // ==========================
        // MUA NGAY (MẶC ĐỊNH 1 CUỐN)
        // ==========================
        btnBuyNow.setOnClickListener(v -> {

            int userId = sessionManager.getUserId();
            if (userId == -1) {
                Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                return;
            }

            if (book.getStock() <= 0) {
                Toast.makeText(this, "Sách đã hết hàng", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo CartItem tạm để truyền qua CheckoutActivity
            CartItem temp = new CartItem(
                    0,                 // id tạm
                    userId,
                    book.getId(),
                    book.getTitle(),
                    book.getImage(),
                    book.getPrice(),
                    currentQuantity,// Mua ngay = 1
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