package com.example.rescueone.sos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class FakeCallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //fake call
        Intent service = new Intent(context, FakeCallService.class);
        context.startService(service);
    }
}
