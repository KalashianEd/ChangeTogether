package com.example.changetogether;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import utils.AndroidUtil;

public class LoginOtpActivity extends AppCompatActivity {

    String phoneNumber;
    Long timeoutSeconds = 60L;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;

    EditText otpinput;
    Button nextButton;
    ProgressBar progressBar;
    TextView resendOtpTextView;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_otp);

        otpinput = findViewById(R.id.login_otp);
        nextButton = findViewById(R.id.login_next_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        resendOtpTextView = findViewById(R.id.resend_otp_textview);

        phoneNumber = getIntent().getStringExtra("phone");
        SendOtp(phoneNumber, false);

        nextButton.setOnClickListener(view -> {
            String enteredOtp = otpinput.getText().toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, enteredOtp);
            SignIn(credential);
            setInProgress(true);
            mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    setInProgress(false);

                    if (task.isSuccessful()) {
                        Intent intent = new Intent(LoginOtpActivity.this, LoginUsernameActivity.class);
                        intent.putExtra("phone", phoneNumber);
                        startActivity(intent);
                        // TODO: Перейти в главный экран
                    } else {
                        AndroidUtil.showToast(getApplicationContext(), "Login failed: " + task.getException().getMessage());
                    }

                }
            });


        });

        resendOtpTextView.setOnClickListener(view -> {
            SendOtp(phoneNumber, true);
        });

    }

    void SendOtp(String phoneNumber, boolean isResend) {
        startResendTimer();
        setInProgress(true);

        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                SignIn(phoneAuthCredential);
                                setInProgress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                AndroidUtil.showToast(getApplicationContext(), "Verification failed");
                                setInProgress(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode = s;
                                resendingToken = forceResendingToken;
                                AndroidUtil.showToast(getApplicationContext(), "OTP sent successfully");
                                setInProgress(false);
                            }
                        });

        // Если это повторная отправка, используем токен перезапроса
        if (isResend && resendingToken != null) {
            builder.setForceResendingToken(resendingToken);
        }
        PhoneAuthProvider.verifyPhoneNumber(builder.build());  // OTP отправляется в любом случае

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

    void SignIn(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        AndroidUtil.showToast(getApplicationContext(), "Login successful!");
                        // TODO: Перейти в главный экран
                    } else {
                        AndroidUtil.showToast(getApplicationContext(), "Login failed: " + task.getException().getMessage());
                    }
                });

    }

    void startResendTimer() {
        resendOtpTextView.setEnabled(false);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeoutSeconds--;
                resendOtpTextView.setText("Resend code in " + timeoutSeconds + " seconds");
                if(timeoutSeconds<=0){
                    timeoutSeconds = 60L;
                    timer.cancel();
                    runOnUiThread(() ->{
                        resendOtpTextView.setEnabled(true);
                    });
                }


            }
        }, 0, 1000);
    }

}
