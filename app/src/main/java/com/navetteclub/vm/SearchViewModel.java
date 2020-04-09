package com.navetteclub.vm;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.facebook.login.LoginManager;
import com.navetteclub.database.callback.FindCallback;
import com.navetteclub.database.entity.User;
import com.navetteclub.database.repositories.UserRepository;
import com.navetteclub.utils.Log;
import com.navetteclub.utils.Preferences;

import java.util.List;

public class SearchViewModel extends ViewModel {

    private static final String TAG = SearchViewModel.class.getSimpleName();

    public enum SearchType { ORIGIN, RETOURS}

    private final MutableLiveData<SearchType> searchType = new MutableLiveData<>();

    public MutableLiveData<SearchType> getSearchType() {
        return searchType;
    }
}
