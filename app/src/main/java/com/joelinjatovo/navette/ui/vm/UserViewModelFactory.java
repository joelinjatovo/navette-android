package com.joelinjatovo.navette.ui.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class UserViewModelFactory implements ViewModelProvider.Factory {

    private Application application;

    public UserViewModelFactory(Application application){
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(application);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
