package com.navetteclub.ui.order;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.navetteclub.R;
import com.navetteclub.databinding.FragmentPrivatizeBinding;

public class PrivatizeFragment extends Fragment {

    private FragmentPrivatizeBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_privatize, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupUi();
    }

    private void setupUi() {
        mBinding.nextButton.setOnClickListener(
                v -> {
                    Navigation.findNavController(v).navigate(R.id.action_privatize_fragment_to_travel_fragment);
                });
    }
}
