package com.navetteclub.ui.order;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.navetteclub.R;
import com.navetteclub.api.clients.RetrofitClient;
import com.navetteclub.api.models.OrderParam;
import com.navetteclub.api.models.OrderRequest;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.api.services.OrderApiService;
import com.navetteclub.database.entity.Club;
import com.navetteclub.database.entity.Order;
import com.navetteclub.database.entity.OrderWithDatas;
import com.navetteclub.database.entity.User;
import com.navetteclub.databinding.FragmentOrderCancelBinding;
import com.navetteclub.databinding.FragmentThanksBinding;
import com.navetteclub.models.RemoteLoaderResult;
import com.navetteclub.ui.pay.StripeFragment;
import com.navetteclub.ui.pay.StripeFragmentArgs;
import com.navetteclub.utils.Log;
import com.navetteclub.utils.UiUtils;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderCancelFragment extends BottomSheetDialogFragment {

    private static final String TAG = OrderCancelFragment.class.getSimpleName();

    private FragmentOrderCancelBinding mBinding;

    private String token;

    private String orderRid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            token = OrderCancelFragmentArgs.fromBundle(getArguments()).getToken();
            orderRid = OrderCancelFragmentArgs.fromBundle(getArguments()).getOrder();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;

                ConstraintLayout constraintLayout = d.findViewById(R.id.constraintLayout);
                FrameLayout.LayoutParams params = null;
                if (constraintLayout != null) {
                    params = (FrameLayout.LayoutParams) constraintLayout.getLayoutParams();
                    params.height = UiUtils.getScreenHeight();
                    constraintLayout.setLayoutParams(params);
                }

                FrameLayout bottomSheet = d.findViewById(R.id.design_bottom_sheet);
                if (bottomSheet != null) {
                    BottomSheetBehavior sheetBehavior = BottomSheetBehavior.from(bottomSheet);
                    sheetBehavior.setSkipCollapsed(true);
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    //sheetBehavior.setPeekHeight(UiUtils.getScreenHeight(), true);
                }
            }
        });

        return dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_cancel, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = NavHostFragment.findNavController(this);
        mBinding.backButton.setOnClickListener(
                v -> {
                    navController.popBackStack();
                });
        mBinding.buttonBack.setOnClickListener(
                v -> {
                    navController.popBackStack();
                });
        mBinding.buttonConfirm.setOnClickListener(
                v -> {
                    cancelOrder(token, orderRid);
                });
    }

    private void cancelOrder(String token, String orderRid) {
        Log.d(TAG, "OrderApiService.cancelOrder()");
        OrderApiService service = RetrofitClient.getInstance().create(OrderApiService.class);
        Call<RetrofitResponse<OrderWithDatas>> call = service.cancelOrder(token, new OrderParam(orderRid), "cancel");
        call.enqueue(new Callback<RetrofitResponse<OrderWithDatas>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<OrderWithDatas>> call,
                                   @NonNull Response<RetrofitResponse<OrderWithDatas>> response) {
                Log.e(TAG, response.toString());
                if (response.body() != null){
                    Log.e(TAG, response.body().toString());
                    if(response.body().isSuccess()) {
                        showSuccessSweetAlert();
                    }else{
                        showErrorSweetAlert(getString(response.body().getStatusResString()), token, orderRid);
                    }
                } else {
                    showErrorSweetAlert(getString(R.string.error_bad_request), token, orderRid);
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<OrderWithDatas>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.getMessage(), throwable);
            }
        });
    }

    private void showErrorSweetAlert(String content, String token, String orderRid){
        new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(content)
                .setConfirmText("Yes, retry!")
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();
                    cancelOrder(token, orderRid);
                })
                .setCancelButton("Cancel", sDialog -> {
                    sDialog.dismissWithAnimation();
                    NavHostFragment.findNavController(OrderCancelFragment.this).popBackStack();
                })
                .show();
    }

    private void showSuccessSweetAlert(){
        new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Success")
                .setContentText("Votre commande a été bien annulée.")
                .setConfirmText("OK")
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();
                    NavHostFragment.findNavController(OrderCancelFragment.this).navigate(R.id.action_order_cancel_fragment_to_orders_fragment);
                })
                .show();
    }
}
