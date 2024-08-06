package com.anniezhang.textfromimage;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etLoginEmail;
    private TextInputEditText etLoginPassword;
    private TextView tvRegisterHere;
    private Button btnLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        tvRegisterHere = findViewById(R.id.tvRegisterHere);
        btnLogin = findViewById(R.id.btnLogin);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Set up click listener for login button
        btnLogin.setOnClickListener(view -> loginUser());

        // Set up click listener for register text view
        tvRegisterHere.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));
    }

    private void loginUser() {
        String email = etLoginEmail.getText().toString();
        String password = etLoginPassword.getText().toString();

        // Validate email and password fields
        if (TextUtils.isEmpty(email)) {
            etLoginEmail.setError("Email cannot be empty");
            etLoginEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            etLoginPassword.setError("Password cannot be empty");
            etLoginPassword.requestFocus();
        } else {
            // Sign in with email and password using Firebase Auth
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "User logged on successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(LoginActivity.this, "Log in Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void OpenSignupPage(View view) {
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
    }
}
