package vak.rentalmanagement.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import vak.rentalmanagement.Extras;
import vak.rentalmanagement.GlideApp;
import vak.rentalmanagement.R;
import vak.rentalmanagement.data.Address;
import vak.rentalmanagement.data.Tenant;
import vak.rentalmanagement.data.Unit;
import vak.rentalmanagement.datamanager.DataManager;
import vak.rentalmanagement.datamanager.InputManager;

/**
 * This class represents the Edit Unit Activity.
 * It displays and allows the user to change the unit's information details, including the Unit's name,
 * photo, type, address, number of beds, number of baths, rent due date, rent amount, year built,
 * floor size, and the tenant associates with that unit.
 *
 * @author Koenn Becker
 * @author Vu Pham
 * @author Angela Ferro Capera
 * @version 1.0
 */
public class EditUnitActivity extends AbstractActivity {

    private Unit unit;
    private DataManager dm;
    private ImageButton ibtnPhoto;
    private Spinner spnUnitType;
    private TextInputLayout tilUnitName;
    private TextInputLayout tilUnitAddress;
    private TextInputLayout tilUnitAddressTwo;
    private TextInputLayout tilUnitCity;
    private TextInputLayout tilUnitState;
    private TextInputLayout tilUnitZip;
    private TextInputLayout tilUnitBeds;
    private TextInputLayout tilUnitBaths;
    private TextInputLayout tilUnitRentDue;
    private TextInputLayout tilUnitRentAmount;
    private TextInputLayout tilUnitYearBuilt;
    private TextInputLayout tilUnitFloorSize;

    private TextView txtUnitName;
    private TextView txtUnitAddress;
    private TextView txtUnitAddressTwo;
    private TextView txtUnitCity;
    private TextView txtUnitState;
    private TextView txtUnitZip;
    private TextView txtUnitBeds;
    private TextView txtUnitBaths;
    private TextView txtUnitRentDue;
    private TextView txtUnitRentAmount;
    private TextView txtUnitYearBuilt;
    private TextView txtUnitFloorSize;
    private TextView lblTenant;
    private String unitId;
    private Tenant tenant;
    private StorageReference storageRef;
    private String pictureImagePath = "";
    private boolean newPhoto;

    /**
     * Set up the EditUnitActivity layout.
     * Initialize the Data Manager and the views, and get the intent.
     * @param savedInstanceState the class state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_unit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState != null) {
            pictureImagePath = savedInstanceState.getString(Extras.IMAGE_PATH);
        }

        Intent intent = getIntent();
        unitId = intent.getStringExtra(Extras.UNIT_ID);
        if (unitId == null) {
            finish();
        }

        dm = DataManager.getInstance();

        ibtnPhoto = findViewById(R.id.ibtnPhoto);
        tilUnitName = findViewById(R.id.tilUnitName);
        spnUnitType = findViewById(R.id.spnUnitType);
        tilUnitAddress = findViewById(R.id.tilUnitAddress);
        tilUnitAddressTwo = findViewById(R.id.tilUnitAddressTwo);
        tilUnitCity = findViewById(R.id.tilUnitCity);
        tilUnitState = findViewById(R.id.tilUnitState);
        tilUnitZip = findViewById(R.id.tilUnitZip);
        tilUnitBeds = findViewById(R.id.tilUnitBeds);
        tilUnitBaths = findViewById(R.id.tilUnitBaths);
        tilUnitRentDue = findViewById(R.id.tilUnitRentDue);
        tilUnitRentAmount = findViewById(R.id.tilUnitRentAmount);
        tilUnitYearBuilt = findViewById(R.id.tilUnitYearBuilt);
        tilUnitFloorSize = findViewById(R.id.tilUnitFloorSize);
        lblTenant = findViewById(R.id.lblTenant);

        txtUnitName = findViewById(R.id.txtUnitName);
        txtUnitAddress = findViewById(R.id.txtUnitAddress);
        txtUnitAddressTwo = findViewById(R.id.txtUnitAddressTwo);
        txtUnitCity = findViewById(R.id.txtUnitCity);
        txtUnitState = findViewById(R.id.txtUnitState);
        txtUnitZip = findViewById(R.id.txtUnitZip);
        txtUnitBeds = findViewById(R.id.txtUnitBeds);
        txtUnitBaths = findViewById(R.id.txtUnitBaths);
        txtUnitRentDue = findViewById(R.id.txtUnitRentDue);
        txtUnitRentAmount = findViewById(R.id.txtUnitRentAmount);
        txtUnitYearBuilt = findViewById(R.id.txtUnitYearBuilt);
        txtUnitFloorSize = findViewById(R.id.txtUnitFloorSize);

        storageRef = FirebaseStorage.getInstance().getReference();
        newPhoto = false;
    }

    /**
     * Call the setupUnitViews every time the activity gains focus.
     */
    @Override
    protected void onResume() {
        super.onResume();
        this.unit = dm.getUnit(unitId);
        setupUnitViews();
    }

    /**
     * Listen for incoming data of the current unit
     */
    @Override
    protected void setupDataListeners() {
        // Unused
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
     * @throws IOException Exception if image does not gets created
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

        // Save a file: path for use with ACTION_VIEW intents
        pictureImagePath = image.getAbsolutePath();
        return image;
    }

    /**
     * Set the photo taken to the ImageButton if the request code matches, the result code matches,
     * and the file exists.
     * @param requestCode the request code for the intent
     * @param resultCode the result code from the intent
     * @param data the intent itself
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
     * Handle action bar item clicks here. The action bar will automatically handle clicks on
     * the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
     * @param item the menu item chosen
     * @return true if te menu works
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
     * Handle the OnClick() event of Save Button.
     * Save the new information and replace it for the old unit.
     * @param view the btnSave Button
     */
    public void btnSaveOnClick(View view) {
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
        if (InputManager.validateNonEmpty(tilUnitName, "Name cannot be empty")) {
            name = tilUnitName.getEditText().getText().toString();
        } else {
            valid = false;
        }

        int type = spnUnitType.getSelectedItemPosition();

        if (InputManager.validateNonEmpty(tilUnitAddress, "Address cannot be empty")) {
            address = tilUnitAddress.getEditText().getText().toString();
        } else {
            valid = false;
        }

        address2 = tilUnitAddressTwo.getEditText().getText().toString().trim();

        if (InputManager.validateNonEmpty(tilUnitCity, "City cannot be empty")) {
            city = tilUnitCity.getEditText().getText().toString();
        } else {
            valid = false;
        }

        if (InputManager.validateNonEmpty(tilUnitState, "Empty")) {
            state = tilUnitState.getEditText().getText().toString();
        } else {
            valid = false;
        }

        if (InputManager.validateNonEmpty(tilUnitZip, "Zip cannot be empty")) {
            if (InputManager.validateNumber(tilUnitZip, "Zip has to be a number")) {
                zip = Integer.valueOf(tilUnitZip.getEditText().getText().toString());
            } else {
                valid = false;
            }
        } else {
            valid = false;
        }

        if (InputManager.validateNonEmpty(tilUnitBeds, "Beds cannot be empty")) {
            if (InputManager.validateNumber(tilUnitBeds, "Beds has to be a number")) {
                beds = Integer.valueOf(tilUnitBeds.getEditText().getText().toString());
            } else {
                valid = false;
            }
        } else {
            valid = false;
        }

        if (InputManager.validateNonEmpty(tilUnitBaths, "Baths cannot be empty")) {
            if (InputManager.validateDouble(tilUnitBaths, "Baths has to be a number")) {
                baths = Double.valueOf(tilUnitBaths.getEditText().getText().toString());
            } else {
                valid = false;
            }
        } else {
            valid = false;
        }

        if (InputManager.validateNonEmpty(tilUnitFloorSize, "Floor size cannot be empty")) {
            if (InputManager.validateNumber(tilUnitFloorSize, "Floor size has to be a number")) {
                floorSize = Integer.valueOf(tilUnitFloorSize.getEditText().getText().toString());
            } else {
                valid = false;
            }
        } else {
            valid = false;
        }

        if (InputManager.validateNonEmpty(tilUnitYearBuilt, "Year cannot be empty")) {
            if (InputManager.validateNumber(tilUnitYearBuilt, "Year has to be a number")) {
                year = Integer.valueOf(tilUnitYearBuilt.getEditText().getText().toString());
            } else {
                valid = false;
            }
        } else {
            valid = false;
        }

        if (InputManager.validateNonEmpty(tilUnitRentAmount, "Rent amount cannot be empty")) {
            if (InputManager.validateDouble(tilUnitRentAmount, "Rent amount has to be a number")) {
                rentAmount = Double.valueOf(tilUnitRentAmount.getEditText().getText().toString());
            } else {
                valid = false;
            }
        } else {
            valid = false;
        }

        if (InputManager.validateNonEmpty(tilUnitRentDue, "Rent due cannot be empty")) {
            if (InputManager.validateNumber(tilUnitRentDue, "Rent due has to be a number")) {
                rentDue = Integer.valueOf(tilUnitRentDue.getEditText().getText().toString());
            } else {
                valid = false;
            }
        } else {
            valid = false;
        }

        if (valid) {

            Address addressObj = new Address();
            addressObj.setFirstLine(address);
            addressObj.setCity(city);
            addressObj.setState(state);
            addressObj.setZipCode(zip);
            if (address2 != null && !address2.isEmpty()) {
                addressObj.setSecondLine(address2);
            }
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

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
            builder.setTitle("Save  ")
                    .setMessage(" Save changes on unit " + unit.getName() + "?")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
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
                                        }
                                    });
                            dm.replaceUnit(unit);
                            finish();
                        }
                    })
                    .setNegativeButton("No", null).show();
        }
    }

    /**
     * Set up the context for the views and the adapter for the Type spinner
     */
    private void setupUnitViews() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, Unit.TYPES);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if (unit.getPhoto() != null) {
            GlideApp.with(this).load(unit.getPhoto()).placeholder(R.drawable.unit_placeholder).into(ibtnPhoto);
        }

        txtUnitName.setText(unit.getName());
        spnUnitType.setAdapter(spinnerAdapter);
        txtUnitAddress.setText(unit.getAddress().getFirstLine());
        txtUnitAddressTwo.setText(unit.getAddress().getSecondLine());
        txtUnitCity.setText(unit.getAddress().getCity());
        txtUnitState.setText(unit.getAddress().getState());
        txtUnitZip.setText(String.valueOf(unit.getAddress().getZipCode()));
        txtUnitBeds.setText(String.valueOf(unit.getBeds()));
        txtUnitBaths.setText(String.valueOf(unit.getBaths()));
        txtUnitRentDue.setText(String.valueOf(unit.getRentDueDay()));
        txtUnitRentAmount.setText(String.valueOf(unit.getRent()));
        txtUnitYearBuilt.setText(String.valueOf(unit.getYearBuilt()));
        txtUnitFloorSize.setText(String.valueOf(unit.getSquareFeet()));

        if (unit.getTenantId() == null) {
            lblTenant.setHint(getString(R.string.strNoTenant));
        } else {
            Tenant tenant = dm.getTenant(unit.getTenantId());
            lblTenant.setHint(tenant.toString());
        }
    }
}
