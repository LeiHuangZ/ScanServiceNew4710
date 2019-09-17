package com.dawn.newlandscan.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hhw.cm60.BootInitActivity;

public class BootReceiver  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i("Huang, BootCompleteReceiver", "action = " + action);
        Intent toService = new Intent(context, BootInitActivity.class);
        toService.putExtra("isBoot", true);
        toService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(toService);
    }
}
