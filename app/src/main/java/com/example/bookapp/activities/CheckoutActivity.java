package com.example.bookapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookapp.R;
import com.example.bookapp.adapters.CheckoutAdapter;
import com.example.bookapp.database.CartDAO;
import com.example.bookapp.database.OrderDAO;
import com.example.bookapp.models.CartItem;
import com.example.bookapp.models.User;
import com.example.bookapp.utils.SessionManager;
import com.example.bookapp.database.UserDAO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserPhone, tvUserAddress;
    private TextView tvSubtotal, tvShippingFee, tvTotal, tvTotalBottom;
    private RecyclerView rvCheckoutItems;
    private Button btnPlaceOrder;

    private CheckoutAdapter adapter;
    private List<CartItem> checkoutItems = new ArrayList<>();
    private SessionManager sessionManager;
    private UserDAO userDAO;
    private OrderDAO orderDAO;
    private CartDAO cartDAO;

    private int userId;
    private double subtotal = 0;
    private double shippingFee = 30000; // Phí ship cố định
    private double totalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        if (userId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userDAO = new UserDAO(this);
        orderDAO = new OrderDAO(this);
        cartDAO = new CartDAO(this);

        // Nhận danh sách item được chọn từ CartActivity
        ArrayList<CartItem> items = (ArrayList<CartItem>) getIntent().getSerializableExtra("SELECTED_ITEMS");
        if (items != null) {
            checkoutItems.addAll(items);
        }

        initViews();
        setupToolbar();
        loadUserInfo();
        setupRecyclerView();
        calculateTotal();
    }

    private void initViews() {
        tvUserName = findViewById(R.id.tvUserName);
        tvUserPhone = findViewById(R.id.tvUserPhone);
        tvUserAddress = findViewById(R.id.tvUserAddress);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvShippingFee = findViewById(R.id.tvShippingFee);
        tvTotal = findViewById(R.id.tvTotal);
        tvTotalBottom = findViewById(R.id.tvTotalBottom);
        rvCheckoutItems = findViewById(R.id.rvCheckoutItems);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        btnPlaceOrder.setOnClickListener(v -> showConfirmDialog());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Thanh toán");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadUserInfo() {
        User user = userDAO.getUserById(userId);
        if (user != null) {
            tvUserName.setText(user.getFullname());
            tvUserPhone.setText(user.getPhone() != null ? user.getPhone() : "Chưa cập nhật");
            tvUserAddress.setText(user.getAddress() != null ? user.getAddress() : "Chưa cập nhật");
        }
    }

    private void setupRecyclerView() {
        rvCheckoutItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CheckoutAdapter(this, checkoutItems);
        rvCheckoutItems.setAdapter(adapter);
    }

    private void calculateTotal() {
        subtotal = 0;
        for (CartItem item : checkoutItems) {
            subtotal += item.getPrice() * item.getQuantity();
        }

        totalAmount = subtotal + shippingFee;

        tvSubtotal.setText(String.format("%,.0f đ", subtotal));
        tvShippingFee.setText(String.format("%,.0f đ", shippingFee));
        tvTotal.setText(String.format("%,.0f đ", totalAmount));
        tvTotalBottom.setText(String.format("%,.0f đ", totalAmount));
    }

    private void showConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận đặt hàng")
                .setMessage("Tổng tiền: " + String.format("%,.0f đ", totalAmount) +
                        "\n\nPhương thức thanh toán: Thanh toán khi nhận hàng (COD)" +
                        "\n\nBạn có chắc muốn đặt hàng?")
                .setPositiveButton("Đặt hàng", (dialog, which) -> placeOrder())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void placeOrder() {
        try {
            // Lấy ngày hiện tại
            String orderDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            // Tạo đơn hàng
            int orderId = orderDAO.createOrder(userId, orderDate, totalAmount, "Pending");

            if (orderId > 0) {
                // Thêm các order items
                for (CartItem item : checkoutItems) {
                    orderDAO.addOrderItem(orderId, item.getBookId(), item.getQuantity(), item.getPrice());

                    // Xóa item khỏi giỏ hàng
                    cartDAO.deleteCartItem(item.getId());
                }

                Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_LONG).show();

                // Chuyển về MainActivity
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Đặt hàng thất bại. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}