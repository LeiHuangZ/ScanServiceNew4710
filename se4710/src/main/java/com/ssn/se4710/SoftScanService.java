package com.ssn.se4710;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.ssn.LogUtils;
import com.ssn.Util;
import com.zebra.adc.decoder.BarCodeReader;
import com.zebra.adc.decoder.parameter.ParameterValue;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.support.v4.app.NotificationCompat.FLAG_NO_CLEAR;

/**
 * @author HuangLei 1252065297@qq.com
 * @CreateDate 2019/1/17 09:36
 * @UpdateUser 更新者
 * @UpdateDate 2019/1/18 11:53
 * 扫描服务主要实现类，后台触发扫描，提供对外控制扫描接口（常驻后台）
 */
public class SoftScanService extends Service implements BarCodeReader.DecodeCallback, BarCodeReader.PictureCallback, BarCodeReader.PreviewCallback,
        SurfaceHolder.Callback, BarCodeReader.VideoCallback, BarCodeReader.ErrorCallback {

    private static final String TAG = "SoftScanService";
    /** 连接扫描头 */
    public static final String ACTION_SCAN_INIT = "com.rfid.SCAN_INIT";
    /** 设置扫描超时 */
    public static final String ACTION_SCAN_TIME = "com.rfid.SCAN_TIME";
    /** 灯光设置 */
    public static final String ACTION_LIGHT_CONFIG = "com.rfid.LIGHT_CONFIG";
    /** 输入模式 */
    public static final String ACTION_SET_SCAN_MODE = "com.rfid.SET_SCAN_MODE";
    /** 照明光强度设置 */
    public static final String ACTION_ILLUMINATION_LEVEL = "com.rfid.ILLUMINATION_LEVEL";
    /** 扫描按键设置 */
    public static final String ACTION_KEY_SET = "com.rfid.KEY_SET";
    /** 开启扫描 */
    public static final String ACTION_SCAN = "com.rfid.SCAN_CMD";
    /** 停止扫描 */
    public static final String ACTION_STOP_SCAN = "com.rfid.STOP_SCAN";
    /** 关闭扫描头 */
    public static final String ACTION_CLOSE_SCAN = "com.rfid.CLOSE_SCAN";

    /**
     * ACTION_SCAN_CONFIG
     */
    public static final String ACTION_SCAN_CONFIG = "com.rfid.SCAN_CONFIG";
    /**
     * ACTION_ENABLE_SYM
     */
    public static final String ACTION_ENABLE_SYM = "com.rfid.ENABLE_SYM";
    /**
     * ACTION_DISENABLE_SYM
     */
    public static final String ACTION_DISENABLE_SYM = "com.rfid.DISENABLE_SYM";
    /**
     * 发送扫描结果的Action，用户可通过注册该Action 的广播来接收扫描数据
     */
    public static final String ACTION_SCAN_RESULT = "com.rfid.SCAN";
    /**
     * MULL Action
     */
    public static final String ACTION_NULL = "com.rfid.NULL";
    /**
     * 管理ConfigFragment中的各Preference中的存储的值
     */
    private SharedPreferences mDefaultSharedPreferences;

    /**
     * 扫描头控制器
     */
    private BarCodeReader bcr = null;

    /**
     * 初始化线程池管理类
     */
    ThreadFactory threadFactory = Executors.defaultThreadFactory();
    ExecutorService mExecutorService = new ThreadPoolExecutor(3, 200, 0L,
            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1024), threadFactory, new ThreadPoolExecutor.AbortPolicy());
    /**
     * 扫描头初始化标志
     */
    private boolean mIsInit = false;
    /**
     * 是否正在扫描，避免多次触发扫描
     */
    private boolean mIsScanning = false;

    /**
     * 用于接收设置扫描参数，开启扫描，关闭服务等指令
     */
    private ScanCommandBroadcast mSettingsReceiver;

    class ScanCommandBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // System Camera call
            boolean iscamera = intent.getBooleanExtra("iscamera", false);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String action = intent.getAction() == null ? ACTION_NULL : intent.getAction();
            switch (action) {
                case ACTION_NULL:
                    Log.i("Huang, " + TAG, "ScanCommandBroadcast, receive action = null");
                    break;
                case ACTION_SCAN_INIT:
                    LogUtils.i(TAG, "ScanCommandBroadcast, receive action scan_init");
                    initReader(iscamera);
                    break;
                case ACTION_SCAN_TIME:
                    LogUtils.i(TAG, "ScanCommandBroadcast, receive action set_timeout");
                    setDecodeTimeout(intent);
                    break;
                case ACTION_LIGHT_CONFIG:
                    LogUtils.i(TAG, "ScanCommandBroadcast, receive action set_lightMod");
                    setDecoderLightMod(intent);
                    break;
                case ACTION_SET_SCAN_MODE:
                    LogUtils.i(TAG, "ScanCommandBroadcast, receive action set_lightMod");
                    setScanMode(intent);
                    break;
                case ACTION_ILLUMINATION_LEVEL:
                    LogUtils.i(TAG, "ScanCommandBroadcast, receive action set_illumination_level");
                    setIlluminationLevel(intent);
                    break;
                case ACTION_KEY_SET:
                    LogUtils.i(TAG, "ScanCommandBroadcast, receive action set_scan_key");
                    setScanKey(intent);
                    break;
                case ACTION_SCAN:
                    LogUtils.i(TAG, "ScanCommandBroadcast, receive action start_scan");
                    startScan();
                    break;
                case ACTION_STOP_SCAN:
                    LogUtils.i(TAG, "ScanCommandBroadcast, receive action stop_scan");
                    stopScan();
                    break;
                case ACTION_CLOSE_SCAN:
                    LogUtils.i(TAG, "ScanCommandBroadcast, receive action close_scan");
                    closeScan(iscamera);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 屏灭屏亮广播
     */
    private BroadcastReceiver powerModeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            boolean isOpen = prefs.getBoolean("switch_scan_service", false);
            if (isOpen) {
                //连接扫描头
                if (Intent.ACTION_SCREEN_ON.equals(action)) {
                    LogUtils.i(TAG, "powerModeReceiver, ACTION_SCREEN_ON");
                    initReader(true);
                }
                //关闭扫描头
                else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                    LogUtils.i(TAG, "powerModeReceiver, ACTION_SCREEN_OFF");
                    closeScan(true);
                }
            }

        }
    };

    private BroadcastReceiver mKeyReceiver = new KeyReceiver();

    private void setNotification() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isOpen = prefs.getBoolean("switch_scan_service", false);
        String id ="channel_1";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, id)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(getString(R.string.notification_title));
        if (!isOpen){
            builder.setContentText(getString(R.string.notification_content));
        }else {
            builder.setContentText(getString(R.string.scan_service_stop));
        }
        //添加事件
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        Intent intent = new Intent(this, ConfigActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, flags);
        builder.setContentIntent(pi);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.scan));
        Notification notification = builder.build();
        notification.flags |= FLAG_NO_CLEAR;
        startForeground(R.string.app_name, notification);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Register scan command BroadCastReceiver
        mSettingsReceiver = new ScanCommandBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_SCAN_INIT);
        intentFilter.addAction(ACTION_SCAN_TIME);
        intentFilter.addAction(ACTION_LIGHT_CONFIG);
        intentFilter.addAction(ACTION_SET_SCAN_MODE);
        intentFilter.addAction(ACTION_ILLUMINATION_LEVEL);
        intentFilter.addAction(ACTION_KEY_SET);
        intentFilter.addAction(ACTION_SCAN);
        intentFilter.addAction(ACTION_STOP_SCAN);
        intentFilter.addAction(ACTION_CLOSE_SCAN);
        registerReceiver(mSettingsReceiver, intentFilter);
        //listner screen on/off
        IntentFilter screenFilter = new IntentFilter();
        screenFilter.addAction(Intent.ACTION_SCREEN_OFF);
        screenFilter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(powerModeReceiver, screenFilter);
        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction("android.intent.action.FUN_KEY");
        intentFilter1.addAction("android.rfid.FUN_KEY");
        registerReceiver(mKeyReceiver, intentFilter1);
        Util.initSoundPool(this);
        LogUtils.i(TAG, "onCreate");

        setNotification();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.i(TAG, "onStartCommand");
        mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
        /*
         * ConfigFragment call or BootReceiver call, do not start scan
         */
        initReader(true);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        LogUtils.i(TAG, "onDestroy");
        closeScan(false);
        // Unregister BroadcastReceiver
        unregisterReceiver(mSettingsReceiver);
        unregisterReceiver(powerModeReceiver);
        unregisterReceiver(mKeyReceiver);
        super.onDestroy();
    }

    /**
     * ------------------------------------------------------SE4710扫描接口实现-------------------------------------------------------------------
     */
    @Override
    public void onDecodeComplete(int symbology, int length, byte[] data, BarCodeReader reader) {
        LogUtils.i(TAG, "onDecodeComplete : symbology = " + symbology);
        LogUtils.i(TAG, "onDecodeComplete : length = " + length);
        /*
         * When symbology is 0 and length is 0, it's the result of decode timeout
         * When symbology is 0 and length is -1, canceled by the user
         */
        if (data != null && length > 0) {
            byte[] codeData = new byte[length];
            System.arraycopy(data, 0, codeData, 0, length);
            // Reset stat to waiting
            bcr.stopDecode();
            mIsScanning = false;
            sendData(codeData, symbology);
            boolean voiceEnabled = mDefaultSharedPreferences.getBoolean(ConfigFragment.KEY_SCAN_VOICE, true);
            if (voiceEnabled) {
                Util.play(1, 0);
            }
            LogUtils.e(TAG, "onDecodeComplete, playVoice");
        } else if (length == -1){
            // Reset stat to waiting
            mIsScanning = false;
        } else if (length == 0){
            bcr.stopDecode();
            mIsScanning = false;
        }
    }

    @Override
    public void onEvent(int event, int info, byte[] data, BarCodeReader reader) {

    }

    @Override
    public void onPictureTaken(int format, int width, int height, byte[] data, BarCodeReader reader) {

    }

    @Override
    public void onVideoFrame(int format, int width, int height, byte[] data, BarCodeReader reader) {

    }

    @Override
    public void onPreviewFrame(byte[] data, BarCodeReader reader) {

    }

    @Override
    public void onError(int error, BarCodeReader reader) {
        LogUtils.i(TAG, "error = " + error);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    /**
     * 开启扫描头，连接扫描头
     */
    private void initReader(boolean iscamera) {
        // System Camera call
        boolean isOpen = mDefaultSharedPreferences.getBoolean(ConfigFragment.KEY_SWITCH_SCAN, false);
        if (iscamera && !isOpen) {
            // Camera Call init, if the switch don't open before, and do not open when exit camera
            return;
        }
        LogUtils.i(TAG, "initReader mIsInit = " + mIsInit);
        if (!mIsInit) {
            bcr = BarCodeReader.open(2, getApplicationContext());
            // Set parameter - Uncomment for QC/MTK platforms
            // For QC/MTK platforms
            bcr.setParameter(765, 0);

            bcr.setParameter(8610, 1);
            // Set Orientation
            // 4 - omnidirectional
            bcr.setParameter(687, 4);
            // add callback
            bcr.setDecodeCallback(this);
            bcr.setErrorCallback(this);
            mDefaultSharedPreferences.edit().putBoolean(ConfigFragment.KEY_SWITCH_SCAN, true).apply();
            mIsInit = true;
            // load previous settings
            String timeout = mDefaultSharedPreferences.getString(ConfigFragment.KEY_DECODE_TIME, "99");
            setDecodeTimeout(new Intent().putExtra(ConfigFragment.KEY_DECODE_TIME, timeout));
            String lightMod = mDefaultSharedPreferences.getString(ConfigFragment.KEY_LIGHTS_CONFIG, "2");
            setDecoderLightMod(new Intent().putExtra(ConfigFragment.KEY_LIGHTS_CONFIG, lightMod));
            String illuminationLevel = mDefaultSharedPreferences.getString(ConfigFragment.KEY_ILLUMINATION_LEVEL, "4");
            setIlluminationLevel(new Intent().putExtra(ConfigFragment.KEY_ILLUMINATION_LEVEL, illuminationLevel));
        }
    }

    /**
     * 扫描超时时间设置
     */
    private void setDecodeTimeout(Intent intent) {
        if (!mIsInit) {
            Log.i("Huang, " + TAG, "setDecodeTimeout fail! Reader is not init, init first");
            return;
        }
        String stringExtra = intent.getStringExtra(ConfigFragment.KEY_DECODE_TIME);
        try {
            int decodeTimeout = Integer.parseInt(stringExtra);
            bcr.setParameter(ParameterValue.DECODE_SESSION_TIMEOUT, decodeTimeout);
            mDefaultSharedPreferences.edit().putString(ConfigFragment.KEY_DECODE_TIME, stringExtra).apply();
        } catch (Exception e) {
            Log.i("Huang, SoftScanService", "setDecoderLightMod fail! Parameter error");
        }
    }

    /**
     * 扫描灯光模式设置
     */
    private void setDecoderLightMod(Intent intent) {
        if (!mIsInit) {
            Log.i("Huang, " + TAG, "setDecoderLightMod fail! Reader is not init, init first");
            return;
        }
        String lightMod = intent.getStringExtra(ConfigFragment.KEY_LIGHTS_CONFIG);
        switch (lightMod) {
            case "0":
                // 仅开启瞄准（关闭照明）
                bcr.setParameter(ParameterValue.DECODING_ILLUMINATION, 0);
                bcr.setParameter(ParameterValue.DECODE_AIMING_PATTERN, 1);
                mDefaultSharedPreferences.edit().putString(ConfigFragment.KEY_LIGHTS_CONFIG, lightMod).apply();
                break;
            case "1":
                // 仅开启照明（关闭瞄准）
                bcr.setParameter(ParameterValue.DECODING_ILLUMINATION, 1);
                bcr.setParameter(ParameterValue.DECODE_AIMING_PATTERN, 0);
                mDefaultSharedPreferences.edit().putString(ConfigFragment.KEY_LIGHTS_CONFIG, lightMod).apply();
                break;
            case "2":
                // 同时使用
                bcr.setParameter(ParameterValue.DECODING_ILLUMINATION, 1);
                bcr.setParameter(ParameterValue.DECODE_AIMING_PATTERN, 1);
                mDefaultSharedPreferences.edit().putString(ConfigFragment.KEY_LIGHTS_CONFIG, lightMod).apply();
                break;
            case "3":
                // 同时关闭
                bcr.setParameter(ParameterValue.DECODING_ILLUMINATION, 0);
                bcr.setParameter(ParameterValue.DECODE_AIMING_PATTERN, 0);
                mDefaultSharedPreferences.edit().putString(ConfigFragment.KEY_LIGHTS_CONFIG, lightMod).apply();
                break;
            default:
                Log.i("Huang, SoftScanService", "setDecoderLightMod fail! Parameter error");
                break;
        }
    }

    /**
     * 输入模式设置
     */
    private void setScanMode(Intent intent){
        int mode = intent.getIntExtra("mode", 1);
        mDefaultSharedPreferences.edit().putString(ConfigFragment.KEY_INPUT_CONFIG, String.valueOf(mode)).apply();
    }

    /**
     * 照明光强度设置
     */
    private void setIlluminationLevel(Intent intent) {
        if (!mIsInit) {
            Log.i("Huang, " + TAG, "setIlluminationLevel fail! Reader is not init, init first");
            return;
        }
        String illuminationLevel = intent.getStringExtra(ConfigFragment.KEY_ILLUMINATION_LEVEL);
        try {
            Integer level = Integer.valueOf(illuminationLevel);
            bcr.setParameter(ParameterValue.ILLUMINATION_POWER_LEVEL, level);
            mDefaultSharedPreferences.edit().putString(ConfigFragment.KEY_ILLUMINATION_LEVEL, illuminationLevel).apply();
        } catch (Exception e) {
            Log.i("Huang, SoftScanService", "setIlluminationLevel fail! Parameter error");
        }
    }

    /**
     * 设置扫描按键
     */
    private void setScanKey(@NonNull Intent intent) {
        if (!mIsInit) {
            Log.i("Huang, " + TAG, "setScanKey fail! Reader is not init, init first");
            return;
        }
        boolean[] scanKeyArray = intent.getBooleanArrayExtra(ConfigFragment.KEY_SCAN_KEY);
        int requiredLen = 7;
        if (scanKeyArray == null || scanKeyArray.length != requiredLen) {
            Log.i("Huang, " + TAG, "setScanKey fail! Parameter error");
        } else {
            for (int i = 1; i <= scanKeyArray.length; i++) {
                boolean flag = scanKeyArray[i];
                mDefaultSharedPreferences.edit().putBoolean(ConfigFragment.KEY_SCAN_KEY_FX + i, flag).apply();
            }
        }
    }

    /**
     * 开启扫描
     */
    private void startScan() {
        if (mIsInit && !mIsScanning) {
            synchronized (SoftScanService.class) {
                // Start decode
                bcr.startDecode();
                LogUtils.e(TAG, "startScan, completed");
            }
            mIsScanning = true;
        } else {
            Log.i("Huang, " + TAG, "startScan fail! Reader is not init or is busy now");
        }
    }

    /**
     * 停止扫描
     */
    private void stopScan() {
        if (mIsInit && mIsScanning) {
            bcr.stopDecode();
            // Key is released
            mIsScanning = false;
        } else {
            Log.i("Huang, " + TAG, "stopScan fail! Reader is not init or event don't work");
        }
    }

    /**
     * 断开扫描头连接
     */
    private void closeScan(boolean iscamera) {
        if (mIsInit) {
            bcr.release();
            bcr = null;
            if (!iscamera) {
                // Camera call, don't reset scan switch stat
                mDefaultSharedPreferences.edit().putBoolean(ConfigFragment.KEY_SWITCH_SCAN, false).apply();
            }
            mIsInit = false;
        } else {
            Log.i("Huang, " + TAG, "closeScan fail! Reader is not init");
        }
    }

    /**
     * 发送扫码数据
     *
     * @param data 扫码数据
     */
    private void sendData(byte[] data, int sym) {
        String inputMod = mDefaultSharedPreferences.getString(ConfigFragment.KEY_INPUT_CONFIG, "1");
        switch (inputMod) {
            case "0":
                // 广播发送
                broadScanResult(data, sym);
                break;
            case "1":
                // 输入框输入
                try {
                    String utf8Num = "1";
                    String gbkNum = "2";
                    String charsetNum = mDefaultSharedPreferences.getString(ConfigFragment.KEY_RESULT_CHAR_SET, "1");
                    String result = "";
                    LogUtils.i(TAG, "charsetNum = " + charsetNum);
                    if (charsetNum.equals(utf8Num)) {
                        result = new String(data, 0, data.length, "UTF-8");
                        LogUtils.i(TAG, "onDecodeComplete : data = " + Arrays.toString(data));
                        LogUtils.i(TAG, "onDecodeComplete : data = " + result);
                    } else if (charsetNum.equals(gbkNum)) {
                        result = new String(data, 0, data.length, "GBK");
                        LogUtils.i(TAG, "onDecodeComplete : data = " + Arrays.toString(data));
                        LogUtils.i(TAG, "onDecodeComplete : data = " + result);
                    }
                    sendToInput(result);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    showToast("解码错误，请尝试设置其他字符编码");
                }
                // 输入框输入之后通过广播再次广播数据
                broadScanResult(data, sym);
                mIsScanning = false;
                break;
            default:
                break;
        }
    }

    /**
     * 将扫描结果以广播的形式发送
     */
    private void broadScanResult(byte[] data, int codeID) {
        Intent intent = new Intent();
        intent.putExtra("data", data);
        intent.putExtra("code_id", codeID);
        intent.setAction(ACTION_SCAN_RESULT);
        sendBroadcast(intent);
    }

    /**
     * 将结果直接输入光标处
     */
    private void sendToInput(String data) {
        boolean enterFlag = false;
        String result = getfixChar(data);
        String append = getAppendChar();
        switch (append) {
            case "1":
                enterFlag = true;
                break;
            case "2":
                result += "\n";
                break;
            case "3":
                result += "\t";
                break;
            case "4":

                break;
            default:
                break;
        }

        Intent toBack = new Intent();
        toBack.setAction("android.rfid.INPUT");
        //发送添加前缀后缀的数据
        toBack.putExtra("data", result);
        toBack.putExtra("enter", enterFlag);
        sendBroadcast(toBack);
    }

    private String getAppendChar() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        return prefs.getString("append_ending_char", "4");
    }

    /**
     * 对扫描结果添加前缀后缀
     */
    private String getfixChar(String data) {
        String result;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String prefix = prefs.getString("prefix_config", "");
        String suffix = prefs.getString("suffix_config", "");
//        String append = prefs$getString("append_ending_char", "");
        result = prefix + data + suffix;
        return result;
    }

    private Toast mToast;

    private void showToast(String content) {
        if (mToast == null) {
            mToast = Toast.makeText(SoftScanService.this, content, Toast.LENGTH_SHORT);
            mToast.show();
        } else {
            mToast.setText(content);
            mToast.show();
        }
    }
}
