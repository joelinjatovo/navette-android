package com.joelinjatovo.navette.api.interceptors;

import android.os.Build;

import com.joelinjatovo.navette.BuildConfig;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class UserAgentInterceptor implements Interceptor {
    private final String userAgent;

    public UserAgentInterceptor(String userAgent) {
        this.userAgent = userAgent;
    }

    public UserAgentInterceptor() {
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
                .build();
        return chain.proceed(requestWithUserAgent);
    }
}
