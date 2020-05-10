package com.navetteclub.ui.driver;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.navetteclub.R;

import com.navetteclub.databinding.FragmentRidePointBinding;
import com.navetteclub.utils.Log;

public class RidePointFragment extends Fragment {

    private static final String TAG = RidePointFragment.class.getSimpleName();

    private FragmentRidePointBinding mBinding;

    private String ridePointId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            ridePointId = RidePointFragmentArgs.fromBundle(getArguments()).getRidePointId();
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG + "Cycle", "onCreateView");
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_ride_point, container, false);

        return mBinding.getRoot();
    }

}
