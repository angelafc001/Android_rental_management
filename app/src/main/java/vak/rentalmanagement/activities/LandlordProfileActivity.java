package vak.rentalmanagement.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.mikhaellopez.circularimageview.CircularImageView;

import javax.annotation.Nullable;

import vak.rentalmanagement.GlideApp;
import vak.rentalmanagement.R;
import vak.rentalmanagement.data.Landlord;
import vak.rentalmanagement.datamanager.DataManager;

/**
 * This class represents the Landlord Profile Activity.
 * It shows the Landlord' profile, including the landlord's name, photo, email, and phone.
 *
 * @author Koenn Becker
 * @author Vu Pham
 * @author Angela Ferro Capera
 * @version 1.0
 */
public class LandlordProfileActivity extends AbstractActivity {

    private DataManager dm;
    private FirebaseFirestore db;
    private CircularImageView imgLandlordPhoto;
    private TextView lblLandlordName;
    private TextView lblMobile;
    private TextView lblEmail;
    private Landlord landlord;

    // Data listeners
    private EventListener<DocumentSnapshot> landlordListener;
    private ListenerRegistration landlordReg;

    /**
     * Set up the LandlordProfileActivity layout.
     * Initialize Firebase, Data manager, and Listener
     * @param savedInstanceState the class state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();
        dm = DataManager.getInstance();

        imgLandlordPhoto =findViewById(R.id.imgLandlordPhoto);
        lblLandlordName = findViewById(R.id.txtTenantFirstName);
        lblMobile = findViewById(R.id.lblMobile);
        lblEmail = findViewById(R.id.lblEmail);
    }

    /**
     * Allows the back arrow in the option menu to go back
     * @return true if the activity was successfully finished
     */
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Set up the menu for LandlordProfileActivity
     * @param menu the menu object of this activity
     * @return true if the setting up is successful
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.landlord_profile, menu);
        return true;
    }

    /**
     * Stop all listeners when the app lose focus
     */
    @Override
    protected void onPause() {
        super.onPause();
        stopAllListeners();
    }

    /**
     * Set up the listeners to listen to any changes that happen to this particular landlord.
     */
    @Override
    protected void setupDataListeners() {
        stopAllListeners();
        DocumentReference docRef = db.collection("users").document(userId);
        landlordListener = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(LandlordProfileActivity.this, "Snapshot exception in Landlord", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    dm.setLandord(documentSnapshot.toObject(Landlord.class));
                    landlord = dm.getLandlord();
                    setupLandlordViews();
                } else {
                    FirebaseAuth.getInstance().signOut();
                }
            }
        };
        landlordReg = docRef.addSnapshotListener(landlordListener);
    }
    /**
     * Handle action bar item clicks here. The action bar will automatically handle clicks on
     * the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
     * @param item the menu item chosen
     * @return true if 
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            Intent intent = new Intent(LandlordProfileActivity.this, EditLandlordProfileActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Stop all the listeners if they're running and are not null.
     */
    private void stopAllListeners() {
        if (landlordReg != null && landlordListener != null) {
            landlordReg.remove();
        }
    }

    /**
     * Set up the context for the views
     */
    private void setupLandlordViews() {
        if (landlord.getPhoto() != null) {
            GlideApp.with(this)
                    .load(landlord.getPhoto())
                    .placeholder(R.drawable.unit_placeholder)
                    .into(imgLandlordPhoto);
        }
        lblLandlordName.setText(landlord.toString());
        lblMobile.setText(landlord.getPhone());
        lblEmail.setText(landlord.getEmail());
    }
}
