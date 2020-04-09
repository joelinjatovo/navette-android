package com.navetteclub.ui.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.navetteclub.R;
import com.navetteclub.database.entity.Point;
import com.navetteclub.databinding.FragmentGoAndBackBinding;
import com.navetteclub.utils.Log;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;

public class GoAndBackFragment extends Fragment {

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getLatLng() + ", " + place.getName() + ", " + place.getId());

                orderViewModel.setReturn(place, true);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i(TAG, "The user canceled the operation.");
            }
        }
    }

    private void setupViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());

        orderViewModel = new ViewModelProvider(this, factory).get(OrderViewModel.class);

        orderViewModel.getOrderLiveData().observe(getViewLifecycleOwner(),
                orderWithDatas -> {
                    if(orderWithDatas == null){
                        return;
                    }

                    // Points
                    if(orderWithDatas.getRetours()!=null) {
                        NavHostFragment.findNavController(this).navigate(R.id.action_go_and_back_fragment_to_detail_fragment);
                    }
                });
    }

    private void setupUi() {
        mBinding.stepView.go(3, true);

        mBinding.yesButton.setOnClickListener(
                v -> {
                    // reset return point
                    orderViewModel.setReturn((Point) null, true);

                    // go to search point
                    GoAndBackFragmentDirections.ActionGoAndBackFragmentToSearchFragment action = GoAndBackFragmentDirections.actionGoAndBackFragmentToSearchFragment();
                    action.setSearchType(SearchType.RETOURS);
                    Navigation.findNavController(v).navigate(action);
                });

        mBinding.noButton.setOnClickListener(
                v -> {
                    Navigation.findNavController(v).navigate(R.id.action_go_and_back_fragment_to_detail_fragment);
                });

        mBinding.toolbar.setNavigationOnClickListener(
                v -> {
                    NavHostFragment.findNavController(this).popBackStack();
                });
    }

}
