package com.navetteclub.ui.club;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.navetteclub.R;
import com.navetteclub.database.entity.ClubAndPoint;
import com.navetteclub.databinding.FragmentClubsBinding;
import com.navetteclub.utils.UiUtils;
import com.navetteclub.vm.ClubViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.models.RemoteLoaderResult;
import com.navetteclub.vm.OrderViewModel;

import java.util.List;

public class ClubsFragment extends BottomSheetDialogFragment {

    private ClubViewModel clubViewModel;

    private FragmentClubsBinding mBinding;

    private ClubRecyclerViewAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;

                FrameLayout bottomSheet = d.findViewById(R.id.design_bottom_sheet);
                if (bottomSheet != null) {
                    BottomSheetBehavior sheetBehavior = BottomSheetBehavior.from(bottomSheet);
                    sheetBehavior.setSkipCollapsed(true);
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    sheetBehavior.setPeekHeight(UiUtils.getScreenHeight());
                }
            }
        });

        return dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_clubs, container, false);

        // Set the adapter
        mAdapter = new ClubRecyclerViewAdapter(mListener);
        RecyclerView recyclerView = mBinding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(mAdapter);

        // Layout height
        mBinding.linearLayout.setMinimumHeight(UiUtils.getScreenHeight());

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        clubViewModel = new ViewModelProvider(requireActivity(), new MyViewModelFactory(requireActivity().getApplication())).get(ClubViewModel.class);

        clubViewModel.getClubs().observe(getViewLifecycleOwner(),
                clubAndPointList -> {
                    if (clubAndPointList == null) {
                        return;
                    }

                    mAdapter.setItems(clubAndPointList);

                    mBinding.setShowError(clubAndPointList.isEmpty());
                });

        clubViewModel.getClubsResult().observe(getViewLifecycleOwner(), new Observer<RemoteLoaderResult<List<ClubAndPoint>>>() {
            @Override
            public void onChanged(RemoteLoaderResult<List<ClubAndPoint>> result) {
                if (result == null) {
                    return;
                }

                mBinding.setIsLoading(false);

                if (result.getError() != null) {
                    mBinding.setShowError(true);
                    mBinding.loaderErrorView.getSubtitleView().setText(result.getError());
                }else{
                    mBinding.setShowError(false);
                }
            }
        });

        mBinding.loaderErrorView.getButton().setOnClickListener(
                v -> {
                    mBinding.setShowError(false);
                    mBinding.setIsLoading(true);
                    clubViewModel.load();
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

        mBinding.backButton.setOnClickListener(
                v -> {
                    NavHostFragment.findNavController(this).popBackStack();
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
            clubViewModel.setClub(item);
            NavHostFragment.findNavController(ClubsFragment.this).navigate(R.id.club_fragment);
        }
    };

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(View v, ClubAndPoint item);
    }
}
