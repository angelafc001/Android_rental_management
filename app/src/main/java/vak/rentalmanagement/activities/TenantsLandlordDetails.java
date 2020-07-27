package vak.rentalmanagement.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import javax.annotation.Nullable;

import vak.rentalmanagement.Extras;
import vak.rentalmanagement.GlideApp;
import vak.rentalmanagement.R;
import vak.rentalmanagement.data.Landlord;
import vak.rentalmanagement.datamanager.DataManager;

/**
 * This class represents the contact information of the current tenant's landlord
 * @author Vu Pham
 * @author Koenn Becker
 * @author Angela Ferro Capera
 * @version 1.0
 */
public class TenantsLandlordDetails extends AbstractActivity {
    private Landlord landlord;
    private String landlordId;
    private DataManager dm;
    private ImageView imgContactPhoto;
    private TextView lblLandlordDetailsName;
    private TextView lblLandlordDetailsPhone;
    private TextView lblLandlordDetailsEmail;
    private Button btnMessage;
    private Button btnCall;
    private Button btnEmail;
    //Data listeners
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EventListener<DocumentSnapshot> landlordListener;
    private ListenerRegistration landlordReg;

    /**
     * Set up the TenantLandlordDetails layout.
     * Initialize Data manager, calls listeners to be set up
     * @param savedInstanceState the class state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_landlord);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dm = DataManager.getInstance();
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        landlordId =  intent.getStringExtra(Extras.LANDLORD_ID);
        lblLandlordDetailsName = findViewById(R.id.lblLandlordDetailsName);
        lblLandlordDetailsPhone = findViewById(R.id.lblLandlordDetailsphone);
        lblLandlordDetailsEmail = findViewById(R.id.lblLandlordDetailsEmail);
        imgContactPhoto = findViewById(R.id.imgContactPhoto);
        btnMessage = findViewById(R.id.btnMessage);
        btnCall = findViewById(R.id.btnCall);
        btnEmail = findViewById(R.id.btnEmail);
    }

    /**
     * It goes back to parent activity TenantBaseActivity
     * @return True if returned, otherwise false
     */
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAllListeners();
    }

    /**
     * Display the current Landlord information in the Layout
     */
    private void displayLandlordInfo() {
        String picture = landlord.getPhoto();
        if (picture != null) {
            GlideApp.with(this)
                    .load(picture)
                    .placeholder(R.drawable.unit_placeholder)
                    .into(imgContactPhoto);
        }
        lblLandlordDetailsName.setText(landlord.toString());
        lblLandlordDetailsPhone.setText(landlord.getPhone());
        lblLandlordDetailsEmail.setText(landlord.getEmail());
    }

    /**
     * Opens Email app of preference. The email app will have the Landlord email information in it,
     * ready to send an email
     * @param view The bottom clicked
     */
    public void emailLandlordOnClick(View view) {
        String landlordEmail = landlord.getEmail();
        String[] landlordEmailArray = {landlordEmail};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, landlordEmailArray);
        emailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(emailIntent, "Choose an email app: "));
    }

    /**
     * Set up listener for any changes on the Landlord of the current Tenant
     */
    @Override
    public void setupDataListeners() {
        stopAllListeners();
        DocumentReference landlordRef = db.collection("users").document(landlordId);
        landlordListener = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(TenantsLandlordDetails.this, "Snapshot exception in Tenant Details", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Landlord newLandlord = documentSnapshot.toObject(Landlord.class);
                    dm.setLandord(newLandlord);
                    landlord = newLandlord;
                    displayLandlordInfo();
                }
            }
        };
        landlordReg = landlordRef.addSnapshotListener(landlordListener);
    }

    private void stopAllListeners() {
        if (landlordReg != null && landlordListener != null) {
            landlordReg.remove();
        }
    }

    /**
     * Start Messenger app ready to message Landlord
     * @param view Button clicked
     */
    public void btnMessageOnClick(View view) {
        String smsNumber = "smsto:" + landlord.getPhone();
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse(smsNumber));
        if (smsIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(smsIntent);
        } else {
            finish();
        }
    }

    /**
     * Prompt the dial keyboard with the landlords phone number ready to call
     *
     * @param view The button clicked
     */
    public void btnCallOnClick(View view) {
        String phoneNumber = String.format("tel: %s", landlord.getPhone());
        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse(phoneNumber));
        if (dialIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(dialIntent);
        } else {
            finish();
        }
    }
}
