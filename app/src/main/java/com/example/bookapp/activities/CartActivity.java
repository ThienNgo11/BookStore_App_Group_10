package com.example.bookapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookapp.R;
import com.example.bookapp.adapters.CartAdapter;
import com.example.bookapp.database.CartDAO;
import com.example.bookapp.models.CartItem;
import com.example.bookapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartItemListener {

    private RecyclerView rvCart;
    private CartAdapter adapter;
    private List<CartItem> cartItems = new ArrayList<>();
    private CartDAO cartDAO;
    private SessionManager sessionManager;

    private CheckBox cbSelectAll;
    private TextView tvTotalPrice, tvTotalItems, tvEmptyCart, tvDeleteSelected;
    private Button btnCheckout, btnBackToHome;
    private View emptyCartLayout, bottomBar;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // SỬ DỤNG SESSION MANAGER
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        if (userId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(CartActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
        cartDAO = new CartDAO(this);

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadCartItems();
    }

    private void initViews() {
        rvCart = findViewById(R.id.rvCart);
        cbSelectAll = findViewById(R.id.cbSelectAll);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvTotalItems = findViewById(R.id.tvTotalItems);
        btnCheckout = findViewById(R.id.btnCheckout);
        tvDeleteSelected = findViewById(R.id.tvDeleteSelected);
        emptyCartLayout = findViewById(R.id.emptyCartLayout);
        tvEmptyCart = findViewById(R.id.tvEmptyCart);
        bottomBar = findViewById(R.id.bottomBar);
        btnBackToHome = findViewById(R.id.btnBackToHome);

        // Checkbox chọn tất cả
        cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (adapter != null) {
                adapter.selectAll(isChecked);
                updateTotalPrice();
            }
        });

        // Nút thanh toán
        btnCheckout.setOnClickListener(v -> {
            List<CartItem> selectedItems = adapter.getSelectedItems();
            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn sản phẩm để thanh toán", Toast.LENGTH_SHORT).show();
                return;
            }
            showCheckoutDialog(selectedItems);
        });

        // Nút xóa
        tvDeleteSelected.setOnClickListener(v -> {
            List<CartItem> selectedItems = adapter.getSelectedItems();
            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn sản phẩm để xóa", Toast.LENGTH_SHORT).show();
                return;
            }
            showDeleteDialog(selectedItems);
        });

        // Nút quay về trang chủ khi giỏ hàng trống
        if (btnBackToHome != null) {
            btnBackToHome.setOnClickListener(v -> {
                goBackToMainActivity();
            });
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Giỏ hàng");
        }

        // SỬA LẠI: Gọi phương thức quay về MainActivity
        toolbar.setNavigationOnClickListener(v -> {
            goBackToMainActivity();
        });
    }

    // PHƯƠNG THỨC MỚI: Luôn quay về MainActivity an toàn
    private void goBackToMainActivity() {
        // Kiểm tra xem MainActivity có đang chạy trong stack không
        Intent intent = new Intent(this, MainActivity.class);

        // Sử dụng flags để đảm bảo MainActivity được đưa lên top
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

        // Kết thúc CartActivity
        finish();
    }


    private void setupRecyclerView() {
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(this, cartItems, this);
        rvCart.setAdapter(adapter);
    }

    private void loadCartItems() {
        cartItems.clear();
        cartItems.addAll(cartDAO.getCartItems(userId));
        adapter.notifyDataSetChanged();

        // Hiển thị empty state nếu giỏ hàng trống
        if (cartItems.isEmpty()) {
            emptyCartLayout.setVisibility(View.VISIBLE);
            rvCart.setVisibility(View.GONE);
            bottomBar.setVisibility(View.GONE);
            cbSelectAll.setVisibility(View.GONE);
        } else {
            emptyCartLayout.setVisibility(View.GONE);
            rvCart.setVisibility(View.VISIBLE);
            bottomBar.setVisibility(View.VISIBLE);
            cbSelectAll.setVisibility(View.VISIBLE);
        }

        updateTotalPrice();
    }

    private void updateTotalPrice() {
        if (adapter == null) return;

        List<CartItem> selectedItems = adapter.getSelectedItems();
        double totalPrice = 0;
        int totalItems = 0;

        for (CartItem item : selectedItems) {
            totalPrice += item.getPrice() * item.getQuantity();
            totalItems += item.getQuantity();
        }

        tvTotalPrice.setText(String.format("%,.0f đ", totalPrice));
        tvTotalItems.setText("Tổng cộng (" + totalItems + " sản phẩm):");

        // Cập nhật trạng thái checkbox "Chọn tất cả"
        cbSelectAll.setChecked(selectedItems.size() == cartItems.size() && !cartItems.isEmpty());
    }

    private void showCheckoutDialog(List<CartItem> selectedItems) {
        double totalPrice = 0;
        for (CartItem item : selectedItems) {
            totalPrice += item.getPrice() * item.getQuantity();
        }

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận đặt hàng")
                .setMessage("Tổng tiền: " + String.format("%,.0f đ", totalPrice) +
                        "\n\nBạn có chắc muốn thanh toán?")
                .setPositiveButton("Đặt hàng", (dialog, which) -> {
                    // Chuyển sang CheckoutActivity
                    Intent intent = new Intent(this, CheckoutActivity.class);
                    intent.putExtra("SELECTED_ITEMS", new ArrayList<>(selectedItems));
                    startActivity(intent);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showDeleteDialog(List<CartItem> selectedItems) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa " + selectedItems.size() + " sản phẩm?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    for (CartItem item : selectedItems) {
                        cartDAO.deleteCartItem(item.getId());
                    }
                    Toast.makeText(this, "Đã xóa " + selectedItems.size() + " sản phẩm", Toast.LENGTH_SHORT).show();
                    loadCartItems();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // Callbacks từ CartAdapter
    @Override
    public void onItemCheckedChanged() {
        updateTotalPrice();
    }

    @Override
    public void onQuantityChanged(CartItem item, int newQuantity) {
        if (newQuantity <= 0) {
            cartDAO.deleteCartItem(item.getId());
            loadCartItems();
        } else {
            cartDAO.updateQuantity(item.getId(), newQuantity);
            item.setQuantity(newQuantity);
            updateTotalPrice();
        }
    }

    @Override
    public void onItemDeleted(CartItem item) {
        showDeleteDialog(List.of(item));
    }
}