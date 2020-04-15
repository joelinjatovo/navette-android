package com.navetteclub.ui.order;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.navetteclub.R;
import com.navetteclub.database.entity.Order;
import com.navetteclub.database.entity.User;
import com.navetteclub.databinding.FragmentDetailBinding;
import com.navetteclub.utils.Log;
import com.navetteclub.vm.AuthViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;

public class DetailFragment extends Fragment {

    private static final String TAG = DetailFragment.class.getSimpleName();

    private FragmentDetailBinding mBinding;

    private OrderViewModel orderViewModel;

    private AuthViewModel authViewModel;

    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG + "Cycle", "onCreateView");
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG + "Cycle", "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG + "Cycle", "onViewCreated");
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        setupOrderViewModel(factory);
        setupAuthViewModel(factory);
        setupUi();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG + "Cycle", "onDestroyView");
    }

    private void setupAuthViewModel(MyViewModelFactory factory) {
        authViewModel = new ViewModelProvider(this, factory).get(AuthViewModel.class);
        authViewModel.getAuthenticationState().observe(getViewLifecycleOwner(),
                authenticationState -> {
                    if (authenticationState == AuthViewModel.AuthenticationState.AUTHENTICATED) {
                        boolean unauthenticated = false;
                        User user = authViewModel.getUser();
                        if(user!=null){
                            if(user.getPhone()==null){
                                unauthenticated = true;
                                mBinding.authErrorView.getTitleView().setText(R.string.error_phone);
                                mBinding.authErrorView.getSubtitleView().setText(R.string.error_phone_desc);
                            }else if(!user.getVerified()){
                                unauthenticated = true;
                                mBinding.authErrorView.getTitleView().setText(R.string.error_verify_phone);
                                mBinding.authErrorView.getSubtitleView().setText(R.string.error_verify_phone_desc);
                            }
                        }
                        mBinding.setIsUnauthenticated(unauthenticated);
                    }else{
                        mBinding.setIsUnauthenticated(true);
                        mBinding.authErrorView.getTitleView().setText(R.string.error);
                        mBinding.authErrorView.getSubtitleView().setText(R.string.error_401);
                    }
                });
    }

    private void setupOrderViewModel(MyViewModelFactory factory) {
        orderViewModel = new ViewModelProvider(this, factory).get(OrderViewModel.class);
    }

    private void setupUi() {
        mBinding.stepView.go(5, true);
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.signing));
        mBinding.bookNowButton.setOnClickListener(
                v -> {
                    // Nothing
                });
        mBinding.authErrorView.getButton().setOnClickListener(
                v -> {
                    User user = authViewModel.getUser();
                    NavController navController = Navigation.findNavController(v);
                    if(user==null){
                        navController.navigate(R.id.navigation_auth);
                    }else{
                        if(user.getPhone()==null){
                            navController.navigate(R.id.action_global_phone_fragment);
                        }else if(!user.getVerified()){
                            navController.navigate(R.id.action_global_verify_phone_fragment);
                        }
                    }
                });
    }

}
