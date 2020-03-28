package com.joelinjatovo.navette.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.joelinjatovo.navette.utils.Log;
import com.joelinjatovo.navette.utils.Utils;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        try {
            if (Utils.haveNetworkConnection(context)) {
                Log.e("keshav", "Online Connect Intenet ");
            } else {
                Log.e("keshav", "Conectivity Failure !!! ");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
