package com.example.resfeber.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.resfeber.R;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    @OnClick(R.id.login)
    public void submitLogin(View view) {
        Intent signInIntent = new Intent(LoginActivity.this, SignInActivity.class);
        startActivity(signInIntent);
        finish();
    }

    @OnClick(R.id.signup)
    public void submitSignUp(View view) {
        Intent signUpIntent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(signUpIntent);
        finish();
    }
}