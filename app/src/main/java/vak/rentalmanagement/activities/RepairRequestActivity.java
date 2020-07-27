package vak.rentalmanagement.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.core.content.FileProvider;
import vak.rentalmanagement.Extras;
import vak.rentalmanagement.GlideApp;
import vak.rentalmanagement.R;
import vak.rentalmanagement.data.RepairRequest;
import vak.rentalmanagement.datamanager.DataManager;
import vak.rentalmanagement.datamanager.RepairRequestExpert;


/**
 * This class represents the Repair Request Activity.
 * Tenant can submit a new Repair Request anytime they need something in the unit fixed.
 * A Repair Request is classify by room, type of issue, picture of the issue, priority level and a
 * brief description of the issue.
 *
 * @author Koenn Becker
 * @author Vu Pham
 * @author Angela Ferro Capera
 * @version 1.0
 */
public class RepairRequestActivity extends AbstractActivity implements AdapterView.OnItemSelectedListener {

    //Private fields
    private ImageButton ibtnRepairRequestPhoto;
    private Spinner spnRoom;
    private Spinner spnIssue;
    private RadioGroup radioGroup;
    private EditText txtRequestDescription;
    private TextInputLayout tilRequestDescription;
    private DataManager dm;
    private String tenantId;
    private String pictureImagePath = "";
    private StorageReference storageRef;

    /**
     * Set up the RepairRequestActivity layout
     * @param savedInstanceState the class state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_request);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dm = DataManager.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        tenantId = auth.getUid();
        if (tenantId == null) {
            finish();
        }

        if (savedInstanceState != null) {
            pictureImagePath = savedInstanceState.getString(Extras.IMAGE_PATH);
        }

        ibtnRepairRequestPhoto = findViewById(R.id.ibtnRepairRequestPhoto);
        spnRoom = findViewById(R.id.spnRoom);
        radioGroup = findViewById(R.id.radioGroup);
        txtRequestDescription = findViewById(R.id.txtRequestDescription);
        tilRequestDescription = findViewById(R.id.tilRequestDescription);
        storageRef = FirebaseStorage.getInstance().getReference();

        ArrayAdapter<String> roomAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                RepairRequestExpert.getRoomList());
        spnRoom.setAdapter(roomAdapter);
        spnRoom.setOnItemSelectedListener(this);
    }

    /**
     * Unused for this activity. No data to get.
     */
    @Override
    protected void setupDataListeners() {
        // Unused
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
     * Save the current state of the class when it is about to lose focus
     * @param outState the bundle to save
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Extras.IMAGE_PATH, pictureImagePath);
    }

    /**
     * Submits a new Repair Request
     * @param view The layout item
     */
    public void btnRequestSubmitOnClick(View view) {
        int priority = 0;
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select a priority", Toast.LENGTH_LONG).show();
            return;
        } else if (radioGroup.getCheckedRadioButtonId() == R.id.radPriorityOne) {
            priority = RepairRequestExpert.PRIORITY_ONE;
        } else if (radioGroup.getCheckedRadioButtonId() == R.id.radPriorityTwo) {
            priority = RepairRequestExpert.PRIORITY_TWO;
        } else if (radioGroup.getCheckedRadioButtonId() == R.id.radPriorityThree) {
            priority = RepairRequestExpert.PRIORITY_THREE;
        }
        String description = "";
        if (txtRequestDescription.getText().toString().trim().length() == 0) {
            tilRequestDescription.setError("Please enter a short description");
            return;
        } else {
            tilRequestDescription.setError(null);
            description = txtRequestDescription.getText().toString();
        }

        String room = RepairRequestExpert.getRoomList()[spnRoom.getSelectedItemPosition()];
        String issue = spnIssue.getSelectedItem().toString();
        final RepairRequest request = new RepairRequest();
        request.setToId(dm.getLandlord().getLandlordId());
        request.setFromId(tenantId);
        request.setDescription(description);
        request.setRoom(room);
        request.setIssue(issue);
        request.setPriority(priority);
        request.setStatus(priority);

        Uri file = Uri.fromFile(new File(pictureImagePath));
        final StorageReference picRef = storageRef.child("images/repairRequest_" + request.getRequestId());

        picRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        picRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                request.setPhoto(uri.toString());
                                dm.replaceRepairRequest(request);
                                Toast.makeText(getApplicationContext(),
                                        "Upload photo succeeded! Wait a few seconds for image to show up.",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(),
                                "Upload photo failed!",
                                Toast.LENGTH_LONG).show();
                    }
                });

        dm.addRepairRequest(request);
        Toast.makeText(this, "Added new request", Toast.LENGTH_LONG).show();
        finish();
    }

    /**
     * Spinner listener. It will populate the type of issue spinner depending on the user room selection
     * @param parent The AdapterView that was clicked
     * @param view The spinner
     * @param position the position of the view in the Adapter
     * @param id The position of the item selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spnIssue = findViewById(R.id.spnIssue);
        int room = spnRoom.getSelectedItemPosition();

        ArrayAdapter<String> issueAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                RepairRequestExpert.getIssueList(room));
        spnIssue.setAdapter(issueAdapter);
    }

    /**
     * Method called when the item selected is not available in the Spinner anymore
     * @param parent The AdapterView that was clicked
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Handle the OnClick() event of the Photo ImageButton.
     * Call dispatchTakePictureIntent() method when clicked.
     * @param view the Photo ImageButton
     */
    public void ibtnRepairRequestPhotoOnClick(View view) {
        dispatchTakePictureIntent();
    }

    /**
     * Call createImageFile() method to create a file, and then take a photo by calling startActivityForResult
     * with a intent that has the photoURI (which has the location of the File)
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(getApplicationContext(),
                        "Cannot create a file to save image",
                        Toast.LENGTH_LONG).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "vak.rentalmanagement.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, Extras.REQUEST_CAMERA);
            }
        }
    }

    /**
     * Create a file to store the photo with a unique name
     * @return a File to store the image in
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        pictureImagePath = image.getAbsolutePath();
        return image;
    }

    /**
     * Set the photo taken to the ImageButton if the request code matches, the result code matches,
     * and the file exists.
     *
     * @param requestCode the request code for the intent
     * @param resultCode  the result code from the intent
     * @param data        the intent itself
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Extras.REQUEST_CAMERA && resultCode == RESULT_OK) {
            File imgFile = new File(pictureImagePath);
            if (imgFile.exists()) {
                GlideApp.with(this).load(imgFile).placeholder(R.drawable.unit_placeholder).into(ibtnRepairRequestPhoto);
            }
        }
    }
}
