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
import vak.rentalmanagement.data.RepairRequest;
import vak.rentalmanagement.datamanager.DataManager;
import vak.rentalmanagement.recycleviewadapters.RepairRequestAdapter;



/**
 * A fragment to display repair requests in a RecyclerView to the landlord.
 *
 * @author Vu Pham
 * @author Koenn Becker
 * @author Angela Ferro Capera
 * @version 1.0
 */
public class RepairRequestListFragment extends Fragment {

    private List<RepairRequest> requests = new ArrayList<>();
    private DataManager dm;
    private View rootView;
    private RecyclerView recyclerView;
    private RepairRequestAdapter adapter;

    /**
     * Required default constructor.
     */
    public RepairRequestListFragment() {
        // Required empty public constructor
    }

    /**
     * Instantiates the data manager.
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
        rootView = inflater.inflate(R.layout.fragment_repair_request_list, container, false);
        recyclerView = rootView.findViewById(R.id.recRepairRequestList);
        adapter = new RepairRequestAdapter(requests, rootView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(Extras.ANIMATION_SPEED);
        itemAnimator.setRemoveDuration(Extras.ANIMATION_SPEED);
        recyclerView.setItemAnimator(itemAnimator);
        return rootView;
    }

    /**
     * Used to update the data contained in the fragment from an activity.
     */
    public void updateData() {
        requests = dm.getRepairRequestList();
        adapter = new RepairRequestAdapter(requests, rootView.getContext());
        recyclerView.setAdapter(adapter);
    }

    /**
     * Refreshes the recycler view when coming back to the fragment.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

    }

}
