package com.navetteclub.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.navetteclub.App;
import com.navetteclub.BuildConfig;
import com.navetteclub.database.entity.User;
import com.navetteclub.ui.MainActivity;
import com.navetteclub.utils.Constants;
import com.navetteclub.utils.Log;
import com.navetteclub.utils.Preferences;
import com.navetteclub.utils.PusherOdk;
import com.navetteclub.utils.Utils;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.util.HttpAuthorizer;

import java.util.HashMap;
import java.util.Map;

public class PusherService extends Service {

    private static final String TAG = PusherService.class.getSimpleName();

    private Handler handler = new Handler(Looper.getMainLooper());

    Thread readThread;

    private Runnable checkRunnable = this::check;

    public PusherService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG + "Pusher", "onCreate() ");
        String token = Preferences.Auth.getCurrentToken(this);
        Long userId = Preferences.Auth.getCurrentUser(this);
        Log.d(TAG + "Pusher", "onCreate() " + token + " / " + userId);
        if(token!=null && userId>0){
            Pusher pusher = PusherOdk.getInstance(token).getPusher();
            pusher.connect();

            if (pusher.getPrivateChannel("private-App.User."+userId) == null) {
                subscribeUser(pusher,"private-App.User."+userId, "user.point.created", "order.created", "order.updated", "item.updated", "item.created", "ride.created", "eide.updated");
            }
        }
        check();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG + "Pusher", "onBind() ");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG + "Pusher", "onStartCommand(" + startId + ") ");
        super.onStartCommand(intent, flags, startId);

        String token = intent.getStringExtra("token");
        long userId = intent.getLongExtra("user_id", 0);
        if(token!=null && userId>0){
            Pusher pusher = PusherOdk.getInstance(token).getPusher();
            pusher.connect();

            if (pusher.getPrivateChannel("private-App.User."+userId) == null) {
                subscribeUser(pusher,"private-App.User."+userId, "user.point.created");
            }
        }

        return START_STICKY;
    }

    private void subscribeUser(Pusher pusher, String channel, String... events) {
        pusher.subscribePrivate(channel,
                new PrivateChannelEventListener() {
                    @Override
                    public void onEvent(PusherEvent event) {
                        Log.d(TAG + "Pusher", "onEvent");
                        Log.d(TAG + "Pusher", event.getEventName());
                        Log.d(TAG + "Pusher", event.getData());
                        readThread = new Thread(() -> {
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction(BuildConfig.PUSHER_SERVICE_NAME);
                            broadcastIntent.putExtra("event_name", event.getEventName());
                            broadcastIntent.putExtra("json_payload", event.getData());
                            sendBroadcast(broadcastIntent);
                        });
                        readThread.start();
                    }

                    @Override
                    public void onSubscriptionSucceeded(String channelName) {
                        Log.d(TAG + "Pusher", "onSubscriptionSucceeded(" + channelName + ")");

                    }

                    @Override
                    public void onAuthenticationFailure(String message, Exception e) {
                        Log.e(TAG + "Pusher", "onAuthenticationFailure(" + message + ")", e);

                    }
                }, events);
    }

    private void check() {
        Log.d(TAG + "Pusher", "check() ");
        handler.removeCallbacks(checkRunnable);
        handler.postDelayed(checkRunnable, 1500);
    }
}
