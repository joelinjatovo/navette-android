package com.joelinjatovo.navette.ui.car;

import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.database.entity.CarAndModel;
import com.joelinjatovo.navette.databinding.FragmentCarsBinding;
import com.joelinjatovo.navette.ui.order.CarRecyclerViewAdapter;
import com.joelinjatovo.navette.ui.order.OrderFragment;
import com.joelinjatovo.navette.vm.MyViewModelFactory;
import com.joelinjatovo.navette.vm.OrderViewModel;

public class CarsFragment extends BottomSheetDialogFragment implements OrderFragment.OnListFragmentInteractionListener {

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

        return mBinding.getRoot();
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
