package vak.rentalmanagement.recycleviewadapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import vak.rentalmanagement.Extras;
import vak.rentalmanagement.GlideApp;
import vak.rentalmanagement.R;
import vak.rentalmanagement.activities.TenantDetailsActivity;
import vak.rentalmanagement.data.Tenant;

/**
 * An adapter for all of landlords tenants to use in the recycler view.
 *
 * @author Vu Pham
 * @author Koenn Becker
 * @author Angela Ferro Capera
 * @version 1.0
 */
public class TenantRecyclerViewAdapter extends RecyclerView.Adapter<TenantViewHolder> {

    private Context context;
    private List<Tenant> tenants;

    /**
     * Saves the context and list of tenants
     * @param tenants list of tenants
     * @param context the context of the app.
     */
    public TenantRecyclerViewAdapter(List<Tenant> tenants, Context context) {
        this.tenants = tenants;
        this.context = context;
    }

    /**
     * Executes when a new view holder is created by the recycler view.
     * @param parent the parent veiwgroup
     * @param i the position
     * @return the new view holder
     */
    @NonNull
    @Override
    public TenantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tenant, parent,false);
        return new TenantViewHolder(view);
    }

    /**
     * Puts in the data to the view holder.
     * @param holder the view holder to setup
     * @param position the position of the view holder in the list
     */
    @Override
    public void onBindViewHolder(@NonNull TenantViewHolder holder, int position) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        final Tenant tenant = tenants.get(position);
        if (tenants.get(position).getPhoto() != null) {
            GlideApp.with(this.context)
                    .load(tenant.getPhoto())
                    .placeholder(R.drawable.unit_placeholder)
                    .into(holder.imgContactPhoto);
        }
        holder.lblContactName.setText(tenant.toString());
        holder.cardTenant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TenantDetailsActivity.class);
                intent.putExtra(Extras.TENANT_ID, tenant.getTenantId());
                context.startActivity(intent);
            }
        });
        holder.ibtnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String smsNumber = "smsto:" + tenant.getPhone();
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                smsIntent.setData(Uri.parse(smsNumber));
                if (smsIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(smsIntent);
                }
            }
        });

        holder.ibtnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = String.format("tel: %s", tenant.getPhone());
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse(phoneNumber));
                if (dialIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(dialIntent);
                }
            }
        });
    }

    /**
     * Tells the recycler view how many items to show.
     * @return the number of items to show
     */
    @Override
    public int getItemCount() {
        return tenants.size();
    }
}
