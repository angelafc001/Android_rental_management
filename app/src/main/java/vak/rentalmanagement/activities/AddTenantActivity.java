package vak.rentalmanagement.activities;

import android.content.Intent;
import android.os.Bundle;

import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.appcompat.widget.Toolbar;
import vak.rentalmanagement.Extras;
import vak.rentalmanagement.R;
import vak.rentalmanagement.data.Tenant;
import vak.rentalmanagement.datamanager.DataManager;

/**
 * This class represents the AddTenantActivity. Here is where a tenant will be associated to a unit
 *
 * @author Vu Pham
 * @author Koenn Becker
 * @author Angela Ferro Capera
 * @version 1.0
 */
public class AddTenantActivity extends AbstractActivity {

    private String unitId;
    private FirebaseFirestore db;
    private DataManager dm;
    private EditText txtTenantPhone;
    private TextInputLayout tilTenantPhone;

    /**
     * Creates the activity and initializes the data manager and database.
     *
     * @param savedInstanceState the previous state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tenant);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dm = DataManager.getInstance();
        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        unitId = intent.getStringExtra(Extras.UNIT_ID);
        if (unitId == null) {
            finish();
        }

        txtTenantPhone = findViewById(R.id.txtTenantPhone);
        tilTenantPhone = findViewById(R.id.tilTenantPhone);
        txtTenantPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    }

    /**
     * Unused for this activity
     */
    @Override
    protected void setupDataListeners() {
        // Unused
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
     * Add  a tenant to the unit
     * @param view
     */
    public void btnAddTenantOnClick(View view) {
        if (TextUtils.isEmpty(txtTenantPhone.getText())) {
            tilTenantPhone.setError("Please add tenant's ID");
            return;
        }
        searchPhone(tilTenantPhone.getEditText().getText().toString());
    }

    /**
     * Look for a tenant by his/her phone number
     * @param phone String value. The phone number of the tenant is been looked for
     */
    private void searchPhone(String phone) {
        Query phoneRef = db.collection("users").whereEqualTo("phone", phone);
        phoneRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                searchPhoneResult(queryDocumentSnapshots);
            }
        });
    }

    /**
     * The callback method for when the Firebase query comes back. This checks if there were any
     * tenants with the matching phone number and if so, adds him/her to the unit.
     *
     * @param snapshot The snapshot from Firebase query.
     */
    private void searchPhoneResult(QuerySnapshot snapshot) {
        if (!snapshot.isEmpty()) {
            if (snapshot.getDocuments().get(0).contains("tenantId")) {
                Tenant tenant = snapshot.getDocuments().get(0).toObject(Tenant.class);
                if (tenant != null) {
                    dm.addTenant(tenant);
                    dm.addTenantToUnit(tenant.getTenantId(), unitId);
                    finish();
                } else {
                    tilTenantPhone.setError("No tenant found");
                }
            } else {
                tilTenantPhone.setError("Phone number must belong to a tenant.");
            }
        } else {
            tilTenantPhone.setError("No tenant found");
        }
    }

}
