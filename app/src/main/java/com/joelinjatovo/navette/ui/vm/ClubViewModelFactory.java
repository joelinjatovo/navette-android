package com.joelinjatovo.navette.ui.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.joelinjatovo.navette.database.repository.ClubRepository;
import com.joelinjatovo.navette.database.repository.UserRepository;

public class ClubViewModelFactory implements ViewModelProvider.Factory {

    private static ClubViewModel clubViewModel;

    private Application application;

    public ClubViewModelFactory(Application application) {
        this.application = application;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ClubViewModel.class)) {
            if(clubViewModel == null){
                clubViewModel = new ClubViewModel(ClubRepository.getInstance(application));
            }
            return (T) clubViewModel;
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
