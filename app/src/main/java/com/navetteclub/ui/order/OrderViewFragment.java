package com.navetteclub.ui.order;

import android.app.ProgressDialog;
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

import com.google.android.material.snackbar.Snackbar;
import com.navetteclub.R;
import com.navetteclub.database.entity.Order;
import com.navetteclub.databinding.FragmentDetailBinding;
import com.navetteclub.databinding.FragmentOrderViewBinding;
import com.navetteclub.utils.Log;
import com.navetteclub.vm.AuthViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;

import java.text.NumberFormat;
import java.util.Currency;

public class OrderViewFragment extends Fragment {

    private static final String TAG = OrderViewFragment.class.getSimpleName();

    private FragmentOrderViewBinding mBinding;

    private OrderViewModel orderViewModel;

    private AuthViewModel authViewModel;

    private ProgressDialog progressDialog;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG + "Cycle", "onCreateView");
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_view, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG + "Cycle", "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG + "Cycle", "onViewCreated");

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.signing));

        setupUi();

        setupOrderViewModel();

        setupAuthViewModel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d(TAG + "Cycle", "onDestroyView");
    }

    private void setupAuthViewModel() {
        authViewModel = new ViewModelProvider(this,
                new MyViewModelFactory(requireActivity().getApplication())).get(AuthViewModel.class);

        authViewModel.getAuthenticationState().observe(getViewLifecycleOwner(),
                authenticationState -> {
                    if (authenticationState == AuthViewModel.AuthenticationState.AUTHENTICATED) {
                        mBinding.setIsUnauthenticated(false);
                    }else{
                        mBinding.setIsUnauthenticated(true);
                    }
                });
    }

    private void setupOrderViewModel() {
        orderViewModel = new ViewModelProvider(this, new MyViewModelFactory(requireActivity().getApplication())).get(OrderViewModel.class);

        orderViewModel.getOrderWithDatasLiveData().observe(getViewLifecycleOwner(),
                orderWithDatas -> {
                    if(orderWithDatas == null){
                        return;
                    }

                    if(orderWithDatas.getOrder()!=null){

                        mBinding.setDistance(orderWithDatas.getOrder().getDistance());

                        mBinding.setDelay(orderWithDatas.getOrder().getDelay());

                        Double amount = orderWithDatas.getOrder().getAmount();
                        if( amount != null && amount > 0 ){
                            String currency = orderWithDatas.getOrder().getCurrency();
                            NumberFormat format = NumberFormat.getCurrencyInstance();
                            //format.setMaximumFractionDigits(2);
                            //format.setMinimumFractionDigits(2);
                            format.setCurrency(Currency.getInstance(currency));
                            mBinding.setAmount(format.format(amount));

                            switch (orderWithDatas.getOrder().getStatus()){
                                case Order.STATUS_PING:
                                    mBinding.bookNowButton.setText(R.string.pay_now);
                                break;
                                case Order.STATUS_OK:
                                    mBinding.bookNowButton.setText(R.string.view);
                                break;
                                default:
                                    mBinding.bookNowButton.setText(R.string.book_now);
                                break;
                            }
                        }
                    }

                });

        orderViewModel.getOrderResult().observe(getViewLifecycleOwner(),
                orderResult -> {
                    if(orderResult == null){
                        return;
                    }

                    progressDialog.hide();

                    if (orderResult.getError() != null) {
                        Log.d(TAG, "'orderResult.getError()'");
                        Snackbar.make(mBinding.getRoot(), orderResult.getError(), Snackbar.LENGTH_SHORT).show();
                    }

                    if (orderResult.getSuccess() != null) {
                        Log.d(TAG, "'orderResult.getSuccess()'");
                        orderViewModel.setOrder(orderResult.getSuccess());
                    }
                });

    }


    private void setupUi() {
        mBinding.bookNowButton.setOnClickListener(
                v -> {
                    if(authViewModel.getUser()!=null
                            && orderViewModel.getOrderWithDatas() != null
                            && orderViewModel.getOrderWithDatas().getOrder() != null
                            && orderViewModel.getOrderWithDatas().getOrder().getRid() != null){
                        Order order = orderViewModel.getOrderWithDatas().getOrder();
                        switch (order.getStatus()){
                            case Order.STATUS_OK:
                            case Order.STATUS_PROCESSING:
                                Navigation.findNavController(v).navigate(R.id.action_order_view_fragment_to_order_map_fragment);
                                break;
                        }
                    }
                });
    }

}
