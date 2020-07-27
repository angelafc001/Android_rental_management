package vak.rentalmanagement.recycleviewadapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import vak.rentalmanagement.Extras;
import vak.rentalmanagement.R;
import vak.rentalmanagement.activities.RequestDetailsActivity;
import vak.rentalmanagement.data.RepairRequest;
import vak.rentalmanagement.datamanager.DataManager;
import vak.rentalmanagement.datamanager.RepairRequestExpert;

/**
 * An adapter for all the repair requests to use in the recycler view.
 *
 * @author Vu Pham
 * @author Koenn Becker
 * @author Angela Ferro Capera
 * @version 1.0
 */
public class RepairRequestAdapter extends RecyclerView.Adapter<RepairRequestViewHolder> {

    private List<RepairRequest> requests;
    private Context context;

    /**
     * Makes a new repair request adapter with the context and the list or requests
     * @param repairRequests the list of the requests
     * @param context the context
     */
    public RepairRequestAdapter(List<RepairRequest> repairRequests, Context context) {
        this.requests = repairRequests;
        this.context = context;
    }

    /**
     * Happens every time a new view holder is created
     * @param parent the parent viewgroup
     * @param viewType the type of view
     * @return the view holder that was created.
     */
    @NonNull
    @Override
    public RepairRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_repair_request, parent, false);
        return new RepairRequestViewHolder(view);
    }

    /**
     * Happens every time a new view holder is created. This sets all the data for each item.
     * @param holder The view holder being setup
     * @param position the position of the view holder
     */
    @Override
    public void onBindViewHolder(@NonNull RepairRequestViewHolder holder, int position) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        final RepairRequest request = requests.get(position);
        holder.lblRequestListRoom.setText(request.getRoom());
        holder.lblRequestListDescription.setText(request.getDescription());
        holder.lblRequestListPriority.setText(RepairRequestExpert.priorityList[request.getPriority()]);
        holder.cardRepairRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, RequestDetailsActivity.class);
                intent.putExtra(Extras.REQUEST_ID, request.getRequestId());
                context.startActivity(intent);
            }
        });

    }

    /**
     * Tells the recycler view how many items to render
     * @return the number of items to render.
     */
    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return requests.size();
    }

    /**
     * Insert an item into the list at the predefined position
     * @param position the position to insert into
     * @param data the item to insert
     */
    public void insert(int position, RepairRequest data) {
        requests.add(position, data);
        notifyItemInserted(position);
    }

    /**
     * Remove an item from the list
     * @param data the item to remove.
     */
    public void remove(RepairRequest data) {
        int position = requests.indexOf(data);
        DataManager.getInstance().removeRepairRequest(data.getRequestId());
        notifyItemRemoved(position);
    }

}
