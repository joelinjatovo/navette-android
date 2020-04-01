package com.navetteclub.ui.auth;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.navetteclub.R;
import com.navetteclub.database.entity.User;
import com.navetteclub.database.entity.UserWithRoles;
import com.navetteclub.databinding.FragmentRegisterBinding;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.RegisterViewModel;

public class RegisterFragment  extends Fragment {

    private FragmentRegisterBinding mBinding;

    private RegisterViewModel registerViewModel;

    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.signing));

        registerViewModel = new ViewModelProvider(this, new MyViewModelFactory(requireActivity().getApplication())).get(RegisterViewModel.class);

        registerViewModel.getRegisterResult().observe(getViewLifecycleOwner(),
                registerResult -> {
                    if (registerResult == null) {
                        return;
                    }

                    progressDialog.hide();

                    if (registerResult.getError() != null) {
                        showRegisterFailed(registerResult.getError());
                    }

                    if (registerResult.getSuccess() != null) {
                        setRegisteredInUser(registerResult.getSuccess());
                        updateUiWithUser(registerResult.getSuccess().getUser());
                    }
                });

        mBinding.registerButton.setOnClickListener(
                v -> {
                    //loadingProgressBar.setVisibility(View.VISIBLE);
                    progressDialog.show();
                    registerViewModel.register(mBinding.nameEditText.getText().toString(), mBinding.phoneCountryCodeSpinner.getSelectedItem().toString() + mBinding.phoneEditText.getText().toString(), mBinding.passwordEditText.getText().toString());
            });

        mBinding.loginButton.setOnClickListener(
                v->{
            });

        mBinding.backButton.setOnClickListener(
                v->{
            });
    }

    private void setRegisteredInUser(UserWithRoles user) {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    private void updateUiWithUser(User model) {
        String welcome = getString(R.string.welcome) + model.getName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showRegisterFailed(@StringRes Integer errorString) {
        Toast.makeText(getContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
