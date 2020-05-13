package com.navetteclub.api.clients;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.navetteclub.BuildConfig;
import com.navetteclub.api.deserializer.DateTypeDeserializer;
import com.navetteclub.api.serializer.DateTypeSerializer;
import com.navetteclub.utils.Constants;
import com.navetteclub.api.interceptors.UserAgentAndApiKeyInterceptor;
import com.navetteclub.utils.Utils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.internal.Util;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            Gson gson = Utils.getGson();
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(new UserAgentAndApiKeyInterceptor())
                    .retryOnConnectionFailure(true);
            if (BuildConfig.DEBUG) {
                builder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
            }
            OkHttpClient okHttpClient = builder.build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .client(okHttpClient)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build(); //Build the Retrofit instance
        }
        return retrofit;
    }
}
