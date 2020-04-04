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

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.navetteclub.R;
import com.navetteclub.database.entity.ClubAndPoint;
import com.navetteclub.database.entity.Notification;
import com.navetteclub.database.entity.User;
import com.navetteclub.databinding.FragmentNotificationBinding;
import com.navetteclub.models.RemoteLoaderResult;
import com.navetteclub.ui.club.ClubRecyclerViewAdapter;
import com.navetteclub.ui.club.ClubsFragment;
import com.navetteclub.ui.order.OrdersFragment;
import com.navetteclub.utils.Log;
import com.navetteclub.vm.AuthViewModel;
import com.navetteclub.vm.ClubViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.NotificationViewModel;

import java.util.List;

public class NotificationFragment extends Fragment {

    private static final String TAG = NotificationFragment.class.getSimpleName();

    private NotificationViewModel mViewModel;

    private AuthViewModel authViewModel;

    private FragmentNotificationBinding mBinding;

    private NotificationRecyclerViewAdapter mAdapter;

    private SearchView searchView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false);

        // Set the adapter
        mAdapter = new NotificationRecyclerViewAdapter(mListener);
        RecyclerView recyclerView = mBinding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(mAdapter);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar();

        mViewModel = new ViewModelProvider(requireActivity(),
                new MyViewModelFactory(requireActivity().getApplication())).get(NotificationViewModel.class);


        authViewModel = new ViewModelProvider(requireActivity(),
                new MyViewModelFactory(requireActivity().getApplication())).get(AuthViewModel.class);

        mViewModel.getNotificationsLiveData().observe(getViewLifecycleOwner(),
                result -> {
                    if(result==null){
                        return;
                    }

                    if(result.getError()!=null){
                        if(result.getError() == R.string.error_401) { // Error 401: Unauthorized
                            authViewModel.logout(requireContext());
                        }else{
                            // Error loading
                            mBinding.setIsLoading(false);
                            mBinding.setShowError(true);
                            mBinding.loaderErrorView.getSubtitleView().setText(result.getError());

                        }
                        Toast.makeText(requireContext(), result.getError(), Toast.LENGTH_SHORT).show();
                    }

                    if(result.getSuccess()!=null){
                        mBinding.setIsLoading(false);
                        mBinding.setShowError(false);
                        mAdapter.setItems(result.getSuccess());
                    }

                    // Reset remote result
                    mViewModel.setNotificationsLiveData(null);
                });

        authViewModel.getAuthenticationState().observe(getViewLifecycleOwner(),
                authenticationState -> {
                    if (authenticationState == AuthViewModel.AuthenticationState.AUTHENTICATED) {
                        User user = authViewModel.getUser();
                        mViewModel.load(user);
                        mBinding.setIsLoading(true);
                        mBinding.setShowError(false);
                        mBinding.setIsUnauthenticated(false);
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
                        mBinding.setIsLoading(true);
                        mBinding.setShowError(false);
                        mViewModel.load(user);
                    }else{
                        mBinding.setIsLoading(false);
                    }
                });

        mBinding.authErrorView.getButton().setOnClickListener(
                v -> {
                    Navigation.findNavController(v).navigate(R.id.action_global_navigation_auth);
                }
        );
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

    private OnListFragmentInteractionListener mListener = new OnListFragmentInteractionListener() {
        @Override
        public void onListFragmentInteraction(View v, Notification item) {
            //
        }
    };

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(View v, Notification item);
    }

}
