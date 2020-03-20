package com.joelinjatovo.navette.ui.main.order;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.joelinjatovo.navette.database.repository.ClubRepository;
import com.joelinjatovo.navette.ui.vm.ClubViewModel;

public class OrderViewModelFactory implements ViewModelProvider.Factory {

    private static OrderViewModel orderViewModel;


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ClubViewModel.class)) {
            if(orderViewModel == null){
                orderViewModel = new OrderViewModel();
            }
            return (T) orderViewModel;
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
