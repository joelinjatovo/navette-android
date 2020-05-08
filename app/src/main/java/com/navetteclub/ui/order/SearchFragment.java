package com.navetteclub.ui.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.navetteclub.R;
import com.navetteclub.database.entity.Location;
import com.navetteclub.databinding.FragmentSearchBinding;
import com.navetteclub.ui.LocationPickerActivity;
import com.navetteclub.ui.OnClickItemListener;
import com.navetteclub.ui.notification.NotificationRecyclerViewAdapter;
import com.navetteclub.utils.Log;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;
import com.navetteclub.vm.SearchViewModel;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SearchFragment extends BottomSheetDialogFragment implements OnClickItemListener<Location> {
    private static final String TAG = SearchFragment.class.getSimpleName();
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int LOCATION_PICKER_REQUEST_CODE = 2;

    private SearchType searchType;

    private FragmentSearchBinding mBinding;

    private SearchViewModel searchViewModel;

    private OrderViewModel orderViewModel;

    private LocationRecyclerViewAdapter mAdapterSaved;

    private LocationRecentRecyclerViewAdapter mAdapterRecent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            // The getSearchType() method will be created automatically.
            searchType = SearchFragmentArgs.fromBundle(getArguments()).getSearchType();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);

        // Set the adapter
        mAdapterSaved = new LocationRecyclerViewAdapter(this);
        RecyclerView recyclerView = mBinding.recyclerView1;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(mAdapterSaved);

        // Set the adapter
        mAdapterRecent = new LocationRecentRecyclerViewAdapter(this);
        RecyclerView recyclerView2 = mBinding.recyclerView2;
        recyclerView2.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView2.setAdapter(mAdapterRecent);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUi();
        setupViewModel();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case AUTOCOMPLETE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Place place = Autocomplete.getPlaceFromIntent(data);
                    Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getLatLng());
                    Location location = new Location();
                    location.setType(Location.TYPE_RECENT);
                    location.setCreatedAt(new Date());
                    location.setId(place.getId());
                    location.setName(place.getName());
                    if(place.getLatLng()!=null){
                        location.setLat(place.getLatLng().latitude);
                        location.setLng(place.getLatLng().longitude);
                    }
                    searchViewModel.upsert(location);
                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    // TODO: Handle the error.
                    Status status = Autocomplete.getStatusFromIntent(data);
                    Log.i(TAG, status.getStatusMessage());
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                }
                break;
            case LOCATION_PICKER_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    String name = data.getStringExtra(LocationPickerActivity.INTENT_EXTRA_NAME);
                    Double lat = data.getDoubleExtra(LocationPickerActivity.INTENT_EXTRA_LNG, 0.0);
                    Double lng = data.getDoubleExtra(LocationPickerActivity.INTENT_EXTRA_LNG, 0.0);
                    Location location = new Location();
                    location.setType(Location.TYPE_RECENT);
                    location.setCreatedAt(new Date());
                    location.setName(name);
                    location.setLat(lat);
                    location.setLng(lng);
                    searchViewModel.upsert(location);
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                }
                break;
        }
    }

    private void setupViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        orderViewModel = new ViewModelProvider(this, factory).get(OrderViewModel.class);
        searchViewModel = new ViewModelProvider(this, factory).get(SearchViewModel.class);

        searchViewModel.getList(Location.TYPE_RECENT).observe(getViewLifecycleOwner(), list -> mAdapterRecent.setItems(list));

        searchViewModel.getList(Location.TYPE_HOME, Location.TYPE_WORK).observe(getViewLifecycleOwner(), list -> mAdapterSaved.setItems(list));
    }

    private void setupUi() {
        mBinding.toolbar.inflateMenu(R.menu.menu_location_search);
        mBinding.toolbar.setOnMenuItemClickListener(
                item -> {
                    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
                    Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                            .build(SearchFragment.this.requireContext());
                    startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
                    return false;
                });

        NavController navController = NavHostFragment.findNavController(SearchFragment.this);
        if(searchType == SearchType.ORIGIN){
            mBinding.toolbar.setTitle(R.string.title_origin);
        }

        if(searchType == SearchType.RETOURS){
            mBinding.toolbar.setTitle(R.string.title_retours);
        }

        mBinding.buttonSelectLocation.setOnClickListener(
                v -> {
                    Intent intent = new Intent(requireActivity(), LocationPickerActivity.class);
                    startActivityForResult(intent, LOCATION_PICKER_REQUEST_CODE);
                });

        mBinding.buttonAddPlace.setOnClickListener(
                v -> {
                    navController.navigate(R.id.action_search_fragment_to_picker_location_fragment);
                });
    }

    @Override
    public void onClick(View v, int position, Location item) {
        if(searchType == SearchType.ORIGIN){
            orderViewModel.setItem1LiveData(item.getName(), new LatLng(item.getLat(), item.getLng()));
            NavHostFragment.findNavController(this).popBackStack();
            return;
        }

        if(searchType == SearchType.RETOURS){
            orderViewModel.setItem2LiveData(item.getName(), new LatLng(item.getLat(), item.getLng()));
            NavHostFragment.findNavController(this).popBackStack();
            return;
        }
    }
}
