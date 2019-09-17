package com.hhw.cm60;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.dawn.newlandscan.ConfigActivity;
import com.dawn.newlandscan.LogUtils;
import com.dawn.newlandscan.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.support.v4.app.NotificationCompat.FLAG_NO_CLEAR;

public class BootInitActivity extends PreferenceActivity {
    private static final String TAG = "Huang, " + BootInitActivity.class.getSimpleName();

    ThreadFactory threadFactory = Executors.defaultThreadFactory();
    ExecutorService mExecutorService = new ThreadPoolExecutor(3, 200, 0L,
            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1024),
            threadFactory, new ThreadPoolExecutor.AbortPolicy());

    private boolean mIsOpen;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.configuration_settings);
        boolean isBoot = this.getIntent().getBooleanExtra("isBoot", false);
        if (isBoot) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
            mIsOpen = prefs.getBoolean("switch_scan_service", false);
            Intent toService = new Intent(this, SoftScanService.class);
//            if (mIsOpen){
//                //如果关机之前已经打开扫描头，再次开机后，让SoftScanService初始化扫描头
//                toService.putExtra("keyCode",-1);
//            }
            startService(toService);
//            setNotification();
//            if (mIsOpen) {
//                if (Util.mIsPad) {
//                    createFloatWindow();
//                }
//            }
            mExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(800);
                        Intent configIntent = new Intent();
                        configIntent.setAction(SoftScanService.ACTION_SCAN_BOOT_INIT);
                        BootInitActivity.this.getApplicationContext().sendBroadcast(configIntent);
                        LogUtils.i(TAG, "bootCompleted");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        BootInitActivity.this.finish();
    }
    private void setNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(getString(R.string.notification_title));
        if (mIsOpen){
            builder.setContentText(getString(R.string.notification_content));
        }else {
            builder.setContentText(this.getResources().getString(R.string.scan_service_stop));
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //添加事件
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        Intent intent = new Intent(this.getApplicationContext(), ConfigActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this.getApplicationContext(), 0, intent, flags);
        builder.setContentIntent(pi);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.scan));
        Notification notification = builder.build();
        notification.flags |= FLAG_NO_CLEAR;
        assert notificationManager != null;
        notificationManager.notify(R.string.app_name, notification);
    }

}
