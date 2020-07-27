package vak.rentalmanagement.recycleviewadapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.List;

import vak.rentalmanagement.Extras;
import vak.rentalmanagement.R;
import vak.rentalmanagement.activities.TenantRequestDetailsActivity;
import vak.rentalmanagement.data.RepairRequest;
import vak.rentalmanagement.datamanager.DataManager;

/**
 * An adapter to show the tenant all of their repair requests
 *
 * @author Vu Pham
 * @author Koenn Becker
 * @author Angela Ferro Capera
 * @version 1.0
 */
public class TenantRepairRequestAdapter extends RecyclerView.Adapter<TenantRepairRequestViewHolder> {

    private List<RepairRequest> requests;
    private Context context;
    private int lastPosition = -1;

    /**
     * Saves the context and the list of repair requests.
     * @param repairRequests
     * @param context
     */
    public TenantRepairRequestAdapter(List<RepairRequest> repairRequests, Context context) {
        this.requests = repairRequests;
        this.context = context;
    }

    /**
     * Executes when a new view holder is created by the Recycler View.
     * @param parent the parent viewgroup
     * @param viewType the type of view
     * @return the new view holder
     */
    @NonNull
    @Override
    public TenantRepairRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tenant_repair_request, parent, false);
        return new TenantRepairRequestViewHolder(view);
    }

    /**
     * Binds the data to the view holder. This is called for every item of the list.
     * @param holder the view holder to setup
     * @param position the position of the view holder in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull TenantRepairRequestViewHolder holder, int position) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        final RepairRequest request = requests.get(position);
        holder.lblRequestListTitle.setText(request.getRoom());
        holder.lblRequestListDescription.setText(request.getDescription());
        holder.lblRequestListPriority.setText(request.toString());
        holder.cardRepairRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TenantRequestDetailsActivity.class);
                intent.putExtra(Extras.REQUEST_ID, request.getRequestId());
                context.startActivity(intent);
            }
        });

        if (position > lastPosition) {

            Animation animation = AnimationUtils.loadAnimation(context,
                    R.anim.up_from_bottom);
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }

    }

    /**
     * Tells the recycler view how many items to show.
     * @return the number of items to show
     */
    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return requests.size();
    }


    /**
     * Inserts a new item into the list at the specified index.
     * @param position the index to insert at
     * @param data the item to add.
     */
    public void insert(int position, RepairRequest data) {
        requests.add(position, data);
        notifyItemInserted(position);
    }

    /**
     * Removes an item from the list
     * @param data the item to remove
     */
    public void remove(RepairRequest data) {
        int position = requests.indexOf(data);
        DataManager.getInstance().removeRepairRequest(data.getRequestId());
        notifyItemRemoved(position);
    }

}
