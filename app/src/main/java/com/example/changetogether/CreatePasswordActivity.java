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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreatePasswordActivity extends AppCompatActivity {

    EditText passwordInput;
    Button createPasswordButton;
    ProgressBar progressBar;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);

        // Bind UI elements
        passwordInput = findViewById(R.id.create_password);
        createPasswordButton = findViewById(R.id.create_password_button);
        progressBar = findViewById(R.id.create_password_progress_bar);

        // Hide progress bar by default
        progressBar.setVisibility(View.GONE);

        // Handle "Create Password" button click
        createPasswordButton.setOnClickListener(v -> {
            String password = passwordInput.getText().toString().trim();

            // Validate password field
            if (password.isEmpty()) {
                passwordInput.setError("Password cannot be empty");
                return;
            }

            // Show progress bar
            progressBar.setVisibility(View.VISIBLE);

            // Create password for the user
            createPassword(password);
        });
    }

    /**
     * Creates a password for the user.
     */
    private void createPassword(String password) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.updatePassword(password)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBar.setVisibility(View.GONE);

                            if (task.isSuccessful()) {
                                Log.d("CreatePasswordActivity", "Password created successfully.");
                                Toast.makeText(CreatePasswordActivity.this, "Password created successfully.", Toast.LENGTH_SHORT).show();

                                // Navigate to username activity
                                Intent intent = new Intent(CreatePasswordActivity.this, LoginUsernameActivity.class);
                                intent.putExtra("email", user.getEmail());
                                startActivity(intent);
                                finish();
                            } else {
                                Log.e("CreatePasswordActivity", "Failed to create password", task.getException());
                                Toast.makeText(CreatePasswordActivity.this, "Failed to create password.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            progressBar.setVisibility(View.GONE);
            Log.e("CreatePasswordActivity", "No user is signed in.");
            Toast.makeText(this, "No user is signed in.", Toast.LENGTH_SHORT).show();
        }
    }
}