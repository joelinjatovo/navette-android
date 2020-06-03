package com.navetteclub.ui.order;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.navetteclub.R;
import com.navetteclub.database.entity.Car;
import com.navetteclub.database.entity.Order;
import com.navetteclub.databinding.FragmentPlaceBinding;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;

public class PlaceFragment extends BottomSheetDialogFragment {

    private OrderViewModel orderViewModel;

    private FragmentPlaceBinding mBinding;

    private int place = 1;

    private int max = 10;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;

                ConstraintLayout constraintLayout = d.findViewById(R.id.constraintLayout);
                FrameLayout.LayoutParams params = null;
                if (constraintLayout != null) {
                    params = (FrameLayout.LayoutParams) constraintLayout.getLayoutParams();
                    //params.height = UiUtils.getScreenHeight();
                    constraintLayout.setLayoutParams(params);
                }

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
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViewModel();
        setupUi();
    }

    private void setupUi() {
        mBinding.plus.setOnClickListener(
                v -> {
                    if(place < max){
                        place++;
                        mBinding.value.setText(String.valueOf(place));
                    }
                });
        mBinding.minus.setOnClickListener(
                v -> {
                    if(place > 1){
                        place--;
                        mBinding.value.setText(String.valueOf(place));
                    }
                });
        mBinding.closeButton.setOnClickListener(
                v -> {
                    NavHostFragment.findNavController(this).popBackStack();
                });
        mBinding.cancelButton.setOnClickListener(
                v -> {
                    NavHostFragment.findNavController(this).popBackStack();
                });
        mBinding.confirmButton.setOnClickListener(
                v -> {
                    Order order = orderViewModel.getOrder();
                    order.setPlace(place);
                    orderViewModel.setOrderLiveData(order);
                    orderViewModel.setPlaceLiveData(place);
                    NavHostFragment.findNavController(this).popBackStack();
                });
    }

    private void setupViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        orderViewModel = new ViewModelProvider(this, factory).get(OrderViewModel.class);
        this.place = orderViewModel.getPlace();
        mBinding.setPlace(this.place);
        Car car = orderViewModel.getCar();
        if(car!=null){
            max = car.getPlace();
        }
    }
}
