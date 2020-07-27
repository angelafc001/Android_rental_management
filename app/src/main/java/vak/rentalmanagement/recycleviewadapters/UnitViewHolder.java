package vak.rentalmanagement.recycleviewadapters;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import vak.rentalmanagement.R;

/**
 * Holds all views to populate a units details int the recycler view
 */
class UnitViewHolder extends RecyclerView.ViewHolder {
    CardView cardUnit;
    TextView lblName;
    TextView lblAddress;
    ImageView imgUnit;
    ImageView ibtnSettings;

    /**
     * Creates a new view holder.
     * @param view the view to get the inflater from.
     */
    public UnitViewHolder(View view) {
        super(view);
        cardUnit = view.findViewById(R.id.cardUnit);
        imgUnit = view.findViewById(R.id.imgUnit);
        lblName = view.findViewById(R.id.txtName);
        lblAddress = view.findViewById(R.id.lblTenantUnitAddress);
        ibtnSettings = view.findViewById(R.id.ibtnSettings);
    }

}
