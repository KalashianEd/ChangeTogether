package com.example.changetogether;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import utils.AndroidUtil;

public class LoginOtpActivity extends AppCompatActivity {

    private String email;
    private EditText otpInput;
    private Button nextButton;
    private ProgressBar progressBar;
    private TextView resendOtpTextView;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);

        otpInput = findViewById(R.id.login_otp);
        nextButton = findViewById(R.id.login_next_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        resendOtpTextView = findViewById(R.id.resend_otp_textview);

        // Retrieve email from intent
        email = getIntent().getStringExtra("email");

        if (email == null || email.isEmpty()) {
            Log.e("LoginOtpActivity", "Email not received!");
            AndroidUtil.showToast(getApplicationContext(), "Email must be provided.");
            finish();
            return;
        }

        // Send the verification email
        sendVerificationEmail();

        resendOtpTextView.setOnClickListener(view -> sendVerificationEmail());

        nextButton.setOnClickListener(view -> checkEmailVerification());
    }

    private void sendVerificationEmail() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            Log.e("LoginOtpActivity", "No user signed in");
            AndroidUtil.showToast(getApplicationContext(), "No user signed in.");
            return;
        }

        // If the user's email is not verified yet, send a verification email
        if (!user.isEmailVerified()) {
            setInProgress(true);
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        setInProgress(false);
                        if (task.isSuccessful()) {
                            AndroidUtil.showToast(getApplicationContext(), "Verification email sent to: " + user.getEmail());
                        } else {
                            AndroidUtil.showToast(getApplicationContext(), "Error: " + task.getException().getMessage());
                        }
                    });
        } else {
            AndroidUtil.showToast(getApplicationContext(), "Email already verified!");
        }
    }

    private void checkEmailVerification() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            setInProgress(true);
            user.reload().addOnCompleteListener(task -> {
                setInProgress(false);
                if (task.isSuccessful() && user.isEmailVerified()) {
                    AndroidUtil.showToast(getApplicationContext(), "Email verified!");

                    // Proceed to the next screen where the user will enter their username
                    Intent intent = new Intent(LoginOtpActivity.this, LoginUsernameActivity.class);
                    intent.putExtra("email", email);  // Pass the email to the next screen
                    startActivity(intent);
                    finish();
                } else {
                    AndroidUtil.showToast(getApplicationContext(), "Please verify your email first.");
                }
            });
        }
    }

    private void setInProgress(boolean inProgress) {
        progressBar.setVisibility(inProgress ? View.VISIBLE : View.GONE);
        nextButton.setVisibility(inProgress ? View.GONE : View.VISIBLE);
    }
}
