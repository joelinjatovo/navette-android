package com.navetteclub.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.navetteclub.api.datasources.LoginDataSource;
import com.navetteclub.api.datasources.RegisterDataSource;
import com.navetteclub.api.repositories.LoginRepository;
import com.navetteclub.api.repositories.RegisterRepository;
import com.navetteclub.database.repositories.CarRepository;
import com.navetteclub.database.repositories.ClubRepository;
import com.navetteclub.database.repositories.NotificationRepository;
import com.navetteclub.database.repositories.UserRepository;
import com.navetteclub.utils.Log;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class MyViewModelFactory implements ViewModelProvider.Factory {

    private static MyViewModelFactory instance;

    private Application application;

    private static NotificationViewModel notificationViewModel;

    private static ClubViewModel clubViewModel;

    private static OrderViewModel orderViewModel;

    private static OrdersViewModel ordersViewModel;

    private static AuthViewModel authViewModel;

    private static PhoneViewModel phoneViewModel;

    private static VerifyPhoneViewModel verifyPhoneViewModel;

    private static RidesViewModel ridesViewModel;

    public static MyViewModelFactory getInstance(Application application){
        Log.d("MyViewModelFactory", "MyViewModelFactory getInstance -");
        if(instance==null){
            Log.d("MyViewModelFactory", "MyViewModelFactory getInstance NEW");
            instance = new MyViewModelFactory(application);
        }
        return instance;
    }

    private MyViewModelFactory(Application application){
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GoogleViewModel.class)) {
            return (T) new GoogleViewModel();
        } else if (modelClass.isAssignableFrom(UserViewModel.class)) {
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
        }  else if (modelClass.isAssignableFrom(PhoneViewModel.class)) {
            if(phoneViewModel == null){
                phoneViewModel = new PhoneViewModel();
            }
            return (T) phoneViewModel;
        }  else if (modelClass.isAssignableFrom(VerifyPhoneViewModel.class)) {
            if(verifyPhoneViewModel == null){
                verifyPhoneViewModel = new VerifyPhoneViewModel();
            }
            return (T) verifyPhoneViewModel;
        } else if (modelClass.isAssignableFrom(OrderViewModel.class)) {
            if(orderViewModel == null){
                Log.d("MyViewModelFactory", "new OrderViewModel(CarRepository.getInstance(application));");
                orderViewModel = new OrderViewModel(CarRepository.getInstance(application));
            }
            return (T) orderViewModel;
        }  else if (modelClass.isAssignableFrom(OrdersViewModel.class)) {
            if(ordersViewModel == null){
                ordersViewModel = new OrdersViewModel();
            }
            return (T) ordersViewModel;
        }   else if (modelClass.isAssignableFrom(RidesViewModel.class)) {
            if(ridesViewModel == null){
                ridesViewModel = new RidesViewModel();
            }
            return (T) ridesViewModel;
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
