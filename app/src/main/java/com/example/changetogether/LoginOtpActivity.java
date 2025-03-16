package com.example.changetogether;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginOtpActivity extends AppCompatActivity {

    private String email;
    private Button nextButton;
    private ProgressBar progressBar;
    private TextView resendOtpTextView;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);

        // Initialize UI elements
        nextButton = findViewById(R.id.login_next_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        resendOtpTextView = findViewById(R.id.resend_otp_textview);

        // Get email from Intent
        email = getIntent().getStringExtra("email");

        // Check if email is provided
        if (email == null || email.isEmpty()) {
            Log.e("LoginOtpActivity", "Email not received!");
            Toast.makeText(this, "Email must be provided.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up "Resend OTP" button
        resendOtpTextView.setOnClickListener(view -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                sendVerificationEmail(user);
            }
        });

        // Set up "Next" button
        nextButton.setOnClickListener(view -> checkEmailVerification());
    }

    /**
     * Checks if the user's email has been verified.
     */
    private void checkEmailVerification() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            setInProgress(true);
            user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    setInProgress(false);
                    if (task.isSuccessful() && user.isEmailVerified()) {
                        Toast.makeText(LoginOtpActivity.this, "Email verified!", Toast.LENGTH_SHORT).show();

                        // Navigate to the create password activity
                        Intent intent = new Intent(LoginOtpActivity.this, CreatePasswordActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginOtpActivity.this, "Please verify your email first.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "No user signed in.", Toast.LENGTH_SHORT).show();
        }
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
                            Toast.makeText(LoginOtpActivity.this, "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("LoginOtpActivity", "Failed to send verification email", task.getException());
                            Toast.makeText(LoginOtpActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Controls the progress state (shows/hides ProgressBar).
     *
     * @param inProgress true to show ProgressBar, false to hide.
     */
    private void setInProgress(boolean inProgress) {
        progressBar.setVisibility(inProgress ? View.VISIBLE : View.GONE);
        nextButton.setVisibility(inProgress ? View.GONE : View.VISIBLE);
    }
}