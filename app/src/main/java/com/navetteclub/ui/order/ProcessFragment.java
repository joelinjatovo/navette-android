package com.navetteclub.ui.order;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.navetteclub.R;
import com.navetteclub.databinding.FragmentProcessBinding;

public class ProcessFragment extends Fragment {

    private static final int NUM_PAGES = 3;

    FragmentProcessBinding mBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_process, container, false);

        return mBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Instantiate a ViewPager2 and a PagerAdapter.
        ScreenSlidePagerAdapter  mAdapter = new ScreenSlidePagerAdapter(requireActivity());
        mBinding.viewPager2.setAdapter(mAdapter);



        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (mBinding.viewPager2.getCurrentItem() == 0) {
                            // If the user is currently looking at the first step, allow the system to handle the
                            // Back button. This calls finish() on this activity and pops the back stack.
                            NavHostFragment.findNavController(ProcessFragment.this).popBackStack();
                        } else {
                            // Otherwise, select the previous step.
                            mBinding.viewPager2.setCurrentItem(mBinding.viewPager2.getCurrentItem() - 1);
                        }
                    }
                });
    }

    private static class ScreenSlidePagerAdapter extends FragmentStateAdapter {

        ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return new PrivateOrderSlidePageFragment();
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
}
