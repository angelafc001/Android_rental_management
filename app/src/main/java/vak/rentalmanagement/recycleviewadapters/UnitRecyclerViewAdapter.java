package vak.rentalmanagement.recycleviewadapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import java.util.List;

import vak.rentalmanagement.Extras;
import vak.rentalmanagement.GlideApp;
import vak.rentalmanagement.R;
import vak.rentalmanagement.activities.EditUnitActivity;
import vak.rentalmanagement.activities.UnitDetailsActivity;
import vak.rentalmanagement.data.Unit;
import vak.rentalmanagement.datamanager.DataManager;

/**
 * An adapter for all the units to use in the recycler view.
 *
 * @author Vu Pham
 * @author Koenn Becker
 * @author Angela Ferro Capera
 * @version 1.0
 */
public class UnitRecyclerViewAdapter extends RecyclerView.Adapter<UnitViewHolder> {

    private List<Unit> units;
    private Context context;
    private int lastPosition = -1;

    /**
     * Saves the list of units and the context.
     * @param units the list of units
     * @param context the context of the app
     */
    public UnitRecyclerViewAdapter(List<Unit> units, Context context) {
        this.units = units;
        this.context = context;
    }

    /**
     * Executes when a new view holder is created.
     * @param parent the view group
     * @param viewType the view type
     * @return the new view holder.
     */
    @NonNull
    @Override
    public UnitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_unit, parent, false);
        return new UnitViewHolder(view);
    }

    /**
     * Binds the unit data to the view holder
     * @param holder the view holder to setup
     * @param position the position of the view holder
     */
    @Override
    public void onBindViewHolder(@NonNull UnitViewHolder holder, int position) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        final Unit unit = units.get(position);

        GlideApp.with(context)
                .load(unit.getPhoto())
                .placeholder(R.drawable.unit_placeholder)
                .into(holder.imgUnit);
        holder.lblName.setText(unit.getName());
        holder.lblAddress.setText(unit.getAddress().toString());

        holder.cardUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UnitDetailsActivity.class);
                intent.putExtra(Extras.UNIT_ID, unit.getUnitId());
                context.startActivity(intent);
            }
        });
        final PopupMenu popup = new PopupMenu(context, holder.ibtnSettings);
        popup.getMenuInflater().inflate(R.menu.unit_popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navUnitDelete: {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);
                        builder.setTitle("Delete Unit")
                                .setMessage(" Delete unit " + unit.getName() + "?")
                                .setIcon(android.R.drawable.ic_delete)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        remove(unit);
                                        Toast.makeText(context,
                                                "Unit succesfully deleted!",
                                                Toast.LENGTH_LONG).show();
                                    }
                                })
                                .setNegativeButton("No", null).show();
                        break;
                    }
                    case R.id.navUnitEdit: {
                        Intent intent = new Intent(context, EditUnitActivity.class);
                        intent.putExtra(Extras.UNIT_ID, unit.getUnitId());
                        context.startActivity(intent);
                        break;
                    }
                }
                return true;
            }
        });

        holder.ibtnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.show();
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
     * Tells the recycler view how many items to show
     * @return how many items to show in the list
     */
    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return units.size();
    }

    /**
     * Inserts a new item at the specified position.
     * @param position the position to add the new item at
     * @param data the item to add
     */
    public void insert(int position, Unit data) {
        units.add(position, data);
        notifyItemInserted(position);
    }

    /**
     * Removes an item from the list
     * @param data the item to remove.
     */
    private void remove(Unit data) {
        int position = units.indexOf(data);
        DataManager.getInstance().removeUnit(data.getUnitId());
        notifyItemRemoved(position);
    }


}
