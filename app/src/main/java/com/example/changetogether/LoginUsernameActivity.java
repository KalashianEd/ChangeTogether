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
import com.google.firebase.firestore.DocumentSnapshot;

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

        phoneNumber = getIntent().getStringExtra("phone"); // Теперь phoneNumber сохраняется в поле класса
        getUsername();

        nextButton.setOnClickListener(view -> setUsername());
    }

    void setUsername() {
        String username = usernameInput.getText().toString();
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

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()) {
                Intent intent = new Intent(LoginUsernameActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    void getUsername() {
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()) {
                userModel = task.getResult().toObject(UserModel.class);
                if (userModel != null) {
                    usernameInput.setText(userModel.getUsername());
                }
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
