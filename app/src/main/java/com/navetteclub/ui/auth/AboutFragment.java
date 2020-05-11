package com.navetteclub.ui.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.navetteclub.BuildConfig;
import com.navetteclub.R;
import com.navetteclub.databinding.FragmentAboutBinding;

public class AboutFragment extends Fragment {

    private static final String TAG = AboutFragment.class.getSimpleName();

    private FragmentAboutBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBinding.versionTextView.setText(BuildConfig.VERSION_NAME);
        mBinding.backButton.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

    }
}
