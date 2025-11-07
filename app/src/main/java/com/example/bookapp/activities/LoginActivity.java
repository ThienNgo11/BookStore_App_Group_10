package com.example.bookapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookapp.R;
import com.example.bookapp.database.UserDAO;
import com.example.bookapp.models.User;
import com.example.bookapp.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin, btnGuest;
    private TextView tvRegister;
    private UserDAO userDAO;
    private SessionManager sessionManager;   // ← thêm dòng này

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userDAO = new UserDAO(this);
        sessionManager = new SessionManager(this); // ← thêm dòng này

        initViews();

        btnLogin.setOnClickListener(v -> loginUser());
        btnGuest.setOnClickListener(v -> loginAsGuest());
        tvRegister.setOnClickListener(v -> goToRegister());
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGuest = findViewById(R.id.btnGuest);
        tvRegister = findViewById(R.id.tvRegister);
    }

    private void goToRegister() {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    private void loginUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = userDAO.authenticateUser(username, password);

        if (user != null) {

            // ✅ LƯU SESSION BẮT BUỘC
            sessionManager.createLoginSession(
                    user.getId(),
                    user.getUsername(),
                    user.getFullname(),
                    user.getRole()
            );

            Intent intent;
            if ("admin".equals(user.getRole())) {
                intent = new Intent(LoginActivity.this, AdminActivity.class);
            } else {
                intent = new Intent(LoginActivity.this, MainActivity.class);
            }

            startActivity(intent);
            finish();

        } else {
            Toast.makeText(this, "Tên đăng nhập hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
        }
    }

    private void loginAsGuest() {
        startActivity(new Intent(LoginActivity.this, GuestActivity.class));
        finish();
    }
}
