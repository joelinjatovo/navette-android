package com.navetteclub.ui.notification;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.navetteclub.R;
import com.navetteclub.database.entity.ClubAndPoint;
import com.navetteclub.database.entity.Notification;
import com.navetteclub.database.entity.User;
import com.navetteclub.databinding.FragmentNotificationBinding;
import com.navetteclub.models.RemoteLoaderResult;
import com.navetteclub.ui.club.ClubRecyclerViewAdapter;
import com.navetteclub.ui.club.ClubsFragment;
import com.navetteclub.utils.Log;
import com.navetteclub.vm.AuthViewModel;
import com.navetteclub.vm.ClubViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.NotificationViewModel;

import java.util.List;

public class NotificationFragment extends Fragment {

    private NotificationViewModel mViewModel;

    private FragmentNotificationBinding mBinding;

    private NotificationRecyclerViewAdapter mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false);

        // Set the adapter
        mAdapter = new NotificationRecyclerViewAdapter(mListener);
        RecyclerView recyclerView = mBinding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(mAdapter);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(requireActivity(),
                new MyViewModelFactory(requireActivity().getApplication())).get(NotificationViewModel.class);

        mViewModel.getNotificationsLiveData().observe(getViewLifecycleOwner(),
                result -> {
                    if(result==null){
                        return;
                    }

                    if(result.getError()!=null){
                        // Error loading
                        mBinding.setIsLoading(false);
                        mBinding.setShowError(true);
                        mBinding.loaderErrorView.getSubtitleView().setText(result.getError());
                        Toast.makeText(requireContext(), result.getError(), Toast.LENGTH_SHORT).show();
                    }

                    if(result.getSuccess()!=null){
                        mBinding.setIsLoading(false);
                        mBinding.setShowError(false);
                        mAdapter.setItems(result.getSuccess());
                    }

                    // Reset remote result
                    mViewModel.setNotificationsLiveData(null);
                });

        AuthViewModel authViewModel = new ViewModelProvider(requireActivity(),
                new MyViewModelFactory(requireActivity().getApplication())).get(AuthViewModel.class);

        authViewModel.getAuthenticationState().observe(getViewLifecycleOwner(),
                authenticationState -> {
                    if (authenticationState == AuthViewModel.AuthenticationState.AUTHENTICATED) {
                        User user = authViewModel.getUser();
                        mViewModel.load(user);
                        mBinding.setIsLoading(true);
                        mBinding.setShowError(false);
                        mBinding.setIsUnauthenticated(false);
                    }else{
                        mBinding.setIsLoading(false);
                        mBinding.setShowError(false);
                        mBinding.setIsUnauthenticated(true);
                    }
                });

        mBinding.loaderErrorView.getButton().setOnClickListener(
                v -> {
                    User user = authViewModel.getUser();
                    if(user!=null){
                        mBinding.setIsLoading(true);
                        mBinding.setShowError(false);
                        mViewModel.load(user);
                    }else{
                        mBinding.setIsLoading(false);
                    }
                });

        mBinding.authErrorView.getButton().setOnClickListener(
                v -> {
                    Navigation.findNavController(v).navigate(R.id.action_global_navigation_auth);
                }
        );
    }

    private OnListFragmentInteractionListener mListener = new OnListFragmentInteractionListener() {
        @Override
        public void onListFragmentInteraction(View v, Notification item) {
        }
    };

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(View v, Notification item);
    }

}
