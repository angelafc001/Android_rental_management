package vak.rentalmanagement.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.ActionBar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import vak.rentalmanagement.Extras;
import vak.rentalmanagement.GlideApp;
import vak.rentalmanagement.R;
import vak.rentalmanagement.data.Address;
import vak.rentalmanagement.data.Landlord;
import vak.rentalmanagement.data.RepairRequest;
import vak.rentalmanagement.data.Tenant;
import vak.rentalmanagement.data.Unit;
import vak.rentalmanagement.datamanager.DataManager;
import vak.rentalmanagement.recycleviewadapters.TenantRepairRequestAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * This class represents the main screen a tenant will see when logging in the app
 *
 * @author Koenn Becker
 * @author Vu Pham
 * @author Angela Ferro Capera
 * @version 1.0
 */

public class TenantBaseActivity extends AbstractActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //private fields
    private DataManager dm;
    private Unit unit;
    private Tenant tenant;
    private Landlord landlord;
    private List<RepairRequest> repairRequestList = new ArrayList<>();
    private Date date;
    private CardView cardTenantsUnit;
    private TextView lblTenantsUnitName;
    private TextView getLblTenantsUnitAddress;
    private ImageView imgTenantsUnitPhoto;
    private ImageView imgLandlordContactPhoto;
    private TextView lblLandlordContactName;
    private TextView lblTenantDate;
    private ProgressBar progressBar;
    private RecyclerView recTenantRepairRequests;
    private TextView lblTenantHeaderName;
    private CircularImageView imgTenantHeader;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EventListener<QuerySnapshot> unitListener;
    private ListenerRegistration unitReg;
    private EventListener<DocumentSnapshot> tenantListener;
    private ListenerRegistration tenantReg;
    private EventListener<DocumentSnapshot> landlordListener;
    private ListenerRegistration landlordReg;
    private EventListener<QuerySnapshot> repairRequestListener;
    private ListenerRegistration repairRequestReg;
    private boolean newPhoto;

    /**
     * On create of TenantBaseActivity
     *
     * @param savedInstanceState the state of the class
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_base);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        dm = DataManager.getInstance();
        date = new Date();
        ActionBar actionBar = getSupportActionBar();
        progressBar = findViewById(R.id.prbrRentDue);
        lblTenantDate = findViewById(R.id.lblTenantDate);
        cardTenantsUnit = findViewById(R.id.cardTenantsUnit);
        lblTenantsUnitName = findViewById(R.id.lblTenantsUnitName);
        getLblTenantsUnitAddress = findViewById(R.id.lblTenantsUnitAddress);
        imgTenantsUnitPhoto = findViewById(R.id.imgTenantsUnitPhoto);
        imgLandlordContactPhoto = findViewById(R.id.imgLandlordContactPhoto);
        lblLandlordContactName = findViewById(R.id.lblLandlordContactName);
        CardView cardLandlordDetails = findViewById(R.id.cardLandlordDetails);
        recTenantRepairRequests = findViewById(R.id.recTenantRepairRequests);


        FloatingActionButton fab = findViewById(R.id.fabTenantNewRequest);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newRepairRequest();
            }
        });

        DrawerLayout drawer = findViewById(R.id.tenant_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.tenant_nav_view);
        View hView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        lblTenantHeaderName = hView.findViewById(R.id.lblHeaderName);
        imgTenantHeader = hView.findViewById(R.id.imgTenantHeader);

        cardLandlordDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unitLandlordDetailsOnClick();
            }
        });
    }

    /**
     * Stops listening to updates when the activity is paused.
     */
    @Override
    protected void onPause() {
        super.onPause();
        stopAllListeners();
        stopTenantListener();
    }

    /**
     * Handles when the back button is clicked. If the navigation drawer is open, just close it.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.tenant_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     *
     * @param menu the top menu
     * @return true if the menu was set up correctly, false otherwise
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tenant_base, menu);
        return true;
    }

    /**
     * Handler of menu bar selected option
     *
     * @param item The item selected
     * @return True if an item was selected, false if not
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * It handles the selection made by user in the navigation drawer
     *
     * @param item the item selected
     * @return true when an item get selected, otherwise false
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.nav_editProfile: {
                intent = new Intent(TenantBaseActivity.this, TenantEditActivity.class);
                intent.putExtra(Extras.TENANT_ID, userId);
                startActivity(intent);
                break;
            }
            case R.id.nav_landlordContact: {
                intent = new Intent(TenantBaseActivity.this, TenantsLandlordDetails.class);
                intent.putExtra(Extras.LANDLORD_ID, landlord.getLandlordId());
                startActivity(intent);
                break;
            }
            case R.id.nav_requestRepair: {
                newRepairRequest();
                break;
            }
            case R.id.nav_myUnit: {
                intent = new Intent(TenantBaseActivity.this, TenantUnitDetailsActivity.class);
                intent.putExtra(Extras.UNIT_ID, unit.getUnitId());
                startActivity(intent);
                break;
            }
            case R.id.navLogout: {
                signOut();
                break;
            }
        }
        DrawerLayout drawer = findViewById(R.id.tenant_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Display the unit details. From TenantUnitDetailsActivity
     */
    private void unitDetailsOnClick() {
        Intent intent = new Intent(TenantBaseActivity.this, TenantUnitDetailsActivity.class);
        intent.putExtra(Extras.UNIT_ID, unit.getUnitId());
        startActivity(intent);
    }

    /**
     * Display the landlord details. From TenantLandlordDetails
     */
    private void unitLandlordDetailsOnClick() {
        Intent intent = new Intent(TenantBaseActivity.this, TenantsLandlordDetails.class);
        intent.putExtra(Extras.LANDLORD_ID, landlord.getLandlordId());
        startActivity(intent);
    }

    /**
     * Display the tenants profile details. From TenantProfileActivity
     *
     * @param view the current view
     */
    public void tenantProfileOnClick(View view) {
        Intent intent = new Intent(TenantBaseActivity.this, TenantProfileActivity.class);
        startActivity(intent);
    }

    /**
     * Display new Repair Request view. From RepairRequestActivity
     */
    private void newRepairRequest() {
        Intent intent = new Intent(TenantBaseActivity.this, RepairRequestActivity.class);
        intent.putExtra(Extras.TENANT_ID, userId);
        startActivity(intent);
    }

    /**
     * Prompt the dial keyboard with the landlords phone number ready to call
     *
     * @param view The button clicked
     */
    public void btnCallLandlordOnClick(View view) {
        String phoneNumber = String.format("tel: %s", landlord.getPhone());
        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse(phoneNumber));
        if (dialIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(dialIntent);
        } else {
            finish();
        }
    }

    /**
     * Prompt the phone message app. It will have the Landlords phone number as receiver
     *
     * @param view The button clicked
     */
    public void messageLandlordOnClick(View view) {
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
     * Set up progress bar. Progress bar shows how far the tenant is to the rent due day
     */
    private void setUpProgressBar() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter2 = new SimpleDateFormat("MM");
        int currentMonth = Integer.parseInt(formatter2.format(date));
        int nextMonth = Integer.parseInt(formatter2.format(date)) + 1;
        double currentDay = Double.parseDouble(formatter.format(date));
        double rentDueDay = unit.getRentDueDay();
        double progressRent = 0.0;
        if (rentDueDay - currentDay < 0) {
            progressRent = ((currentDay - rentDueDay)/currentDay) * 100;
            lblTenantDate.setText(String.format("%s/%s/2019", Integer.toString(nextMonth), String.valueOf(unit.getRentDueDay())));
        } else {
            progressRent = (currentDay / rentDueDay) * 100;
            lblTenantDate.setText(String.format("%s/%s/2019", Integer.toString(currentMonth), String.valueOf(unit.getRentDueDay())));
        }
        progressBar.setProgress((int) progressRent);
    }

    /**
     * Set up listener for the current tenant
     */
    private void setupTenantListener() {
        DocumentReference docRef = db.collection("users").document(userId);
        tenantListener = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(TenantBaseActivity.this, "Snapshot exception in Tenant Base", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    tenant = documentSnapshot.toObject(Tenant.class);
                    if (!dm.setTenant(tenant)) {
                        dm.addTenant(tenant);
                    }
                    setUpUnitListener();
                    setUpTenantViews();
                } else {
                    FirebaseAuth.getInstance().signOut();
                }
            }
        };
        tenantReg = docRef.addSnapshotListener(tenantListener);
    }

    /**
     * Listen for the current Tenant unit and changes made in it
     */
    private void setUpUnitListener() {
        Query unitRef = db.collection("units").whereEqualTo("tenantId", userId);
        unitListener = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(TenantBaseActivity.this, "Snapshot exception in Unit listener TenantBaseActivity",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    if (queryDocumentSnapshots.size() == 1) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        unit = doc.toObject(Unit.class);
                        dm.setUnit(unit);
                        setupUnitViews();
                        setupLandlordListener();
                    }
                }
            }
        };
        unitReg = unitRef.addSnapshotListener(unitListener);
    }

    /**
     * Listen for the current Tenant Landlord information from Firebase
     */
    private void setupLandlordListener() {

        DocumentReference docRef = db.collection("users").document(unit.getLandlordId());
        landlordListener = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(TenantBaseActivity.this, "Snapshot exception in Landlord listener" +
                            "TenantBaseActivity", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    landlord = documentSnapshot.toObject(Landlord.class);
                    dm.setLandord(landlord);
                    setupLandlordViews();
                }
            }
        };
        landlordReg = docRef.addSnapshotListener(landlordListener);
    }

    /**
     * Listen for the Request Repairs the current Tenant has submitted and their state
     */
    private void setupRequestRepairListener() {
        Query repairRequestRef = db.collection("requests").whereEqualTo("fromId", userId);
        repairRequestListener = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(TenantBaseActivity.this,
                            "Snapshot exception in TenantBase--RepairRequestQuery", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (queryDocumentSnapshots != null) {
                    ArrayList<RepairRequest> requestList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        RepairRequest repairRequest = doc.toObject(RepairRequest.class);
                        requestList.add(repairRequest);
                    }
                    dm.setRepairRequestList(requestList);
                    repairRequestList = dm.getRepairRequestList();
                    setUpRepairRequestViews();
                }
            }
        };
        repairRequestReg = repairRequestRef.addSnapshotListener(repairRequestListener);
    }

    /**
     * Set up the views for the current list of Repair Request
     */
    private void setUpRepairRequestViews() {

        TenantRepairRequestAdapter adapter = new TenantRepairRequestAdapter(repairRequestList, this);
        recTenantRepairRequests.setAdapter(adapter);
        recTenantRepairRequests.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(Extras.ANIMATION_SPEED);
        itemAnimator.setRemoveDuration(Extras.ANIMATION_SPEED);
        cardTenantsUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unitDetailsOnClick();
            }
        });

        recTenantRepairRequests.setItemAnimator(itemAnimator);
    }

    /**
     * Check if the user logged in is a new tenant with no unit assigned to them. If  the
     * current Tenant has an unit assigned to them, it loads the unit info.
     */
    private void setUpUnitDetails() {
        if (tenant.getTenantLandlordId() == null) {
            this.unit = new Unit("00", new Address("0", "0", "0", 0), 0, null, 0, 0, 0,
                    0, 0, 0, "0");
        } else {
            unit = dm.getTenantUnit(tenant.getTenantId());
        }
    }

    /**
     * Set up the Landlord information
     */
    private void setupLandlordViews() {
        lblLandlordContactName.setText(landlord.toString());
        if (landlord.getPhoto() != null) {
            GlideApp.with(this)
                    .load(landlord.getPhoto())
                    .placeholder(R.drawable.unit_placeholder)
                    .into(imgLandlordContactPhoto);
        }
    }

    /**
     * Load the current Tenant unit details information
     */
    private void setupUnitViews() {
        setUpUnitDetails();
        setUpProgressBar();
        lblTenantsUnitName.setText(unit.getName());
        getLblTenantsUnitAddress.setText(unit.getAddress().toString());

        if (unit.getPhoto() != null) {
            GlideApp.with(this /* context */)
                    .load(unit.getPhoto())
                    .placeholder(R.drawable.unit_placeholder).transition(withCrossFade())
                    .into(imgTenantsUnitPhoto);
        }
    }

    /**
     * Load current Tenant information into activity
     */
    private void setUpTenantViews() {
        if (tenant.getPhoto() != null) {
            GlideApp.with(this).load(tenant.getPhoto()).placeholder(R.drawable.unit_placeholder).into(imgTenantHeader);
        }
        lblTenantHeaderName.setText(tenant.toString());
    }

    /**
     * Stop listeners if activity is not on foreground anymore
     */
    private void stopAllListeners() {

        if (unitReg != null && unitListener != null) {
            unitReg.remove();
        }

        if (landlordReg != null && landlordListener != null) {
            landlordReg.remove();
        }

        if (repairRequestReg != null && repairRequestListener != null) {
            repairRequestReg.remove();
        }

        if (tenantReg != null && tenantListener != null) {
            tenantReg.remove();
        }
    }

    /**
     * Set up the listeners when onResume is called
     */
    @Override
    protected void setupDataListeners() {
        stopAllListeners();
        setupTenantListener();
        setupRequestRepairListener();
    }

    /**
     * Stop tenant listener
     */
    private void stopTenantListener() {
        if (tenantReg != null && tenantListener != null) {
            tenantReg.remove();
        }
    }
}

