package com.example.resfeber.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.resfeber.R;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    @BindView(R.id.username)
    EditText mEmail;
    @BindView(R.id.pass)
    EditText mPassword;
    @BindView(R.id.progressBar5)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        progressBar.setVisibility(View.INVISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @OnClick(R.id.signup)
    public void signUpSubmit(View view) {
        final String email = mEmail.getText().toString().trim();
        final String password = mPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            mEmail.setError(getString(R.string.emailRequired));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            mPassword.setError(getString(R.string.passRequired));
            return;
        }
        if (password.length() < 6) {
            mPassword.setError(getString(R.string.passLength));
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                Toast.makeText(SignUpActivity.this, getString(R.string.userCreated), Toast.LENGTH_SHORT).show();
                Intent mainIntent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(mainIntent);
                progressBar.setVisibility(View.INVISIBLE);
                finish();
            } else {
                Toast.makeText(SignUpActivity.this, getString(R.string.error) + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent loginIntent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}