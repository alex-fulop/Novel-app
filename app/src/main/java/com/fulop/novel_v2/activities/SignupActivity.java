package com.fulop.novel_v2.activities;

import static com.fulop.novel_v2.util.Constants.DATA_USERS;
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
import com.fulop.novel_v2.models.User;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity {

    public static final String EMPTY_EMAIL = "Email is required";
    public static final String EMPTY_PASSWORD = "Password is required";
    public static final String EMPTY_USERNAME = "Username is required";

    private final FirebaseFirestore firebaseDB = FirebaseFirestore.getInstance();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseAuth.AuthStateListener firebaseAuthListener = firebaseAuth -> {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            startActivity(HomeActivity.newIntent(SignupActivity.this));
            finish();
        }
    };

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText usernameEditText;
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private TextInputLayout usernameTextInputLayout;
    private LinearLayout signupProgressLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        emailTextInputLayout = findViewById(R.id.emailTextInputLayout);
        passwordTextInputLayout = findViewById(R.id.emailPasswordInputLayout);
        signupProgressLayout = findViewById(R.id.signupProgressLayout);
        usernameEditText = findViewById(R.id.usernameEditText);
        usernameTextInputLayout = findViewById(R.id.usernameTextInputLayout);

        setOnTextChangeListener(emailEditText, emailTextInputLayout);
        setOnTextChangeListener(passwordEditText, passwordTextInputLayout);
        setOnTextChangeListener(usernameEditText, usernameTextInputLayout);

        signupProgressLayout.setOnTouchListener((v, event) -> true);
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

    public void onSignup(View view) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String username = usernameEditText.getText().toString();

        boolean proceed = true;

        if (username.isEmpty()) {
            usernameTextInputLayout.setError(EMPTY_USERNAME);
            usernameTextInputLayout.setErrorEnabled(true);
            proceed = false;
        }

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
            signupProgressLayout.setVisibility(View.VISIBLE);
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            String error = String.format("Signup Error: %s",
                                    requireNonNull(task.getException()).getLocalizedMessage());
                            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                        } else {
                            User user = new User();
                            user.setUsername(username);
                            user.setEmail(email);
                            firebaseDB.collection(DATA_USERS).document(firebaseAuth.getUid()).set(user);
                        }
                        signupProgressLayout.setVisibility(View.GONE);

                    }).addOnFailureListener(e -> {
                e.printStackTrace();
                signupProgressLayout.setVisibility(View.GONE);
            });
        }
    }

    public void goToLogin(View view) {
        startActivity(LoginActivity.newIntent(this));
        finish();
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, SignupActivity.class);
    }
}