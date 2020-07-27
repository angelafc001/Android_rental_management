package vak.rentalmanagement.datamanager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import vak.rentalmanagement.data.Landlord;
import vak.rentalmanagement.data.RepairRequest;
import vak.rentalmanagement.data.Tenant;
import vak.rentalmanagement.data.Unit;

/**
 * Controls uploading all data to Firestore.
 *
 * @author Vu Pham
 * @author Koenn Becker
 * @author Angela Ferro Capera
 * @version 1.0
 */
public class DataHandler {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    /**
     * Adds or replaces the unit in Firestore.
     *
     * @param unit the unit to add, if it already exits on the cloud it will be replaced.
     */
    public void addUnit(Unit unit) {
        DocumentReference ref = db.collection("units").document(String.valueOf(unit.getUnitId()));
        ref.set(unit).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    /**
     * Gets the next unit ID.
     *
     * @return String - the next unit ID.
     */
    public String getUnitId() {
        return db.collection("units").document().getId();
    }

    /**
     * Deletes the unit from Firestore if it exists.
     *
     * @param unit the unit to remove.
     */
    public void deleteUnit(Unit unit) {
        DocumentReference ref = db.collection("units").document(String.valueOf(unit.getUnitId()));
        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    /**
     * Adds or replaces the landlord in Firestore.
     *
     * @param landlord the landlord to add, if it already exits on the cloud it will be replaced.
     */
    public void addLandlord(Landlord landlord) {
        DocumentReference ref = db.collection("users").document(landlord.getLandlordId());
        ref.set(landlord).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    /**
     * Replaces the tenant in Firestore, if it doesn't exist, it will get added.
     * @param tenant the tenant to replace.
     */
    public void updateTenant(Tenant tenant) {
        DocumentReference ref = db.collection("users").document(tenant.getTenantId());
        ref.set(tenant);
    }

    /**
     * Deletes the unit's photo from Firebase Storage.
     * @param unit the unit whose photo to delete.
     */
    public void removeUnitPhoto(Unit unit) {
        storageRef.child("images/unit_" + unit.getUnitId()).delete();
    }

    /**
     * Adds or replaces the repair request in Firestore.
     *
     * @param request the RepairRequest to add, if it already exits on the cloud it will be replaced.
     */
    public void addRepairRequest(RepairRequest request) {
        DocumentReference ref = db.collection("requests").document(request.getRequestId());
        ref.set(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    /**
     * Gets the next repair request ID.
     *
     * @return the next repair request ID.
     */
    public String getRepairRequestId() {
        return db.collection("requests").document().getId();
    }

    /**
     * Removes the repair request from Firestore if it exists.
     * @param request the request to remove
     */
    public void removeRequest(RepairRequest request) {
        DocumentReference ref = db.collection("requests").document(String.valueOf(request.getRequestId()));
        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }
}
