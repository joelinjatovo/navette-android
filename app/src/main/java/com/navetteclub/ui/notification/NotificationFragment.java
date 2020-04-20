package com.navetteclub.ui.notification;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.navetteclub.R;
import com.navetteclub.api.models.Pagination;
import com.navetteclub.api.models.google.Step;
import com.navetteclub.database.entity.Notification;
import com.navetteclub.database.entity.User;
import com.navetteclub.databinding.FragmentNotificationBinding;
import com.navetteclub.ui.OnClickItemListener;
import com.navetteclub.ui.PaginationListener;
import com.navetteclub.utils.Log;
import com.navetteclub.vm.AuthViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.NotificationViewModel;

import java.util.ArrayList;

public class NotificationFragment extends Fragment implements OnClickItemListener<Notification>, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = NotificationFragment.class.getSimpleName();

    private NotificationViewModel notificationViewModel;

    private AuthViewModel authViewModel;

    private FragmentNotificationBinding mBinding;

    private NotificationRecyclerViewAdapter mAdapter;

    private SearchView searchView;

    private Pagination pagination;

    private int currentPage = 0;

    private boolean isLoading = false;

    private PaginationListener scrollListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false);

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

        // Set the adapter
        mAdapter = new NotificationRecyclerViewAdapter(this);
        RecyclerView recyclerView = mBinding.recyclerView;
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(scrollListener);
        recyclerView.setAdapter(mAdapter);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar();
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        notificationViewModel = new ViewModelProvider(requireActivity(), factory).get(NotificationViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity(), factory).get(AuthViewModel.class);
        notificationViewModel.getPaginationResult().observe(getViewLifecycleOwner(),
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
        notificationViewModel.getNotificationResult().observe(getViewLifecycleOwner(),
                result -> {
                    if(result==null){
                        return;
                    }

                    isLoading = false;
                    mBinding.swipeRefresh.setRefreshing(false);
                    if (currentPage > 1) mAdapter.removeLoading();

                    if(result.getError()!=null){
                        if(result.getError() == R.string.error_401) { // Error 401: Unauthorized
                            authViewModel.logout(requireContext());
                        }else{
                            // Error loading
                            mBinding.setIsLoading(false);
                            mBinding.setShowError(true);
                            mBinding.loaderErrorView.getTitleView().setText(R.string.loader_error_title);
                            mBinding.loaderErrorView.getSubtitleView().setText(result.getError());

                        }
                        Toast.makeText(requireContext(), result.getError(), Toast.LENGTH_SHORT).show();
                    }

                    if(result.getSuccess()!=null){
                        mBinding.setIsLoading(false);
                        ArrayList<Notification> items = (ArrayList<Notification>) result.getSuccess();
                        if(items.isEmpty()){
                            mBinding.setShowError(true);
                            mBinding.loaderErrorView.getTitleView().setText(R.string.title_empty);
                            mBinding.loaderErrorView.getSubtitleView().setText(R.string.empty_notifications);
                        }else{
                            mBinding.setShowError(false);
                            mAdapter.addItems(items);

                            if(pagination!=null && !pagination.isLastPage()){
                                mAdapter.addLoading();
                            }
                        }
                    }

                    // Reset remote result
                    notificationViewModel.setNotificationsResult(null);
                });

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
                    Navigation.findNavController(v).navigate(R.id.action_global_navigation_auth);
                });

        mBinding.swipeRefresh.setOnRefreshListener(this);
    }


    @Override
    public void onClick(View v, int position, Notification item) {

    }

    @Override
    public void onRefresh() {
        currentPage = 0;
        isLoading = false;
        mAdapter.clear();
        doApiCall();
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
            notificationViewModel.load(user, currentPage);
            mBinding.setShowError(false);
            mBinding.setIsUnauthenticated(false);
            if(currentPage==1){
                // Show main loader view for the first page only
                mBinding.setIsLoading(true);
            }
        }
    }

}
