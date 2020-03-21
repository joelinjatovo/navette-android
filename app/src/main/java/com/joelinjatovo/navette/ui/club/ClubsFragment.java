package com.joelinjatovo.navette.ui.club;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.database.entity.ClubAndPoint;
import com.joelinjatovo.navette.databinding.FragmentClubsListBinding;
import com.joelinjatovo.navette.ui.club.ClubRecyclerViewAdapter;
import com.joelinjatovo.navette.vm.ClubViewModel;
import com.joelinjatovo.navette.vm.MyViewModelFactory;
import com.joelinjatovo.navette.models.RemoteLoaderResult;

import java.util.List;

public class ClubsFragment extends Fragment {

    private ClubViewModel clubViewModel;

    private FragmentClubsListBinding mBinding;

    private ClubRecyclerViewAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_clubs_list, container, false);

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

                if (result.getSuccess() != null) {
                    mAdapter.setItems(result.getSuccess());
                }
            }
        });
    }

    private OnListFragmentInteractionListener mListener = new OnListFragmentInteractionListener() {
        @Override
        public void onListFragmentInteraction(ClubAndPoint item) {

        }
    };

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(ClubAndPoint item);
    }
}
