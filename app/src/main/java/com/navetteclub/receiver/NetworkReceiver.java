package com.navetteclub.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.ViewModelProvider;

import com.navetteclub.utils.Log;
import com.navetteclub.utils.Utils;

public class NetworkReceiver extends BroadcastReceiver {

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
