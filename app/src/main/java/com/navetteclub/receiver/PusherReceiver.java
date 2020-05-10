package com.navetteclub.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.legacy.content.WakefulBroadcastReceiver;
import androidx.navigation.NavDeepLinkBuilder;

import com.google.gson.Gson;
import com.navetteclub.App;
import com.navetteclub.R;
import com.navetteclub.api.models.ItemParam;
import com.navetteclub.models.ItemPayload;
import com.navetteclub.models.OrderPayload;
import com.navetteclub.models.RidePayload;
import com.navetteclub.services.LocationUpdatesService;
import com.navetteclub.services.PusherService;
import com.navetteclub.ui.MainActivity;
import com.navetteclub.ui.driver.RideMapFragment;
import com.navetteclub.ui.order.LiveFragment;
import com.navetteclub.ui.order.OrderFragmentDirections;
import com.navetteclub.ui.order.OrderViewFragment;
import com.navetteclub.ui.order.OrderViewFragmentDirections;
import com.navetteclub.utils.Log;
import com.navetteclub.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class PusherReceiver extends BroadcastReceiver {

    private static final String TAG = PusherReceiver.class.getSimpleName();

    private boolean isRegistered;

    public void register(final Context context, IntentFilter filter) {
        if (!isRegistered) {
            context.registerReceiver(this, filter);
            isRegistered = true;
        }
    }

    public void unregister(final Context context) {
        if (isRegistered) {
            context.unregisterReceiver(this);
            isRegistered = false;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            Log.e(TAG, "No extras sent to NotificationExtenderService in its Intent!\n" + intent);
            return;
        }
        String event = bundle.getString(PusherService.EXTRA_EVENT_NAME);
        String payload = bundle.getString(PusherService.EXTRA_PAYLOAD);
        Log.d(TAG, "Event "  + event);
        Log.d(TAG, "Payload "  + payload);

        if(event!=null && payload!=null){
            Gson gson = Utils.getGson();;
            switch (event){
                case "order.created":
                case "order.updated":
                    OrderPayload orderPayload = gson.fromJson(payload, OrderPayload.class);
                    if(orderPayload!=null){
                        Intent intent1 = new Intent(App.applicationContext, MainActivity.class);
                        intent1.setData(OrderViewFragment.getUri(orderPayload.getOrderId()));
                        PendingIntent contentIntent = PendingIntent.getActivity(
                                App.applicationContext,
                                0,
                                intent1,
                                PendingIntent.FLAG_ONE_SHOT);
                        buildNotification(contentIntent, App.applicationContext.getString(orderPayload.getNewStatusStringDesc()));
                    }
                    break;
                case "item.created":
                case "item.updated":
                    ItemPayload itemPayload = gson.fromJson(payload, ItemPayload.class);
                    if(itemPayload!=null){
                        Intent intent1 = new Intent(App.applicationContext, MainActivity.class);
                        intent1.setData(LiveFragment.getUri(itemPayload.getItemId()));
                        PendingIntent contentIntent = PendingIntent.getActivity(
                                App.applicationContext,
                                0,
                                intent1,
                                PendingIntent.FLAG_ONE_SHOT);
                        buildNotification(contentIntent, App.applicationContext.getString(itemPayload.getNewStatusStringDesc()));
                    }
                    break;
                case "ride.created":
                case "ride.updated":
                    RidePayload ridePayload = gson.fromJson(payload, RidePayload.class);
                    if(ridePayload!=null){
                        Intent intent1 = new Intent(App.applicationContext, MainActivity.class);
                        intent1.setData(RideMapFragment.getUri(ridePayload.getRideId()));
                        PendingIntent contentIntent = PendingIntent.getActivity(
                                App.applicationContext,
                                0,
                                intent1,
                                PendingIntent.FLAG_ONE_SHOT);
                        buildNotification(contentIntent, App.applicationContext.getString(ridePayload.getNewStatusStringDesc()));
                    }
                    break;

            }
        }
    }

    private void buildNotification(PendingIntent pendingIntent, String messagesNotif) {
        NotificationManager mNotificationManager =
                (NotificationManager) App.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(App.applicationContext, PusherService.CHANNEL_ID)
                .setAutoCancel(true)
                .setColor(App.applicationContext.getResources().getColor(R.color.colorAccent))
                .setSmallIcon(R.drawable.ic_logo_notification_32) // Small Icon required or notification doesn't display
                .setContentTitle(App.applicationContext.getString(R.string.app_name))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messagesNotif))
                .setContentText(messagesNotif)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setTicker(messagesNotif);

        notifBuilder.setVibrate(new long[]{0, 100, 0, 100});
        notifBuilder.setContentIntent(pendingIntent);
        if (mNotificationManager != null) {
            mNotificationManager.cancel(1);
        }

        Notification notification = notifBuilder.build();
        notification.ledARGB = 0xff00ff00;
        notification.ledOnMS = 1000;
        notification.ledOffMS = 1000;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;

        if (mNotificationManager != null) {
            mNotificationManager.notify(1, notification);
        }
    }
}

