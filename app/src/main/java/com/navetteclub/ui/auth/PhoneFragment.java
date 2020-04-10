package com.navetteclub.ui.auth;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.navetteclub.R;
import com.navetteclub.database.callback.UpsertCallback;
import com.navetteclub.database.entity.User;
import com.navetteclub.databinding.FragmentForgotBinding;
import com.navetteclub.databinding.FragmentPhoneBinding;
import com.navetteclub.utils.Log;
import com.navetteclub.utils.Preferences;
import com.navetteclub.vm.AuthViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.PhoneViewModel;
import com.navetteclub.vm.UserViewModel;

import java.util.List;

public class PhoneFragment extends BottomSheetDialogFragment implements TextWatcher {

    private static final String TAG = PhoneFragment.class.getSimpleName();

    private FragmentPhoneBinding mBinding;

    private ProgressDialog progressDialog;

    private AuthViewModel authViewModel;

    private UserViewModel userViewModel;

    private PhoneViewModel phoneViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_phone, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.signing));

        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());

        authViewModel = new ViewModelProvider(requireActivity(), factory).get(AuthViewModel.class);

        userViewModel = new ViewModelProvider(requireActivity(), factory).get(UserViewModel.class);

        phoneViewModel = new ViewModelProvider(requireActivity(), factory).get(PhoneViewModel.class);

        final NavController navController = Navigation.findNavController(view);
        authViewModel.getAuthenticationState().observe(getViewLifecycleOwner(),
                authenticationState -> {
                    switch (authenticationState){
                        case AUTHENTICATED:
                            Log.d(TAG, "'AUTHENTICATED'");
                            User user = authViewModel.getUser();
                            if(user!=null){
                                if(!user.getVerified()){
                                    navController.navigate(R.id.action_phone_fragment_to_verify_phone_fragment);
                                }else{
                                    navController.popBackStack();
                                }
                            }
                            break;
                        default:
                            // Nothing
                            Log.d(TAG, "'UNKNOWN'");
                            break;
                    }
                });

        phoneViewModel.getPhoneResult().observe(getViewLifecycleOwner(),
                result -> {
                    if (result == null) {
                        return;
                    }

                    if (result.getError() != null) {
                        Log.d(TAG, "'result.getError()'");
                        progressDialog.hide();
                        Snackbar.make(mBinding.getRoot(), result.getError(), Snackbar.LENGTH_SHORT).show();
                    }

                    if (result.getSuccess() != null) {
                        Log.d(TAG, "'result.getSuccess()'");
                        User user = authViewModel.getUser();
                        user.setPhone(result.getSuccess().getPhone());
                        user.setVerified(result.getSuccess().getVerified());
                        userViewModel.upsert(upsertCallback, result.getSuccess());
                    }

                    // Reset remote result
                    phoneViewModel.setPhoneResult(null);
                });

        phoneViewModel.getPhoneFormState().observe(getViewLifecycleOwner(),
                formState -> {
                    if (formState == null) {
                        return;
                    }

                    mBinding.newPasswordButton.setEnabled(formState.isDataValid());

                    if (formState.getPhoneError() != null) {
                        mBinding.phoneEditText.setError(getString(formState.getPhoneError()));
                    }
                });

        mBinding.phoneEditText.addTextChangedListener(this);

        mBinding.newPasswordButton.setOnClickListener(
                v -> {
                    progressDialog.show();
                    String phone = mBinding.phoneCountryCodeSpinner.getSelectedItem().toString() + mBinding.phoneEditText.getText().toString();
                    User user = authViewModel.getUser();
                    user.setPhone(phone);
                    phoneViewModel.update(user);
                });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        phoneViewModel.phoneDataChanged(
                mBinding.phoneCountryCodeSpinner.getSelectedItem().toString(),
                mBinding.phoneEditText.getText().toString()
        );
    }

    private UpsertCallback<User> upsertCallback = new UpsertCallback<User>() {
        @Override
        public void onUpsertError() {
            progressDialog.hide();
            Toast.makeText(getContext(), getString(R.string.error_database), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onUpsertSuccess(List<User> users) {
            User user = users.get(0);
            Preferences.Auth.setCurrentUser(getContext(), user);
            authViewModel.authenticate(user);
            updateUiWithUser(user);
            progressDialog.hide();
        }

        private void updateUiWithUser(User user) {
            String welcome = getString(R.string.welcome) + user.getName();
            Toast.makeText(getContext(), welcome, Toast.LENGTH_LONG).show();
        }
    };
}
