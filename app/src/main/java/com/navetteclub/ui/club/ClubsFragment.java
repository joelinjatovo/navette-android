package com.navetteclub.ui.club;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
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
import android.view.MenuItem;
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
import com.navetteclub.database.entity.OrderWithDatas;
import com.navetteclub.databinding.FragmentClubsBinding;
import com.navetteclub.ui.OnClickItemListener;
import com.navetteclub.ui.order.OrdersFragment;
import com.navetteclub.utils.Log;
import com.navetteclub.utils.UiUtils;
import com.navetteclub.vm.ClubViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.models.RemoteLoaderResult;
import com.navetteclub.vm.OrderViewModel;

import java.util.List;

public class ClubsFragment extends BottomSheetDialogFragment implements OnClickItemListener<ClubAndPoint> {

    private static final String TAG = OrdersFragment.class.getSimpleName();

    private ClubViewModel clubViewModel;

    private FragmentClubsBinding mBinding;

    private ClubRecyclerViewAdapter mAdapter;

    private android.widget.SearchView searchView;

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
        mAdapter = new ClubRecyclerViewAdapter(this);
        RecyclerView recyclerView = mBinding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(mAdapter);

        // Layout height
        mBinding.getRoot().setMinimumHeight(UiUtils.getScreenHeight());

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar();
        setupViewModel();
        setupUi();
    }

    private void setupUi() {
        mBinding.toolbar.setNavigationOnClickListener(
                v -> {
                    NavHostFragment.findNavController(this).popBackStack();
                });

        mBinding.loaderErrorView.getButton().setOnClickListener(
                v -> {
                    mBinding.setShowError(false);
                    mBinding.setIsLoading(true);
                    clubViewModel.load();
                });
    }

    private void setupViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());

        clubViewModel = new ViewModelProvider(requireActivity(), factory).get(ClubViewModel.class);

        clubViewModel.getClubs().observe(getViewLifecycleOwner(),
                clubAndPointList -> {
                    if (clubAndPointList == null) {
                        return;
                    }
                    mAdapter.setItems(clubAndPointList);
                    mBinding.setShowError(clubAndPointList.isEmpty());
                });

        clubViewModel.getClubsResult().observe(getViewLifecycleOwner(),
                result -> {
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
                });
    }

    private void setupToolbar() {
        mBinding.toolbar.inflateMenu(R.menu.menu_search);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = mBinding.toolbar.getMenu().findItem(R.id.search);
        if (searchItem != null) {
            searchView = (android.widget.SearchView) searchItem.getActionView();
        }

        if (searchView != null && searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));

            android.widget.SearchView.OnQueryTextListener queryTextListener = new android.widget.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String query) {
                    Log.i(TAG + "Search", query);
                    search(query);  // <-- use room dao
                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.i(TAG + "Search", query);
                    search(query);  // <-- use room dao
                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }

        mBinding.toolbar.setOnMenuItemClickListener(item -> item.getItemId() != R.id.search);
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

    @Override
    public void onClick(View view, int position, ClubAndPoint item) {
        clubViewModel.setClub(item);
        NavHostFragment.findNavController(ClubsFragment.this).navigate(R.id.club_fragment);
    }
}
