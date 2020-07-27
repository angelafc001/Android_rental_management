package vak.rentalmanagement.activities;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

import javax.annotation.Nullable;

import androidx.cardview.widget.CardView;
import vak.rentalmanagement.Extras;
import vak.rentalmanagement.GlideApp;
import vak.rentalmanagement.R;
import vak.rentalmanagement.data.Tenant;
import vak.rentalmanagement.data.Unit;
import vak.rentalmanagement.datamanager.DataManager;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * This class represents the Unit Detail Activity.
 * It displays a unit's information details, including the Unit's name, photo, type, address, number
 * of beds, number of baths, rent due date, rent amount, year built, floor size, and the tenant
 * associates with that unit.
 *
 * @author Koenn Becker
 * @author Vu Pham
 * @author Angela Ferro Capera
 * @version 1.0
 */
public class UnitDetailsActivity extends AbstractActivity {

    private Unit unit;
    private DataManager dm;
    private ImageView imgUnitPhoto;
    private TextView lblUnitName;
    private TextView lblUnitType;
    private TextView lblUnitAddress;
    private TextView lblUnitBeds;
    private TextView lblUnitBaths;
    private TextView lblRentDue;
    private TextView lblRentAmount;
    private TextView lblYearBuilt;
    private TextView lblFloorSize;
    private CardView cardUnitTenant;
    private ViewSwitcher switcher;
    private TextView lblTenantName;
    private CircularImageView imgTenantPhoto;
    private String unitId;
    private Tenant tenant;
    private CollapsingToolbarLayout colToolbar;

    private FirebaseFirestore db;
    private EventListener<DocumentSnapshot> unitListener;
    private ListenerRegistration unitReg;
    private EventListener<DocumentSnapshot> tenantListener;
    private ListenerRegistration tenantReg;

    /**
     * Set up the UnitDetailsActivity layout.
     * Initialize the Data Manager, the views and the CardView listener, and get the intent.
     *
     * @param savedInstanceState the class state
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        colToolbar = findViewById(R.id.colToolbar);

        dm = DataManager.getInstance();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        unitId = intent.getStringExtra(Extras.UNIT_ID);
        if (unitId == null) {
            finish();
        }

        imgUnitPhoto = findViewById(R.id.imgUnitPhoto);
        lblUnitName = findViewById(R.id.txtTenantFirstName);
        lblUnitType = findViewById(R.id.spnUnitType);
        lblUnitAddress = findViewById(R.id.txtUnitAddress);
        lblUnitBeds = findViewById(R.id.txtUnitBeds);
        lblUnitBaths = findViewById(R.id.txtUnitBaths);
        lblRentDue = findViewById(R.id.txtRentDue);
        lblUnitName = findViewById(R.id.txtTenantFirstName);
        lblRentAmount = findViewById(R.id.txtRentAmount);
        lblYearBuilt = findViewById(R.id.txtYearBuilt);
        lblFloorSize = findViewById(R.id.txtFloorSize);
        switcher = findViewById(R.id.switcher);
        LinearLayout viewAddTenant = switcher.findViewById(R.id.view2);
        cardUnitTenant = switcher.findViewById(R.id.cardUnitTenant);
        lblTenantName = cardUnitTenant.findViewById(R.id.lblTenantName);
        imgTenantPhoto = findViewById(R.id.imgLandlordContactPhoto);
        cardUnitTenant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardUnitTenantOnClick();
            }
        });
    }

    /**
     * Stop all listeners when the activity is not longer in the foreground
     */
    @Override
    protected void onPause() {
        super.onPause();
        stopAllListeners();
    }

    /**
     * Set up the menu for UnitDetailsActivity
     *
     * @param menu the menu object of this activity
     * @return true if the setting up is successful
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.unit_details, menu);

        return true;
    }

    /**
     * Handle action bar item clicks here. The action bar will automatically handle clicks on
     * the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
     *
     * @param item the menu item chosen
     * @return true if the menu works
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit_unit) {
            Intent intent = new Intent(UnitDetailsActivity.this, EditUnitActivity.class);
            intent.putExtra(Extras.UNIT_ID, this.unit.getUnitId());
            startActivity(intent);
            return true;
        } else if (id == R.id.action_delete_unit) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
            builder.setTitle("Delete ")
                    .setMessage("Do you want to delete unit " + unit.getName() + " from your unit list?")
                    .setIcon(android.R.drawable.ic_delete)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dm.removeUnit(unitId);
                            Toast.makeText(getApplicationContext(),
                                    "Unit successfully deleted!",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }).setNegativeButton("No", null).show();
            //return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handle the OnClick() event of Add Tenant button.
     * Start the AddTenantActivity with an intent that has the current unit's unitID.
     *
     * @param view the Add Tenant Button
     */
    public void btnAddTenantOnClick(View view) {
        Intent intent = new Intent(UnitDetailsActivity.this, AddTenantActivity.class);
        intent.putExtra(Extras.UNIT_ID, unitId);
        startActivity(intent);
    }

    /**
     * Handle the OnClick() event of the CardView.
     * Start the TenantDetailsActivity with an intent that has the current tenant's ID.
     */
    private void cardUnitTenantOnClick() {
        Intent intent = new Intent(UnitDetailsActivity.this, TenantDetailsActivity.class);
        intent.putExtra(Extras.TENANT_ID, tenant.getTenantId());
        startActivity(intent);
    }

    /**
     * Handle the OnClick() event of Delete Tenant ImageButton.
     * Remove the current tenant from the unit.
     *
     * @param view the Delete Tenant Image Button
     */
    public void ibtnDeleteTenantOnClick(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setTitle("Remove Tenant")
                .setMessage("Are you sure you want to remove " + tenant.getFirstName() + " " + tenant.getLastName()
                        + " from the unit?")
                .setIcon(android.R.drawable.ic_delete)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dm.removeTenantFromUnit(tenant.getTenantId(), unitId);
                        setupTenantListener();
                        Toast.makeText(getApplicationContext(),
                                "The tenant has been removed",
                                Toast.LENGTH_LONG).show();

                    }
                })
                .setNegativeButton("No", null).show();
    }

    /**
     * Set up the context for the views
     */
    private void setupUnitViews() {
        if (unit.getPhoto() != null) {
            GlideApp.with(this /* context */)
                    .load(unit.getPhoto())
                    .placeholder(R.drawable.unit_placeholder).transition(withCrossFade())
                    .into(imgUnitPhoto);
        }

        colToolbar.setTitle(unit.getName());
        lblUnitName.setText(unit.getName());
        lblUnitType.setText(unit.getTypeString());
        lblUnitAddress.setText(unit.getAddress().toString());
        lblUnitBeds.setText(String.valueOf(unit.getBeds()));
        lblUnitBaths.setText(String.valueOf(unit.getBaths()));
        lblRentDue.setText(String.valueOf(unit.getRentDueDay()));
        lblRentAmount.setText(String.valueOf(unit.getRent()));
        lblYearBuilt.setText(String.valueOf(unit.getYearBuilt()));
        lblFloorSize.setText(String.valueOf(unit.getSquareFeet()));
    }

    /**
     * Set up the tenant card
     */
    private void setupTenantCard() {
        if (switcher.getCurrentView().equals(cardUnitTenant)) {
            if (unit.getTenantId() == null) {
                switcher.showNext();
            } else {
                lblTenantName.setText(tenant.toString());
                if (tenant.getPhone() != null) {
                    GlideApp.with(this).load(tenant.getPhoto()).placeholder(R.drawable.unit_placeholder).into(imgTenantPhoto);
                }

            }
        } else {
            if (unit.getTenantId() != null) {
                switcher.showNext();
                lblTenantName.setText(tenant.toString());
                if (tenant.getPhone() != null) {
                    GlideApp.with(this).load(tenant.getPhoto()).placeholder(R.drawable.unit_placeholder).into(imgTenantPhoto);
                }
            }
        }
    }

    /**
     * Listens to data incomming from Firestore.
     */
    protected void setupDataListeners() {
        stopAllListeners();

        //Unit listener
        DocumentReference docRef = db.collection("units").document(unitId);
        unitListener = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(UnitDetailsActivity.this, "Snapshot exception in Unit Details", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Unit newUnit = documentSnapshot.toObject(Unit.class);
                    dm.setUnit(newUnit);
                    unit = newUnit;
                    setupTenantListener();
                    setupUnitViews();
                }
            }
        };
        unitReg = docRef.addSnapshotListener(unitListener);
    }

    /**
     * Listen for changes in the  information of the tenant that is associated to the current unit
     */
    private void setupTenantListener() {
        //Tenant listener
        if (unit != null && unit.getTenantId() != null) {
            DocumentReference tenantRef = db.collection("users").document(unit.getTenantId());
            tenantListener = new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Toast.makeText(UnitDetailsActivity.this, "Snapshot exception in Unit Details", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Tenant newTenant = documentSnapshot.toObject(Tenant.class);
                        if (!dm.setTenant(newTenant)) {
                            dm.addTenant(newTenant);
                        }
                        tenant = newTenant;
                        setupTenantCard();
                    }
                }
            };
            tenantReg = tenantRef.addSnapshotListener(tenantListener);
        }
    }

    /**
     * Remove what is in the ListenerRegistration when gets called
     */
    private void stopAllListeners() {
        if (unitReg != null && unitListener != null) {
            unitReg.remove();
        }

        if (tenantReg != null && tenantListener != null) {
            tenantReg.remove();
        }
    }
}
