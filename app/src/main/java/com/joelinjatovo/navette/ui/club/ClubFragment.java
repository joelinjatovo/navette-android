package com.joelinjatovo.navette.ui.club;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.database.entity.ClubAndPoint;
import com.joelinjatovo.navette.databinding.FragmentClubBinding;
import com.joelinjatovo.navette.utils.Constants;
import com.joelinjatovo.navette.vm.ClubViewModel;
import com.joelinjatovo.navette.vm.MyViewModelFactory;
import com.joelinjatovo.navette.vm.OrderViewModel;
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
                                .load(Constants.BASE_URL + clubAndPoint.getClub().getImageUrl())
                                .into(mBinding.imageView);
                    }
                });

        mBinding.clearButton.setOnClickListener(
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
