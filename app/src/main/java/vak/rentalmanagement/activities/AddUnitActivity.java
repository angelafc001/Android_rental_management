package vak.rentalmanagement.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import vak.rentalmanagement.Extras;
import vak.rentalmanagement.GlideApp;
import vak.rentalmanagement.R;
import vak.rentalmanagement.data.Address;
import vak.rentalmanagement.data.Unit;
import vak.rentalmanagement.datamanager.DataHandler;
import vak.rentalmanagement.datamanager.DataManager;
import vak.rentalmanagement.datamanager.InputManager;

/**
 * This class represents the AddUnitActivity class. It allows the landlord to add a new unit
 * to his/her list of units
 *
 * @author Koenn Becker
 * @autor Vu Pham
 * @autor Angela Ferro Capera
 * @version  1.0
 */
public class AddUnitActivity extends AbstractActivity {

    private DataManager dm;
    private ImageButton ibtnPhoto;
    private TextInputLayout tilNewUnitName;
    private TextInputLayout tilNewUnitAddress;
    private TextInputLayout tilNewUnitAddressTwo;
    private TextInputLayout tilNewUnitCity;
    private TextInputLayout tilNewUnitState;
    private TextInputLayout tilNewUnitZip;
    private TextInputLayout tilNewUnitBeds;
    private TextInputLayout tilNewUnitBaths;
    private TextInputLayout tilNewUnitRentDue;
    private TextInputLayout tilNewUnitRentAmount;
    private TextInputLayout tilNewUnitYearBuilt;
    private TextInputLayout tilNewUnitFloorSize;
    private Spinner spnUnitType;
    private String pictureImagePath = "";
    private StorageReference storageRef;
    private boolean newPhoto;


    /**
     * The onCreate of AddUnitActivity that initializes all views and data manager.
     *
     * @param savedInstanceState the class state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_unit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            pictureImagePath = savedInstanceState.getString(Extras.IMAGE_PATH);
        }
        dm = DataManager.getInstance();
        tilNewUnitName = findViewById(R.id.tilNewUnitName);
        tilNewUnitAddress = findViewById(R.id.tilNewUnitAddress);
        tilNewUnitAddressTwo = findViewById(R.id.tilNewUnitAddressTwo);
        tilNewUnitCity = findViewById(R.id.tilNewUnitCity);
        tilNewUnitState = findViewById(R.id.tilNewUnitState);
        tilNewUnitZip = findViewById(R.id.tilNewUnitZip);
        tilNewUnitBeds = findViewById(R.id.tilNewUnitBeds);
        tilNewUnitBaths = findViewById(R.id.tilNewUnitBaths);
        tilNewUnitRentDue = findViewById(R.id.tilNewUnitRentDue);
        tilNewUnitRentAmount = findViewById(R.id.tilNewUnitRentAmount);
        tilNewUnitYearBuilt = findViewById(R.id.tilNewUnitYearBuilt);
        tilNewUnitFloorSize = findViewById(R.id.tilNewUnitFloorSize);
        ibtnPhoto = findViewById(R.id.ibtnPhoto);
        spnUnitType = findViewById(R.id.spnUnitType);
        storageRef = FirebaseStorage.getInstance().getReference();

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Unit.TYPES);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnUnitType.setAdapter(spinnerAdapter);
        newPhoto = false;
    }

    /**
     * Listen for incoming data of the current Landlord
     */
    @Override
    protected void setupDataListeners() {
        //unused
    }

    /**
     * Checks if there is a image file and if there is, it will display it.
     */
    @Override
    protected void onResume() {
        super.onResume();
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
     * Allows the back arrow in the option menu to go back
     *
     * @return true if the activity was succesfully finished
     */
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Save the information entered by the landlord into the new unit created
     * @param view the bottom clicked
     */
    public void btnAddUnitSaveOnClick(View view) {
        String name = null;
        String address = null;
        String address2 = null;
        String city = null;
        String state = null;
        int zip = -1;
        int beds = -1;
        double baths = -1;
        int floorSize = -1;
        double rentAmount = -1;
        int rentDue = -1;
        int year = -1;

        boolean valid = true;
        if (InputManager.validateNonEmpty(tilNewUnitName, "Name cannot be empty")) {
            name = tilNewUnitName.getEditText().getText().toString();
        } else {
            valid = false;
        }

        int type = spnUnitType.getSelectedItemPosition();

        if (InputManager.validateNonEmpty(tilNewUnitAddress, "Address cannot be empty")) {
            address = tilNewUnitAddress.getEditText().getText().toString();
        } else {
            valid = false;
        }

        address2 = tilNewUnitAddressTwo.getEditText().getText().toString().trim();

        if (InputManager.validateNonEmpty(tilNewUnitCity, "City cannot be empty")) {
            city = tilNewUnitCity.getEditText().getText().toString();
        } else {
            valid = false;
        }

        if (InputManager.validateNonEmpty(tilNewUnitState, "Empty")) {
            state = tilNewUnitState.getEditText().getText().toString();
        } else {
            valid = false;
        }

        if (InputManager.validateNonEmpty(tilNewUnitZip, "Zip cannot be empty")) {
            if (InputManager.validateNumber(tilNewUnitZip, "Zip has to be a number")) {
                zip = Integer.valueOf(tilNewUnitZip.getEditText().getText().toString());
            } else {
                valid = false;
            }
        } else {
            valid = false;
        }

        if (InputManager.validateNonEmpty(tilNewUnitBeds, "Beds cannot be empty")) {
            if (InputManager.validateNumber(tilNewUnitBeds, "Beds has to be a number")) {
                beds = Integer.valueOf(tilNewUnitBeds.getEditText().getText().toString());
            } else {
                valid = false;
            }
        } else {
            valid = false;
        }

        if (InputManager.validateNonEmpty(tilNewUnitBaths, "Baths cannot be empty")) {
            if (InputManager.validateDouble(tilNewUnitBaths, "Baths has to be a number")) {
                baths = Double.valueOf(tilNewUnitBaths.getEditText().getText().toString());
            } else {
                valid = false;
            }
        } else {
            valid = false;
        }

        if (InputManager.validateNonEmpty(tilNewUnitFloorSize, "Floor size cannot be empty")) {
            if (InputManager.validateNumber(tilNewUnitFloorSize, "Floor size has to be a number")) {
                floorSize = Integer.valueOf(tilNewUnitFloorSize.getEditText().getText().toString());
            } else {
                valid = false;
            }
        } else {
            valid = false;
        }

        if (InputManager.validateNonEmpty(tilNewUnitYearBuilt, "Year cannot be empty")) {
            if (InputManager.validateNumber(tilNewUnitYearBuilt, "Year has to be a number")) {
                year = Integer.valueOf(tilNewUnitYearBuilt.getEditText().getText().toString());
            } else {
                valid = false;
            }
        } else {
            valid = false;
        }

        if (InputManager.validateNonEmpty(tilNewUnitRentAmount, "Rent amount cannot be empty")) {
            if (InputManager.validateDouble(tilNewUnitRentAmount, "Rent amount has to be a number")) {
                rentAmount = Double.valueOf(tilNewUnitRentAmount.getEditText().getText().toString());
            } else {
                valid = false;
            }
        } else {
            valid = false;
        }

        if (InputManager.validateNonEmpty(tilNewUnitRentDue, "Rent due cannot be empty")) {
            if (InputManager.validateNumber(tilNewUnitRentDue, "Rent due has to be a number")) {
                rentDue = Integer.valueOf(tilNewUnitRentDue.getEditText().getText().toString());
            } else {
                valid = false;
            }
        } else {
            valid = false;
        }

        if (valid) {
            final Unit unit = new Unit();
            Address addressObj = new Address();
            addressObj.setFirstLine(address);
            addressObj.setCity(city);
            addressObj.setState(state);
            addressObj.setZipCode(zip);
            if (address2 != null && !address2.isEmpty()) {
                addressObj.setSecondLine(address2);
            }
            unit.setUnitId(new DataHandler().getUnitId());
            unit.setName(name);
            unit.setAddress(addressObj);
            unit.setBaths(baths);
            unit.setBeds(beds);
            unit.setLandlordId(FirebaseAuth.getInstance().getCurrentUser().getUid());
            unit.setRent(rentAmount);
            unit.setRentDueDay(rentDue);
            unit.setSquareFeet(floorSize);
            unit.setType(type);
            unit.setYearBuilt(year);
            unit.setRequestIds(new ArrayList<String>());

            Uri file = Uri.fromFile(new File(pictureImagePath));
            final StorageReference picRef = storageRef.child("images/unit_" + unit.getUnitId());

            picRef.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            picRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    unit.setPhoto(uri.toString());
                                    dm.replaceUnit(unit);
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

            dm.addUnit(unit);
            Toast.makeText(this, "New unit successfully added!", Toast.LENGTH_LONG).show();
            finish();
        }

    }

    /**
     * Handle the OnClick() event of the Photo ImageButton.
     * Call dispatchTakePictureIntent() method when clicked.
     * @param view the Photo ImageButton
     */
    public void ibtnPhotoOnClick(View view) {
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
                GlideApp.with(this).load(imgFile).placeholder(R.drawable.unit_placeholder).into(ibtnPhoto);
                newPhoto = true;
            }
        }
    }
}
