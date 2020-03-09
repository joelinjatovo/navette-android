package com.joelinjatovo.navette.api.clients;

import com.joelinjatovo.navette.BuildConfig;
import com.joelinjatovo.navette.utils.Constants;
import com.joelinjatovo.navette.api.interceptors.ApiKeyInterceptor;
import com.joelinjatovo.navette.api.interceptors.UserAgentInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;

    private static String getBaseUrl() {
        if (BuildConfig.DEBUG) {
            return Constants.BASE_URL;
        }
        return Constants.BASE_URL;
    }
    public static Retrofit getInstance() {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(new UserAgentInterceptor())
                    .addInterceptor(new ApiKeyInterceptor(Constants.API_KEY))
                    .retryOnConnectionFailure(true)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(getBaseUrl())
                    .client(okHttpClient)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create()) //Add the converter//
                    .build(); //Build the Retrofit instance
        }
        return retrofit;
    }
}
