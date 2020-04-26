package com.navetteclub.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.legacy.content.WakefulBroadcastReceiver;

import com.navetteclub.App;

public class PusherReceiver extends BroadcastReceiver {

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
        //ComponentName cn = new ComponentName(App.getInstance().getPackageName(), Notifikasi.class.getName());
        //+startWakefulService(context, intent.setComponent(cn));
    }
}

