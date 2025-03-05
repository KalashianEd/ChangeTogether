package com.example.changetogether;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import utils.AndroidUtil;

public class LoginOtpActivity extends AppCompatActivity {

    String email;
    EditText otpInput;
    Button nextButton;
    ProgressBar progressBar;
    TextView resendOtpTextView;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);

        otpInput = findViewById(R.id.login_otp);
        nextButton = findViewById(R.id.login_next_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        resendOtpTextView = findViewById(R.id.resend_otp_textview);

        email = getIntent().getStringExtra("email");

        sendSignInLink(email);

        // Проверяем, открыто ли приложение через ссылку
        if (isSignInWithEmailLink(FirebaseAuth.getInstance(), getIntent())) {
            handleSignInWithEmailLink();
        }

        resendOtpTextView.setOnClickListener(view -> sendSignInLink(email));
    }

    private void sendSignInLink(String email) {
        setInProgress(true);

        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                .setUrl("https://your-app-url.com") // URL, куда будет перенаправлен пользователь
                .setHandleCodeInApp(true) // Указывает, что ссылка должна обрабатываться внутри приложения
                .setAndroidPackageName(
                        "com.example.changetogether", // Ваш package name
                        true, // Установлено ли приложение
                        null) // Минимальная версия приложения
                .build();

        mAuth.sendSignInLinkToEmail(email, actionCodeSettings)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        AndroidUtil.showToast(getApplicationContext(), "Ссылка отправлена на email: " + email);
                    } else {
                        AndroidUtil.showToast(getApplicationContext(), "Ошибка отправки ссылки: " + task.getException().getMessage());
                    }
                });
    }

    private boolean isSignInWithEmailLink(FirebaseAuth auth, Intent intent) {
        if (intent != null && intent.getData() != null) {
            String link = intent.getData().toString();
            return auth.isSignInWithEmailLink(link);
        }
        return false;
    }

    private void handleSignInWithEmailLink() {
        Uri deepLink = getIntent().getData();
        if (deepLink != null) {
            mAuth.signInWithEmailLink(email, deepLink.toString())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            AndroidUtil.showToast(getApplicationContext(), "Вход выполнен успешно!");

                            Intent intent = new Intent(LoginOtpActivity.this, LoginUsernameActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            finish();
                        } else {
                            AndroidUtil.showToast(getApplicationContext(), "Ошибка входа: " + task.getException().getMessage());
                        }
                    });
        }
    }

    void setInProgress(boolean inProgress) {
        progressBar.setVisibility(inProgress ? View.VISIBLE : View.GONE);
        nextButton.setVisibility(inProgress ? View.GONE : View.VISIBLE);
    }
}