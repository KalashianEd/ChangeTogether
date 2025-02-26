package com.example.changetogether;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private TextView greetingTextView, registrationNumberTextView, phoneTextView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        greetingTextView = view.findViewById(R.id.greetingTextView);
        registrationNumberTextView = view.findViewById(R.id.registrationNumberTextView);
        phoneTextView = view.findViewById(R.id.phoneTextView);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser(); //heyhey
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String phoneNumber = currentUser.getPhoneNumber();

            phoneTextView.setText("Ваш номер: " + (phoneNumber != null ? phoneNumber : "Не указан"));

            DocumentReference userRef = db.collection("users").document(userId);
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String username = documentSnapshot.getString("username");
                    Long registrationNumber = documentSnapshot.getLong("registrationNumber");

                    greetingTextView.setText("Здравствуйте, " + (username != null ? username : "Пользователь") + "!");
                    registrationNumberTextView.setText("Номер регистрации: " + (registrationNumber != null ? registrationNumber : "Не указан"));
                }
            });
        }

        return view;
    }
}
