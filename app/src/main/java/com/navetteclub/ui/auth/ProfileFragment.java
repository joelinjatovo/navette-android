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
import com.navetteclub.database.entity.User;
import com.navetteclub.databinding.FragmentProfileBinding;
import com.navetteclub.utils.Constants;
import com.navetteclub.utils.Log;
import com.navetteclub.vm.AuthViewModel;
import com.navetteclub.vm.LoginViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.squareup.picasso.Picasso;

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
        mBinding.menuOrders.setOnClickListener(this);
        mBinding.menuRides.setOnClickListener(this);
        mBinding.menuLogout.setOnClickListener(this);
        mBinding.verifyButton.setOnClickListener(v -> NavHostFragment.findNavController(ProfileFragment.this).navigate(R.id.action_global_navigation_auth));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.menu_orders:
                NavHostFragment.findNavController(this).navigate(R.id.action_global_navigation_orders);
                break;
            case R.id.menu_rides:
                NavHostFragment.findNavController(this).navigate(R.id.action_global_navigation_rides);
                break;
            case R.id.menu_logout:
                loginViewModel.setLoginResult(null);
                authViewModel.logout(requireContext());
                break;
        }
    }

    private void showWelcomeMessage() {
        User user = authViewModel.getUser();
        Log.d(TAG, "showWelcomeMessage()" + user);
        if(user==null){
            return;
        }

        mBinding.setUser(user);
        if(user.getImageUrl()!=null){
            Picasso.get()
                    .load(Constants.getBaseUrl() + user.getImageUrl())
                    .placeholder(R.drawable.user_placeholder)
                    .error(R.drawable.user_placeholder)
                    .into(mBinding.avatarImageView);
        }
        if(user.getRoles()!=null && !user.getRoles().isEmpty()){
            if(user.getRoles().contains(User.ROLE_ADMIN)){
                mBinding.setRole(getString(R.string.admin));
            }else if(user.getRoles().contains(User.ROLE_DRIVER)){
                mBinding.setRole(getString(R.string.driver));
            }else if(user.getRoles().contains(User.ROLE_CUSTOMER)){
                mBinding.setRole(getString(R.string.customer));
            }else{
                mBinding.setRole(getString(R.string.unknown_role));
            }
        }
    }
}
