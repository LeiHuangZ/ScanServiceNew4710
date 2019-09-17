package com.hhw.cm60;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i("Huang, Receiver", "BootCompleteReceiver action = " + action);
        Intent bootInit = new Intent(context, BootInitActivity.class);
        bootInit.putExtra("isBoot", true);
        bootInit.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(bootInit);
    }
}
