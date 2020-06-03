package com.navetteclub.ui.car;

import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.navetteclub.R;
import com.navetteclub.database.entity.Car;
import com.navetteclub.databinding.FragmentCarsBinding;
import com.navetteclub.ui.OnClickItemListener;
import com.navetteclub.utils.UiUtils;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;

import java.util.ArrayList;

public class CarsFragment extends Fragment implements OnClickItemListener<Car> {

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

        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());

        orderViewModel = new ViewModelProvider(requireActivity(), factory).get(OrderViewModel.class);

        orderViewModel.getCarsResult().observe(getViewLifecycleOwner(),
                result -> {
                    if(result == null){
                        return;
                    }

                    if(result.getError()!=null){
                        // Error loading
                        mBinding.setIsLoading(false);
                        mBinding.setShowError(true);
                        mBinding.loaderErrorView.getTitleView().setText(R.string.loader_error_title);
                        mBinding.loaderErrorView.getSubtitleView().setText(result.getError());
                        Toast.makeText(requireContext(), result.getError(), Toast.LENGTH_SHORT).show();
                    }

                    if(result.getSuccess()!=null){
                        mBinding.setIsLoading(false);
                        ArrayList<Car> items = (ArrayList<Car>) result.getSuccess();
                        if(items.isEmpty()){
                            mBinding.setShowError(true);
                            mBinding.loaderErrorView.getTitleView().setText(R.string.title_empty);
                            mBinding.loaderErrorView.getSubtitleView().setText(R.string.empty_cars);
                        }else{
                            mBinding.setShowError(false);
                            mAdapter.setItems(items);
                        }
                    }

                });

        mBinding.loaderErrorView.getButton().setOnClickListener(
                v -> {
                    try{
                        mBinding.setIsLoading(true);
                        mBinding.setShowError(false);
                        orderViewModel.loadCars();
                    }catch (NullPointerException ignored){
                        mBinding.setIsLoading(false);
                        mBinding.setShowError(true);
                    }
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

        mBinding.toolbar.setNavigationOnClickListener(
                v -> {
                    NavHostFragment.findNavController(this).popBackStack();
                });
    }

    @Override
    public void onClick(View view, int position, Car item) {
        orderViewModel.setCarLiveData(item);
        Navigation.findNavController(view).navigate(R.id.action_cars_fragment_to_privatize_fragment);
    }
}
