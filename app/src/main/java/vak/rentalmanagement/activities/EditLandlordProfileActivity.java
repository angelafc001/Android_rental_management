package vak.rentalmanagement.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import vak.rentalmanagement.Extras;
import vak.rentalmanagement.GlideApp;
import vak.rentalmanagement.R;
import vak.rentalmanagement.data.Landlord;
import vak.rentalmanagement.datamanager.DataManager;

/**
 * This class represents the Edit Landlord Profile Activity.
 * It displays and allows the user to change the landlord's information details, including the
 * landlord's first name, last name, mobile, and email.
 *
 * @author Koenn Becker
 * @author Vu Pham
 * @author Angela Ferro Capera
 * @version 1.0
 */
public class EditLandlordProfileActivity extends AbstractActivity {

    private DataManager dm;
    private CircularImageView imgLandlordPhoto;
    private EditText txtLandlordFirstName;
    private EditText txtLandlordLastName;
    private EditText txtMobilePhone;
    private EditText txtEmail;
    private Landlord landlord;
    private StorageReference storageRef;
    private boolean newPhoto;
    private String pictureImagePath = "";


    /**
     * Set up the EditLandlordProfileActivity layout.
     * Initialize the Data Manager and the views.
     *
     * @param savedInstanceState the class state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_landlord_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dm = DataManager.getInstance();
        landlord = dm.getLandlord();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (savedInstanceState != null) {
            pictureImagePath = savedInstanceState.getString(Extras.IMAGE_PATH);
        }

        txtLandlordFirstName = findViewById(R.id.txtLandlordFirstName);
        txtLandlordLastName = findViewById(R.id.txtLandlordLastName);
        txtMobilePhone = findViewById(R.id.txtMobilePhone);
        txtMobilePhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        txtEmail = findViewById(R.id.lblEmail);
        imgLandlordPhoto = findViewById(R.id.imgLandlordPhoto);
        newPhoto = false;

        storageRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Extras.IMAGE_PATH, pictureImagePath);
    }

    /**
     * Call the setupLandlordViews() method every time the activity gains focus.
     */
    @Override
    protected void onResume() {
        super.onResume();
        setupLandlordViews();
    }

    /**
     * Listen for incoming data of the current Landlord
     */
    @Override
    protected void setupDataListeners() {
        //unused
    }

    /**
     * Handle the OnClick() event of the Photo ImageButton.
     * Call dispatchTakePictureIntent() method when clicked.
     *
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
                GlideApp.with(this).load(imgFile).placeholder(R.drawable.unit_placeholder).into(imgLandlordPhoto);
                newPhoto = true;
            }
        }
    }

    /**
     * Create a file to store the photo with a unique name
     *
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
     * Handle the OnClick() event of Save button.
     * Save the new information and replace it for the old landlord.
     *
     * @param view the Save button
     */
    public void btnSaveOnClick(View view) {
        String firstName = txtLandlordFirstName.getText().toString();
        String lastName = txtLandlordLastName.getText().toString();

        if (TextUtils.isEmpty(firstName) && TextUtils.isEmpty(lastName)) {
            txtLandlordLastName.setError("Last name or first name is required.");
            return;
        }
        String email = txtEmail.getText().toString();

        if (TextUtils.isEmpty(email)) {
            txtEmail.setError("Email is required.");
            return;
        }

        String mobile = PhoneNumberUtils.stripSeparators(txtMobilePhone.getText().toString());
        if (mobile.length() < 7 || !android.util.Patterns.PHONE.matcher(mobile).matches()) {
            txtMobilePhone.setError("Valid phone number is required.");
            return;
        }

        if (newPhoto) {
            Uri file = Uri.fromFile(new File(pictureImagePath));
            final StorageReference picRef = storageRef.child("images/landlord_" + landlord.getLandlordId());
            picRef.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            picRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setPhotoUri(uri)
                                            .build();
                                    user.updateProfile(profileUpdates);
                                    landlord.setPhoto(uri.toString());
                                    dm.replaceLandord(landlord);
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

        }
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
        FirebaseAuth.getInstance().getCurrentUser().updateEmail(email);
        Landlord newLandlord = new Landlord(firstName, lastName, email, mobile);
        newLandlord.setLandlordId(landlord.getLandlordId());
        newLandlord.setPhoto(landlord.getPhoto());
        dm.replaceLandord(newLandlord);

        finish();
    }

    /**
     * Set up the context for the views
     */
    private void setupLandlordViews() {
        if (landlord.getPhoto() != null) {
            GlideApp.with(this)
                    .load(landlord.getPhoto())
                    .placeholder(R.drawable.unit_placeholder)
                    .into(imgLandlordPhoto);
        }
        txtLandlordFirstName.setText(landlord.getFirstName());
        txtLandlordLastName.setText(landlord.getLastName());
        txtMobilePhone.setText(landlord.getPhone());
        txtEmail.setText(landlord.getEmail());
    }
}
