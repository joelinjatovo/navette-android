package com.navetteclub.ui.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.navetteclub.R;
import com.navetteclub.database.entity.Item;
import com.navetteclub.database.entity.Point;
import com.navetteclub.databinding.FragmentGoAndBackBinding;
import com.navetteclub.utils.Log;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;

public class GoAndBackFragment extends BottomSheetDialogFragment {

    private static final String TAG = GoAndBackFragment.class.getSimpleName();

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    private FragmentGoAndBackBinding mBinding;

    private OrderViewModel orderViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_go_and_back, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViewModel();
        setupUi();
    }

    private void setupViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        orderViewModel = new ViewModelProvider(this, factory).get(OrderViewModel.class);
        orderViewModel.getItem2PointLiveData().observe(getViewLifecycleOwner(),
                point -> {
                    if(point!=null){
                        NavHostFragment.findNavController(this).navigate(R.id.action_go_and_back_fragment_to_detail_fragment);
                    }
                });
    }

    private void setupUi() {
        mBinding.yesButton.setOnClickListener(
                v -> {
                    orderViewModel.setOrderTypeLiveData(OrderType.GO_BACK);
                    // go to search point
                    GoAndBackFragmentDirections.ActionGoAndBackFragmentToSearchFragment action = GoAndBackFragmentDirections.actionGoAndBackFragmentToSearchFragment();
                    action.setSearchType(SearchType.RETOURS);
                    NavHostFragment.findNavController(this).navigate(action);
                });

        mBinding.noButton.setOnClickListener(
                v -> {
                    orderViewModel.setOrderTypeLiveData(OrderType.GO);
                    orderViewModel.setItem2LiveData((Item) null);
                    orderViewModel.setItem2PointLiveData(null);
                    NavHostFragment.findNavController(this).navigate(R.id.action_go_and_back_fragment_to_detail_fragment);
                });
        mBinding.closeButton.setOnClickListener(
                v -> {
                    orderViewModel.setOrderTypeLiveData(OrderType.GO);
                    orderViewModel.setItem2LiveData((Item) null);
                    orderViewModel.setItem2PointLiveData(null);
                    NavHostFragment.findNavController(this).popBackStack();
                });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        orderViewModel.setOrderTypeLiveData(OrderType.GO);
                        NavHostFragment.findNavController(GoAndBackFragment.this).popBackStack();
                    }
                });
    }

}
