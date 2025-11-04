package com.example.bookapp.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookapp.R;
import com.example.bookapp.adapters.UserAdapter;
import com.example.bookapp.database.UserDAO;
import com.example.bookapp.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class UsersManagementActivity extends AppCompatActivity {
    private RecyclerView rvUsers;
    private UserAdapter adapter;
    private List<User> userList = new ArrayList<>();
    private List<User> fullUserList = new ArrayList<>();
    private UserDAO userDAO;
    private TextView tvTotalUsers, tvActiveUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_management);

        userDAO = new UserDAO(this);
        initViews();
        setupRecyclerView();
        loadUsers();
        loadStatistics();
        setupSearch();
        setupNavigation();
    }

    private void initViews() {
        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvActiveUsers = findViewById(R.id.tvActiveUsers);
        rvUsers = findViewById(R.id.rvUsers);

        FloatingActionButton fabAddUser = findViewById(R.id.fabAddUser);
        fabAddUser.setOnClickListener(v -> showAddUserDialog());
    }

    private void setupRecyclerView() {
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(this, userList);
        adapter.setOnUserClickListener(new UserAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(User user) {
                showUserActionsDialog(user);
            }

            @Override
            public void onUserLongClick(User user) {
                showUserActionsDialog(user);
            }
        });
        rvUsers.setAdapter(adapter);
    }

    private void setupSearch() {
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterUsers(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterUsers(newText);
                return false;
            }
        });
    }

    private void setupNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_users);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_books) {
                startActivity(new Intent(this, AdminActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_users) {
                return true;
            } else if (itemId == R.id.nav_orders) {
                startActivity(new Intent(UsersManagementActivity.this, OrdersActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                Toast.makeText(this, "Tài khoản Admin", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    private void loadStatistics() {
        int totalUsers = userDAO.getTotalUsers();
        int activeUsers = userDAO.getActiveUsersCount();

        tvTotalUsers.setText(String.valueOf(totalUsers));
        tvActiveUsers.setText(String.valueOf(activeUsers));
    }

    private void loadUsers() {
        userList.clear();
        fullUserList.clear();
        userList.addAll(userDAO.getAllUsers());
        fullUserList.addAll(userList);
        adapter.notifyDataSetChanged();

        if (userList.isEmpty()) {
            Toast.makeText(this, "Chưa có user nào", Toast.LENGTH_SHORT).show();
        }
    }

    private void filterUsers(String query) {
        userList.clear();
        if (TextUtils.isEmpty(query)) {
            userList.addAll(fullUserList);
        } else {
            for (User user : fullUserList) {
                if (user.getUsername().toLowerCase().contains(query.toLowerCase()) ||
                        user.getFullname().toLowerCase().contains(query.toLowerCase()) ||
                        (user.getEmail() != null && user.getEmail().toLowerCase().contains(query.toLowerCase()))) {
                    userList.add(user);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void showUserActionsDialog(User user) {
        String[] actions = user.isActive() ?
                new String[]{"Chỉnh sửa", "Ngừng hoạt động"} :
                new String[]{"Chỉnh sửa", "Kích hoạt"};

        new AlertDialog.Builder(this)
                .setTitle(user.getUsername())
                .setItems(actions, (dialog, which) -> {
                    switch (which) {
                        case 0: // Edit
                            showEditUserDialog(user);
                            break;
                        case 1: // Toggle Active
                            toggleUserActive(user);
                            break;
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void toggleUserActive(User user) {
        // Kiểm tra nếu là admin thì không cho ngừng hoạt động
        if (userDAO.isAdmin(user.getId()) && user.isActive()) {
            Toast.makeText(this, "Không thể ngừng hoạt động tài khoản admin!", Toast.LENGTH_SHORT).show();
            return;
        }

        String message = user.isActive() ?
                "Bạn có chắc muốn ngừng hoạt động tài khoản \"" + user.getUsername() + "\"?" :
                "Bạn có chắc muốn kích hoạt lại tài khoản \"" + user.getUsername() + "\"?";

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage(message)
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    user.setActive(!user.isActive());
                    if (userDAO.updateUser(user)) {
                        String toastMessage = user.isActive() ?
                                "Đã kích hoạt tài khoản" : "Đã ngừng hoạt động tài khoản";
                        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
                        loadUsers();
                        loadStatistics();
                    } else {
                        Toast.makeText(this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // Dialog thêm user
    private void showAddUserDialog() {
        try {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_add_edit_user);
            dialog.setTitle("Thêm User");

            EditText etUsername = dialog.findViewById(R.id.etUsername);
            EditText etPassword = dialog.findViewById(R.id.etPassword);
            EditText etFullname = dialog.findViewById(R.id.etFullname);
            EditText etEmail = dialog.findViewById(R.id.etEmail);
            EditText etPhone = dialog.findViewById(R.id.etPhone);
            EditText etAddress = dialog.findViewById(R.id.etAddress);
            EditText etRole = dialog.findViewById(R.id.etRole);

            Button btnSave = dialog.findViewById(R.id.btnSave);
            btnSave.setOnClickListener(v -> {
                if (validateInputs(etUsername, etPassword, etFullname)) {
                    if (userDAO.isUsernameExists(etUsername.getText().toString())) {
                        etUsername.setError("Username đã tồn tại");
                        return;
                    }
                    User newUser = new User(0, etUsername.getText().toString(),
                            etPassword.getText().toString(), etFullname.getText().toString(),
                            etEmail.getText().toString(), etPhone.getText().toString(),
                            etAddress.getText().toString(), etRole.getText().toString());
                    // Mặc định user mới sẽ active
                    newUser.setActive(true);
                    userDAO.insertUser(newUser);
                    Toast.makeText(this, "Thêm user thành công", Toast.LENGTH_SHORT).show();
                    loadUsers();
                    loadStatistics();
                    dialog.dismiss();
                }
            });
            dialog.show();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi dialog thêm user: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showEditUserDialog(User user) {
        try {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_add_edit_user);
            dialog.setTitle("Sửa User");

            EditText etUsername = dialog.findViewById(R.id.etUsername);
            etUsername.setText(user.getUsername());
            EditText etPassword = dialog.findViewById(R.id.etPassword);
            etPassword.setText(user.getPassword());
            EditText etFullname = dialog.findViewById(R.id.etFullname);
            etFullname.setText(user.getFullname());
            EditText etEmail = dialog.findViewById(R.id.etEmail);
            etEmail.setText(user.getEmail());
            EditText etPhone = dialog.findViewById(R.id.etPhone);
            etPhone.setText(user.getPhone());
            EditText etAddress = dialog.findViewById(R.id.etAddress);
            etAddress.setText(user.getAddress());
            EditText etRole = dialog.findViewById(R.id.etRole);
            etRole.setText(user.getRole());

            Button btnSave = dialog.findViewById(R.id.btnSave);
            btnSave.setOnClickListener(v -> {
                if (validateInputs(etUsername, etPassword, etFullname)) {
                    // Kiểm tra username nếu thay đổi
                    String newUsername = etUsername.getText().toString();
                    if (!newUsername.equals(user.getUsername()) && userDAO.isUsernameExists(newUsername)) {
                        etUsername.setError("Username đã tồn tại");
                        return;
                    }
                    user.setUsername(newUsername);
                    user.setPassword(etPassword.getText().toString());
                    user.setFullname(etFullname.getText().toString());
                    user.setEmail(etEmail.getText().toString());
                    user.setPhone(etPhone.getText().toString());
                    user.setAddress(etAddress.getText().toString());
                    user.setRole(etRole.getText().toString());
                    userDAO.updateUser(user);
                    Toast.makeText(this, "Sửa user thành công", Toast.LENGTH_SHORT).show();
                    loadUsers();
                    loadStatistics();
                    dialog.dismiss();
                }
            });
            dialog.show();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi dialog sửa user: " + e.getMessage(), Toast.LENGTH_LONG).show();
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