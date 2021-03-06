package com.navetteclub.ui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.navetteclub.R;
import com.navetteclub.databinding.FragmentScreenSlidePagePrivateOrderBinding;

public class PrivateOrderSlidePageFragment extends Fragment {

    private FragmentScreenSlidePagePrivateOrderBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_screen_slide_page_private_order, container, false);

        return mBinding.getRoot();
    }
}
