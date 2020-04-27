package com.navetteclub;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDexApplication;

import com.navetteclub.receiver.PusherReceiver;
import com.navetteclub.services.PusherService;
import com.navetteclub.ui.MainActivity;
import com.navetteclub.utils.Constants;
import com.navetteclub.utils.Log;
import com.stripe.android.PaymentConfiguration;

public class App extends MultiDexApplication implements LifecycleObserver {

    private static final String TAG = App.class.getSimpleName();

    private static App instance;

    private static PusherReceiver mReceiver;

    public static Context applicationContext;

    public static volatile Handler applicationHandler;

    private static boolean isBackgrounded = true;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        applicationContext = getApplicationContext();
        applicationHandler = new Handler(applicationContext.getMainLooper());

        /*
        SharedPreferences googleBug = getSharedPreferences("google_bug_154855417", Context.MODE_PRIVATE);
        if (!googleBug.contains("fixed")) {
            File corruptedZoomTables = new File(getFilesDir(), "ZoomTables.data");
            corruptedZoomTables.delete();
            googleBug.edit().putBoolean("fixed", true).apply();
        }
        */

        // Stripe
        PaymentConfiguration.init(
                getApplicationContext(),
                Constants.getStripeApiKey()
        );

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        isBackgrounded = true;
        Log.d("MyApp", "App in background");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        isBackgrounded = false;
        Log.d("MyApp", "App in foreground");
    }

    public static boolean isAppOnBackground() {
        return isBackgrounded;
    }

    public static boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) applicationContext.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void startPusherService(String token, Long userId) {
        Log.d(TAG, "startPusherService(" + userId + ")");

        mReceiver = new PusherReceiver();
        final IntentFilter filter = new IntentFilter();
        filter.addAction(PusherService.ACTION_BROADCAST);
        mReceiver.register(applicationContext, filter);

        Intent serviceIntent = new Intent(applicationContext, PusherService.class);
        serviceIntent.putExtra("token", token);
        serviceIntent.putExtra("user_id", userId);
        applicationContext.startService(serviceIntent);
    }

    /**
     * Stop Services
     */
    public static void stopPushService() {
        mReceiver.unregister(applicationContext);
        applicationContext.stopService(new Intent(applicationContext, PusherService.class));

        if (android.os.Build.VERSION.SDK_INT >= 19) {
            PendingIntent pintent2 =
                    PendingIntent.getService(applicationContext, 0, new Intent(applicationContext, PusherService.class), 0);
            AlarmManager alarm2 = (AlarmManager) applicationContext.getSystemService(Context.ALARM_SERVICE);
            if (alarm2 != null) {
                alarm2.cancel(pintent2);
            }
        }
    }
}
