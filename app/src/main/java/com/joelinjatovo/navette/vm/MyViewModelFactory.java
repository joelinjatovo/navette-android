package com.joelinjatovo.navette.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.joelinjatovo.navette.api.datasources.LoginDataSource;
import com.joelinjatovo.navette.api.datasources.RegisterDataSource;
import com.joelinjatovo.navette.api.repositories.LoginRepository;
import com.joelinjatovo.navette.api.repositories.RegisterRepository;
import com.joelinjatovo.navette.database.repositories.CarRepository;
import com.joelinjatovo.navette.database.repositories.ClubRepository;
import com.joelinjatovo.navette.database.repositories.NotificationRepository;
import com.joelinjatovo.navette.database.repositories.UserRepository;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class MyViewModelFactory implements ViewModelProvider.Factory {

    private Application application;

    private static NotificationViewModel notificationViewModel;

    private static ClubViewModel clubViewModel;

    private static OrderViewModel orderViewModel;

    private static AuthViewModel authViewModel;

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
            return (T) new LoginViewModel(LoginRepository.getInstance(new LoginDataSource()));
        } else if (modelClass.isAssignableFrom(NotificationViewModel.class)) {
            if(notificationViewModel == null){
                notificationViewModel = new NotificationViewModel(NotificationRepository.getInstance(application));
            }
            return (T) notificationViewModel;
        }  else if (modelClass.isAssignableFrom(ClubViewModel.class)) {
            if(clubViewModel == null){
                clubViewModel = new ClubViewModel(ClubRepository.getInstance(application));
            }
            return (T) clubViewModel;
        } else if (modelClass.isAssignableFrom(OrderViewModel.class)) {
            if(orderViewModel == null){
                orderViewModel = new OrderViewModel(CarRepository.getInstance(application));
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
