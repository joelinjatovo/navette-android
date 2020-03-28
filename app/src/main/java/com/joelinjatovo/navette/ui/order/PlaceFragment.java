package com.joelinjatovo.navette.ui.order;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.database.entity.ClubAndPoint;
import com.joelinjatovo.navette.databinding.FragmentClubsBinding;
import com.joelinjatovo.navette.databinding.FragmentPlaceBinding;
import com.joelinjatovo.navette.models.RemoteLoaderResult;
import com.joelinjatovo.navette.ui.club.ClubRecyclerViewAdapter;
import com.joelinjatovo.navette.utils.UiUtils;
import com.joelinjatovo.navette.vm.ClubViewModel;
import com.joelinjatovo.navette.vm.MyViewModelFactory;
import com.joelinjatovo.navette.vm.OrderViewModel;

import java.util.List;

public class PlaceFragment extends BottomSheetDialogFragment {

    private OrderViewModel orderViewModel;

    private FragmentPlaceBinding mBinding;

    private int place = 0;

    private int max = 10;

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
                }
            }
        });

        return dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_place, container, false);

        //mBinding.container.getLayoutParams().height = UiUtils.getScreenHeight();

        setupUi();

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViewModel();
    }

    private void setupUi() {
        mBinding.plus.setOnClickListener(
                v -> {
                    if( place < max ) {
                        orderViewModel.setPlace( place + 1 );
                    }else{
                        orderViewModel.setPlace( max );
                    }
                });

        mBinding.minus.setOnClickListener(
                v -> {
                    if(place>1){
                        orderViewModel.setPlace( place - 1 );
                    }else{
                        orderViewModel.setPlace( 1 );
                    }
                });
    }

    private void setupViewModel() {
        orderViewModel = new ViewModelProvider(this, new MyViewModelFactory(requireActivity().getApplication())).get(OrderViewModel.class);

        orderViewModel.getPlace().observe(getViewLifecycleOwner(),
                place -> {
                    this.place = place;
                    mBinding.setPlace(place);
                });
    }
}
