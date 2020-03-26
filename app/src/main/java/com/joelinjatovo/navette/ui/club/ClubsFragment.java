package com.joelinjatovo.navette.ui.club;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.database.entity.ClubAndPoint;
import com.joelinjatovo.navette.databinding.FragmentClubsBinding;
import com.joelinjatovo.navette.vm.ClubViewModel;
import com.joelinjatovo.navette.vm.MyViewModelFactory;
import com.joelinjatovo.navette.models.RemoteLoaderResult;
import com.joelinjatovo.navette.vm.OrderViewModel;

import java.util.List;

public class ClubsFragment extends Fragment {

    private ClubViewModel clubViewModel;

    private OrderViewModel orderViewModel;

    private FragmentClubsBinding mBinding;

    private ClubRecyclerViewAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_clubs, container, false);

        // Set the adapter
        mAdapter = new ClubRecyclerViewAdapter(mListener);
        RecyclerView recyclerView = mBinding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(mAdapter);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        clubViewModel = new ViewModelProvider(requireActivity(), new MyViewModelFactory(requireActivity().getApplication())).get(ClubViewModel.class);

        orderViewModel = new ViewModelProvider(requireActivity(), new MyViewModelFactory(requireActivity().getApplication())).get(OrderViewModel.class);

        clubViewModel.getClubs().observe(getViewLifecycleOwner(),
                clubAndPointList -> {
                    if (clubAndPointList == null) {
                        return;
                    }

                    mAdapter.setItems(clubAndPointList);
                });

        clubViewModel.getRetrofitResult().observe(getViewLifecycleOwner(), new Observer<RemoteLoaderResult<List<ClubAndPoint>>>() {
            @Override
            public void onChanged(RemoteLoaderResult<List<ClubAndPoint>> result) {
                if (result == null) {
                    return;
                }

                if (result.getError() != null) {
                    Toast.makeText(requireContext(), result.getError(), Toast.LENGTH_SHORT).show();
                    Snackbar.make(mBinding.getRoot(), result.getError(), Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        mBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //mAdapter.filter(query); // <-- use adapter comparator
                search(query);  // <-- use room dao
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                //mAdapter.filter(query); // <-- use adapter comparator
                search(query);  // <-- use room dao
                return true;
            }
        });
    }

    private void search(String search){
         search = "%" + search + "%";
         clubViewModel.search(search).observe(getViewLifecycleOwner(), clubAndPointList -> {
             if (clubAndPointList == null) {
                 return;
             }

             mAdapter.setItems(clubAndPointList);
         });
    }

    private OnListFragmentInteractionListener mListener = new OnListFragmentInteractionListener() {
        @Override
        public void onListFragmentInteraction(View v, ClubAndPoint item) {
            orderViewModel.setClub(item);
            Navigation.findNavController(v).popBackStack();
        }
    };

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(View v, ClubAndPoint item);
    }
}
