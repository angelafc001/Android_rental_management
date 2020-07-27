package vak.rentalmanagement.fragments;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import vak.rentalmanagement.Extras;
import vak.rentalmanagement.R;
import vak.rentalmanagement.data.Tenant;
import vak.rentalmanagement.datamanager.DataManager;
import vak.rentalmanagement.recycleviewadapters.TenantRecyclerViewAdapter;

/**
 * A fragment to display all tenants in a RecyclerView to the landlord.
 *
 * @author Vu Pham
 * @author Koenn Becker
 * @author Angela Ferro Capera
 * @version 1.0
 */
public class TenantListFragment extends Fragment {

    private DataManager dm;
    private List<Tenant> tenants = new ArrayList<>();
    private View rootView;
    private RecyclerView recyclerView;
    private TenantRecyclerViewAdapter adapter;

    /**
     * Required default constructor.
     */
    public TenantListFragment() {
        // Required empty public constructor
    }

    /**
     * Instantiates the data manager.
     *
     * @param savedInstanceState the previous state
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dm = DataManager.getInstance();
    }

    /**
     * Inflates the fragment and sets up the RecyclerView and adapter.
     * @param inflater the inflater to inflate views with
     * @param container the parent
     * @param savedInstanceState previous state
     * @return the View of the fragment.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_tenant_list, container, false);
        recyclerView = rootView.findViewById(R.id.recTenantList);
        adapter = new TenantRecyclerViewAdapter(tenants, rootView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(Extras.ANIMATION_SPEED);
        itemAnimator.setRemoveDuration(Extras.ANIMATION_SPEED);
        recyclerView.setItemAnimator(itemAnimator);
        return rootView;
    }

    /**
     * Updates the data in the recycler view from the activity.
     */
    public void updateData() {
        tenants = dm.getTenantList();
        adapter = new TenantRecyclerViewAdapter(tenants, rootView.getContext());
        recyclerView.setAdapter(adapter);
    }

    /**
     * Updates the recycler view when the fragment is resumed.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

    }
}
