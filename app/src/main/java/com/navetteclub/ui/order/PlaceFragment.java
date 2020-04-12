package com.navetteclub.ui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.navetteclub.R;
import com.navetteclub.database.entity.ClubAndPoint;
import com.navetteclub.database.entity.Order;
import com.navetteclub.databinding.FragmentPlaceBinding;
import com.navetteclub.models.RemoteLoaderResult;
import com.navetteclub.utils.UiUtils;
import com.navetteclub.vm.ClubViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;

public class PlaceFragment extends Fragment {

    private OrderViewModel orderViewModel;

    private FragmentPlaceBinding mBinding;

    private int place = 0;

    private int max = 10;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_place, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUi();
        setupViewModel();
    }

    private void setupUi() {
        mBinding.stepView.go(2, true);
        mBinding.plus.setOnClickListener(
                v -> {
                    if( place < max ) {
                        orderViewModel.setPlace( place + 1 );
                    }else{
                        orderViewModel.setPlace( max );
                    }
                });

        mBinding.minus.setOnClickListener(
                v -> {
                    if(place>1){
                        orderViewModel.setPlace( place - 1 );
                    }else{
                        orderViewModel.setPlace( 1 );
                    }
                });

        mBinding.ok.setOnClickListener(
                v -> {
                    Navigation.findNavController(v).navigate(R.id.action_place_fragment_to_go_and_back_fragment);
                });

        mBinding.toolbar.setNavigationOnClickListener(
                v -> {
                    NavHostFragment.findNavController(this).popBackStack();
                });
    }

    private void setupViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        orderViewModel = new ViewModelProvider(this, factory).get(OrderViewModel.class);
        orderViewModel.getOrderLiveData().observe(getViewLifecycleOwner(),
                orderWithDatas -> {
                    if(orderWithDatas==null){
                        return;
                    }
                    Order order = orderWithDatas.getOrder();
                    if(order==null){
                        return;
                    }
                    this.place = order.getPlace();
                    mBinding.setPlace(place);
                });
    }
}
