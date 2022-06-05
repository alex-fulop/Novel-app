package com.fulop.novel_v2.activities;

import static java.util.Objects.requireNonNull;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fulop.novel_v2.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    public static final String EMPTY_EMAIL = "Email is required";
    public static final String EMPTY_PASSWORD = "Password is required";

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseAuth.AuthStateListener firebaseAuthListener = firebaseAuth -> {
        FirebaseUser user = firebaseAuth.getCurrentUser();
//        if(user == null) = GET USER FROM DB
        if (user != null) {
            startActivity(MainActivity.newIntent(LoginActivity.this));
            finish();
        }
    };

    private EditText emailEditText;
    private EditText passwordEditText;
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private LinearLayout loginProgressLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        emailTextInputLayout = findViewById(R.id.emailTextInputLayout);
        passwordTextInputLayout = findViewById(R.id.emailPasswordInputLayout);
        loginProgressLayout = findViewById(R.id.loginProgressLayout);

        setOnTextChangeListener(emailEditText, emailTextInputLayout);
        setOnTextChangeListener(passwordEditText, passwordTextInputLayout);

        loginProgressLayout.setOnTouchListener((v, event) -> true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(firebaseAuthListener);
    }

    private void setOnTextChangeListener(EditText editText, TextInputLayout textInputLayout) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void onLogin(View view) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        boolean proceed = true;

        if (email.isEmpty()) {
            emailTextInputLayout.setError(EMPTY_EMAIL);
            emailTextInputLayout.setErrorEnabled(true);
            proceed = false;
        }

        if (password.isEmpty()) {
            passwordTextInputLayout.setError(EMPTY_PASSWORD);
            passwordTextInputLayout.setErrorEnabled(true);
            proceed = false;
        }

        if (proceed) {
            loginProgressLayout.setVisibility(View.VISIBLE);
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            loginProgressLayout.setVisibility(View.GONE);
                            String error = String.format("Login Error: %s",
                                    requireNonNull(task.getException()).getLocalizedMessage());
                            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(e -> {
                e.printStackTrace();
                loginProgressLayout.setVisibility(View.GONE);
            });
        }
    }

    public void goToSignup(View view) {
        startActivity(SignupActivity.newIntent(this));
        finish();
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }
}