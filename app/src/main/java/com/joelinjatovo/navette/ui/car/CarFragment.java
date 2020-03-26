package com.joelinjatovo.navette.ui.car;

import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.databinding.FragmentCarBinding;
import com.joelinjatovo.navette.utils.Constants;
import com.joelinjatovo.navette.vm.MyViewModelFactory;
import com.joelinjatovo.navette.vm.OrderViewModel;
import com.squareup.picasso.Picasso;

public class CarFragment extends BottomSheetDialogFragment {

    private FragmentCarBinding mBinding;

    private OrderViewModel orderViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_car, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        orderViewModel = new ViewModelProvider(requireActivity(), new MyViewModelFactory(requireActivity().getApplication())).get(OrderViewModel.class);

        orderViewModel.getCar().observe(getViewLifecycleOwner(),
                carAndModel -> {
                    if(carAndModel==null){
                        return;
                    }

                    if(carAndModel.getCar()!=null){
                        mBinding.setCar(carAndModel.getCar());

                        Picasso.get()
                                .load(Constants.BASE_URL + carAndModel.getCar().getImageUrl())
                                .into(mBinding.imageView);
                    }

                    if(carAndModel.getModel()!=null) {
                        mBinding.setModel(carAndModel.getModel());
                    }
                });


    }
}
