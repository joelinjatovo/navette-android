package com.joelinjatovo.navette.ui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.database.entity.Notification;
import com.joelinjatovo.navette.database.entity.OrderWithDatas;
import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.databinding.FragmentNotificationBinding;
import com.joelinjatovo.navette.databinding.FragmentOrdersBinding;
import com.joelinjatovo.navette.ui.notification.NotificationRecyclerViewAdapter;
import com.joelinjatovo.navette.vm.AuthViewModel;
import com.joelinjatovo.navette.vm.MyViewModelFactory;
import com.joelinjatovo.navette.vm.NotificationViewModel;
import com.joelinjatovo.navette.vm.OrdersViewModel;

public class OrdersFragment extends Fragment {

    private FragmentOrdersBinding mBinding;

    private OrderRecyclerViewAdapter mAdapter;

    private OrdersViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_orders, container, false);

        mAdapter = new OrderRecyclerViewAdapter(mListener);
        RecyclerView recyclerView = mBinding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(mAdapter);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(requireActivity(),
                new MyViewModelFactory(requireActivity().getApplication())).get(OrdersViewModel.class);

        mViewModel.getOrdersLiveData().observe(getViewLifecycleOwner(),
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
                    mViewModel.setOrdersLiveData(null);
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
                    Navigation.findNavController(v).navigate(R.id.login_fragment);
                }
        );
    }

    private OnListFragmentInteractionListener mListener = new OnListFragmentInteractionListener() {
        @Override
        public void onListFragmentInteraction(View v, OrderWithDatas item) {
        }
    };

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(View v, OrderWithDatas item);
    }

}
