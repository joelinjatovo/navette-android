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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.navetteclub.R;
import com.navetteclub.database.entity.Point;
import com.navetteclub.databinding.FragmentPrivatizeBinding;
import com.navetteclub.databinding.FragmentTravelBinding;
import com.navetteclub.utils.Log;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;

public class TravelFragment extends Fragment {

    private static final String TAG = TravelFragment.class.getSimpleName();

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    private FragmentTravelBinding mBinding;

    private OrderViewModel orderViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_travel, container, false);

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

                orderViewModel.setReturn(place);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i(TAG, "The user canceled the operation.");
            }
        }
    }

    private void setupViewModel() {
        orderViewModel = new ViewModelProvider(this,
                new MyViewModelFactory(requireActivity().getApplication())).get(OrderViewModel.class);


        orderViewModel.getOrderWithDatasLiveData().observe(getViewLifecycleOwner(),
                orderWithDatas -> {
                    if(orderWithDatas == null){
                        return;
                    }

                    // Points
                    if(orderWithDatas.getPoints()!=null){
                        Log.d(TAG, "Size=" + orderWithDatas.getPoints().size());

                        // Retours
                        if(orderWithDatas.getPoints().size()>2) {
                            Point point = orderWithDatas.getPoints().get(2);
                            if(point!=null) {
                                NavHostFragment.findNavController(this).navigate(R.id.action_travel_fragment_to_detail_fragment);
                            }
                        }
                    }
                });
    }

    private void setupUi() {
        mBinding.stepView.go(3, true);

        mBinding.yesButton.setOnClickListener(
                v -> {
                    // reset return point
                    orderViewModel.setReturn(null);

                    // go to search point
                    Navigation.findNavController(v).navigate(R.id.action_travel_fragment_to_search_fragment);
                });

        mBinding.noButton.setOnClickListener(
                v -> {
                    Navigation.findNavController(v).navigate(R.id.action_travel_fragment_to_detail_fragment);
                });
    }

}
