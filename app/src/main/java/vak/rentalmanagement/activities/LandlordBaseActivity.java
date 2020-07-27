package vak.rentalmanagement.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

import javax.annotation.Nullable;

import vak.rentalmanagement.GlideApp;
import vak.rentalmanagement.R;
import vak.rentalmanagement.data.Landlord;
import vak.rentalmanagement.data.RepairRequest;
import vak.rentalmanagement.data.Tenant;
import vak.rentalmanagement.data.Unit;
import vak.rentalmanagement.datamanager.DataManager;
import vak.rentalmanagement.fragments.RepairRequestListFragment;
import vak.rentalmanagement.fragments.TenantListFragment;
import vak.rentalmanagement.fragments.UnitListFragment;

/**
 * This class represents the main screen of the Landlord view.
 * @author Vu Pham
 * @author Koenn Becker
 * @author Angela Ferro Capera
 * @version 1.0
 */

public class LandlordBaseActivity extends AbstractActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private Fragment fragment;
    private FloatingActionButton fab;
    private CircularImageView ibtnHeaderPhoto;
    private TextView lblHeaderName;
    private DataManager dm;
    private Landlord landlord;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    // Data listeners
    private EventListener<DocumentSnapshot> landlordListener;
    private ListenerRegistration landlordReg;

    private EventListener<QuerySnapshot> unitListener;
    private ListenerRegistration unitReg;

    private ListenerRegistration tenantReg;

    private EventListener<QuerySnapshot> requestListener;
    private ListenerRegistration requestReg;

    /**
     * On create of LandlordBaseActivity
     * @param savedInstanceState the state of the class
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_base);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fabAddUnit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LandlordBaseActivity.this, AddUnitActivity.class);
                startActivity(intent);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationViewLandlord = findViewById(R.id.nav_view);
        View headerView = navigationViewLandlord.getHeaderView(0);
        navigationViewLandlord.setNavigationItemSelectedListener(this);

        if (savedInstanceState != null) {
            //Restore the fragment's instance
            fragment = getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment");
            if (!(fragment instanceof UnitListFragment)) {
                fab.hide();
            }
        } else {
            fragment = new UnitListFragment();
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frmLandlordFragment, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        navigationViewLandlord.getMenu().getItem(0).setChecked(true);

        dm = DataManager.getInstance();

        lblHeaderName = headerView.findViewById(R.id.lblHeaderName);
        ibtnHeaderPhoto = headerView.findViewById(R.id.ibtnHeaderPhoto);
    }

    /**
     * Stops listening to updates when the activity is paused.
     */
    @Override
    protected void onPause() {
        super.onPause();
        stopAllListeners();
        stopLandlordListener();
    }
    /**
     * Handles when the back button is clicked. If the navigation drawer is open, just close it.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Handle action bar item clicks here. The action bar will
     * automatically handle clicks on the Home/Up button
     * @param item the item selected
     * @return true if an item was selected, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Save the current fragment state into the bundle
     * @param outState Bundle, the current fragment being displayed
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the fragment's instance
        getSupportFragmentManager().putFragment(outState, "currentFragment", fragment);
    }

    /**
     * Display the correct fragment base on the user selection
     * @param item the item selected from the navigation bar
     * @return true if an item is selected, false otherwise
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.navUnitList: {
                if (!(fragment instanceof UnitListFragment)) {
                    fragment = new UnitListFragment();
                    fab.show();
                    setupDataListeners();
                }
                break;
            }
            case R.id.navTenantList: {
                if (!(fragment instanceof TenantListFragment)) {
                    fragment = new TenantListFragment();
                    fab.hide();
                    setupDataListeners();
                }
                break;
            }
            case R.id.navRepairRequestList: {
                if (!(fragment instanceof RepairRequestListFragment)) {
                    fragment = new RepairRequestListFragment();
                    fab.hide();
                    setupDataListeners();
                }
                break;
            }
            case R.id.navLogout: {
                signOut();
                break;
            }
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frmLandlordFragment, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Start new activity displaying the details of the current landlord
     * @param view
     */
    public void landlordProfileOnClick(View view) {
        Intent intent = new Intent(LandlordBaseActivity.this, LandlordProfileActivity.class);
        startActivity(intent);
    }

    /**
     * Listens to data in comming from Firestore.
     */
    protected void setupDataListeners() {
        stopAllListeners();

        if (landlordReg == null) {
            setupLandlordListener();
        }

        if (fragment instanceof UnitListFragment) {
            // Listen for all Unit info.
            Query unitsRef = db.collection("units").whereEqualTo("landlordId", userId);
            unitListener = new EventListener<QuerySnapshot>() {

                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Toast.makeText(LandlordBaseActivity.this,
                                "Snapshot exception in UnitList", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (queryDocumentSnapshots != null ) {
                        ArrayList<Unit> unitList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Unit unit = doc.toObject(Unit.class);
                            unitList.add(unit);
                        }
                        dm.setUnitList(unitList);
                        ((UnitListFragment)fragment).updateData();
                    }
                }
            };
            unitReg = unitsRef.addSnapshotListener(unitListener);

        } else if (fragment instanceof TenantListFragment) {
            // Listen for all Tenant changes.

            // Find all tenant IDs
            Query tenantsRef = db.collection("users").whereEqualTo("tenantLandlordId", userId);
            EventListener<QuerySnapshot> tenantListener = new EventListener<QuerySnapshot>() {

                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Toast.makeText(LandlordBaseActivity.this,
                                "Snapshot exception in TenantList", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        ArrayList<Tenant> tenantList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Tenant tenant = doc.toObject(Tenant.class);
                            tenantList.add(tenant);
                        }
                        dm.setTenantList(tenantList);
                        ((TenantListFragment) fragment).updateData();
                    }
                }
            };
            tenantReg = tenantsRef.addSnapshotListener(tenantListener);

        } else if (fragment instanceof RepairRequestListFragment) {
            // Listen for all Repair Request changes.
            Query requestsRef = db.collection("requests").whereEqualTo("toId", auth.getUid());
            requestListener = new EventListener<QuerySnapshot>() {

                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Toast.makeText(LandlordBaseActivity.this,
                                "Snapshot exception in TenantList", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (queryDocumentSnapshots != null ) {
                        ArrayList<RepairRequest> requestList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            RepairRequest request = doc.toObject(RepairRequest.class);
                            requestList.add(request);
                        }
                        dm.setRepairRequestList(requestList);
                        ((RepairRequestListFragment)fragment).updateData();
                    }
                }
            };
            requestReg = requestsRef.addSnapshotListener(requestListener);
        }

    }

    /**
     * Listen for incoming data of the current Landlord
     */
    private void setupLandlordListener() {
        //Landlord listener
        DocumentReference docRef = db.collection("users").document(auth.getUid());
        landlordListener = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(LandlordBaseActivity.this, "Snapshot exception in Landlord", Toast.LENGTH_SHORT).show();
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
     * If ListenerRegistration are not null, it removes what is in them. This method gets called
     * when the activity is not on the foreground
     */
    private void stopAllListeners() {

        if (unitReg != null && unitListener != null) {
            unitReg.remove();
        }

        if (tenantReg != null && unitListener != null) {
            tenantReg.remove();
        }

        if (requestReg != null && requestListener != null) {
            requestReg.remove();
        }
    }

    /**
     * If ListenerRegistration are not null, it removes what is in them. This method gets called
     * when the activity is not on the foreground
     */
    private void stopLandlordListener() {
        if (landlordReg != null && landlordListener != null) {
            landlordReg.remove();
            landlordReg = null;
        }

    }

    /**
     * Display the information of the current Landlord
     */
    private void setupLandlordViews() {
        if (landlord.getPhoto() != null) {
            GlideApp.with(this)
                    .load(landlord.getPhoto())
                    .placeholder(R.drawable.unit_placeholder)
                    .into(ibtnHeaderPhoto);
        }

        lblHeaderName.setText(landlord.toString());
    }
}
