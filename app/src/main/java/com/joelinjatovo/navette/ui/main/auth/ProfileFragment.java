package com.joelinjatovo.navette.ui.main.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding mBinding;

    private AuthViewModel authViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(requireActivity(), new AuthViewModelFactory()).get(AuthViewModel.class);

        final NavController navController = Navigation.findNavController(view);
        authViewModel.getAuthenticationState().observe(getViewLifecycleOwner(),
                authenticationState -> {
                    switch (authenticationState) {
                        case AUTHENTICATED:
                            showWelcomeMessage();
                            break;
                        case UNAUTHENTICATED:
                            navController.navigate(R.id.login_fragment);
                            break;
                    }
                });
    }

    private void showWelcomeMessage() {
    }
}
