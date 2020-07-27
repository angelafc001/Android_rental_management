package vak.rentalmanagement.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import vak.rentalmanagement.Extras;
import vak.rentalmanagement.R;

/**
 * This class represents the view that a brand new Tenant will see if they have yet not
 * been linked to a unit  by the landlord.
 * @author Vu Pham
 * @author Koenn Becker
 * @author Angela Ferro Capera
 * @version  1.0
 */
public class TenantBaseActivityNew extends AbstractActivity {

    /**
     * On create of the new tenant activity
     * @param savedInstanceState the state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_tenant_base_blank);
        Intent intent = getIntent();
        String tenantId = intent.getStringExtra(Extras.TENANT_ID);
        TextView userPhone = findViewById(R.id.lblUserId);
        userPhone.setText(tenantId);
    }

    /**
     * Override method from the AbstractActivity class
     */
    @Override
    protected void setupDataListeners() {
        // Unused
    }

    /**
     * Log out the app when clicked
     * @param view the logout button
     */
    public void btnLogOutOnClick(View view) {
        signOut();
    }

}
