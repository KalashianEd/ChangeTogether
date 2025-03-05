package com.example.changetogether;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.changetogether.model.UserModel;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginUsernameActivity extends AppCompatActivity {

    EditText usernameInput;
    Button nextButton;
    ProgressBar progressBar;
    String email; // Заменяем phoneNumber на email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_username);

        usernameInput = findViewById(R.id.login_username);
        nextButton = findViewById(R.id.login_finish);
        progressBar = findViewById(R.id.login_progress_bar);

        email = getIntent().getStringExtra("email"); // Получаем email из предыдущей активности

        nextButton.setOnClickListener(view -> setUsername());
    }

    void setUsername() {
        String username = usernameInput.getText().toString().trim();
        if (username.isEmpty() || username.length() < 3) {
            usernameInput.setError("Имя должно быть не менее 3 символов");
            return;
        }

        setInProgress(true);

        DocumentReference userRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(FirebaseAuth.getInstance().getUid());

        // Сохраняем email вместо phoneNumber
        userRef.set(new UserModel(email, username, Timestamp.now())).addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()) {
                Intent intent = new Intent(LoginUsernameActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    void setInProgress(boolean inProgress) {
        progressBar.setVisibility(inProgress ? View.VISIBLE : View.GONE);
        nextButton.setVisibility(inProgress ? View.GONE : View.VISIBLE);
    }
}