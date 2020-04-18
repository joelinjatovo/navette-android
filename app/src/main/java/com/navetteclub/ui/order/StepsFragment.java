package com.navetteclub.ui.order;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.navetteclub.R;
import com.navetteclub.api.models.google.Leg;
import com.navetteclub.api.models.google.Route;
import com.navetteclub.api.models.google.Step;
import com.navetteclub.database.entity.OrderWithDatas;
import com.navetteclub.database.entity.User;
import com.navetteclub.databinding.FragmentStepsBinding;
import com.navetteclub.databinding.FragmentTimelineBinding;
import com.navetteclub.models.Timeline;
import com.navetteclub.ui.OnClickItemListener;
import com.navetteclub.utils.Log;
import com.navetteclub.utils.UiUtils;
import com.navetteclub.vm.AuthViewModel;
import com.navetteclub.vm.GoogleViewModel;
import com.navetteclub.vm.MyViewModelFactory;

import java.util.Arrays;

public class StepsFragment extends BottomSheetDialogFragment implements OnClickItemListener<Step> {

    private static final String TAG = StepsFragment.class.getSimpleName();

    private FragmentStepsBinding mBinding;

    private StepRecyclerViewAdapter mAdapter;

    private AuthViewModel authViewModel;

    private GoogleViewModel googleViewModel;

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
                    sheetBehavior.setPeekHeight(UiUtils.getScreenHeight());
                }
            }
        });

        return dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_steps, container, false);

        mAdapter = new StepRecyclerViewAdapter(this);
        RecyclerView recyclerView = mBinding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(mAdapter);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar();
        setupUi();
        setupViewModel();
    }

    private void setupViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());

        authViewModel = new ViewModelProvider(requireActivity(),
                factory).get(AuthViewModel.class);

        authViewModel.getAuthenticationState().observe(getViewLifecycleOwner(),
                authenticationState -> {
                    if (authenticationState == AuthViewModel.AuthenticationState.AUTHENTICATED) {
                        mBinding.setIsLoading(false);
                        mBinding.setShowError(false);
                        mBinding.setIsUnauthenticated(false);
                        loadDirection();
                    }else{
                        mBinding.setIsLoading(false);
                        mBinding.setShowError(false);
                        mBinding.setIsUnauthenticated(true);
                    }
                });

        googleViewModel = new ViewModelProvider(requireActivity(),
                factory).get(GoogleViewModel.class);

        googleViewModel.getDirection1Result().observe(getViewLifecycleOwner(),
                result -> {
                    if(result==null){
                        return;
                    }

                    if(result.body()!=null){
                        Route route = result.body().getRoutes().get(0);
                        for(Leg leg: route.getLegs()){
                            if(leg.getSteps().length == 0){
                                showEmptyResponseError();
                            }else{
                                mBinding.setIsLoading(false);
                                mBinding.setShowError(false);
                                mAdapter.setItems(Arrays.asList(leg.getSteps()));
                            }
                        }
                    }
                });

        googleViewModel.getError1Result().observe(getViewLifecycleOwner(),
                error -> {
                    if(error==null){
                        return;
                    }

                    showEmptyResponseError(error);

                });
    }

    private void showEmptyResponseError(String error) {
        mBinding.setIsLoading(false);
        mBinding.setShowError(true);
        mBinding.loaderErrorView.getTitleView().setText(R.string.title_empty);
        mBinding.loaderErrorView.getSubtitleView().setText(error);
    }

    private void showEmptyResponseError() {
        showEmptyResponseError(getString(R.string.empty_steps));
    }

    private void setupToolbar() {
        mBinding.toolbar.setNavigationOnClickListener(
                v -> {
                    NavHostFragment.findNavController(this).popBackStack();
                });
    }

    private void setupUi() {
        mBinding.authErrorView.getButton().setOnClickListener(
                v -> {
                    Navigation.findNavController(v).navigate(R.id.action_orders_fragment_to_navigation_auth);
                });

        mBinding.loaderErrorView.getButton().setOnClickListener(
                v -> {
                    User user = authViewModel.getUser();
                    if(user!=null){
                        mBinding.setIsLoading(true);
                        mBinding.setShowError(false);
                        loadDirection();
                    }else{
                        mBinding.setIsLoading(false);
                    }
                });
    }

    private void loadDirection() {
        LatLng origin = new LatLng(-18.9175793, 47.5422862);
        LatLng destination = new LatLng(-18.9127615, 47.5344172);
        googleViewModel.loadDirection(getString(R.string.google_maps_key), origin, destination, null, true);
    }

    @Override
    public void onClick(View v, int position, Step item) {

    }
}
