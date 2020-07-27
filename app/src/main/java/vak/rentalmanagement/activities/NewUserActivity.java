package vak.rentalmanagement.activities;

import androidx.annotation.NonNull;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import vak.rentalmanagement.Extras;
import vak.rentalmanagement.R;
import vak.rentalmanagement.data.Landlord;
import vak.rentalmanagement.data.Tenant;
import vak.rentalmanagement.datamanager.UserManager;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * This activity lets the user choose what type of account they would like to create.
 *
 * @author Koenn Becker
 * @author Vu Pham
 * @author Angela Ferro Capera
 * @version 1.0
 */
public class NewUserActivity extends AbstractActivity {

    private TextInputEditText txtNewUserPhone;
    private TextInputLayout layNewUserPhone;
    private FirebaseAuth auth;
    private UserManager um;
    private FirebaseFirestore db;

    /**
     * Instantiates the views and starts the animations.
     *
     * @param savedInstanceState the previous state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        Button btnNewTenant = findViewById(R.id.btnNewTenant);
        Button btnNewLandlord = findViewById(R.id.btnNewLandlord);
        TextView lblNewUserIntro = findViewById(R.id.lblNewUserIntro);
        txtNewUserPhone = findViewById(R.id.txtNewUserPhone);
        txtNewUserPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        layNewUserPhone = findViewById(R.id.layNewUserPhone);
        auth = FirebaseAuth.getInstance();
        um = UserManager.getInstance();
        db = FirebaseFirestore.getInstance();

        if (savedInstanceState == null) {

            Animation fade = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
            fade.setDuration(450);
            fade.setStartOffset(1000);
            lblNewUserIntro.startAnimation(fade);

            Animation phone = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
            phone.setStartOffset(1500);
            phone.setDuration(400);
            layNewUserPhone.startAnimation(phone);

            Animation left = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left);
            left.setDuration(650);
            left.setStartOffset(1600);
            left.setInterpolator(new FastOutSlowInInterpolator());
            btnNewTenant.startAnimation(left);

            Animation right = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_right);
            right.setDuration(650);
            right.setStartOffset(1600);
            right.setInterpolator(new FastOutSlowInInterpolator());
            btnNewLandlord.startAnimation(right);
        }
    }

    /**
     * Unused for this activity, no data.
     */
    @Override
    protected void setupDataListeners() {

    }

    /**
     * Event handler for the "Landlord" button.
     * @param view The view that started the event
     */
    public void createLandlord(View view) {
        if (validPhone()) {
            String phone = layNewUserPhone.getEditText().getText().toString();
            validateNewPhone(phone, true);
        }
    }

    private void validateNewPhone(String phone, final boolean isLandlord) {
        Query phoneRef = db.collection("users").whereEqualTo("phone", phone);
        phoneRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()) {
                    createNewUser(isLandlord);
                } else {
                    layNewUserPhone.setError("Someone already has that phone number");
                }
            }
        });
    }

    private void createNewUser(boolean isLandlord) {
        String displayName = auth.getCurrentUser().getDisplayName();
        int spaceIndex = displayName.indexOf(" ");
        String first = displayName;
        String last = "";
        if (spaceIndex > 0) {
            first = displayName.substring(0, spaceIndex);
            last = displayName.substring(spaceIndex);
        }
        final String phone = layNewUserPhone.getEditText().getText().toString();
        if (isLandlord) {
            Landlord landlord = new Landlord(first, last, null, auth.getCurrentUser().getEmail(), phone, new ArrayList<String>());
            landlord.setLandlordId(auth.getCurrentUser().getUid());

            um.createUser(landlord, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(NewUserActivity.this, "New user created!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(NewUserActivity.this, LandlordBaseActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(NewUserActivity.this, "New user unsuccessful", Toast.LENGTH_SHORT).show();
                        auth.signOut();
                    }
                }
            });
        } else {
            final Tenant tenant = new Tenant(first, last, null, auth.getCurrentUser().getEmail(), phone);
            tenant.setTenantId(auth.getCurrentUser().getUid());

            um.createUser(tenant, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(NewUserActivity.this, "New user created!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(NewUserActivity.this, TenantBaseActivityNew.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra(Extras.TENANT_ID, phone);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(NewUserActivity.this, "New user unsuccessful", Toast.LENGTH_SHORT).show();
                        auth.signOut();
                    }
                }
            });
        }

    }

    /**
     * Event handler for the "Tenant" button.
     * @param view The view that started the event
     */
    public void createTenant(View view) {
        if (validPhone()) {
            final String phone = layNewUserPhone.getEditText().getText().toString();
            validateNewPhone(phone, false);
        }
    }

    /**
     * Event handler for the "Logout" button
     * @param view The view that started the event
     */
    public void btnNewUserLogoutOnClick(View view) {
        auth.signOut();
    }

    /**
     * Validates the phone input to unsure it is not empty.
     * @return True if valid
     */
    private boolean validPhone() {
        if (txtNewUserPhone.getText().toString().trim().isEmpty()) {
            layNewUserPhone.setError("Phone number cannot be empty.");
            layNewUserPhone.setErrorEnabled(true);
            return false;
        }
        layNewUserPhone.setError(null);
        layNewUserPhone.setErrorEnabled(false);
        return true;
    }

}
