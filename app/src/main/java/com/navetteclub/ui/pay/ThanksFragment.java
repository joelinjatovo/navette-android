package com.navetteclub.ui.pay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.navetteclub.R;
import com.navetteclub.databinding.FragmentCheckoutBinding;
import com.navetteclub.databinding.FragmentThanksBinding;

public class ThanksFragment extends Fragment {

    private FragmentThanksBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_thanks, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.buttonClose.setOnClickListener(
                v -> {
                    NavHostFragment.findNavController(this).popBackStack(R.id.navigation_home, false);
                });
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        NavHostFragment.findNavController(ThanksFragment.this).popBackStack(R.id.navigation_home, false);
                    }
                });
    }
}
