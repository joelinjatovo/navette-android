package com.joelinjatovo.navette.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.joelinjatovo.navette.api.datasource.LoginDataSource;
import com.joelinjatovo.navette.api.datasource.RegisterDataSource;
import com.joelinjatovo.navette.data.repositories.LoginRepository;
import com.joelinjatovo.navette.data.repositories.RegisterRepository;
import com.joelinjatovo.navette.database.repository.ClubRepository;
import com.joelinjatovo.navette.database.repository.UserRepository;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class MyViewModelFactory implements ViewModelProvider.Factory {

    private Application application;

    private static ClubViewModel clubViewModel;

    private static OrderViewModel orderViewModel;

    private static AuthViewModel authViewModel;

    private static LoginViewModel loginViewModel;

    public MyViewModelFactory(Application application){
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(application);
        } else if (modelClass.isAssignableFrom(RegisterViewModel.class)) {
            return (T) new RegisterViewModel(RegisterRepository.getInstance(new RegisterDataSource()));
        } else if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            if(loginViewModel == null){
                loginViewModel = new LoginViewModel(LoginRepository.getInstance(new LoginDataSource()));
            }
            return (T) loginViewModel;
        } else if (modelClass.isAssignableFrom(ClubViewModel.class)) {
            if(clubViewModel == null){
                clubViewModel = new ClubViewModel(ClubRepository.getInstance(application));
            }
            return (T) clubViewModel;
        } else if (modelClass.isAssignableFrom(OrderViewModel.class)) {
            if(orderViewModel == null){
                orderViewModel = new OrderViewModel();
            }
            return (T) orderViewModel;
        } else if (modelClass.isAssignableFrom(AuthViewModel.class)) {
            if(authViewModel == null){
                authViewModel = new AuthViewModel(UserRepository.getInstance(application));
            }
            return (T) authViewModel;
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
