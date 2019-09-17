package com.ssn.se4710;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.ssn.LogUtils;

import static android.support.v4.app.NotificationCompat.FLAG_NO_CLEAR;

/**
 * @author HuangLei 1252065297@qq.com
 * @CreateDate 2019/1/18 10:15
 * @UpdateUser 更新者
 * @UpdateDate 2019/1/18 10:15
 * 开机启动当前透明界面，以初始化码制及另外的设置信息
 */
public class BootInitActivity extends Activity {
    private static final String TAG = BootInitActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(android.R.id.content, new ConfigFragment());
        fragmentTransaction.commit();
        LogUtils.i(TAG, "onCreate-3");
//        boolean isBoot = this.getIntent().getBooleanExtra("isBoot", false);
//        if (isBoot) {
//            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            Intent toService = new Intent(this.getApplicationContext(), SoftScanService.class);
        LogUtils.i(TAG, "onCreate-2");
//            toService.putExtra("fromBoot", true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.getApplicationContext().startForegroundService(toService);
        } else {
            this.getApplicationContext().startService(toService);
        }
        LogUtils.i(TAG, "onCreate-1");
            setNotification();
        LogUtils.i(TAG, "onCreate-complete");
//            if (mIsOpen) {
//                if (Util.mIsPad) {
//                    createFloatWindow();
//                }
//            }
            finish();
//        }
    }

    private void setNotification() {
        LogUtils.i(TAG, "onCreate1");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        boolean isOpen = prefs.getBoolean("switch_scan_service", true);
        LogUtils.i(TAG, "onCreate2");
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        LogUtils.i(TAG, "onCreate3");
        String id ="channel_1";
        String description = "123";
        LogUtils.i(TAG, "onCreate4");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(id, "123", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            LogUtils.i(TAG, "onCreate5");
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getApplicationContext(), id)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(getString(R.string.notification_title));
        LogUtils.i(TAG, "onCreate6");
        if (!isOpen){
            builder.setContentText(getString(R.string.notification_content));
        }else {
            builder.setContentText(getString(R.string.scan_service_stop));
        }
        LogUtils.i(TAG, "onCreate7");
        //添加事件
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        Intent intent = new Intent(this.getApplicationContext(), ConfigActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this.getApplicationContext(), 0, intent, flags);
        LogUtils.i(TAG, "onCreate8");
        builder.setContentIntent(pi);
        LogUtils.i(TAG, "onCreate9");
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.scan));
        LogUtils.i(TAG, "onCreate10");
        Notification notification = builder.build();
        LogUtils.i(TAG, "onCreate11");
        notification.flags |= FLAG_NO_CLEAR;
        LogUtils.i(TAG, "onCreate12");
        assert notificationManager != null;
        //暂时去掉通知
        notificationManager.notify(R.string.app_name, notification);
        LogUtils.i(TAG, "onCreate13");
    }

    private void createFloatWindow(){
        Intent intent = new Intent(BootInitActivity.this, FloatService.class);
        startService(intent);
    }

}
