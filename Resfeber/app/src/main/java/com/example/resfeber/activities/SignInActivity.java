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
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignInActivity extends AppCompatActivity {

    @BindView(R.id.username)
    EditText usernameEditText;
    @BindView(R.id.pass)
    EditText passEditText;
    @BindView(R.id.progressBar4)
    ProgressBar progressBar;
    @BindView(R.id.google_sign_in)
    SignInButton googleButton;
    @BindView(R.id.facebook_sign_in)
    LoginButton facebookButton;

    private final int RC_SIGN_IN = 120;
    private FirebaseAuth firebaseAuth;
    private static GoogleSignInClient googleSignInClient;
    private CallbackManager callbackManager;
    private static final String EMAIL = "email";
    private static final String PROFILE = "public_profile";
    private static final String FRIENDS = "user_friends";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        progressBar.setVisibility(View.INVISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        callbackManager = CallbackManager.Factory.create();
        facebookButton.setReadPermissions(EMAIL, PROFILE, FRIENDS);

        facebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(SignInActivity.this, getString(R.string.loginSucc), Toast.LENGTH_SHORT).show();
                Intent signInIntent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(signInIntent);
                finish();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(SignInActivity.this, getString(R.string.tryAgain), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(SignInActivity.this, getString(R.string.error) + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @OnClick(R.id.login2)
    public void signInSubmit(View view) {
        String email = usernameEditText.getText().toString().trim();
        String password = passEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            usernameEditText.setError(getString(R.string.emailRequired));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passEditText.setError(getString(R.string.passRequired));
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SignInActivity.this, getString(R.string.loginSucc), Toast.LENGTH_SHORT).show();

                Intent signInIntent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(signInIntent);
                progressBar.setVisibility(View.INVISIBLE);
                finish();

            } else {
                Toast.makeText(SignInActivity.this, getString(R.string.error) + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = (GoogleSignInAccount) ((Task) task).getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(SignInActivity.this, getString(R.string.error) + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignInActivity.this, getString(R.string.loginSucc), Toast.LENGTH_SHORT).show();
                        Intent mainIntent = new Intent(SignInActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    } else {
                        Toast.makeText(SignInActivity.this, getString(R.string.error) + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @OnClick(R.id.google_sign_in)
    public void googleSignIn(View view) {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public static GoogleSignInClient getGoogleSignInClient() {
        return googleSignInClient;
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential);
    }

    @Override
    public void onBackPressed() {
        Intent loginIntent = new Intent(SignInActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}