package com.example.bookapp.activities;

import android.app.Dialog;
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
import com.example.bookapp.adapters.UserOrderAdapter;
import com.example.bookapp.adapters.OrderItemAdapter;
import com.example.bookapp.database.OrderDAO;
import com.example.bookapp.database.CartDAO;
import com.example.bookapp.database.BookDAO;
import com.example.bookapp.models.Order;
import com.example.bookapp.models.OrderItem;
import com.example.bookapp.models.Book;
import com.example.bookapp.utils.SessionManager;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class UserOrdersActivity extends AppCompatActivity {
    private RecyclerView rvOrders;
    private UserOrderAdapter adapter;
    private List<Order> orderList = new ArrayList<>();
    private List<Order> fullOrderList = new ArrayList<>();
    private OrderDAO orderDAO;
    private TabLayout tabLayout;
    private TextView tvEmptyOrders;
    private SessionManager sessionManager;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_orders);

        sessionManager = new SessionManager(this);
        currentUserId = sessionManager.getUserId();

        if (currentUserId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        orderDAO = new OrderDAO(this);
        initViews();
        setupToolbar();
        setupTabs();
        setupRecyclerView();
        loadOrders("All");
    }

    private void initViews() {
        rvOrders = findViewById(R.id.rvOrders);
        tabLayout = findViewById(R.id.tabLayout);
        tvEmptyOrders = findViewById(R.id.tvEmptyOrders);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Đơn hàng của tôi");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Tất cả"));
        tabLayout.addTab(tabLayout.newTab().setText("Chờ xác nhận"));
        tabLayout.addTab(tabLayout.newTab().setText("Đã xác nhận"));
        tabLayout.addTab(tabLayout.newTab().setText("Đang giao"));
        tabLayout.addTab(tabLayout.newTab().setText("Hoàn thành"));
        tabLayout.addTab(tabLayout.newTab().setText("Đã hủy"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String status = getStatusFromTab(tab.getPosition());
                loadOrders(status);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private String getStatusFromTab(int position) {
        switch (position) {
            case 0: return "All";
            case 1: return "Pending";
            case 2: return "Accepted";
            case 3: return "Shipped";
            case 4: return "Completed";
            case 5: return "Rejected";
            default: return "All";
        }
    }

    private void setupRecyclerView() {
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserOrderAdapter(this, orderList, new UserOrderAdapter.OnOrderActionListener() {
            @Override
            public void onOrderClick(Order order) {
                showOrderDetailDialog(order);
            }

            @Override
            public void onCancelOrder(Order order) {
                cancelOrder(order);
            }

            @Override
            public void onReorder(Order order) {
                showReorderConfirmDialog(order);
            }
        });
        rvOrders.setAdapter(adapter);
    }

    private void loadOrders(String status) {
        orderList.clear();
        fullOrderList.clear();

        List<Order> userOrders = orderDAO.getUserOrders(currentUserId);

        if ("All".equals(status)) {
            orderList.addAll(userOrders);
        } else {
            for (Order order : userOrders) {
                if (order.getStatus().equals(status)) {
                    orderList.add(order);
                }
            }
        }

        fullOrderList.addAll(orderList);
        adapter.notifyDataSetChanged();

        if (orderList.isEmpty()) {
            tvEmptyOrders.setVisibility(View.VISIBLE);
            rvOrders.setVisibility(View.GONE);
            tvEmptyOrders.setText("Chưa có đơn hàng nào");
        } else {
            tvEmptyOrders.setVisibility(View.GONE);
            rvOrders.setVisibility(View.VISIBLE);
        }
    }

    private void showOrderDetailDialog(Order order) {
        try {
            Order fullOrder = orderDAO.getOrderById(order.getId());
            List<OrderItem> orderItems = orderDAO.getOrderItems(order.getId());

            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_user_order_detail);

            TextView tvOrderId = dialog.findViewById(R.id.tvOrderId);
            TextView tvOrderDate = dialog.findViewById(R.id.tvOrderDate);
            TextView tvOrderStatus = dialog.findViewById(R.id.tvOrderStatus);
            TextView tvCustomerName = dialog.findViewById(R.id.tvCustomerName);
            TextView tvCustomerPhone = dialog.findViewById(R.id.tvCustomerPhone);
            TextView tvCustomerAddress = dialog.findViewById(R.id.tvCustomerAddress);
            TextView tvTotalAmount = dialog.findViewById(R.id.tvTotalAmount);
            RecyclerView rvOrderItems = dialog.findViewById(R.id.rvOrderItems);
            Button btnClose = dialog.findViewById(R.id.btnClose);
            Button btnCancelOrder = dialog.findViewById(R.id.btnCancelOrder);

            tvOrderId.setText("Mã đơn hàng: #" + fullOrder.getId());
            tvOrderDate.setText("Ngày đặt: " + fullOrder.getOrderDate());
            tvOrderStatus.setText(getStatusText(fullOrder.getStatus()));
            tvCustomerName.setText(fullOrder.getUserName());
            tvCustomerPhone.setText(fullOrder.getUserPhone() != null ? fullOrder.getUserPhone() : "Chưa cập nhật");
            tvCustomerAddress.setText(fullOrder.getUserAddress() != null ? fullOrder.getUserAddress() : "Chưa cập nhật");
            tvTotalAmount.setText(String.format("%,d đ", (int) fullOrder.getTotalAmount()));

            OrderItemAdapter orderItemAdapter = new OrderItemAdapter(this, orderItems);
            rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
            rvOrderItems.setAdapter(orderItemAdapter);

            if ("Pending".equals(fullOrder.getStatus())) {
                btnCancelOrder.setVisibility(View.VISIBLE);
                btnCancelOrder.setOnClickListener(v -> {
                    dialog.dismiss();
                    cancelOrder(fullOrder);
                });
            } else {
                btnCancelOrder.setVisibility(View.GONE);
            }

            btnClose.setOnClickListener(v -> dialog.dismiss());

            dialog.show();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi hiển thị chi tiết đơn hàng", Toast.LENGTH_SHORT).show();
        }
    }

    private void cancelOrder(Order order) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Hủy đơn hàng")
                .setMessage("Bạn có chắc chắn muốn hủy đơn hàng #" + order.getId() + "?")
                .setPositiveButton("Hủy đơn", (dialog, which) -> {
                    boolean success = orderDAO.updateOrderStatus(order.getId(), "Rejected");
                    if (success) {
                        Toast.makeText(this, "Đã hủy đơn hàng", Toast.LENGTH_SHORT).show();
                        loadOrders(getStatusFromTab(tabLayout.getSelectedTabPosition()));
                    } else {
                        Toast.makeText(this, "Hủy đơn hàng thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Đóng", null)
                .show();
    }

    // ========================================
    // CHỨC NĂNG MUA LẠI
    // ========================================

    private void showReorderConfirmDialog(Order order) {
        List<OrderItem> orderItems = orderDAO.getOrderItems(order.getId());

        if (orderItems.isEmpty()) {
            Toast.makeText(this, "Đơn hàng không có sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder message = new StringBuilder();
        message.append("Thêm lại các sản phẩm sau vào giỏ hàng:\n\n");

        for (OrderItem item : orderItems) {
            message.append("• ").append(item.getBookTitle())
                    .append(" (x").append(item.getQuantity()).append(")\n");
        }

        message.append("\nBạn có muốn tiếp tục?");

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Mua lại đơn hàng #" + order.getId())
                .setMessage(message.toString())
                .setPositiveButton("Thêm vào giỏ", (dialog, which) -> {
                    executeReorder(orderItems);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void executeReorder(List<OrderItem> orderItems) {
        CartDAO cartDAO = new CartDAO(this);
        BookDAO bookDAO = new BookDAO(this);

        int successCount = 0;
        int outOfStockCount = 0;
        List<String> outOfStockBooks = new ArrayList<>();

        for (OrderItem item : orderItems) {
            Book book = bookDAO.getBookById(item.getBookId());

            if (book == null) {
                continue;
            }

            if (book.getStock() <= 0) {
                outOfStockCount++;
                outOfStockBooks.add(book.getTitle());
                continue;
            }

            int quantityToAdd = Math.min(item.getQuantity(), book.getStock());

            boolean success = cartDAO.addToCart(currentUserId, item.getBookId(), quantityToAdd);

            if (success) {
                successCount++;
            }
        }

        showReorderResult(successCount, outOfStockCount, outOfStockBooks);
    }

    private void showReorderResult(int successCount, int outOfStockCount, List<String> outOfStockBooks) {
        if (successCount > 0) {
            String message = "Đã thêm " + successCount + " sản phẩm vào giỏ hàng";

            if (outOfStockCount > 0) {
                message += "\n\n" + outOfStockCount + " sản phẩm hết hàng:\n";
                for (String bookTitle : outOfStockBooks) {
                    message += "• " + bookTitle + "\n";
                }
            }

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Mua lại thành công")
                    .setMessage(message)
                    .setPositiveButton("Xem giỏ hàng", (dialog, which) -> {
                        Intent intent = new Intent(UserOrdersActivity.this, CartActivity.class);
                        startActivity(intent);
                    })
                    .setNegativeButton("Tiếp tục mua", null)
                    .show();
        } else {
            Toast.makeText(this, "Không thể thêm sản phẩm vào giỏ hàng", Toast.LENGTH_LONG).show();
        }
    }

    private String getStatusText(String status) {
        switch (status) {
            case "Pending": return "Chờ xác nhận";
            case "Accepted": return "Đã xác nhận";
            case "Shipped": return "Đang giao hàng";
            case "Completed": return "Hoàn thành";
            case "Rejected": return "Đã hủy";
            default: return status;
        }
    }
}