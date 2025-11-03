package com.example.bookapp.activities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bookapp.R;
public class GuestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        tvWelcome.setText("Chào mừng Khách");
    }
}