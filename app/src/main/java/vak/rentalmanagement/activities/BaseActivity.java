package vak.rentalmanagement.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import vak.rentalmanagement.Extras;
import vak.rentalmanagement.R;

/**
 * This activity determines whether a user is a landlord or tenant, or if they are a new user completely.
 *
 * @author Koenn Becker
 * @autor Vu Pham
 * @autor Angela Ferro Capera
 * @version  1.0
 */
public class BaseActivity extends AbstractActivity {

    private FirebaseFirestore db;

    /**
     * Instantiates the database.
     *
     * @param savedInstanceState the previous state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Launcher);
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    /**
     * This method checks if there is a user document in Firebase for the logged in user. If there is not,
     * they will be directed to the NewUserActivity to choose if they would like to be a landlord or tenant.
     * If they do have a document, it will check what type of user they are and direct them to the correct activity.
     */
    protected void setupDataListeners() {
        DocumentReference ref = db.collection("users").document(userId);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    assert doc != null;
                    if (doc.exists()) {
                        // User exists, determine if they are a Landlord
                        if (doc.contains("landlordId")) {
                            Intent intent = new Intent(BaseActivity.this, LandlordBaseActivity.class);
                            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            if (doc.get("tenantLandlordId") == null) {
                                // No unit
                                Intent intent = new Intent(BaseActivity.this, TenantBaseActivityNew.class);
                                String phone = (String) doc.get("phone");
                                intent.putExtra(Extras.TENANT_ID, phone);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(BaseActivity.this, TenantBaseActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }
                    } else {
                        // New user
                        Intent intent = new Intent(BaseActivity.this, NewUserActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            }
        });
    }

}
