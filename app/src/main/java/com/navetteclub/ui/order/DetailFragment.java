package com.navetteclub.ui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.navetteclub.R;
import com.navetteclub.databinding.FragmentDetailBinding;
import com.navetteclub.databinding.FragmentTravelBinding;

public class DetailFragment extends Fragment {

    private FragmentDetailBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false);

        return mBinding.getRoot();
    }

}
