package com.ssn.se4710;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.ssn.LogUtils;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.support.v4.app.NotificationCompat.FLAG_NO_CLEAR;

/**
 * @author HuangLei 1252065297@qq.com
 * @CreateDate 2019/1/17 18:02
 * @UpdateUser 更新者
 * @UpdateDate 2019/1/17 18:02
 * 主配置界面
 */
public class ConfigFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String TAG = ConfigFragment.class.getSimpleName();

    public static final String KEY_SWITCH_SCAN = "switch_scan_service";
    public static final String KEY_INIT_SCAN = "init_scan";
    public static final String KEY_START_SCAN = "start_scan";
    public static final String KEY_STOP_SCAN = "stop_scan";
    public static final String KEY_DECODE_TIME = "decode_time_limit";
    public static final String KEY_RESULT_CHAR_SET = "result_char_set";
    public static final String KEY_LIGHTS_CONFIG = "lightsConfig";
    public static final String KEY_ILLUMINATION_LEVEL = "illuminationPowerLevel";
    public static final String KEY_SCAN_KEY = "scan_Key";
    public static final String KEY_SCAN_KEY_FX = "key_f";
    public static final String KEY_INPUT_CONFIG = "inputConfig";
    public static final String KEY_APPEND_ENDING_CHAR = "append_ending_char";
    public static final String KEY_SCAN_VOICE = "scanning_voice";

    private static final String KEY_CATEGORY_SYM_CONFIG = "category_sym_config";
    private static final String KEY_CATEGORY_SCANNING = "category_scanning";
    private static final String KEY_CATEGORY_SCAN_TIME = "category_scan_time";
    private NotificationManager mNotificationManager;
    private LocalBroadcastManager mLocalBroadcastManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.configuration_settings);

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this.getActivity().getApplicationContext());
        initView();
        LogUtils.i(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.i(TAG, "onDestroy");
    }

    private void initView() {
        ListPreference listPreferenceTimeout = (ListPreference) findPreference(KEY_DECODE_TIME);
        ListPreference listPreferenceLights = (ListPreference) findPreference(KEY_LIGHTS_CONFIG);
        ListPreference listPreferenceIllumination = (ListPreference) findPreference(KEY_ILLUMINATION_LEVEL);
        ListPreference listPreferenceInput = (ListPreference) findPreference(KEY_INPUT_CONFIG);
        ListPreference listPreferenceAppend = (ListPreference) findPreference(KEY_APPEND_ENDING_CHAR);
        ListPreference listPreferenceCharset = (ListPreference) findPreference(KEY_RESULT_CHAR_SET);
        //初始超时设置
        updatePreference(listPreferenceTimeout, listPreferenceTimeout.getValue());
        listPreferenceTimeout.setOnPreferenceChangeListener(this);
        //灯光设置
        updatePreference(listPreferenceLights, listPreferenceLights.getValue());
        listPreferenceLights.setOnPreferenceChangeListener(this);
        //照明灯光强度设置
        updatePreference(listPreferenceIllumination, listPreferenceIllumination.getValue());
        listPreferenceIllumination.setOnPreferenceChangeListener(this);
        //扫描结果字符编码设置
        updatePreference(listPreferenceCharset, listPreferenceCharset.getValue());
        listPreferenceCharset.setOnPreferenceChangeListener(this);
        //输入模式设置
        updatePreference(listPreferenceInput, listPreferenceInput.getValue());
        listPreferenceInput.setOnPreferenceChangeListener(this);
        //追加结束符
        updatePreference(listPreferenceAppend, listPreferenceAppend.getValue());
        listPreferenceAppend.setOnPreferenceChangeListener(this);
        // Preference监听
        SwitchPreference switchScanService = (SwitchPreference) findPreference(KEY_SWITCH_SCAN);
        switchScanService.setOnPreferenceChangeListener(this);
        // 更新所有设置项的可访问状态
        enableAllSettings(switchScanService.isChecked());
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        LogUtils.i(TAG, "onPreferenceChange, key = " + key);
        LogUtils.i(TAG, "onPreferenceChange, newValue = " + newValue);
        switch (key) {
            case KEY_SWITCH_SCAN:
                boolean switchOpen = (boolean) newValue;
//                Activity configActivity = ConfigFragment.this.getActivity();
//                Intent serviceIntent = new Intent(configActivity.getApplicationContext(), SoftScanService.class);
//                serviceIntent.putExtra("fromConfig", true);
                if (switchOpen) {
//                    this.getActivity().getApplicationContext().startService(serviceIntent);
                    sendCmdBroadcast(SoftScanService.ACTION_SCAN_INIT, KEY_INIT_SCAN, String.valueOf(newValue));
                } else {
//                    this.getActivity().getApplicationContext().stopService(serviceIntent);
                    sendCmdBroadcast(SoftScanService.ACTION_CLOSE_SCAN, KEY_INIT_SCAN, String.valueOf(newValue));
                }
                setNotification();
                // 更新设置项的可访问状态
                enableAllSettings(switchOpen);
                break;
            case KEY_DECODE_TIME:
                // 更新Preference中的entry值
                updatePreference((ListPreference) preference, (String) newValue);
                // 更新设置
                sendCmdBroadcast(SoftScanService.ACTION_SCAN_TIME, KEY_DECODE_TIME, (String) newValue);
                break;
            case KEY_LIGHTS_CONFIG:
                updatePreference((ListPreference) preference ,(String) newValue);
                sendCmdBroadcast(SoftScanService.ACTION_LIGHT_CONFIG, KEY_LIGHTS_CONFIG, (String) newValue);
                break;
            case KEY_ILLUMINATION_LEVEL:
                updatePreference((ListPreference) preference ,(String) newValue);
                sendCmdBroadcast(SoftScanService.ACTION_ILLUMINATION_LEVEL, KEY_ILLUMINATION_LEVEL, (String) newValue);
                break;
            case KEY_INPUT_CONFIG:
            case KEY_RESULT_CHAR_SET:
            case KEY_APPEND_ENDING_CHAR:
                updatePreference((ListPreference) preference, (String) newValue);
                break;
            default:
                break;
        }
        return true;
    }

    private void updatePreference(ListPreference preference, String newValue) {
        CharSequence[] entries = preference.getEntries();
        int index = preference.findIndexOfValue(newValue);
        preference.setSummary(entries[index]);
    }

    /**
     * 设置项是否可访问
     *
     * @param b 是否允许
     */
    private void enableAllSettings(boolean b) {
        findPreference(KEY_CATEGORY_SCANNING).setEnabled(b);
        findPreference(KEY_CATEGORY_SCAN_TIME).setEnabled(b);
    }

    /**
     * 通知Service改变设置
     *
     * @param action 需要改变的设置
     */
    private void sendCmdBroadcast(String action, String key, String extras) {
        Intent configIntent = new Intent();
        configIntent.setAction(action);
        configIntent.putExtra(key, extras);
        this.getActivity().getApplicationContext().sendBroadcast(configIntent);
    }

    private void setNotification() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        boolean isOpen = prefs.getBoolean("switch_scan_service", true);
        mNotificationManager = (NotificationManager) this.getActivity().getSystemService(NOTIFICATION_SERVICE);
        String id ="channel_1";
        String description = "123";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(id, "123", NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getActivity(), id)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(getString(R.string.notification_title));
        if (!isOpen){
            builder.setContentText(getString(R.string.notification_content));
        }else {
            builder.setContentText(getString(R.string.scan_service_stop));
        }
        //添加事件
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        Intent intent = new Intent(this.getActivity(), ConfigActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this.getActivity(), 0, intent, flags);
        builder.setContentIntent(pi);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.scan));
        Notification notification = builder.build();
        notification.flags |= FLAG_NO_CLEAR;
        assert mNotificationManager != null;
        //暂时去掉通知
        mNotificationManager.notify(R.string.app_name, notification);
    }
}
