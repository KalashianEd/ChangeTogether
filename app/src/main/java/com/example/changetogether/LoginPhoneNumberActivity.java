package com.example.changetogether;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPhoneNumberActivity extends AppCompatActivity {

    EditText emailInput;
    Button nextBtn;
    ProgressBar progressBar;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone_number);

        // Bind UI elements
        emailInput = findViewById(R.id.login_mobile_number);
        nextBtn = findViewById(R.id.send_otp_btn);
        progressBar = findViewById(R.id.login_progress_bar);

        // Hide progress bar by default
        progressBar.setVisibility(View.GONE);

        // Handle "Next" button click
        nextBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();

            // Validate email field
            if (email.isEmpty()) {
                emailInput.setError("Email cannot be empty");
                return;
            }

            // Validate email format
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.setError("Please enter a valid email");
                return;
            }

            // Log email for debugging
            Log.d("LoginPhoneNumberActivity", "Email entered: " + email);

            // Show progress bar
            progressBar.setVisibility(View.VISIBLE);

            // Create a new user in Firebase Authentication
            createUserInFirebase(email);
        });
    }

    /**
     * Creates a new user in Firebase Authentication.
     */
    private void createUserInFirebase(String email) {
        // Use a temporary password (or enable email-only sign-in in Firebase Console)
        String temporaryPassword = "TempPassword123!";

        mAuth.createUserWithEmailAndPassword(email, temporaryPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            Log.d("LoginPhoneNumberActivity", "User created successfully.");
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {
                                // Send verification email
                                sendVerificationEmail(user);
                            } else {
                                Log.e("LoginPhoneNumberActivity", "User is null");
                                Toast.makeText(LoginPhoneNumberActivity.this, "Failed to create user.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("LoginPhoneNumberActivity", "Failed to create user", task.getException());
                            Toast.makeText(LoginPhoneNumberActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Sends a verification email to the user.
     */
    private void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("LoginPhoneNumberActivity", "Verification email sent.");
                            Toast.makeText(LoginPhoneNumberActivity.this, "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();

                            // Navigate to OTP activity
                            Intent intent = new Intent(LoginPhoneNumberActivity.this, LoginOtpActivity.class);
                            intent.putExtra("email", user.getEmail());
                            startActivity(intent);
                        } else {
                            Log.e("LoginPhoneNumberActivity", "Failed to send verification email", task.getException());
                            Toast.makeText(LoginPhoneNumberActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}