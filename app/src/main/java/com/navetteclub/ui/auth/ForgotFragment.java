package com.navetteclub.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.navetteclub.R;
import com.navetteclub.databinding.FragmentForgotBinding;

/*
* @TODO: Implementation du mot de passe oublié
 */
public class ForgotFragment extends BottomSheetDialogFragment {

    private static final String TAG = ForgotFragment.class.getSimpleName();

    private FragmentForgotBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_forgot, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);
        setupBackAction(navController);
    }

    private void setupBackAction(NavController navController) {
        mBinding.backButton.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        //authViewModel.logout(requireContext());
                        navController.popBackStack();
                    }
                });
    }
}
