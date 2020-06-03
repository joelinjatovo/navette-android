package com.navetteclub.vm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.navetteclub.database.callback.UpsertCallback;
import com.navetteclub.database.entity.Location;
import com.navetteclub.database.repositories.LocationRepository;

import java.util.List;

public class SearchViewModel extends ViewModel {

    private static final String TAG = SearchViewModel.class.getSimpleName();

    private final LocationRepository locationRepository;

    public enum SearchType { ORIGIN, RETOURS}

    private final MutableLiveData<SearchType> searchType = new MutableLiveData<>();


    SearchViewModel(LocationRepository repository) {
        this.locationRepository = repository;
    }

    public LiveData<List<Location>> getList(String... types) {
        return locationRepository.getList(types);
    }

    public void delete(Location location) {
        new Thread(() -> locationRepository.delete(location)).start();
    }

    public void clearLocationRecent(String... types) {
        new Thread(() -> locationRepository.deleteByType(types)).start();
    }

    public void upsert(UpsertCallback<Location> callback, Location... locations) {
        locationRepository.upsert(callback, locations);
    }

    public MutableLiveData<SearchType> getSearchType() {
        return searchType;
    }
}
