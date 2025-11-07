package com.example.bookapp.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bookapp.R;
import com.example.bookapp.database.UserDAO;
import com.example.bookapp.models.User;
import com.example.bookapp.utils.SecurityUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername, tvFullname, tvEmail, tvPhone, tvAddress;
    private UserDAO userDAO;
    private User currentUser;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // THÊM 2 DÒNG NÀY
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // BẮT BUỘC!

        userDAO = new UserDAO(this);
        currentUsername = getIntent().getStringExtra("USERNAME");

        if (currentUsername == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadUserData();
        setupBottomNavigation();
    }

    private void initViews() {
        tvUsername = findViewById(R.id.tvUsername);
        tvFullname = findViewById(R.id.tvFullname);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
    }

    private void loadUserData() {
        currentUser = userDAO.getUserByUsername(currentUsername);
        if (currentUser != null) {
            tvUsername.setText(currentUser.getUsername());
            tvFullname.setText(currentUser.getFullname());
            tvEmail.setText(currentUser.getEmail());
            tvPhone.setText(currentUser.getPhone());
            tvAddress.setText(currentUser.getAddress());
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_profile);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class)
                        .putExtra("USERNAME", currentUsername));
                finish();
                return true;
            } else if (id == R.id.nav_books) {
//                startActivity(new Intent(this, BookListActivity.class)
//                        .putExtra("USERNAME", currentUsername));
//                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                return true;
            }
            return false;
        });
    }

    // TẠO OPTION MENU (ba chấm)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_option_menu, menu);
        return true;
    }

    // XỬ LÝ KHI BẤM VÀO ITEM TRONG OPTION MENU
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_edit_info) {
            showEditInfoDialog();
            return true;
        } else if (id == R.id.menu_change_password) {
            showChangePasswordDialog();
            return true;
        } else if (id == R.id.menu_logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showEditInfoDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_edit_profile);
        dialog.setTitle("Chỉnh sửa thông tin");

        EditText etFullname = dialog.findViewById(R.id.etFullname);
        EditText etEmail = dialog.findViewById(R.id.etEmail);
        EditText etPhone = dialog.findViewById(R.id.etPhone);
        EditText etAddress = dialog.findViewById(R.id.etAddress);
        Button btnSave = dialog.findViewById(R.id.btnSave);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        // Điền dữ liệu hiện tại
        etFullname.setText(currentUser.getFullname());
        etEmail.setText(currentUser.getEmail());
        etPhone.setText(currentUser.getPhone());
        etAddress.setText(currentUser.getAddress());

        btnSave.setOnClickListener(v -> {
            String fullname = etFullname.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            if (fullname.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
                return;
            }

            currentUser.setFullname(fullname);
            currentUser.setEmail(email);
            currentUser.setPhone(phone);
            currentUser.setAddress(address);

            userDAO.updateUserInfo(currentUser);

            // Cập nhật lại giao diện
            tvFullname.setText(fullname);
            tvEmail.setText(email);
            tvPhone.setText(phone);
            tvAddress.setText(address);

            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showChangePasswordDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_change_password);

        EditText etOldPassword = dialog.findViewById(R.id.etOldPassword);
        EditText etNewPassword = dialog.findViewById(R.id.etNewPassword);
        EditText etConfirmPassword = dialog.findViewById(R.id.etConfirmPassword);
        Button btnSave = dialog.findViewById(R.id.btnSave);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(v -> {
            String oldPass = etOldPassword.getText().toString().trim();
            String newPass = etNewPassword.getText().toString().trim();
            String confirmPass = etConfirmPassword.getText().toString().trim();

            // 1. Kiểm tra rỗng
            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. BĂM MẬT KHẨU CŨ → SO SÁNH VỚI DB (dùng cùng hàm như login)
            String hashedOld = SecurityUtils.hashPassword(oldPass);
            if (hashedOld == null || !hashedOld.equals(currentUser.getPassword())) {
                Toast.makeText(this, "Mật khẩu cũ không đúng", Toast.LENGTH_SHORT).show();
                return;
            }

            // 3. Kiểm tra xác nhận
            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            // 4. Kiểm tra độ dài
            if (newPass.length() < 6) {
                Toast.makeText(this, "Mật khẩu phải từ 6 ký tự trở lên", Toast.LENGTH_SHORT).show();
                return;
            }

            // 5. BĂM MẬT KHẨU MỚI → LƯU VÀO DB (dùng cùng hàm như register)
            String hashedNew = SecurityUtils.hashPassword(newPass);
            if (hashedNew == null) {
                Toast.makeText(this, "Lỗi hệ thống (mã hóa)", Toast.LENGTH_SHORT).show();
                return;
            }

            // 6. LƯU VÀO DB → DÙNG CÙNG HÀM NHƯ REGISTER
            userDAO.updateUserPassword(currentUser.getId(), hashedNew);

            // 7. Cập nhật local để lần sau so sánh đúng
            currentUser.setPassword(hashedNew);

            Toast.makeText(this, "Đổi mật khẩu thành công!", Toast.LENGTH_LONG).show();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void logout() {
        new android.app.AlertDialog.Builder(this)
                .setMessage("Đăng xuất?")
                .setPositiveButton("Có", (d, w) -> {
                    startActivity(new Intent(this, LoginActivity.class));
                    finishAffinity();
                })
                .setNegativeButton("Không", null)
                .show();
    }
}