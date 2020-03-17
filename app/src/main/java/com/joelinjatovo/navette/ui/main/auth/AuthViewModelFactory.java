package com.joelinjatovo.navette.ui.main.auth;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.joelinjatovo.navette.api.datasource.LoginDataSource;
import com.joelinjatovo.navette.data.repositories.LoginRepository;

public class AuthViewModelFactory implements ViewModelProvider.Factory {

    private static AuthViewModel authViewModel;

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AuthViewModel.class)) {
            //if(authViewModel == null){
                authViewModel = new AuthViewModel(LoginRepository.getInstance(new LoginDataSource()));
            //}
            return (T) authViewModel;
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
