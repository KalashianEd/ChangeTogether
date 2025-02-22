package com.example.changetogether;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.changetogether.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import utils.FirebaseUtil;

public class LoginUsernameActivity extends AppCompatActivity {

    EditText usernameInput;
    Button nextButton;
    ProgressBar progressBar;
    String phoneNumber;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_username);

        usernameInput = findViewById(R.id.login_username);
        nextButton = findViewById(R.id.login_finish);
        progressBar = findViewById(R.id.login_progress_bar);

        phoneNumber = getIntent().getStringExtra("phone");
        getUsername();

        nextButton.setOnClickListener(view -> setUsername());
    }

    void setUsername() {
        String username = usernameInput.getText().toString().trim();
        if (username.isEmpty() || username.length() < 3) {
            usernameInput.setError("Username is less than 3 characters");
            return;
        }

        setInProgress(true);

        if (userModel != null) {
            userModel.setUsername(username);
        } else {
            userModel = new UserModel(phoneNumber, username, Timestamp.now());
        }

        DocumentReference userRef = FirebaseUtil.currentUserDetails();
        if (userRef == null) {
            setInProgress(false);
            usernameInput.setError("Ошибка получения пользователя");
            return;
        }

        userRef.set(userModel).addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()) {
                Intent intent = new Intent(LoginUsernameActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Exception e = task.getException();
                if (e != null) {
                    e.printStackTrace();
                    usernameInput.setError("Ошибка сохранения: " + e.getMessage());
                }
            }
        });
    }

    void getUsername() {
        setInProgress(true);

        DocumentReference userRef = FirebaseUtil.currentUserDetails();
        if (userRef == null) {
            setInProgress(false);
            usernameInput.setError("Ошибка получения пользователя");
            return;
        }

        userRef.get().addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    userModel = document.toObject(UserModel.class);
                    if (userModel != null) {
                        usernameInput.setText(userModel.getUsername());
                    }
                } else {
                    System.out.println("Документ не найден!");
                }
            } else {
                Exception e = task.getException();
                if (e != null) e.printStackTrace();
            }
        });
    }

    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            nextButton.setVisibility(View.VISIBLE);
        }
    }
}