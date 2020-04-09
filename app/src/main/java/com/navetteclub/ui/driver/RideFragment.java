package com.navetteclub.ui.driver;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.navetteclub.R;
import com.navetteclub.database.entity.OrderWithDatas;
import com.navetteclub.database.entity.Ride;
import com.navetteclub.database.entity.RideWithDatas;
import com.navetteclub.database.entity.User;
import com.navetteclub.databinding.FragmentRideBinding;
import com.navetteclub.ui.OnClickItemListener;
import com.navetteclub.utils.Log;
import com.navetteclub.vm.AuthViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;
import com.navetteclub.vm.OrdersViewModel;
import com.navetteclub.vm.RidesViewModel;

import java.util.ArrayList;

public class RideFragment extends Fragment {

    private static final String TAG = RideFragment.class.getSimpleName();

    private FragmentRideBinding mBinding;

    private OrderRecyclerViewAdapter mAdapter;

    private AuthViewModel authViewModel;

    private RidesViewModel ridesViewModel;

    private SearchView searchView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_ride, container, false);

        mAdapter = new OrderRecyclerViewAdapter(mListener);
        RecyclerView recyclerView = mBinding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(mAdapter);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI();
        setupAuthViewModel();
        setupRidesViewModel();
    }

    private void setupRidesViewModel() {

        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());

        ridesViewModel = new ViewModelProvider(requireActivity(), factory).get(RidesViewModel.class);

        ridesViewModel.getRideLiveData().observe(getViewLifecycleOwner(),
                ride -> {
                    if(ride==null){
                        return;
                    }

                    NavHostFragment.findNavController(this).navigate(R.id.action_ride_fragment_to_ride_map_fragment);
                });

        ridesViewModel.getOrdersLiveData().observe(getViewLifecycleOwner(),
                result -> {
                    if(result==null){
                        return;
                    }

                    if(result.getError()!=null){
                        if(result.getError() == R.string.error_401) { // Error 401: Unauthorized
                            authViewModel.logout(requireContext());
                        }else{
                            // Error loading
                            mBinding.setIsLoading(false);
                            mBinding.setShowError(true);
                            mBinding.loaderErrorView.getTitleView().setText(R.string.loader_error_title);
                            mBinding.loaderErrorView.getSubtitleView().setText(result.getError());
                        }
                        Toast.makeText(requireContext(), result.getError(), Toast.LENGTH_SHORT).show();
                    }

                    if(result.getSuccess()!=null){
                        mBinding.setIsLoading(false);
                        ArrayList<OrderWithDatas> items = (ArrayList<OrderWithDatas>) result.getSuccess();
                        if(items.isEmpty()){
                            mBinding.setShowError(true);
                            mBinding.loaderErrorView.getTitleView().setText(R.string.title_empty);
                            mBinding.loaderErrorView.getSubtitleView().setText(R.string.empty_orders);
                        }else{
                            mBinding.setShowError(false);
                            mAdapter.setItems(items);
                        }
                    }

                    // Reset remote result
                    //ridesViewModel.setOrdersLiveData(null);
                });
    }

    private void setupAuthViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());

        authViewModel = new ViewModelProvider(requireActivity(), factory).get(AuthViewModel.class);

        authViewModel.getAuthenticationState().observe(getViewLifecycleOwner(),
                authenticationState -> {
                    if (authenticationState == AuthViewModel.AuthenticationState.AUTHENTICATED) {
                        User user = authViewModel.getUser();
                        Ride ride = ridesViewModel.getRideWithDatas().getRide();
                        ridesViewModel.loadOrders(user, ride);
                        mBinding.setIsLoading(true);
                        mBinding.setShowError(false);
                        mBinding.setIsUnauthenticated(false);
                    }else{
                        mBinding.setIsLoading(false);
                        mBinding.setShowError(false);
                        mBinding.setIsUnauthenticated(true);
                    }
                });
    }

    private void setupUI() {
        mBinding.toolbar.setNavigationOnClickListener(
                v -> {
                    NavHostFragment.findNavController(this).popBackStack();
                });

        mBinding.startRideButton.setOnClickListener(v->{
            User user = authViewModel.getUser();
            Ride ride = ridesViewModel.getRideWithDatas().getRide();
            if(user!=null && ride!=null){
                ridesViewModel.start(user, ride);
            }
        });

        mBinding.loaderErrorView.getButton().setOnClickListener(
                v -> {
                    User user = authViewModel.getUser();
                    if(user!=null){
                        Ride ride = ridesViewModel.getRideWithDatas().getRide();
                        ridesViewModel.loadOrders(user, ride);
                        mBinding.setIsLoading(true);
                        mBinding.setShowError(false);
                    }else{
                        mBinding.setIsLoading(false);
                    }
                });

        mBinding.authErrorView.getButton().setOnClickListener(
                v -> {
                    //Navigation.findNavController(v).navigate(R.id.action_orders_fragment_to_navigation_auth);
                });
    }

    private OnClickItemListener<OrderWithDatas> mListener = (v, pos, item) -> {
        //NavHostFragment.findNavController(RideFragment.this).navigate(R.id.action_rides_fragment_to_ride_fragment);
    };

}
