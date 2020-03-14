package com.joelinjatovo.navette.ui.auth.register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.joelinjatovo.navette.data.repositories.RegisterRepository;

public class RegisterViewModel extends ViewModel {

    private MutableLiveData<RegisterResult> registerResult = new MutableLiveData<>();

    private RegisterRepository registerRepository;

    public RegisterViewModel(RegisterRepository registerRepository) {
        this.registerRepository = registerRepository;
    }

    public LiveData<RegisterResult> getRegisterResult() {
        return registerResult;
    }

    public void register(String name, String phone, String password) {
        registerRepository.register(name, phone, password, registerResult);
    }
}
