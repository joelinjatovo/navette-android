package com.navetteclub.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.navigation.NavigationView;
import com.navetteclub.R;
import com.navetteclub.databinding.FragmentProfileBinding;
import com.navetteclub.utils.Log;
import com.navetteclub.vm.AuthViewModel;
import com.navetteclub.vm.LoginViewModel;
import com.navetteclub.vm.MyViewModelFactory;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = ProfileFragment.class.getSimpleName();

    private FragmentProfileBinding mBinding;

    private AuthViewModel authViewModel;

    private LoginViewModel loginViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());

        authViewModel = new ViewModelProvider(requireActivity(), factory).get(AuthViewModel.class);

        loginViewModel = new ViewModelProvider(requireActivity(), factory).get(LoginViewModel.class);

        final NavController navController = Navigation.findNavController(view);
        authViewModel.getAuthenticationState().observe(getViewLifecycleOwner(),
                authenticationState -> {
                    switch (authenticationState) {
                        case AUTHENTICATED:
                            Log.d(TAG, "'AUTHENTICATED'");
                            showWelcomeMessage();
                            break;
                        case INVALID_AUTHENTICATION:
                        case UNAUTHENTICATED:
                            Log.d(TAG, "'UNAUTHENTICATED'");
                            mBinding.setUser(null);
                            navController.navigate(R.id.action_global_navigation_auth);
                            break;
                    }
                });

        mBinding.editButton.setOnClickListener(this);
        mBinding.orders.setOnClickListener(this);
        mBinding.rides.setOnClickListener(this);
        mBinding.settings.setOnClickListener(this);
        mBinding.logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.orders:
                NavHostFragment.findNavController(this).navigate(R.id.action_global_navigation_orders);
                break;
            case R.id.rides:
                NavHostFragment.findNavController(this).navigate(R.id.action_global_navigation_rides);
                break;
            case R.id.logout:
                loginViewModel.setLoginResult(null);
                authViewModel.logout(requireContext());
                break;
        }
    }

    private void showWelcomeMessage() {
        Log.d(TAG, "showWelcomeMessage()");
        mBinding.setUser(authViewModel.getUser());
    }
}
