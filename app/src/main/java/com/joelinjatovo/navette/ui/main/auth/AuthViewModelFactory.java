package com.joelinjatovo.navette.ui.main.auth;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.joelinjatovo.navette.database.repository.UserRepository;

public class AuthViewModelFactory implements ViewModelProvider.Factory {

    private static AuthViewModel authViewModel;

    private Application application;

    public AuthViewModelFactory(Application application) {
        this.application = application;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AuthViewModel.class)) {
            if(authViewModel == null){
                authViewModel = new AuthViewModel(UserRepository.getInstance(application));
            }
            return (T) authViewModel;
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
