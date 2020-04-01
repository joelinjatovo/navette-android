package com.navetteclub.api.clients;

import com.joelinjatovo.navette.BuildConfig;
import com.navetteclub.utils.Constants;
import com.navetteclub.api.interceptors.UserAgentAndApiKeyInterceptor;

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
                    .addInterceptor(new UserAgentAndApiKeyInterceptor())
                    //.addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
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
