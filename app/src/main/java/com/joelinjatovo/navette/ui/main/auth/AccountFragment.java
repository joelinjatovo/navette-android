package com.joelinjatovo.navette.ui.main.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.databinding.FragmentAccountBinding;
import com.joelinjatovo.navette.databinding.FragmentProfileBinding;

public class AccountFragment extends Fragment {

    private FragmentAccountBinding mBinding;

    private AuthViewModel authViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_account, container, false);
        return mBinding.getRoot();
    }
}
