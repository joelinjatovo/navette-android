package com.navetteclub.ui.order;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.navetteclub.R;
import com.navetteclub.databinding.FragmentPrivatizeBinding;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;

public class PrivatizeFragment extends Fragment {

    private static final String TAG = PrivatizeFragment.class.getSimpleName();

    private FragmentPrivatizeBinding mBinding;

    private OrderViewModel orderViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_privatize, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViewModel();

        setupUi();
    }

    private void setupViewModel() {
        orderViewModel = new ViewModelProvider(this,
                new MyViewModelFactory(requireActivity().getApplication())).get(OrderViewModel.class);
    }

    private void setupUi() {
        mBinding.yesButton.setOnClickListener(
                v -> {
                    orderViewModel.setPrivatized(true);
                    Navigation.findNavController(v).navigate(R.id.action_privatize_fragment_to_travel_fragment);
                });

        mBinding.noButton.setOnClickListener(
                v -> {
                    orderViewModel.setPrivatized(false);
                    Navigation.findNavController(v).navigate(R.id.action_privatize_fragment_to_travel_fragment);
                });
    }
}