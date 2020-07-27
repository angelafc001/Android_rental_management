package vak.rentalmanagement.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
import vak.rentalmanagement.data.Tenant;
import vak.rentalmanagement.datamanager.DataHandler;
import vak.rentalmanagement.datamanager.DataManager;
import vak.rentalmanagement.datamanager.RepairRequestExpert;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * The activity to show the repair request details to the landlord
 *
 * @author Koenn Becker
 * @author Vu Pham
 * @author Angela Ferro Capera
 * @version 1.0
 */
public class RequestDetailsActivity extends AbstractActivity {

    private String requestId;
    private RepairRequest request;
    private Tenant tenant;
    private DataManager dm;

    private FirebaseFirestore db;
    private ListenerRegistration requestReg;
    private EventListener<DocumentSnapshot> requestListener;

    private ListenerRegistration tenantReg;
    private EventListener<DocumentSnapshot> tenantListener;

    private ImageView imgRequestDetailsPhoto;
    private TextView lblRequestDetailsRoom;
    private TextView lblRequestDetailsPriority;
    private TextView lblRequestDetailsBy;
    private TextView lblRequestDetailsStatus;
    private TextView lblRequestDetailsDescription;
    private Button btnRequestDetailsNext;
    private ProgressBar prgRequestDetailsStatus;

    /**
     * Instantiates all the views and data manager.
     * @param savedInstanceState the previous state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dm = DataManager.getInstance();
        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        requestId = intent.getStringExtra(Extras.REQUEST_ID);
        if (requestId == null) {
            finish();
        }

        imgRequestDetailsPhoto = findViewById(R.id.imgRequestDetailsPhoto);
        lblRequestDetailsRoom = findViewById(R.id.lblRequestDetailsRoom);
        lblRequestDetailsPriority = findViewById(R.id.lblRequestDetailsPriority);
        lblRequestDetailsBy = findViewById(R.id.lblRequestDetailsBy);
        lblRequestDetailsStatus = findViewById(R.id.lblRequestDetailsStatus);
        lblRequestDetailsDescription = findViewById(R.id.lblRequestDetailsDescription);
        ImageButton ibtnRequestDetailsBack = findViewById(R.id.ibtnRequestDetailsBack);
        ibtnRequestDetailsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ibtnRequestDetailsBackOnClick();
            }
        });
        btnRequestDetailsNext = findViewById(R.id.btnRequetsDetailsNext);
        btnRequestDetailsNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRequestDetailsNext();
            }
        });
        prgRequestDetailsStatus = findViewById(R.id.prgRequestDetailsStatus);

    }

    /**
     * Stops listening for data changes when the activity pauses.
     */
    @Override
    protected void onPause() {
        super.onPause();
        stopAllListeners();
    }

    /**
     * Progresses the status of the repair request if there is any progress left to complete.
     */
    private void btnRequestDetailsNext() {
        final int newStatus = request.getStatus() + 1;

        if (newStatus < RepairRequestExpert.statusList.length) {
            AlertDialog.Builder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle("Change Request Status")
                    .setMessage("Do you want to set the status of this request to: " +
                            RepairRequestExpert.statusList[newStatus] + "?")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            request.setStatus(newStatus);
                            dm.replaceRepairRequest(request);
                        }
                    })
                    .setNegativeButton("No", null).show();
        }
    }

    /**
     * Regresses the progress of the repair request if there is a status to regress.
     */
    private void ibtnRequestDetailsBackOnClick() {
        final int oldStatus = request.getStatus() - 1;
        if (oldStatus >= 1) {
            AlertDialog builder = new MaterialAlertDialogBuilder(this)
                    .setTitle("Go to Previous Status")
                    .setMessage("Do you want to revert the status of this request to: " +
                            RepairRequestExpert.statusList[oldStatus] + "?")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            request.setStatus(oldStatus);
                            dm.replaceRepairRequest(request);
                        }
                    })
                    .setNegativeButton("No", null).show();
        } else {
            Toast.makeText(this, "You cannot undo past \"Viewed\"", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Modifies the "up" button to function as the back button.
     * @param item the menu item selected
     * @return true if this method handles that action.
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
     * Starts listening for changes to the current request.
     */
    @Override
    protected void setupDataListeners() {
        stopAllListeners();

        DocumentReference requestRef = db.collection("requests").document(requestId);
        requestListener = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(RequestDetailsActivity.this,
                            "Snapshot exception in RequestDetails", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (documentSnapshot != null) {
                    request = documentSnapshot.toObject(RepairRequest.class);
                    if (request.getStatus() == RepairRequestExpert.STATUS_SENT) {
                        request.setStatus(RepairRequestExpert.STATUS_VIEWED);
                        DataHandler dh = new DataHandler();
                        dh.addRepairRequest(request);
                    }
                    dm.setRepairRequest(request);
                    setupTenantListener();
                    setupRequestViews();

                }
            }
        };
        requestReg = requestRef.addSnapshotListener(requestListener);
    }

    /**
     * Starts listening to changes to the tenant who made the request.
     */
    private void setupTenantListener() {
        DocumentReference tenantRef = db.collection("users").document(request.getFromId());
        tenantListener = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(RequestDetailsActivity.this,
                            "Snapshot exception in RequestDetails", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (documentSnapshot != null) {
                    tenant = documentSnapshot.toObject(Tenant.class);
                    dm.setTenant(tenant);
                    setupTenantViews();
                }
            }
        };
        tenantReg = tenantRef.addSnapshotListener(tenantListener);
    }

    /**
     * Sets the name of the tenant text view. Called if any changes were detected.
     */
    private void setupTenantViews() {
        lblRequestDetailsBy.setText(tenant.toString());
    }

    /**
     * Sets up all views to show details of request. Called if any changes were detected.
     */
    private void setupRequestViews() {
        if (request.getPhoto() != null) {
            GlideApp.with(this).load(request.getPhoto()).transition(withCrossFade()).placeholder(R.drawable.unit_placeholder).into(imgRequestDetailsPhoto);
        }
        lblRequestDetailsRoom.setText(request.getRoom());
        lblRequestDetailsPriority.setText(request.toString());
        lblRequestDetailsStatus.setText(RepairRequestExpert.statusList[request.getStatus()]);
        lblRequestDetailsDescription.setText(request.getDescription());
        prgRequestDetailsStatus.setMax(RepairRequestExpert.statusList.length);
        prgRequestDetailsStatus.setProgress((request.getStatus() + 1));
        if ((request.getStatus() +1 ) < RepairRequestExpert.statusList.length) {
            btnRequestDetailsNext.setText(RepairRequestExpert.statusList[request.getStatus() + 1]);
            btnRequestDetailsNext.setEnabled(true);
        } else {
            btnRequestDetailsNext.setText(getString(R.string.strComplete));
            btnRequestDetailsNext.setEnabled(false);
        }
    }

    /**
     * Stops listening for data changes.
     */
    private void stopAllListeners() {
        if (requestReg != null && requestListener != null) {
            requestReg.remove();
        }

        if (tenantReg != null && tenantListener != null) {
            tenantReg.remove();
        }
    }
}
