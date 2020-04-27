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
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.legacy.content.WakefulBroadcastReceiver;

import com.navetteclub.App;
import com.navetteclub.R;
import com.navetteclub.services.LocationUpdatesService;
import com.navetteclub.services.PusherService;
import com.navetteclub.utils.Log;

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
        buildNotification(intent, payload);
    }

    private void buildNotification(Intent intent, String messagesNotif) {
        NotificationManager mNotificationManager =
                (NotificationManager) App.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(App.applicationContext, PusherService.CHANNEL_ID)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_arrow_left) // Small Icon required or notification doesn't display
                .setContentTitle(App.applicationContext.getString(R.string.app_name))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messagesNotif))
                .setContentText(messagesNotif)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setTicker(messagesNotif);

        notifBuilder.setVibrate(new long[]{0, 100, 0, 100});
        PendingIntent contentIntent = PendingIntent.getActivity(App.applicationContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        notifBuilder.setContentIntent(contentIntent);
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

