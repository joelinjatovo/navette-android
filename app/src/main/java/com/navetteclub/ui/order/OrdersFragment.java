package com.navetteclub.ui.order;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

import com.navetteclub.R;
import com.navetteclub.database.entity.OrderWithDatas;
import com.navetteclub.database.entity.User;
import com.navetteclub.databinding.FragmentOrdersBinding;
import com.navetteclub.utils.Log;
import com.navetteclub.vm.AuthViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;
import com.navetteclub.vm.OrdersViewModel;

public class OrdersFragment extends Fragment {

    private static final String TAG = OrdersFragment.class.getSimpleName();

    private FragmentOrdersBinding mBinding;

    private OrderRecyclerViewAdapter mAdapter;

    private OrdersViewModel mViewModel;

    private OrderViewModel orderViewModel;

    private SearchView searchView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_orders, container, false);

        mAdapter = new OrderRecyclerViewAdapter(mListener);
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
                new MyViewModelFactory(requireActivity().getApplication())).get(OrdersViewModel.class);

        orderViewModel = new ViewModelProvider(requireActivity(),
                new MyViewModelFactory(requireActivity().getApplication())).get(OrderViewModel.class);

        mViewModel.getOrdersLiveData().observe(getViewLifecycleOwner(),
                result -> {
                    if(result==null){
                        return;
                    }

                    if(result.getError()!=null){
                        // Error loading
                        mBinding.setIsLoading(false);
                        mBinding.setShowError(true);
                        mBinding.loaderErrorView.getSubtitleView().setText(result.getError());
                        Toast.makeText(requireContext(), result.getError(), Toast.LENGTH_SHORT).show();
                    }

                    if(result.getSuccess()!=null){
                        mBinding.setIsLoading(false);
                        mBinding.setShowError(false);
                        mAdapter.setItems(result.getSuccess());
                    }

                    // Reset remote result
                    mViewModel.setOrdersLiveData(null);
                });

        AuthViewModel authViewModel = new ViewModelProvider(requireActivity(),
                new MyViewModelFactory(requireActivity().getApplication())).get(AuthViewModel.class);

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
                    Navigation.findNavController(v).navigate(R.id.login_fragment);
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
        public void onListFragmentInteraction(View v, OrderWithDatas item) {
            orderViewModel.setOrderWithDatasLiveData(item);
            NavHostFragment.findNavController(OrdersFragment.this).navigate(R.id.action_orders_fragment_to_order_view_fragment);
        }
    };

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(View v, OrderWithDatas item);
    }

}
