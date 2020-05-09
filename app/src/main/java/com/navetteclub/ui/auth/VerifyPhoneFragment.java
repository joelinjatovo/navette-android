package com.navetteclub.ui.auth;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
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
import com.navetteclub.databinding.FragmentVerifyPhoneBinding;
import com.navetteclub.utils.Log;
import com.navetteclub.utils.Preferences;
import com.navetteclub.vm.AuthViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.UserViewModel;
import com.navetteclub.vm.VerifyPhoneViewModel;

import java.util.List;

public class VerifyPhoneFragment extends Fragment implements TextWatcher {

    private static final String TAG = VerifyPhoneFragment.class.getSimpleName();

    private FragmentVerifyPhoneBinding mBinding;

    private ProgressDialog progressDialog;

    private AuthViewModel authViewModel;

    private UserViewModel userViewModel;

    private VerifyPhoneViewModel verifyPhoneViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_verify_phone, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        setupAuthViewModel(factory, navController);
        setupUserViewModel(factory);
        setupVerifyPhoneViewModel(factory);
        setupUi();
        setupBackAction(navController);
    }

    private void setupVerifyPhoneViewModel(MyViewModelFactory factory) {
        verifyPhoneViewModel = new ViewModelProvider(requireActivity(), factory).get(VerifyPhoneViewModel.class);
        verifyPhoneViewModel.getVerifyPhoneResult().observe(getViewLifecycleOwner(),
                result -> {
                    if (result == null) {
                        return;
                    }

                    progressDialog.dismiss();

                    if (result.getError() != null) {
                        Log.d(TAG, "'result.getError()'");
                        Snackbar.make(mBinding.getRoot(), result.getError(), Snackbar.LENGTH_SHORT).show();
                    }

                    if (result.getSuccess() != null) {
                        Log.d(TAG, "'result.getSuccess()'");
                        User user = authViewModel.getUser();
                        user.setVerified(result.getSuccess().getVerified());
                        userViewModel.upsert(upsertCallback, result.getSuccess());
                    }

                    // Reset remote result
                    verifyPhoneViewModel.setVerifyPhoneResult(null);
                });

        verifyPhoneViewModel.getResendCodeResult().observe(getViewLifecycleOwner(),
                result -> {
                    if (result == null) {
                        return;
                    }

                    progressDialog.dismiss();

                    if (result.getError() != null) {
                        Log.d(TAG, "'result.getError()'");
                        Snackbar.make(mBinding.getRoot(), result.getError(), Snackbar.LENGTH_SHORT).show();
                    }

                    if (result.getSuccess() != null) {
                        Log.d(TAG, "'result.getSuccess()'");
                        Snackbar.make(mBinding.getRoot(), R.string.code_sent, Snackbar.LENGTH_SHORT).show();
                    }

                    // Reset remote result
                    verifyPhoneViewModel.setResendCodeResult(null);
                });

        verifyPhoneViewModel.getVerifyPhoneFormState().observe(getViewLifecycleOwner(),
                formState -> {
                    if (formState == null) {
                        return;
                    }

                    mBinding.verifyButton.setEnabled(formState.isDataValid());

                    if (formState.getVerifyCodeError() != null) {
                        mBinding.verifyCodeEditText.setError(getString(formState.getVerifyCodeError()));
                    }
                });
    }

    private void setupUserViewModel(MyViewModelFactory factory) {
        userViewModel = new ViewModelProvider(requireActivity(), factory).get(UserViewModel.class);
    }

    private void setupAuthViewModel(MyViewModelFactory factory, NavController navController) {
        authViewModel = new ViewModelProvider(requireActivity(), factory).get(AuthViewModel.class);
        authViewModel.getAuthenticationState().observe(getViewLifecycleOwner(),
                authenticationState -> {
                    switch (authenticationState){
                        case AUTHENTICATED:
                            Log.d(TAG, "'AUTHENTICATED'");
                            User user = authViewModel.getUser();
                            if(user!=null && user.getVerified()){
                                navController.popBackStack();
                            }
                            break;
                        default:
                            // Nothing
                            break;
                    }
                });
    }

    private void setupUi() {
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.signing));
        mBinding.verifyCodeEditText.addTextChangedListener(this);
        mBinding.verifyButton.setOnClickListener(
                v -> {
                    progressDialog.show();
                    User user = authViewModel.getUser();
                    verifyPhoneViewModel.verify(user, mBinding.verifyCodeEditText.getText().toString());
                });
        mBinding.resendButton.setOnClickListener(
                v -> {
                    progressDialog.show();
                    User user = authViewModel.getUser();
                    verifyPhoneViewModel.resend(user);
                });
    }

    private void setupBackAction(NavController navController) {
        mBinding.backButton.setOnClickListener(v -> Navigation.findNavController(v).popBackStack(R.id.navigation_home, false));
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        //authViewModel.logout(requireContext());
                        navController.popBackStack(R.id.navigation_home, false);
                    }
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
        verifyPhoneViewModel.verifyCodeDataChanged(
                mBinding.verifyCodeEditText.getText().toString()
        );
    }

    private UpsertCallback<User> upsertCallback = new UpsertCallback<User>() {
        @Override
        public void onUpsertError() {
            Toast.makeText(getContext(), getString(R.string.error_database), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onUpsertSuccess(List<User> users) {
            Log.d(TAG, "UpsertCallback.onUpsertSuccess(users)");
            User user = users.get(0);
            Preferences.Auth.setCurrentUser(getContext(), user);
            authViewModel.authenticate(user);
            updateUiWithUser(user);
        }

        private void updateUiWithUser(User user) {
            String welcome = getString(R.string.welcome) + user.getName();
            Toast.makeText(getContext(), welcome, Toast.LENGTH_LONG).show();
        }
    };
}
