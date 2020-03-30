package com.joelinjatovo.navette.ui.auth;

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
import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.databinding.FragmentProfileBinding;
import com.joelinjatovo.navette.utils.Log;
import com.joelinjatovo.navette.vm.AuthViewModel;
import com.joelinjatovo.navette.vm.LoginViewModel;
import com.joelinjatovo.navette.vm.MyViewModelFactory;

public class ProfileFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

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

        authViewModel = new ViewModelProvider(requireActivity(),
                new MyViewModelFactory(requireActivity().getApplication())).get(AuthViewModel.class);

        loginViewModel = new ViewModelProvider(requireActivity(),
                new MyViewModelFactory(requireActivity().getApplication())).get(LoginViewModel.class);

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
                            navController.navigate(R.id.login_fragment);
                            break;
                    }
                });

        mBinding.navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.sync:
                NavHostFragment.findNavController(this).navigate(R.id.orders_fragment);
            break;
            case R.id.logout:
                loginViewModel.setLoginResult(null);
                authViewModel.logout(requireContext());
            break;
        }
        return false;
    }

    private void showWelcomeMessage() {
        Log.d(TAG, "showWelcomeMessage()");
        mBinding.setUser(authViewModel.getUser());
    }
}
