package com.navetteclub.api.interceptors;

import android.os.Build;

import com.navetteclub.BuildConfig;
import com.navetteclub.utils.Constants;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class UserAgentAndApiKeyInterceptor implements Interceptor {
    private final String userAgent;

    public UserAgentAndApiKeyInterceptor(String userAgent) {
        this.userAgent = userAgent;
    }

    public UserAgentAndApiKeyInterceptor() {
        this(String.format(Locale.US,
                "Navette/%s (Android %s; %s; %s %s; %s) %s/%s",
                BuildConfig.VERSION_CODE,
                BuildConfig.BUILD_TYPE,
                Build.MODEL,
                Build.BRAND,
                Build.DEVICE,
                Locale.getDefault().getLanguage(),
                BuildConfig.APPLICATION_ID,
                BuildConfig.VERSION_NAME));
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originRequest = chain.request();
        Request requestWithUserAgent = originRequest.newBuilder()
                .header("User-Agent", userAgent)
                .header("x-api-key", Constants.getApiKey())
                .build();
        return chain.proceed(requestWithUserAgent);
    }
}
