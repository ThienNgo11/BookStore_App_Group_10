package com.example.bookapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.bookapp.utils.SecurityUtils;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookapp.R;

import com.example.bookapp.database.UserDAO;
import com.example.bookapp.models.User;

import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etUsername, etEmail, etPhone, etAddress, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvBackToLogin;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userDAO = new UserDAO(this);

        initViews();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        tvBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Quay lại màn hình đăng nhập
            }
        });
    }

    private void initViews() {
        etFullName = findViewById(R.id.etFullName);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
    }

    private void registerUser() {
        String fullName = etFullName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        //Kiểm tra định dạng email
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Định dạng email không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate input
        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra username đã tồn tại chưa
        if (userDAO.isUsernameExists(username)) {
            Toast.makeText(this, "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Băm mật khẩu
        String hashedPassword = SecurityUtils.hashPassword(password);
        if (hashedPassword == null) {
            Toast.makeText(this, "Lỗi mã hóa mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Tạo một mã OTP ngẫu nhiên (chỉ để demo)
        int fakeOtpp = new Random().nextInt(899999) + 100000; // Tạo số 6 chữ số
        String fakeOtp = fakeOtpp + "";
        // 2. "Giả vờ" gửi OTP bằng cách hiển thị nó lên
        // (Trong một dự án thật, em sẽ gọi API để gửi SMS ở đây)
        new AlertDialog.Builder(this)
                .setTitle("Xác thực OTP (DEMO)")
                .setMessage("Hệ thống (demo) đã gửi một mã OTP.\n\nMã của bạn là: " + fakeOtp)
                .setPositiveButton("Tôi đã nhớ mã", (dialog, which) -> {
                    // Sau khi người dùng thấy mã, ta hiển thị dialog để họ nhập
                    showOtpInputDialog(fakeOtp, fullName, username, email, phone, address, password);
                })
                .setCancelable(false)
                .show();

//        // Thêm user vào database
//        User newUser = new User(0, username, hashedPassword, fullName, email, phone, address, "user");
//        if (userDAO.insertUser(newUser)) {
//            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
//            finish(); // Quay lại màn hình đăng nhập
//        } else {
//            Toast.makeText(this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
//        }
    }
    // Kiểm tra định dạng email
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void showOtpInputDialog(String correctOtp,
                                    String fullName, String username, String email,
                                    String phone, String address, String password) {

        // Tạo một EditText để người dùng nhập
        final EditText input = new EditText(this);
        input.setHint("Nhập 6 số OTP");
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setMaxLines(1);

        new AlertDialog.Builder(this)
                .setTitle("Xác thực OTP")
                .setView(input)
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    String enteredOtp = input.getText().toString().trim();

                    // 3. So sánh mã
                    if (enteredOtp.equals(correctOtp)) {
                        // 4. NẾU ĐÚNG: Tiến hành đăng ký thật sự
                        completeRegistration(fullName, username, email, phone, address, password);
                    } else {
                        // 5. NẾU SAI: Báo lỗi
                        Toast.makeText(this, "Mã OTP không đúng!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void completeRegistration(String fullName, String username, String email,
                                      String phone, String address, String plainPassword) {

        // 1. Băm mật khẩu (Dùng lớp SecurityUtils mà thầy đã chỉ em)
        String hashedPassword = com.example.bookapp.utils.SecurityUtils.hashPassword(plainPassword);
        if (hashedPassword == null) {
            Toast.makeText(this, "Lỗi mã hóa mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Kiểm tra username (Bây giờ mới kiểm tra)
        if (userDAO.isUsernameExists(username)) {
            Toast.makeText(this, "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. Thêm user vào database
        User newUser = new User(0, username, hashedPassword, fullName, email, phone, address, "user");

        if (userDAO.insertUser(newUser)) {
            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
            finish(); // Quay lại màn hình đăng nhập
        } else {
            Toast.makeText(this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}