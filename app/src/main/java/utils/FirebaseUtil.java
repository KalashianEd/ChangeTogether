package utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtil {

    public static DocumentReference currentUserDetails() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return null;  // Пользователь не аутентифицирован
        }
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return FirebaseFirestore.getInstance().collection("users").document(uid);
    }
}
