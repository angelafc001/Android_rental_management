package vak.rentalmanagement.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import vak.rentalmanagement.data.RepairRequest;
import vak.rentalmanagement.datamanager.DataManager;
import vak.rentalmanagement.datamanager.RepairRequestExpert;

/**
 * This class represents the details of a particular repair request submitted by the current tenant
 * @author Vu Pham
 * @author Koenn Becker
 * @author Angela Ferro Capera
 * @version 1.0
 */
public class TenantRequestDetailsActivity extends AbstractActivity {

    private TextView lblRequestDetailsRoom;
    private TextView lblRequestDetailsPriority;
    private TextView lblRequestDetailsDescription;
    private RepairRequest request;
    private ImageView imgRequestDetailsPhoto;
    private TextView lblRequestDetailsStatus;
    private ProgressBar prgRequestDetailsStatus;

    private DataManager dm;
    private FirebaseFirestore db;
    private ListenerRegistration requestReg;
    private EventListener<DocumentSnapshot> requestListener;

    private String requestId;

    /**
     * On create of TenantRequestDetailsActivity
     * @param savedInstanceState the state of the class
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_request_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = FirebaseFirestore.getInstance();
        dm = DataManager.getInstance();
        Intent intent = getIntent();
        requestId = intent.getStringExtra(Extras.REQUEST_ID);
        if (requestId == null) {
            finish();
        }

        prgRequestDetailsStatus = findViewById(R.id.prgRequestDetailsStatus);
        lblRequestDetailsStatus = findViewById(R.id.lblRequestDetailsStatus);
        imgRequestDetailsPhoto = findViewById(R.id.imgRequestDetailsPhoto);
        lblRequestDetailsRoom = findViewById(R.id.lblRequestDetailsRoom);
        lblRequestDetailsPriority = findViewById(R.id.lblRequestDetailsPriority);
        lblRequestDetailsDescription = findViewById(R.id.lblRequestDetailsDescription);
    }

    /**
     * Inherited method. Set up the listeners for the current Tenant and the repair request
     */
    @Override
    protected void setupDataListeners() {
        DocumentReference requestRef = db.collection("requests").document(requestId);
        requestListener = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(TenantRequestDetailsActivity.this,
                            "Snapshot exception in RequestDetails", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (documentSnapshot != null) {
                    request = documentSnapshot.toObject(RepairRequest.class);
                    dm.setRepairRequest(request);
                    setupRequestViews();

                }
            }
        };
        requestReg = requestRef.addSnapshotListener(requestListener);
    }

    /**
     * Handle the item of the menu selected by the user
     * @param item the item selected from the menu
     * @return true if an item was selected, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    /**
     * Set up the views of the current request, displaying the information
     */
    private void setupRequestViews() {
        if (request.getPhoto() != null) {
            GlideApp.with(this).load(request.getPhoto()).placeholder(R.drawable.unit_placeholder).into(imgRequestDetailsPhoto);
        }
        lblRequestDetailsStatus.setText(RepairRequestExpert.statusList[request.getStatus()]);
        prgRequestDetailsStatus.setMax(RepairRequestExpert.statusList.length);
        prgRequestDetailsStatus.setProgress((request.getStatus() + 1));
        lblRequestDetailsRoom.setText(request.getRoom());
        lblRequestDetailsPriority.setText(request.toString());
        lblRequestDetailsDescription.setText(request.getDescription());
    }
}
