package com.joelinjatovo.navette.ui.auth.login;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.joelinjatovo.navette.api.datasource.LoginDataSource;
import com.joelinjatovo.navette.data.repositories.LoginRepository;
import com.joelinjatovo.navette.ui.auth.login.LoginViewModel;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class LoginViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(LoginRepository.getInstance(new LoginDataSource()));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
