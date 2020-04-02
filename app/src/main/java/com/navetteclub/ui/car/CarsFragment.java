package com.navetteclub.ui.car;

import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.navetteclub.R;
import com.navetteclub.database.entity.CarAndModel;
import com.navetteclub.databinding.FragmentCarsBinding;
import com.navetteclub.ui.order.OrderFragment;
import com.navetteclub.ui.order.ProcessFragment;
import com.navetteclub.utils.UiUtils;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;

public class CarsFragment extends Fragment implements OrderFragment.OnListFragmentInteractionListener {

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
        mBinding.viewPager2.setAdapter(mAdapter);
        mBinding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        });

        ViewPager2.LayoutParams params = mBinding.viewPager2.getLayoutParams();
        params.height = UiUtils.getScreenHeight();

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.stepView.go(0, true);

        orderViewModel = new ViewModelProvider(requireActivity(), new MyViewModelFactory(requireActivity().getApplication())).get(OrderViewModel.class);

        orderViewModel.getCars().observe(getViewLifecycleOwner(),
                carAndModels -> {
                    if(carAndModels == null || mAdapter == null){
                        return;
                    }

                    mAdapter.setItems(carAndModels);
                });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (mBinding.viewPager2.getCurrentItem() == 0) {
                            // If the user is currently looking at the first step, allow the system to handle the
                            // Back button. This calls finish() on this activity and pops the back stack.
                            NavHostFragment.findNavController(CarsFragment.this).popBackStack();
                        } else {
                            // Otherwise, select the previous step.
                            mBinding.viewPager2.setCurrentItem(mBinding.viewPager2.getCurrentItem() - 1);
                        }
                    }
                });
    }

    @Override
    public void onListFragmentInteraction(View view, int position, CarAndModel item) {
        orderViewModel.setCar(item.getCar());
        Navigation.findNavController(view).navigate(R.id.action_cars_fragment_to_privatize_fragment);
    }
}
