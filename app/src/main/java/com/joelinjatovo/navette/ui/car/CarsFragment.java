package com.joelinjatovo.navette.ui.car;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.database.entity.CarAndModel;
import com.joelinjatovo.navette.databinding.FragmentCarsBinding;
import com.joelinjatovo.navette.ui.order.OrderFragment;
import com.joelinjatovo.navette.utils.Log;
import com.joelinjatovo.navette.utils.UiUtils;
import com.joelinjatovo.navette.vm.MyViewModelFactory;
import com.joelinjatovo.navette.vm.OrderViewModel;

public class CarsFragment extends BottomSheetDialogFragment implements OrderFragment.OnListFragmentInteractionListener {

    private static final String TAG = CarsFragment.class.getSimpleName();

    private FragmentCarsBinding mBinding;

    private OrderViewModel orderViewModel;

    private CarViewPagerAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_cars, container, false);

        mAdapter = new CarViewPagerAdapter(this);
        mBinding.viewPager.setAdapter(mAdapter);
        mBinding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        });

        ViewPager2.LayoutParams params = mBinding.viewPager.getLayoutParams();
        params.height = UiUtils.getScreenHeight();

        return mBinding.getRoot();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;

                FrameLayout bottomSheet = d.findViewById(R.id.design_bottom_sheet);
                if (bottomSheet != null) {
                    BottomSheetBehavior sheetBehavior = BottomSheetBehavior.from(bottomSheet);
                    sheetBehavior.setSkipCollapsed(true);
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else{
                    Log.d(TAG, "bottomSheet Null");
                }
            }
        });

        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        orderViewModel = new ViewModelProvider(requireActivity(), new MyViewModelFactory(requireActivity().getApplication())).get(OrderViewModel.class);

        orderViewModel.getCars().observe(getViewLifecycleOwner(), carAndModels -> {
            if(carAndModels == null || mAdapter == null){
                return;
            }

            mAdapter.setItems(carAndModels);
        });
    }

    @Override
    public void onListFragmentInteraction(View view, int position, CarAndModel item) {
        orderViewModel.setCar(item);
        NavHostFragment.findNavController(this).popBackStack(R.id.order_fragment, false);
    }
}
