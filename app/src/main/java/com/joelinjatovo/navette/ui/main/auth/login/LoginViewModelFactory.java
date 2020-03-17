package com.joelinjatovo.navette.ui.main.auth.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.joelinjatovo.navette.api.datasource.LoginDataSource;
import com.joelinjatovo.navette.api.responses.RetrofitResponse;
import com.joelinjatovo.navette.data.repositories.LoginRepository;
import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.ui.main.auth.AuthViewModel;

import retrofit2.Callback;

public class LoginViewModelFactory implements ViewModelProvider.Factory {

    private static LoginViewModel authViewModel;

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            if(authViewModel == null){
                authViewModel = new LoginViewModel(LoginRepository.getInstance(new LoginDataSource()));
            }
            return (T) authViewModel;
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
