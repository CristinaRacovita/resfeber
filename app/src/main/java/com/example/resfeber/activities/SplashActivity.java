package com.example.resfeber.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.resfeber.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        int SPLASH_TIME_OUT = 2000;
        new Handler().postDelayed(() -> {
            Intent logInIntent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(logInIntent);
            finish();
        }, SPLASH_TIME_OUT);
    }
}