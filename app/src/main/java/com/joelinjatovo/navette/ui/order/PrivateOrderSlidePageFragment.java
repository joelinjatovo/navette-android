package com.joelinjatovo.navette.ui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.databinding.FragmentScreenSlidePagePrivateOrderBinding;

class PrivateOrderSlidePageFragment extends Fragment {

    private FragmentScreenSlidePagePrivateOrderBinding mBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_screen_slide_page_private_order, container, false);

        return mBinding.getRoot();
    }
}
