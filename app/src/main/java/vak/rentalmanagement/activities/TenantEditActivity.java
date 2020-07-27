package vak.rentalmanagement.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.core.content.FileProvider;
import vak.rentalmanagement.Extras;
import vak.rentalmanagement.GlideApp;
import vak.rentalmanagement.R;
import vak.rentalmanagement.data.Tenant;
import vak.rentalmanagement.datamanager.DataManager;

/**
 * This class represents the TenantEditActivity. It allows the tenant to edit his or her profile
 * information
 *
 * @author  Vu Pham
 * @author  Koenn Becker
 * @author  Angela Ferro Capera
 * @version  1.0
 */
public class TenantEditActivity extends AbstractActivity {

    private DataManager dm;
    private CircularImageView imgTenantPhoto;
    private EditText txtTenantFirstName;
    private EditText txtTenantLastName;
    private EditText txtMobilePhone;
    private EditText txtEmail;
    private Tenant tenant;
    private String pictureImagePath = "";
    private boolean newPhoto;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private StorageReference storageRef;

    /**
     * On create of TenantEditActivity
     * @param savedInstanceState the state of the class
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            pictureImagePath = savedInstanceState.getString(Extras.IMAGE_PATH);
        }

        Intent intent = getIntent();
        String tenantId = intent.getStringExtra(Extras.TENANT_ID);
        dm = DataManager.getInstance();
        newPhoto = false;

        tenant = dm.getTenant(tenantId);

        imgTenantPhoto = findViewById(R.id.imgTenantPhoto);
        txtTenantFirstName =  findViewById(R.id.txtTenantFirstName);
        txtTenantLastName =  findViewById(R.id.txtTenantLastName);
        txtMobilePhone = findViewById(R.id.txtMobilePhone);
        txtEmail = findViewById(R.id.lblEmail);
        txtMobilePhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        storageRef = FirebaseStorage.getInstance().getReference();
    }

    /**
     * It set up the tenant views once the activity regain focus
     */
    @Override
    protected void onResume() {
        super.onResume();
        setupTenantViews();
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
     * Overriden method. It sets up the listeners for the current Tenant
     */
    @Override
    protected void setupDataListeners() {
        // Unused
    }

    /**
     * Save the changes made in the tenant profile into Firebase
     * @param view the bottom clicked
     */
    public void btnSaveOnClick(View view) {
        String firstName = txtTenantFirstName.getText().toString();
        String lastName = txtTenantLastName.getText().toString();

        if (TextUtils.isEmpty(firstName) && TextUtils.isEmpty(lastName)) {
            txtTenantLastName.setError("Last name or first name is required.");
            return;
        }
        String phoneNumber = PhoneNumberUtils.stripSeparators(txtMobilePhone.getText().toString());
        if (phoneNumber.length() < 7 || !android.util.Patterns.PHONE.matcher(phoneNumber).matches()) {
            txtMobilePhone.setError("Valid phone number is required.");
            return;
        }

        if (newPhoto) {
            Uri file = Uri.fromFile(new File(pictureImagePath));
            final StorageReference picRef = storageRef.child("images/tenant" + tenant.getTenantId());
            Toast.makeText(getApplicationContext(),
                    "Please wait a few seconds for the image to show up.",
                    Toast.LENGTH_SHORT).show();
            picRef.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            picRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    tenant.setPhoto(uri.toString());
                                    dm.replaceTenant(tenant);
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

        }
        String email =  txtEmail.getText().toString();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(),
                            "Email changed for log in.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        Tenant tenant = new Tenant(firstName,lastName, null, email, phoneNumber);
        tenant.setTenantLandlordId(this.tenant.getTenantLandlordId());
        tenant.setTenantId(auth.getUid());
        tenant.setPhoto(this.tenant.getPhoto());
        dm.replaceTenant(tenant);

        finish();
    }

    /**
     * Set up the views of the current tenant, displaying his or her information
     */
    private void setupTenantViews() {
        if (newPhoto) {
            File imgFile = new File(pictureImagePath);
            if (imgFile.exists()) {
                GlideApp.with(this).load(imgFile).placeholder(R.drawable.unit_placeholder).into(imgTenantPhoto);
            }
        } else {
            if (tenant.getPhoto() != null) {
                GlideApp.with(this).load(tenant.getPhoto()).placeholder(R.drawable.unit_placeholder).into(imgTenantPhoto);
            }
        }
        txtTenantFirstName.setText(tenant.getFirstName());
        txtTenantLastName.setText(tenant.getLastName());
        txtMobilePhone.setText(tenant.getPhone());
        txtEmail.setText(tenant.getEmail());
    }

    /**
     * Handle the OnClick() event of the Photo ImageButton.
     * Call dispatchTakePictureIntent() method when clicked.
     * @param view the Photo ImageButton
     */
    public void imgLandlordPhotoOnClick(View view) {
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
            newPhoto = true;
        }
    }
}
