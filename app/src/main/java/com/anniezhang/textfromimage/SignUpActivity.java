package com.anniezhang.textfromimage;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    // UI elements for email, password, login navigation, and registration
    TextInputEditText etRegEmail;
    TextInputEditText etRegPassword;
    TextView tvLoginHere;
    Button btnRegister;

    // Firebase Authentication instance
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize UI elements
        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPassword = findViewById(R.id.etRegPass);
        tvLoginHere = findViewById(R.id.tvLoginHere);
        btnRegister = findViewById(R.id.btnRegister);

        // Get FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // Set click listener for the Register button
        btnRegister.setOnClickListener(view -> {
            creatUser(); // Create a new user account
        });

        // Set click listener for the "Login Here" text
        tvLoginHere.setOnClickListener(view -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class)); // Navigate to LoginActivity
        });
    }

    private void creatUser() {
        // Get email and password inputs
        String email = etRegEmail.getText().toString();
        String password = etRegPassword.getText().toString();

        // Validate input fields
        if (TextUtils.isEmpty(email)) {
            etRegEmail.setError("Email cannot be empty"); // Show error if email is empty
            etRegEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            etRegPassword.setError("Password cannot be empty"); // Show error if password is empty
            etRegPassword.requestFocus();
        } else {
            // Create a new user using Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Show success message and navigate to LoginActivity
                            Toast.makeText(SignUpActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                        } else {
                            // Show error message
                            Toast.makeText(SignUpActivity.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
    }
}
