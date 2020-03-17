package com.joelinjatovo.navette.ui.main.auth;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.joelinjatovo.navette.api.clients.RetrofitClient;
import com.joelinjatovo.navette.api.datasource.LoginDataSource;
import com.joelinjatovo.navette.api.responses.RetrofitResponse;
import com.joelinjatovo.navette.data.repositories.LoginRepository;
import com.joelinjatovo.navette.database.entity.User;

import retrofit2.Callback;

public class AuthViewModelFactory implements ViewModelProvider.Factory {

    private static AuthViewModel authViewModel;

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AuthViewModel.class)) {
            if(authViewModel == null){
                authViewModel = new AuthViewModel();
            }
            return (T) authViewModel;
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
