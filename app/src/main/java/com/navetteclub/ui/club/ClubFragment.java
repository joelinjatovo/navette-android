package com.navetteclub.ui.club;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.navetteclub.R;
import com.navetteclub.database.entity.ClubAndPoint;
import com.navetteclub.databinding.FragmentClubBinding;
import com.navetteclub.utils.Constants;
import com.navetteclub.utils.UiUtils;
import com.navetteclub.vm.ClubViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;
import com.squareup.picasso.Picasso;

public class ClubFragment extends BottomSheetDialogFragment {

    private FragmentClubBinding mBinding;

    private OrderViewModel orderViewModel;

    private ClubViewModel clubViewModel;

    private ClubAndPoint mClubAndPoint;

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

                CoordinatorLayout coordinatorLayout = d.findViewById(R.id.coordinatorLayout);
                FrameLayout.LayoutParams params = null;
                if (coordinatorLayout != null) {
                    params = (FrameLayout.LayoutParams) coordinatorLayout.getLayoutParams();
                    params.height = UiUtils.getScreenHeight();
                    coordinatorLayout.setLayoutParams(params);
                }

                FrameLayout bottomSheet = d.findViewById(R.id.design_bottom_sheet);
                if (bottomSheet != null) {
                    BottomSheetBehavior sheetBehavior = BottomSheetBehavior.from(bottomSheet);
                    sheetBehavior.setSkipCollapsed(true);
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    //sheetBehavior.setPeekHeight(UiUtils.getScreenHeight(), true);
                }
            }
        });

        return dialog;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_club, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        orderViewModel = new ViewModelProvider(requireActivity(),
                new MyViewModelFactory(requireActivity().getApplication())).get(OrderViewModel.class);

        clubViewModel = new ViewModelProvider(requireActivity(),
                new MyViewModelFactory(requireActivity().getApplication())).get(ClubViewModel.class);

        clubViewModel.getClub().observe(getViewLifecycleOwner(),
                clubAndPoint -> {
                    if(clubAndPoint==null){
                        return;
                    }

                    mClubAndPoint = clubAndPoint;

                    if(clubAndPoint.getClub()!=null){
                        mBinding.setClub(clubAndPoint.getClub());

                        Picasso.get()
                                .load(Constants.getBaseUrl() + clubAndPoint.getClub().getImageUrl())
                                .into(mBinding.imageView);
                    }
                });

        mBinding.toolbar.setNavigationOnClickListener(
                v -> {
                    NavHostFragment.findNavController(this).popBackStack();
                });

        mBinding.confirmButton.setOnClickListener(
                v -> {
                    orderViewModel.setClub(mClubAndPoint.getClub(), mClubAndPoint.getPoint());
                    NavHostFragment.findNavController(this).navigate(R.id.action_club_to_order);
                });
    }
}
