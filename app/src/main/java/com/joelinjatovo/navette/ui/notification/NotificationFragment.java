package com.joelinjatovo.navette.ui.notification;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.database.entity.ClubAndPoint;
import com.joelinjatovo.navette.database.entity.Notification;
import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.databinding.FragmentNotificationBinding;
import com.joelinjatovo.navette.models.RemoteLoaderResult;
import com.joelinjatovo.navette.ui.club.ClubRecyclerViewAdapter;
import com.joelinjatovo.navette.ui.club.ClubsFragment;
import com.joelinjatovo.navette.utils.Log;
import com.joelinjatovo.navette.vm.AuthViewModel;
import com.joelinjatovo.navette.vm.ClubViewModel;
import com.joelinjatovo.navette.vm.MyViewModelFactory;
import com.joelinjatovo.navette.vm.NotificationViewModel;

import java.util.List;

public class NotificationFragment extends Fragment {

    private NotificationViewModel mViewModel;

    private FragmentNotificationBinding mBinding;

    private NotificationRecyclerViewAdapter mAdapter;

    public static NotificationFragment newInstance() {
        return new NotificationFragment();
    }

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = new ViewModelProvider(requireActivity(),
                new MyViewModelFactory(requireActivity().getApplication())).get(NotificationViewModel.class);

        mViewModel.getNotificationsLiveData().observe(getViewLifecycleOwner(),
                result -> {
                    if(result==null){
                        return;
                    }

                    if(result.getError()!=null){
                        // Error loading
                    }

                    if(result.getSuccess()!=null){
                        mAdapter.setItems(result.getSuccess());
                    }

                });

        AuthViewModel authViewModel = new ViewModelProvider(requireActivity(),
                new MyViewModelFactory(requireActivity().getApplication())).get(AuthViewModel.class);

        authViewModel.getAuthenticationState().observe(getViewLifecycleOwner(),
                authenticationState -> {
                    if (authenticationState == AuthViewModel.AuthenticationState.AUTHENTICATED) {
                        User user = authViewModel.getUser();
                        mViewModel.load(user);
                        mBinding.setIsUnauthenticated(false);
                    }else{
                        mBinding.setIsUnauthenticated(true);
                    }
                });

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
