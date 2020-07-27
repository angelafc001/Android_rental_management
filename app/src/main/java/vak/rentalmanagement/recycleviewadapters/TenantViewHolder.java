package vak.rentalmanagement.recycleviewadapters;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

import vak.rentalmanagement.R;

/**
 * Holds the views for the tenant list item.
 */
class TenantViewHolder extends RecyclerView.ViewHolder{
    CardView cardTenant;
    TextView lblContactName;
    CircularImageView imgContactPhoto;
    ImageButton ibtnMessage;
    ImageButton ibtnCall;

    /**
     * Creates a new view holder
     * @param view the view to get inflater from.
     */
    public TenantViewHolder(View view) {
        super(view);
        cardTenant = view.findViewById(R.id.cardTenant);
        imgContactPhoto = view.findViewById(R.id.imgContactPhoto);
        lblContactName = view.findViewById(R.id.lblContactName);
        ibtnCall = view.findViewById(R.id.ibtnCall);
        ibtnMessage = view.findViewById(R.id.ibtnMessage);
    }

}
