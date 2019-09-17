package com.ssn.se4710;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.KeyEvent;

import com.ssn.LogUtils;

/**
 * @author HuangLei 1252065297@qq.com
 * @CreateDate 2019/1/17 18:03
 * @UpdateUser 更新者
 * @UpdateDate 2019/1/17 18:03
 * 接收按键广播触发扫描
 */
public class KeyReceiver extends BroadcastReceiver {
    private static final String TAG = KeyReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int keyCode = intent.getIntExtra("keyCode", 0);
        //兼容H941
        if (keyCode == 0) {
            keyCode = intent.getIntExtra("keycode", 0);
        }
        boolean keyDown = intent.getBooleanExtra("keydown", false);
        boolean f1Enable = prefs.getBoolean("key_f1", true);
        boolean f2Enable = prefs.getBoolean("key_f2", true);
        boolean f3Enable = prefs.getBoolean("key_f3", true);
        boolean f4Enable = prefs.getBoolean("key_f4", true);
        boolean f5Enable = prefs.getBoolean("key_f5", true);
        boolean f6Enable = prefs.getBoolean("key_f6", true);
        boolean f7Enable = prefs.getBoolean("key_f7", true);
        // If the scan switch in ConfigFragment is opened, start scan
        if (f1Enable && keyCode == KeyEvent.KEYCODE_F1) {
            sendScanBroad(context, keyDown);
        } else if (f2Enable && keyCode == KeyEvent.KEYCODE_F2) {
            sendScanBroad(context, keyDown);
        } else if (f3Enable && keyCode == KeyEvent.KEYCODE_F3) {
            sendScanBroad(context, keyDown);
        } else if (f4Enable && keyCode == KeyEvent.KEYCODE_F4) {
            sendScanBroad(context, keyDown);
        } else if (f5Enable && keyCode == KeyEvent.KEYCODE_F5) {
            sendScanBroad(context, keyDown);
        } else if (f6Enable && keyCode == KeyEvent.KEYCODE_F6) {
            sendScanBroad(context, keyDown);
        } else if (f7Enable && keyCode == KeyEvent.KEYCODE_F7) {
            sendScanBroad(context, keyDown);
        }

    }

    private void sendScanBroad(Context context, boolean keydown) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isKeyPressed = prefs.getBoolean("isKeyPressed", false);
        LogUtils.i(TAG, "isKeyPressed = " + isKeyPressed);
        Intent broadIntent = new Intent();
        if (keydown && !isKeyPressed) {
            broadIntent.setAction(SoftScanService.ACTION_SCAN);
            isKeyPressed = true;
        } else if (!keydown && isKeyPressed){
            broadIntent.setAction(SoftScanService.ACTION_STOP_SCAN);
            isKeyPressed = false;
        }
        context.sendBroadcast(broadIntent);
        prefs.edit().putBoolean("isKeyPressed", isKeyPressed).apply();
    }
}
