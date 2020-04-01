package com.joelinjatovo.navette.ui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.databinding.FragmentPrivatizeBinding;
import com.joelinjatovo.navette.databinding.FragmentTravelBinding;

public class TravelFragment extends Fragment {

    private FragmentTravelBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_travel, container, false);

        return mBinding.getRoot();
    }

}
