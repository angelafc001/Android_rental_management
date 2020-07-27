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

import vak.rentalmanagement.Extras;
import vak.rentalmanagement.GlideApp;
import vak.rentalmanagement.R;
import vak.rentalmanagement.data.Tenant;
import vak.rentalmanagement.datamanager.DataManager;

/**
 * This class represent the profile of the current tenant
 * @author Vu Pham
 * @author  Koenn Becker
 * @author  Angela Ferro Capera
 * @version 1.0
 */
public class TenantProfileActivity extends AbstractActivity {

    private DataManager dm;
    private FirebaseFirestore db;
    private Tenant tenant;
    private TextView lblTenantName;
    private TextView lblPhone;
    private TextView lblEmail;
    private CircularImageView imgTenant;

    private EventListener<DocumentSnapshot> tenantListener;
    private ListenerRegistration tenantReg;

    /**
     * On create of TenantProfileActivity
     * @param savedInstanceState the state of the class
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();
        dm = DataManager.getInstance();

        lblTenantName = findViewById(R.id.lblTenantName);
        lblPhone = findViewById(R.id.lblTenantPhone);
        lblEmail = findViewById(R.id.lblTenantEmail);
        imgTenant = findViewById(R.id.imgTenantPhoto);

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
     * Stop all listeners when the app lose focus
     */
    @Override
    protected void onPause() {
        super.onPause();
        stopAllListeners();
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
            Intent intent = new Intent(TenantProfileActivity.this, TenantEditActivity.class);
            intent.putExtra(Extras.TENANT_ID, userId);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     *
     * @param menu the top menu
     * @return true if the menu was set up correctly, false otherwise
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tenant_profile, menu);
        return true;
    }

    /**
     * Set up the listeners to listen to any changes that happen to this particular landlord.
     */
    @Override
    protected void setupDataListeners() {
        DocumentReference docRef = db.collection("users").document(userId);
        tenantListener = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(TenantProfileActivity.this, "Snapshot exception in TenantProfile", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    tenant = documentSnapshot.toObject(Tenant.class);
                    dm.setTenant(tenant);
                    setUpTenantDetails();
                } else {
                    FirebaseAuth.getInstance().signOut();
                }
            }
        };
        tenantReg = docRef.addSnapshotListener(tenantListener);
    }

    /**
     * Update the Tenant's info after being edited
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        setUpTenantDetails();
    }

    /**
     * Set up the  current Tenant information to be display in the layout
     */
    private void setUpTenantDetails(){
        if (tenant.getPhoto() != null) {
            GlideApp.with(this)
                    .load(tenant.getPhoto())
                    .placeholder(R.drawable.unit_placeholder)
                    .into(imgTenant);
        }
        lblTenantName.setText(tenant.toString());
        lblPhone.setText(tenant.getPhone());
        lblEmail.setText(tenant.getEmail());
    }

    /**
     * Stop all the listeners if they're running and are not null.
     */
    private void stopAllListeners() {
        if (tenantReg != null && tenantListener != null) {
            tenantReg.remove();
        }
    }
}
