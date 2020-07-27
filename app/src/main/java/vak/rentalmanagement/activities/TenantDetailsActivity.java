package vak.rentalmanagement.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import vak.rentalmanagement.data.Tenant;
import vak.rentalmanagement.data.Unit;
import vak.rentalmanagement.datamanager.DataManager;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * This class represents the tenantDetailsActivity. It displays the details of the current Tenant.
 * @version  1.0
 * @author Vu Pham
 * @author  Koenn Becker
 * @author Angela Ferro Capera
 */
public class TenantDetailsActivity extends AbstractActivity {


    private Tenant tenant;
    private Unit unit;
    private String tenantId;
    private DataManager dm;
    private ImageView imgContactPhoto;
    private TextView lblTenantName;
    private TextView lblTenantPhone;
    private TextView lblTenantEmail;
    private CardView cardTenantHouse;
    private TextView lblTenantHouseName;
    private TextView lblTenantHouseAddress;
    private ImageView imgTenantHousePhoto;
    private Button btnMessage;
    private Button btnCall;
    private Button btnEmail;

    private FirebaseFirestore db;
    private EventListener<DocumentSnapshot> tenantListener;
    private ListenerRegistration tenantReg;
    private EventListener<DocumentSnapshot> unitListener;
    private ListenerRegistration unitReg;

    /**
     * Create the activity
     * @param savedInstanceState State of the class
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dm = DataManager.getInstance();
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        tenantId = intent.getStringExtra(Extras.TENANT_ID);
        if (tenantId == null) {
            finish();
        }
        imgContactPhoto = findViewById(R.id.imgContactPhoto);
        lblTenantName = findViewById(R.id.lblTenantName);
        lblTenantPhone = findViewById(R.id.lblTenantPhone);
        lblTenantEmail = findViewById(R.id.lblTenantEmail);
        cardTenantHouse = findViewById(R.id.cardTenantHouse);
        lblTenantHouseName = findViewById(R.id.lblTenantHouseName);
        lblTenantHouseAddress = findViewById(R.id.lblTenantHouseAddress);
        imgTenantHousePhoto = findViewById(R.id.imgTenantHousePhoto);
        btnCall = findViewById(R.id.btnCall);
        btnMessage = findViewById(R.id.btnMessage);
        btnEmail = findViewById(R.id.btnEmail);
    }
    /**
     * Stop all listeners and pauses the activity until it comes back to the foreground
     */
    @Override
    protected void onPause() {
        super.onPause();
        stopAllListeners();
    }

    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     * @param menu The menu
     * @return true if the menu was inflated, false otherwise
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tenant_detail, menu);
        return true;
    }

    /**
     * Handle action bar item clicks here.
     * @param item the item selected from the menu
     * @return True if item selected, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete_tenant) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
            builder.setTitle("Delete ")
                    .setMessage("Do you want to delete " + tenant.getFirstName() +" "+
                            tenant.getLastName() + " from your tenant list?")
                    .setIcon(android.R.drawable.ic_delete)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dm.removeTenantFromUnit(tenant.getTenantId(), unit.getUnitId());

                            Toast.makeText(getApplicationContext(),
                                    "The tenant has been removed",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }).setNegativeButton("No", null).show();
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Start dialer app. The phone number of the tenant will be populated automatically
     * @param view the bottom clicked
     */
    public void tenantCallOnClick(View view) {

        String phoneNumber = String.format("tel: %s", tenant.getPhone());
        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse(phoneNumber));
        if (dialIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(dialIntent);
        } else {
            finish();
        }
    }
    /**
     * Start messenger app with the phone number of the tenant automatically populated in the
     * destination field
     * @param view the bottom clicked
     */
    public void tenantTextOnClick(View view) {
            btnMessage = findViewById(R.id.btnMessage);
            String smsNumber = "smsto:" + tenant.getPhone();
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
            smsIntent.setData(Uri.parse(smsNumber));
            if (smsIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(smsIntent);
            } else {
                finish();
            }
    }

    /**
     * Start email app selected by user with the email from the tenant automatically populated in the
     * destination field
     * @param view the bottom clicked
     */
    public void emailTenantOnClick(View view) {

        btnEmail = findViewById(R.id.btnEmail);

        String landlordEmail = tenant.getEmail();
        String[] landlordEmailArray = {landlordEmail};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL,landlordEmailArray);
        emailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(emailIntent, "Choose an email app: "));
    }

    /**
     * Set up the view for the tenant, displaying the tenant information
     */
    private void setupTenantViews() {
        if (tenant.getPhoto() != null) {
            GlideApp.with(this)
                    .load(tenant.getPhoto())
                    .placeholder(R.drawable.unit_placeholder)
                    .into(imgContactPhoto);
        }
        lblTenantName.setText(tenant.toString());
        lblTenantPhone.setText(tenant.getPhone());
        lblTenantEmail.setText(tenant.getEmail());
    }

    /**
     * Set up listener for the tenants. Any changes on tenants will be reflected on the app
     */
    public void setupDataListeners() {
        stopAllListeners();
        //Tenant listener
        DocumentReference tenantRef = db.collection("users").document(tenantId);
        tenantListener = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(TenantDetailsActivity.this, "Snapshot exception in Tenant Details", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Tenant newTenant = documentSnapshot.toObject(Tenant.class);
                    dm.setTenant(newTenant);
                    tenant = newTenant;
                    unit = dm.getTenantUnit(tenantId);
                    setupUnitListener();
                    setupTenantViews();
                }
            }
        };
        tenantReg = tenantRef.addSnapshotListener(tenantListener);
    }

    /**
     * Set up listener for the unit associated to the tenant. Any changes in the unit will be reflected
     * in the app
     */
    private void setupUnitListener() {

        DocumentReference unitRef = db.collection("units").document(unit.getUnitId());
        unitListener = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(TenantDetailsActivity.this, "Snapshot exception in Tenant Details", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Unit newUnit = documentSnapshot.toObject(Unit.class);
                    dm.setUnit(newUnit);
                    unit = newUnit;
                    setupUnitViews();
                }
            }
        };
        unitReg = unitRef.addSnapshotListener(unitListener);
    }

    /**
     * Stop listeners. If listeners has been already instantiated, it will remove what is in the
     * tenant register
     */
    private void stopAllListeners() {
        if (tenantReg != null && tenantListener != null) {
            tenantReg.remove();
        }

        if (unitReg != null && unitListener != null) {
            unitReg.remove();
        }
    }

    /**
     * Set up the information of the unit associated with the current tenant
     */
    private void setupUnitViews() {
        if (unit != null) {
            lblTenantHouseName.setText(unit.getName());
            lblTenantHouseAddress.setText(unit.getAddress().toString());
            if (unit.getPhoto() != null) {
                GlideApp.with(this /* context */)
                        .load(unit.getPhoto())
                        .placeholder(R.drawable.unit_placeholder).transition(withCrossFade())
                        .into(imgTenantHousePhoto);
            }
            cardTenantHouse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TenantDetailsActivity.this, UnitDetailsActivity.class);
                    intent.putExtra(Extras.UNIT_ID, unit.getUnitId());
                    startActivity(intent);
                }
            });
        }
    }
}
