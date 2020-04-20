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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.navetteclub.R;
import com.navetteclub.api.models.Pagination;
import com.navetteclub.database.entity.OrderWithDatas;
import com.navetteclub.database.entity.RideWithDatas;
import com.navetteclub.database.entity.User;
import com.navetteclub.databinding.FragmentOrdersBinding;
import com.navetteclub.databinding.FragmentRidesBinding;
import com.navetteclub.ui.OnClickItemListener;
import com.navetteclub.ui.PaginationListener;
import com.navetteclub.ui.notification.NotificationRecyclerViewAdapter;
import com.navetteclub.ui.order.OrdersFragment;
import com.navetteclub.utils.Log;
import com.navetteclub.vm.AuthViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;
import com.navetteclub.vm.OrdersViewModel;
import com.navetteclub.vm.RidesViewModel;

import java.util.ArrayList;

public class RidesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = RidesFragment.class.getSimpleName();

    private FragmentRidesBinding mBinding;

    private RideRecyclerViewAdapter mAdapter;

    private AuthViewModel authViewModel;

    private RidesViewModel ridesViewModel;

    private SearchView searchView;

    private Pagination pagination;

    private int currentPage = 0;

    private boolean isLoading = false;

    private PaginationListener scrollListener;

    private OnClickItemListener<RideWithDatas> mListener = (v,pos,item) -> {
        if(authViewModel.getUser()!=null && item!=null && item.getRide()!=null){
            RidesFragmentDirections.ActionRidesFragmentToRidePointFragment action = RidesFragmentDirections
                    .actionRidesFragmentToRidePointFragment(
                            authViewModel.getUser().getAuthorizationToken(),
                            item.getRide().getId());
            NavHostFragment.findNavController(RidesFragment.this).navigate(action);
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_rides, container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        scrollListener = new PaginationListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                doApiCall();
            }

            @Override
            public boolean isLastPage() {
                return pagination != null && pagination.isLastPage();
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        };

        mAdapter = new RideRecyclerViewAdapter(mListener);
        RecyclerView recyclerView = mBinding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.addOnScrollListener(scrollListener);
        recyclerView.setAdapter(mAdapter);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar();
        setupUI();
        setupAuthViewModel();
        setupRidesViewModel();
    }

    @Override
    public void onRefresh() {
        currentPage = 0;
        isLoading = false;
        mAdapter.clear();
        doApiCall();
    }

    private void setupUI() {
        mBinding.toolbar.setNavigationOnClickListener(
                v -> {
                    NavHostFragment.findNavController(this).popBackStack();
                });

        mBinding.loaderErrorView.getButton().setOnClickListener(
                v -> {
                    User user = authViewModel.getUser();
                    if(user!=null){
                        if(currentPage>0) currentPage--;
                        doApiCall();
                    }else{
                        mBinding.setIsLoading(false);
                    }
                });

        mBinding.authErrorView.getButton().setOnClickListener(
                v -> {
                    Navigation.findNavController(v).navigate(R.id.action_orders_fragment_to_navigation_auth);
                });

        mBinding.swipeRefresh.setOnRefreshListener(this);
    }

    private void setupAuthViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        authViewModel = new ViewModelProvider(requireActivity(), factory).get(AuthViewModel.class);
        authViewModel.getAuthenticationState().observe(getViewLifecycleOwner(),
                authenticationState -> {
                    if (authenticationState == AuthViewModel.AuthenticationState.AUTHENTICATED) {
                        doApiCall();
                    }else{
                        mBinding.setIsLoading(false);
                        mBinding.setShowError(false);
                        mBinding.setIsUnauthenticated(true);
                    }
                });
    }

    private void setupRidesViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        ridesViewModel = new ViewModelProvider(requireActivity(), factory).get(RidesViewModel.class);
        ridesViewModel.getPaginationResult().observe(getViewLifecycleOwner(),
                result -> {
                    if(result==null){
                        return;
                    }
                    pagination = result;
                    currentPage = pagination.currentPage;
                    if(scrollListener!=null){
                        scrollListener.setPageSize(pagination.perPage);
                    }
                });
        ridesViewModel.getRidesResult().observe(getViewLifecycleOwner(),
                result -> {
                    if(result==null){
                        return;
                    }

                    mBinding.setIsLoading(false);
                    mBinding.swipeRefresh.setRefreshing(false);
                    if (currentPage > 1) mAdapter.removeLoading();

                    if(result.getError()!=null){
                        if(result.getError() == R.string.error_401) { // Error 401: Unauthorized
                            authViewModel.logout(requireContext());
                        }else{
                            // Error loading
                            mBinding.setShowError(true);
                            mBinding.loaderErrorView.getTitleView().setText(R.string.loader_error_title);
                            mBinding.loaderErrorView.getSubtitleView().setText(result.getError());
                        }
                        Toast.makeText(requireContext(), result.getError(), Toast.LENGTH_SHORT).show();
                    }

                    if(result.getSuccess()!=null){
                        ArrayList<RideWithDatas> items = (ArrayList<RideWithDatas>) result.getSuccess();
                        if(items.isEmpty()){
                            mBinding.setShowError(true);
                            mBinding.loaderErrorView.getTitleView().setText(R.string.title_empty);
                            mBinding.loaderErrorView.getSubtitleView().setText(R.string.empty_rides);
                        }else{
                            mBinding.setShowError(false);
                            mAdapter.addItems(items);

                            if(pagination!=null && !pagination.isLastPage()){
                                mAdapter.addLoading();
                            }
                        }
                    }

                    // Reset remote result
                    ridesViewModel.setRidesResult(null);
                });
    }

    private void setupToolbar() {
        mBinding.toolbar.inflateMenu(R.menu.menu_search);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = mBinding.toolbar.getMenu().findItem(R.id.search);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }

        if (searchView != null && searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));

            SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.i(TAG + "Search", newText);

                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.i(TAG + "Search", query);

                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }

        mBinding.toolbar.setOnMenuItemClickListener(item -> item.getItemId() != R.id.search);
    }

    private void doApiCall() {
        User user = authViewModel.getUser();
        if(user!=null && !isLoading){
            isLoading = true;
            currentPage++;
            ridesViewModel.load(user, currentPage);
            mBinding.setShowError(false);
            mBinding.setIsUnauthenticated(false);
            if(currentPage==1){
                // Show main loader view for the first page only
                mBinding.setIsLoading(true);
            }
        }
    }
}
