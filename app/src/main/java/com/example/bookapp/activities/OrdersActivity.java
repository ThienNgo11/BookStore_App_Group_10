package com.example.bookapp.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookapp.R;
import com.example.bookapp.adapters.OrderAdapter;
import com.example.bookapp.adapters.OrderItemAdapter;
import com.example.bookapp.database.OrderDAO;
import com.example.bookapp.models.Order;
import com.example.bookapp.models.OrderItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private OrderAdapter adapter;
    private List<Order> orderList = new ArrayList<>();
    private List<Order> fullOrderList = new ArrayList<>();
    private OrderDAO orderDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders);

        orderDAO = new OrderDAO(this);
        initViews();
        setupRecyclerView();
        loadOrders();
        setupSearch();
        setupNavigation();
    }

    private void initViews() {
        rvOrders = findViewById(R.id.rvOrders);
    }

    private void setupRecyclerView() {
        rvOrders.setLayoutManager(new LinearLayoutManager(this));

        // Setup Adapter với listener
        adapter = new OrderAdapter(this, orderList, new OrderAdapter.OnOrderActionListener() {
            @Override
            public void onApprove(Order order) {
                updateOrderStatus(order, "Accepted");
            }

            @Override
            public void onReject(Order order) {
                updateOrderStatus(order, "Rejected");
            }
            @Override
            public void onOrderClick(Order order) {
                showOrderDetailDialog(order);
            }
        });
        rvOrders.setAdapter(adapter);
    }

    private void showOrderDetailDialog(Order order) {
        try {
            // Lấy thông tin chi tiết đơn hàng
            Order fullOrder = orderDAO.getOrderById(order.getId());
            List<OrderItem> orderItems = orderDAO.getOrderItems(order.getId());

            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_order_detail);
            dialog.setTitle("Chi tiết đơn hàng #" + order.getId());

            // Ánh xạ views
            TextView tvOrderId = dialog.findViewById(R.id.tvOrderId);
            TextView tvCustomerName = dialog.findViewById(R.id.tvCustomerName);
            TextView tvCustomerPhone = dialog.findViewById(R.id.tvCustomerPhone);
            TextView tvCustomerAddress = dialog.findViewById(R.id.tvCustomerAddress);
            TextView tvOrderDate = dialog.findViewById(R.id.tvOrderDate);
            TextView tvOrderStatus = dialog.findViewById(R.id.tvOrderStatus);
            TextView tvTotalAmount = dialog.findViewById(R.id.tvTotalAmount);
            RecyclerView rvOrderItems = dialog.findViewById(R.id.rvOrderItems);
            Button btnClose = dialog.findViewById(R.id.btnClose);

            // Set data
            tvOrderId.setText("Mã đơn: #" + fullOrder.getId());
            tvCustomerName.setText("Khách hàng: " + fullOrder.getUserName());
            tvCustomerPhone.setText("SĐT: " + (fullOrder.getUserPhone() != null ? fullOrder.getUserPhone() : "Chưa cung cấp"));
            tvCustomerAddress.setText("Địa chỉ: " + (fullOrder.getUserAddress() != null ? fullOrder.getUserAddress() : "Chưa cung cấp"));
            tvOrderDate.setText("Ngày đặt: " + fullOrder.getOrderDate());
            tvOrderStatus.setText("Trạng thái: " + fullOrder.getStatus());
            tvTotalAmount.setText(String.format("Tổng tiền: %,d đ", (int) fullOrder.getTotalAmount()));

            // Setup RecyclerView cho order items
            OrderItemAdapter orderItemAdapter = new OrderItemAdapter(this, orderItems);
            rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
            rvOrderItems.setAdapter(orderItemAdapter);

            btnClose.setOnClickListener(v -> dialog.dismiss());

            dialog.show();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi hiển thị chi tiết đơn hàng", Toast.LENGTH_SHORT).show();
        }
    }
    private void setupSearch() {
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterOrders(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterOrders(newText);
                return false;
            }
        });
    }

    private void setupNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_orders);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_books) {
                startActivity(new Intent(OrdersActivity.this, AdminActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_users) {
                startActivity(new Intent(OrdersActivity.this, UsersManagementActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_orders) {
                // Đã ở trang orders, không cần làm gì
                return true;
            } else if (itemId == R.id.nav_profile) {
                Toast.makeText(this, "Tài khoản Admin", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    private void loadOrders() {
        orderList.clear();
        fullOrderList.clear();
        orderList.addAll(orderDAO.getAllOrders());
        fullOrderList.addAll(orderList);
        adapter.notifyDataSetChanged();

        if (orderList.isEmpty()) {
            Toast.makeText(this, "Không có đơn hàng nào", Toast.LENGTH_SHORT).show();
        }
    }

    private void filterOrders(String query) {
        orderList.clear();
        if (query.isEmpty()) {
            orderList.addAll(fullOrderList);
        } else {
            for (Order order : fullOrderList) {
                if (String.valueOf(order.getId()).contains(query) ||
                        order.getUserName().toLowerCase().contains(query.toLowerCase())) {
                    orderList.add(order);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void updateOrderStatus(Order order, String newStatus) {
        boolean success = orderDAO.updateOrderStatus(order.getId(), newStatus);
        if (success) {
            Toast.makeText(this, "Cập nhật trạng thái thành " + newStatus, Toast.LENGTH_SHORT).show();
            loadOrders(); // Tải lại danh sách để cập nhật UI
        } else {
            Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}