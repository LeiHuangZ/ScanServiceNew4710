package com.hhw.cm60;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.dawn.decoderapijni.EngineCode;
import com.dawn.decoderapijni.EngineCodeMenu;
import com.dawn.decoderapijni.SoftEngine;
import com.dawn.newlandscan.ConfigActivity;
import com.dawn.newlandscan.R;
import com.ssn.LogUtils;
import com.ssn.PreferenceKey;
import com.ssn.Util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
 * CM60 扫描服务主要实现类，后台触发扫描，提供对外控制扫描接口（常驻后台）
 */
public class SoftScanService extends Service {

    private static final String TAG = "Huang, SoftScanService";
    /**
     * 连接扫描头(开机时连接扫描头)
     */
    public static final String ACTION_SCAN_BOOT_INIT = "com.rfid.SCAN_BOOT_INIT";
    /**
     * 连接扫描头(开机后重新打开扫描开关或调用连接扫描头)
     */
    public static final String ACTION_SCAN_INIT = "com.rfid.SCAN_INIT";
    /**
     * 设置扫描超时
     */
    public static final String ACTION_SCAN_TIME = "com.rfid.SCAN_TIME";
    /**
     * 灯光设置
     */
    public static final String ACTION_LIGHT_CONFIG = "com.rfid.LIGHT_CONFIG";
    /**
     * 输入模式
     */
    public static final String ACTION_SET_SCAN_MODE = "com.rfid.SET_SCAN_MODE";
    /**
     * 照明光强度设置
     */
    public static final String ACTION_ILLUMINATION_LEVEL = "com.rfid.ILLUMINATION_LEVEL";
    /**
     * 扫描按键设置
     */
    public static final String ACTION_KEY_SET = "com.rfid.KEY_SET";
    /**
     * 开启扫描
     */
    public static final String ACTION_SCAN = "com.rfid.SCAN_CMD";
    /**
     * 停止扫描
     */
    public static final String ACTION_STOP_SCAN = "com.rfid.STOP_SCAN";
    /**
     * 关闭扫描头
     */
    public static final String ACTION_CLOSE_SCAN = "com.rfid.CLOSE_SCAN";
    /**
     * 设置扫描参数
     */
    public static final String ACTION_SCAN_PARAM = "com.rfid.SCAN_PARAM";

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
     * 管理PreferenceKey中的各Preference中的存储的值
     */
    private SharedPreferences mDefaultSharedPreferences;

    /**
     * 扫描头控制器
     */
    private SoftEngine mSoftEngine;
    public static List<EngineCode> engineCodeList;

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
            LogUtils.i(TAG, "ScanCommandBroadcast, iscamera: " + iscamera);
            String action = intent.getAction() == null ? ACTION_NULL : intent.getAction();
            switch (action) {
                case ACTION_NULL:
                    Log.i(TAG, "ScanCommandBroadcast, receive action = null");
                    break;
                case ACTION_SCAN_BOOT_INIT:
                    LogUtils.i(TAG, "ScanCommandBroadcast, receive action scan_boot_init");
                    bootInitReader();
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
                    LogUtils.i(TAG, "ScanCommandBroadcast, receive action set_scan_mode");
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
                case ACTION_SCAN_PARAM:
                    LogUtils.i(TAG, "ScanCommandBroadcast, receive action set_scan_param");
                    setScanParam(intent);
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
                    closeScan();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 屏灭屏亮广播
     */
    /*private BroadcastReceiver powerModeReceiver = new BroadcastReceiver() {

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
    };*/

    @Override
    public void onCreate() {
        super.onCreate();
        // Register scan command BroadCastReceiver
        mSettingsReceiver = new ScanCommandBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_SCAN_BOOT_INIT);
        intentFilter.addAction(ACTION_SCAN_INIT);
        intentFilter.addAction(ACTION_SCAN_TIME);
//        intentFilter.addAction(ACTION_LIGHT_CONFIG);
        intentFilter.addAction(ACTION_SET_SCAN_MODE);
//        intentFilter.addAction(ACTION_ILLUMINATION_LEVEL);
        intentFilter.addAction(ACTION_KEY_SET);
        intentFilter.addAction(ACTION_SCAN);
        intentFilter.addAction(ACTION_STOP_SCAN);
        intentFilter.addAction(ACTION_CLOSE_SCAN);
        registerReceiver(mSettingsReceiver, intentFilter);
        //listner screen on/off
        /*IntentFilter screenFilter = new IntentFilter();
        screenFilter.addAction(Intent.ACTION_SCREEN_OFF);
        screenFilter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(powerModeReceiver, screenFilter);*/
        Util.initSoundPool(this);
        LogUtils.i(TAG, "onCreate");
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
         * PreferenceKey call or BootCompleteReceiver call, do not start scan
         */
        //initReader(true);
        setNotification();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        LogUtils.i(TAG, "onDestroy");
        closeScan();
        // Unregister BroadcastReceiver
        unregisterReceiver(mSettingsReceiver);
        //unregisterReceiver(powerModeReceiver);
        super.onDestroy();
    }

    /**
     * ------------------------------------------------------CM60扫描接口回调实现-------------------------------------------------------------------
     */
    private static final int MSG_SEND_SCAN_RESULT = 1;
    private MessageHandler mMessageHandler = new MessageHandler();

    private static class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case MSG_SEND_SCAN_RESULT:
                    //处理扫描结果
                    String strResult;
                    String strAim;
                    byte[] data = (byte[]) msg.obj;
                    int i;
                    i=0;
                    while (data[i] != 0) {
                        i++;
                    }
                    strAim = new String((byte[]) msg.obj, 0, i);
                    LogUtils.e(TAG, "Aim:" + strAim);
                    strResult = new String((byte[]) msg.obj, 128, msg.arg2 - 128);
                    byte[] codeBytes = new byte[msg.arg2 - 128];
                    byte[] msgBytes = (byte[]) msg.obj;
                    System.arraycopy(msgBytes, 128, codeBytes, 0, codeBytes.length);
                    LogUtils.e(TAG, "Result:" + msg.arg2 + " body:" + strResult);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }

    /**
     * ------------------------------------------------------扫描头控制接口-------------------------------------------------------------------
     * <p>
     * 开启扫描头，连接扫描头
     */
    private void bootInitReader(){
        mExecutorService.execute(scanInitThread);
    }

    private void initReader(boolean iscamera) {
        // System Camera call
        boolean isOpen = mDefaultSharedPreferences.getBoolean(PreferenceKey.KEY_SWITCH_SCAN, false);
        if (iscamera && !isOpen) {
            // Camera Call init, if the switch don't open before, and do not open when exit camera
            return;
        }
        LogUtils.i(TAG, "initReader mIsInit = " + mIsInit);
        if (!mIsInit) {
            mIsInit = true;
            mDefaultSharedPreferences.edit().putBoolean(PreferenceKey.KEY_SWITCH_SCAN, true).apply();
            setNotification();
            // load previous settings
//            String timeout = mDefaultSharedPreferences.getString(PreferenceKey.KEY_DECODE_TIME, "99");
//            setDecodeTimeout(new Intent().putExtra(PreferenceKey.KEY_DECODE_TIME, timeout));
//            String lightMod = mDefaultSharedPreferences.getString(PreferenceKey.KEY_LIGHTS_CONFIG, "2");
//            setDecoderLightMod(new Intent().putExtra(PreferenceKey.KEY_LIGHTS_CONFIG, lightMod));
//            String illuminationLevel = mDefaultSharedPreferences.getString(PreferenceKey.KEY_ILLUMINATION_LEVEL, "4");
//            setIlluminationLevel(new Intent().putExtra(PreferenceKey.KEY_ILLUMINATION_LEVEL, illuminationLevel));
        }
    }

    private Runnable scanInitThread = new Runnable() {
        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            mSoftEngine = SoftEngine.getInstance(SoftScanService.this);
            if (mSoftEngine == null) {
                Log.e(TAG, "SoftEngine.getInstance return null");
            } else {
                if (mSoftEngine.initSoftEngine()) {
                    mSoftEngine.setScanningCallback(new SoftEngine.ScanningCallback() {
                        @Override
                        public void onScanningCallback(int eventCode, int param1, byte[] param2, int length) {
                            Message msg = Message.obtain(mMessageHandler, MSG_SEND_SCAN_RESULT, 0, length, param2);
                            LogUtils.e(TAG, "setScanningCallback = " + msg.obj);
                            msg.sendToTarget();
                        }
                    });
                    mSoftEngine.Open();
                    mExecutorService.execute(scanParamThread);
//                    mDefaultSharedPreferences.edit().putBoolean(PreferenceKey.KEY_SWITCH_SCAN, true).apply();
                    Log.e(TAG, "init time = " + (System.currentTimeMillis() - startTime));
                } else {
                    LogUtils.e(TAG, "initSoftEngine fail ");
                }
            }
        }
    };

    private Runnable scanParamThread = new Runnable() {
        @Override
        public void run() {
            try {
                Class<?> engineCodeClass = Class.forName(EngineCode.class.getName());
                engineCodeList = new ArrayList<>();
                for (EngineCodeMenu.Code1DName scanName : EngineCodeMenu.Code1DName.values()) {
                    EngineCode engineCode = new EngineCode();
                    engineCode.setName(scanName.getDname());
                    for (EngineCodeMenu.CodeParam param : EngineCodeMenu.CodeParam.values()) {
                        String data = SoftEngine.getInstance().ScanGet(scanName.getDname(), param.getParamName());
                        if (data != null) {
                            LogUtils.i(TAG, "scanParam " + param.getParamName() + ", data: " + data);
                            Method method = engineCodeClass.getDeclaredMethod("set" + param.getParamName(), String.class);
                            method.setAccessible(true);
                            method.invoke(engineCode, data);
                        }
                    }
                    engineCodeList.add(engineCode);
                    LogUtils.d(TAG, "engineCodeList add :" + engineCode.getName());
                }
                boolean isOpen = mDefaultSharedPreferences.getBoolean(PreferenceKey.KEY_SWITCH_SCAN, false);
                if (isOpen){
                    mIsInit = true;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 扫描超时时间设置
     */
    private void setDecodeTimeout(Intent intent) {
        if (!mIsInit) {
            Log.i(TAG, "setDecodeTimeout fail! Reader is not init, init first");
            return;
        }
        String stringExtra = intent.getStringExtra(PreferenceKey.KEY_DECODE_TIME);
        try {
            int decodeTimeout = Integer.parseInt(stringExtra);
//            bcr.setParameter(ParameterValue.DECODE_SESSION_TIMEOUT, decodeTimeout);
            mDefaultSharedPreferences.edit().putString(PreferenceKey.KEY_DECODE_TIME, stringExtra).apply();
        } catch (Exception e) {
            Log.i("Huang, SoftScanService", "setDecoderLightMod fail! Parameter error");
        }
    }

    /**
     * 扫描灯光模式设置
     */
    private void setDecoderLightMod(Intent intent) {
        if (!mIsInit) {
            Log.i(TAG, "setDecoderLightMod fail! Reader is not init, init first");
            return;
        }
        String lightMod = intent.getStringExtra(PreferenceKey.KEY_LIGHTS_CONFIG);
        switch (lightMod) {
            case "0":
                // 仅开启瞄准（关闭照明）
//                bcr.setParameter(ParameterValue.DECODING_ILLUMINATION, 0);
//                bcr.setParameter(ParameterValue.DECODE_AIMING_PATTERN, 1);
                mDefaultSharedPreferences.edit().putString(PreferenceKey.KEY_LIGHTS_CONFIG, lightMod).apply();
                break;
            case "1":
                // 仅开启照明（关闭瞄准）
//                bcr.setParameter(ParameterValue.DECODING_ILLUMINATION, 1);
//                bcr.setParameter(ParameterValue.DECODE_AIMING_PATTERN, 0);
                mDefaultSharedPreferences.edit().putString(PreferenceKey.KEY_LIGHTS_CONFIG, lightMod).apply();
                break;
            case "2":
                // 同时使用
//                bcr.setParameter(ParameterValue.DECODING_ILLUMINATION, 1);
//                bcr.setParameter(ParameterValue.DECODE_AIMING_PATTERN, 1);
                mDefaultSharedPreferences.edit().putString(PreferenceKey.KEY_LIGHTS_CONFIG, lightMod).apply();
                break;
            case "3":
                // 同时关闭
//                bcr.setParameter(ParameterValue.DECODING_ILLUMINATION, 0);
//                bcr.setParameter(ParameterValue.DECODE_AIMING_PATTERN, 0);
                mDefaultSharedPreferences.edit().putString(PreferenceKey.KEY_LIGHTS_CONFIG, lightMod).apply();
                break;
            default:
                Log.i("Huang, SoftScanService", "setDecoderLightMod fail! Parameter error");
                break;
        }
    }

    /**
     * 输入模式设置
     */
    private void setScanMode(Intent intent) {
        int mode = intent.getIntExtra("mode", 1);
        mDefaultSharedPreferences.edit().putString(PreferenceKey.KEY_INPUT_CONFIG, String.valueOf(mode)).apply();
    }

    /**
     * 照明光强度设置
     */
    private void setIlluminationLevel(Intent intent) {
        if (!mIsInit) {
            Log.i(TAG, "setIlluminationLevel fail! Reader is not init, init first");
            return;
        }
        String illuminationLevel = intent.getStringExtra(PreferenceKey.KEY_ILLUMINATION_LEVEL);
        try {
            Integer level = Integer.valueOf(illuminationLevel);
//            bcr.setParameter(ParameterValue.ILLUMINATION_POWER_LEVEL, level);
            mDefaultSharedPreferences.edit().putString(PreferenceKey.KEY_ILLUMINATION_LEVEL, illuminationLevel).apply();
        } catch (Exception e) {
            Log.i(TAG, "setIlluminationLevel fail! Parameter error");
        }
    }

    /**
     * 设置扫描按键
     */
    private void setScanKey(@NonNull Intent intent) {
        if (!mIsInit) {
            Log.i(TAG, "setScanKey fail! Reader is not init, init first");
            return;
        }
        boolean[] scanKeyArray = intent.getBooleanArrayExtra(PreferenceKey.KEY_SCAN_KEY);
        int requiredLen = 7;
        if (scanKeyArray == null || scanKeyArray.length != requiredLen) {
            Log.i(TAG, "setScanKey fail! Parameter error");
        } else {
            for (int i = 1; i <= requiredLen; i++) {
                boolean flag = scanKeyArray[i - 1];
                mDefaultSharedPreferences.edit().putBoolean(PreferenceKey.KEY_SCAN_KEY_FX + i, flag).apply();
            }
        }
    }

    /**
     * 设置扫描码制参数
     */
    private void setScanParam(Intent configIntent){
        String id = configIntent.getStringExtra("id");
        String param = configIntent.getStringExtra("param");
        String value = configIntent.getStringExtra("value");
        SoftEngine.getInstance(SoftScanService.this).ScanSet(id,param,value);
    }

    /**
     * 开启扫描
     */
    private void startScan() {
        if (mIsInit && !mIsScanning) {
            synchronized (SoftScanService.class) {
                // Start decode
                long startTime = System.currentTimeMillis();
                mSoftEngine.Open();
                mSoftEngine.StartDecode();
                Log.e(TAG, "触发扫码耗时 = " + (System.currentTimeMillis() - startTime));
            }
            mIsScanning = true;
        } else {
            Log.i(TAG, "startScan fail! Reader is not init or is busy now");
        }
    }

    /**
     * 停止扫描
     */
    private void stopScan() {
        if (mIsInit && mIsScanning) {
            mSoftEngine.StopDecode();
            // Key is released
            mIsScanning = false;
        } else {
            Log.i(TAG, "stopScan fail! Reader is not init or event don't work");
        }
    }

    /**
     * 断开扫描头连接
     */
    private void closeScan() {
        if (mIsInit) {
            mSoftEngine.StopDecode();
//            mSoftEngine.Close();
//            mSoftEngine.Deinit();
//            if (!iscamera) {
                // Camera call, don't reset scan switch stat
                mDefaultSharedPreferences.edit().putBoolean(PreferenceKey.KEY_SWITCH_SCAN, false).apply();
//            }
            mIsInit = false;
            setNotification();
        } else {
            Log.i(TAG, "closeScan fail! Reader is not init");
        }
    }

    /**
     * ------------------------------------------------------扫描头扫码结果处理-------------------------------------------------------------------
     * <p>
     * 发送扫码数据
     *
     * @param data 扫码数据
     */
    private void sendData(byte[] data, int sym) {
        String inputMod = mDefaultSharedPreferences.getString(PreferenceKey.KEY_INPUT_CONFIG, "1");
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
                    String charsetNum = mDefaultSharedPreferences.getString(PreferenceKey.KEY_RESULT_CHAR_SET, "1");
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

    private void setNotification() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isOpen = prefs.getBoolean("switch_scan_service", false);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(getString(R.string.notification_title));
        if (!isOpen){
            builder.setContentText(getString(R.string.scan_service_stop));
        }else {
            builder.setContentText(getString(R.string.notification_content));
        }
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        //添加事件
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        Intent intent = new Intent(this, ConfigActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, flags);
        builder.setContentIntent(pi);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.scan));
        Notification notification = builder.build();
        notification.flags |= FLAG_NO_CLEAR;
        assert mNotificationManager != null;
        //暂时去掉通知
        startForeground(R.string.app_name, notification);
    }
}
