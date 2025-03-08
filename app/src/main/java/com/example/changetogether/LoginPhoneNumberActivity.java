package com.example.changetogether;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class LoginPhoneNumberActivity extends AppCompatActivity {

    EditText emailInput;
    Button nextBtn;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone_number);

        // Привязываем элементы интерфейса
        emailInput = findViewById(R.id.login_mobile_number); // Переименуем поле ввода
        nextBtn = findViewById(R.id.send_otp_btn);
        progressBar = findViewById(R.id.login_progress_bar);

        // Скрываем прогресс-бар по умолчанию
        progressBar.setVisibility(View.GONE);

        // Обработчик нажатия на кнопку "Далее"
        nextBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();

            // Проверяем, что поле не пустое
            if (email.isEmpty()) {
                emailInput.setError("Поле email не может быть пустым");
                return;
            }

            // Проверяем формат email
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.setError("Введите корректный email");
                return;
            }

            // Log email value before proceeding
            Log.d("LoginPhoneNumberActivity", "Email entered: " + email);

            // Показываем прогресс-бар
            progressBar.setVisibility(View.VISIBLE);

            // Имитация отправки данных (можно заменить на реальный запрос к серверу)
            new android.os.Handler().postDelayed(() -> {
                // Скрываем прогресс-бар после завершения
                progressBar.setVisibility(View.GONE);

                // Переходим на следующую активность
                Intent intent = new Intent(LoginPhoneNumberActivity.this, LoginOtpActivity.class);
                intent.putExtra("email", email); // Передаем email
                Log.d("LoginPhoneNumberActivity", "Passing email to LoginOtpActivity: " + email);
                startActivity(intent);
            }, 2000); // Задержка для имитации загрузки (2 секунды)
        });
    }
}
