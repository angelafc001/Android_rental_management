package vak.rentalmanagement.recycleviewadapters;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import vak.rentalmanagement.R;

/**
 * Holds all views for the tenant's repair requests
 *
 * @author Vu Pham
 * @author Koenn Becker
 * @author Angela Ferro Capera
 * @version 1.0
 */
class TenantRepairRequestViewHolder extends RecyclerView.ViewHolder {

    CardView cardRepairRequest;
    TextView lblRequestListTitle;
    TextView lblRequestListDescription;
    TextView lblRequestListPriority;

    /**
     * Creates a new view holder
     * @param itemView the view to get the inflater from
     */
    public TenantRepairRequestViewHolder(@NonNull View itemView) {
        super(itemView);
        cardRepairRequest = itemView.findViewById(R.id.cardRepairRequest);
        lblRequestListTitle = itemView.findViewById(R.id.lblRequestListRoom);
        lblRequestListDescription = itemView.findViewById(R.id.lblRequestListDescription);
        lblRequestListPriority = itemView.findViewById(R.id.lblRequestListPriority);
    }
}
