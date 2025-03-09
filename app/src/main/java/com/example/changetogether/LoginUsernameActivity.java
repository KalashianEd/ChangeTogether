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

import com.example.changetogether.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginUsernameActivity extends AppCompatActivity {

    EditText usernameInput;
    Button nextButton;
    ProgressBar progressBar;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_username);

        // Bind UI elements
        usernameInput = findViewById(R.id.login_username);
        nextButton = findViewById(R.id.login_finish);
        progressBar = findViewById(R.id.login_progress_bar);

        // Get email from Intent
        email = getIntent().getStringExtra("email");

        // Check if email is provided
        if (email == null || email.isEmpty()) {
            Log.e("LoginUsernameActivity", "Email is missing!");
            Toast.makeText(this, "Email is missing!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up "Next" button
        nextButton.setOnClickListener(view -> setUsername());
    }

    /**
     * Sets the username.
     */
    void setUsername() {
        String username = usernameInput.getText().toString().trim();

        // Validate username
        if (username.isEmpty() || username.length() < 3) {
            usernameInput.setError("Username must be at least 3 characters long");
            return;
        }

        setInProgress(true);

        // Get current Firebase user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e("LoginUsernameActivity", "No user signed in");
            Toast.makeText(this, "User is not authenticated. Please log in.", Toast.LENGTH_SHORT).show();
            setInProgress(false);
            return;
        }

        String userId = currentUser.getUid();
        Log.d("LoginUsernameActivity", "Current user ID: " + userId);

        // Check if user exists in Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    // Update username
                    updateUsername(userRef, username);
                } else {
                    // Create new user
                    createNewUser(userRef, username);
                }
            } else {
                Log.e("LoginUsernameActivity", "Failed to check user existence", task.getException());
                Toast.makeText(this, "Failed to check user existence.", Toast.LENGTH_SHORT).show();
                setInProgress(false);
            }
        });
    }

    /**
     * Updates the username in Firestore.
     */
    private void updateUsername(DocumentReference userRef, String username) {
        userRef.update("username", username)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        navigateToMain();
                    } else {
                        Log.e("LoginUsernameActivity", "Failed to update username", task.getException());
                        Toast.makeText(this, "Failed to update username.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Creates a new user in Firestore.
     */
    private void createNewUser(DocumentReference userRef, String username) {
        UserModel userModel = new UserModel(email, username, Timestamp.now());
        userRef.set(userModel).addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()) {
                navigateToMain();
            } else {
                Log.e("LoginUsernameActivity", "Failed to create user", task.getException());
                Toast.makeText(this, "Failed to create user.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Navigates to the main activity.
     */
    private void navigateToMain() {
        Intent intent = new Intent(LoginUsernameActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Controls the progress state (shows/hides ProgressBar).
     *
     * @param inProgress true to show ProgressBar, false to hide.
     */
    void setInProgress(boolean inProgress) {
        progressBar.setVisibility(inProgress ? View.VISIBLE : View.GONE);
        nextButton.setVisibility(inProgress ? View.GONE : View.VISIBLE);
    }
}