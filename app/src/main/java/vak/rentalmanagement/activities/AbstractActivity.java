package vak.rentalmanagement.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import vak.rentalmanagement.Extras;
import vak.rentalmanagement.R;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * This activity is extended by all other activities so that authentication is verified every time
 * an activity is created.
 *
 * @author Vu Pham
 * @author Koenn Becker
 * @author Angela Ferro Capera
 * @version 1.0
 */
public abstract class AbstractActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    String userId;

    /**
     * Checks if there is a user logged into Firebase Auth and if not, starts the sign in activity.
     * If they are it will call the abstract setupDataListeners() method to get the data from Firestore.
     *
     * @param savedInstanceState the previous state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    userId = user.getUid();
                    setupDataListeners();
                } else {
                    userId = null;
                    startActivityForResult(AuthUI.getInstance()
                            .createSignInIntentBuilder().setTheme(R.style.AppTheme_Auth).build(), Extras.REQUEST_AUTH);
                }
            }
        };
    }

    /**
     * This method gets the result from the sign in activity. If the sign in is good, it will be directed to the
     * BaseActivity to determine if the user is a Landlord or Tenant.
     *
     * @param requestCode The request that the result is from.
     * @param resultCode The result from the activity
     * @param data Any data bundled with the activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Extras.REQUEST_AUTH) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed in!",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(AbstractActivity.this, BaseActivity.class);
                startActivity(intent);
                finish();
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Toast.makeText(this, "Sign in canceled.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "Sign in canceled, no network",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(this, "Error signing in.",
                        Toast.LENGTH_SHORT).show();

            }
        }
    }

    /**
     * Stops listening to auth changes since the app isn't focused.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    /**
     * When the app regains focus, start listening to auth changes.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (authListener != null) {
            auth.addAuthStateListener(authListener);
        }
    }

    /**
     * Signs out of Firebase.
     */
    void signOut() {
        auth.signOut();
    }

    /**
     * The abstract method to handle setting up data listeners after authentication has been
     * established. This method must be overridden by all child activities.
     */
    protected abstract void setupDataListeners();
}
