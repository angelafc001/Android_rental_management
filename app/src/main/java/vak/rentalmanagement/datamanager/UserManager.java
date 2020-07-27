package vak.rentalmanagement.datamanager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import vak.rentalmanagement.data.Landlord;
import vak.rentalmanagement.data.Tenant;

/**
 * CA class to help manage users in Firestore DB.
 *
 * @author Vu Pham
 * @author Koenn Becker
 * @author Angela Ferro Capera
 * @version 1.0
 */
public class UserManager {

    private static UserManager instance;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    /**
     * Default constructor
     */
    private UserManager() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    /**
     * Gets the singleton instance of UserManager.
     * @return UserManager - the singleton instance.
     */
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    /**
     * Creates a new Landlord and uploads it to Firestore.
     * @param user the user to add.
     * @param listener the callback after user is added.
     */
    public void createUser(Landlord user, OnCompleteListener<Void> listener) {
        DocumentReference ref = db.collection("users").document(auth.getCurrentUser().getUid());
        ref.set(user).addOnCompleteListener(listener);
    }

    /**
     * Creates a new Tenant and uploads it to Firestore.
     * @param user the user to add.
     * @param listener the callback after user is added.
     */
    public void createUser(Tenant user, OnCompleteListener<Void> listener) {
        DocumentReference ref = db.collection("users").document(auth.getCurrentUser().getUid());
        ref.set(user).addOnCompleteListener(listener);
    }
}
