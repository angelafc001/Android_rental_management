package vak.rentalmanagement.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;
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
import vak.rentalmanagement.data.Unit;
import vak.rentalmanagement.datamanager.DataManager;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * This class represents the details of the Unit associated to the current tenant
 * @author Koenn Becker
 * @author Vu Pham
 * @author Angela Ferro Capera
 * @version 1.0
 */
public class TenantUnitDetailsActivity extends AbstractActivity {

    private Unit unit;
    private DataManager dm;
    private TextView lblName;
    private TextView lblType;
    private TextView lblAddress;
    private TextView lblBeds;
    private TextView lblBaths;
    private TextView lblSqFt;
    private TextView lblCost;
    private TextView lblRentDue;
    private TextView lblYearBuilt;
    private TextView lblUnitLandlord;
    private ImageView imgUnitPhoto;
    private String unitId;
    private CollapsingToolbarLayout colToolbar;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EventListener<DocumentSnapshot> unitListener;
    private ListenerRegistration unitReg;

    /**
     * TenantUnitDetails on create
     * @param savedInstanceState the state of the class
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_unit_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        colToolbar = findViewById(R.id.colToolbar);

        dm = DataManager.getInstance();
        Intent intent = getIntent();
        unitId = intent.getStringExtra(Extras.UNIT_ID);
        if (unitId == null) {
            finish();
        }
        lblName = findViewById(R.id.lblTenantUnitName);
        lblType = findViewById(R.id.lblType);
        lblAddress = findViewById(R.id.lblTenantUnitAddress);
        lblBeds = findViewById(R.id.lblTenantUnitBeds);
        lblBaths = findViewById(R.id.lblTenantUnitBaths);
        lblSqFt = findViewById(R.id.lblTenantUnitSqFt);
        lblCost = findViewById(R.id.lblTenantRentAmout);
        lblRentDue = findViewById(R.id.lblTenantRentDueDate);
        lblYearBuilt = findViewById(R.id.lblTenantUnitYearBuilt);
        lblUnitLandlord = findViewById(R.id.lblTenantUnitLandlord);
        imgUnitPhoto = findViewById(R.id.imgUnitPhoto);
    }

    /**
     * Call stopAllListeners when the activity is not longer in the foreground
     */
    @Override
    protected void onPause() {
        super.onPause();
        stopAllListeners();
    }

    /**
     * Allows the back arrow in the option menu to go back
     *
     * @return true if the activity was successfully finished
     */
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Display the information of the unit associated with the current tenant
     */
    private void displayTenantUnit(){
        Landlord landlord = dm.getLandlord();
        if (unit.getPhoto() != null) {
            GlideApp.with(this /* context */)
                    .load(unit.getPhoto())
                    .placeholder(R.drawable.unit_placeholder).transition(withCrossFade())
                    .into(imgUnitPhoto);
        }
        colToolbar.setTitle(unit.getName());
        lblName.setText(unit.getName());
        lblAddress.setText(unit.getAddress().toString());
        lblType.setText(unit.getTypeString());
        String beds = Integer.toString(unit.getBeds());
        lblBeds.setText(beds);
        String baths = Double.toString(unit.getBaths());
        lblBaths.setText(baths);
        String sqft = Integer.toString(unit.getSquareFeet());
        lblSqFt.setText(sqft);
        String rent = Double.toString(unit.getRent());
        lblCost.setText(rent);
        String rentDay = Integer.toString(unit.getRentDueDay());
        lblRentDue.setText(rentDay);
        String year = Integer.toString(unit.getYearBuilt());
        lblYearBuilt.setText(year);
        String name = landlord.getFirstName() + " " + landlord.getLastName();
        lblUnitLandlord.setText(name);
    }

    /**
     * Set up the listeners for changes in the unit
     */
    @Override
    protected void setupDataListeners() {
        stopAllListeners();
        DocumentReference docRef = db.collection("units").document(unitId);
        unitListener = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(TenantUnitDetailsActivity.this, "Snapshot exception in  Tenant Unit Details", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Unit newUnit = documentSnapshot.toObject(Unit.class);
                    dm.setUnit(newUnit);
                    unit = newUnit;
                    displayTenantUnit();
                }
            }
        };
        unitReg = docRef.addSnapshotListener(unitListener);
    }

    /**
     * If the register of unit is not null, it removes what is in it. This method gets called when the
     * activity is not on the foreground
     */
    private void stopAllListeners() {
        if (unitReg != null && unitListener != null) {
            unitReg.remove();
        }
    }
}
