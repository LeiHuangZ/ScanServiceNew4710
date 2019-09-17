package com.dawn.decoderapijni;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ssn.decoderapijni.R;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ScanService extends Service{

    private static final String TAG = ScanService.class.getCanonicalName();


    /**
     * //ACTION_SCAN_CMD
     */
    public static final String ACTION_SCAN = "com.rfid.SCAN_CMD";
    /**
     * //ACTION_SCAN_INIT
     */
    public static final String ACTION_SCAN_INIT = "com.rfid.SCAN_INIT";
    /**
     * //ACTION_SCAN_TIME
     */
    public static final String ACTION_SCAN_TIME = "com.rfid.SCAN_TIME";
    /**
     * //ACTION_SCAN_CONFIG
     */
    public static final String ACTION_SCAN_CONFIG = "com.rfid.SCAN_CONFIG";
    /**
     * //ACTION_SCAN_SET_TIMEOUT
     */
    public static final String ACTION_SCAN_SET_TIMEOUT = "com.rfid.SCAN_SET_TIMEOUT";
    /**
     * //ACTION_KILL_SCAN
     */
    public static final String ACTION_KILL_SCAN = "com.rfid.KILL_SCAN";
    /**
     * //ACTION_CLOSE_SCAN
     */
    public static final String ACTION_CLOSE_SCAN = "com.rfid.CLOSE_SCAN";
    /**
     * //ACTION_SET_SCAN_MODE
     */
    public static final String ACTION_SET_SCAN_MODE = "com.rfid.SET_SCAN_MODE";
    /**
     * //ACTION_ENABLE_SYM
     */
    public static final String ACTION_ENABLE_SYM = "com.rfid.ENABLE_SYM";
    /**
     * //ACTION_DISENABLE_SYM
     */
    public static final String ACTION_DISENABLE_SYM = "com.rfid.DISENABLE_SYM";
    /**
     * 设置按键控制
     */
    public static final String ACTION_KEY_SET= "com.rfid.KEY_SET";
    /**
     * //扫描结果广播返回
     */
    public static final String SCAN_RESULT = "com.rfid.SCAN";
    /**
     * //扫描结果字符编码
     */
    public static final String ACTION_SCAN_CHAR_SET = "com.rfid.SCAN_CHAR_SET";

    private static final int MSG_SEND_SCAN_RESULT = 1000;
    private final int MSG_START_PREVIEW = 1002;

    private static final int SCAN_SET_LOG_LEVEL = 0x0157;
    private static final int SCAN_SET_SCAN_UPDATE = 0x0122;
    private final static int SCAN_SET_DECODE_IMAGE = 0x10E1;
    public static ScanListener mScanListener;
    public static List<EngineCode> engineCodeList;
    public static boolean initStatus = false;
    private static Context mContext;

    private SharedPreferences mPrefs;
    static SoundPool mSoundPool;
    static int soundId = 1;
    /**
     * 接收外部广播
     */
    public ScanBroadcast scanBroadcast;
    /**
     * 按键是否松开
     */
    private static boolean isUp = true;

    public ScanService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate方法被调用!");
        mContext = this ;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSoundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 10);
        soundId = mSoundPool.load(getApplicationContext(), R.raw.dingdj5, 10);
        //注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SCAN);
        filter.addAction(ACTION_SCAN_INIT);
        filter.addAction(ACTION_KILL_SCAN);
        filter.addAction(ACTION_SCAN_TIME);
        filter.addAction(ACTION_SCAN_CONFIG);
        filter.addAction(ACTION_SCAN_SET_TIMEOUT);
        filter.addAction(ACTION_SET_SCAN_MODE);
        filter.addAction(ACTION_CLOSE_SCAN);
        filter.addAction(ACTION_ENABLE_SYM);
        filter.addAction(ACTION_KEY_SET);//ACTION_SCAN_CHAR_SET
        filter.addAction(ACTION_SCAN_CHAR_SET);
        scanBroadcast = new ScanBroadcast();
        registerReceiver(scanBroadcast, filter);
    }


    @Override
    public void onDestroy() {
        Log.v("Huang, SoftScanService", "onDestroy()");
        super.onDestroy();
        unregisterReceiver(scanBroadcast);
//        unregisterReceiver(powerModeReceiver);
    }

    public static void deInit() {
        SoftEngine.getInstance().StopDecode();
        SoftEngine.getInstance().Close();
        SoftEngine.getInstance().Deinit();
        mContext = null;
        initStatus = false;

    }

    private static boolean isRunnig = false ;
    private static boolean keyDown = false ;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(null, flags, startId);
        }
        if(!initStatus){
             ScanInitThread.start();
        }

        //按键是否按下
        keyDown = intent.getBooleanExtra("keyDown", false);
//        Log.e(TAG, "onStartCommand ...... ") ;
        if (keyDown  && isUp) {
//            Log.e(TAG, "++++ startScan +++++ ...... ") ;
            isUp = false;
            //创建扫描线程
            startScan();
        }
        //松开按键停止扫描
        else if (isRunnig && !keyDown) {
            isUp = true;
//            Log.e(TAG, "++++ stopScan +++++ ...... ") ;
            stopScan();

        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind方法被调用!");
        return null;
    }

    @SuppressLint("HandlerLeak")
    private Handler mMessageHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                        case MSG_SEND_SCAN_RESULT:
                    SoftEngine.getInstance().StopDecode();
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    //扫码模式
                    String mode = prefs.getString("inputConfig", "0");
                    //字符编码
                    String charSet = prefs.getString("result_char_set", "1");
                    //处理扫描结果
                    String strResult;
                    String strAim;
                    byte[] data = (byte[])msg.obj;

                    int i = 0;
                    for(i=0;data[i] != 0;i++){ }
                    strAim = new String((byte[]) msg.obj, 0,i);
                    Log.e(TAG,"Aim:" + strAim);
                    strResult = new String((byte[]) msg.obj, 128,msg.arg2-128);
                    byte[] codeBytes = new byte[msg.arg2 - 128];
                    byte[] msgBytes = (byte[])msg.obj ;
                    System.arraycopy(msgBytes, 128, codeBytes, 0, codeBytes.length);
                    Log.e(TAG,"Result:" + msg.arg2 + " body:" + strResult);
                    // 播放声音
                    mSoundPool.play(soundId, 1, 1, 0, 0, (float) 1.0);
                    String editTextMode = "1";
                    //判断广播方式还是直接输入方式
                    if("0".endsWith(mode)){
                        sendScanResult(codeBytes,(byte)0x00);
                    }else{
                        if (editTextMode.equals(charSet)) {
                            //后台输入
                            sendToInput(new String(codeBytes));
                        } else {
                            try {
                                //后台输入
                                sendToInput(new String(codeBytes, "gbk"));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                    break;

                default:
                    break;
            }
        }
    };



    private  Thread ScanInitThread = new Thread(new Runnable() {
        @Override
        public void run() {
            long currTime = System.currentTimeMillis() ;
            if(SoftEngine.getInstance(mContext).initSoftEngine()){

                SoftEngine.getInstance().setScanningCallback(new SoftEngine.ScanningCallback() {
                    @Override
                    public void onScanningCallback(int eventCode, int param1, byte[] param2, int length) {

                        Message msg = Message.obtain(mMessageHandler, MSG_SEND_SCAN_RESULT, 0, length, param2);
                        Log.e(TAG, "setScanningCallback = "+ msg.obj);
                        msg.sendToTarget();
                    }
                });
                SoftEngine.getInstance().Open();
                ScanParamThread.start();
                initStatus = true;
            }
            Log.e(TAG, "init time = " + (System.currentTimeMillis() - currTime));
        }
    });

    private static Thread ScanParamThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Class<?> engineCodeClass = Class.forName(EngineCode.class.getName());
                engineCodeList = new ArrayList<>();
                for (EngineCodeMenu.Code1DName scanName : EngineCodeMenu.Code1DName.values()){
                    EngineCode engineCode = new EngineCode();
                    engineCode.setName(scanName.getDname());
                    for (EngineCodeMenu.CodeParam param : EngineCodeMenu.CodeParam.values()) {
                        String data = SoftEngine.getInstance().ScanGet(scanName.getDname(), param.getParamName());
                        if (data != null) {
                            Method method = engineCodeClass.getDeclaredMethod("set" + param.getParamName(), String.class);
//                            Log.d(TAG,"engineCodeList set :" +param.getParamName() );
                            method.setAccessible(true);
                            method.invoke(engineCode, data);
                        }
                    }

                    Log.d(TAG,"engineCodeList add :" + engineCode.getName());
                    engineCodeList.add(engineCode);
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
    });

    public static void scanInit(Context context){
        mContext = context;
    }

    public  void scanReInit(){
        SoftEngine.getInstance().StopDecode();
        SoftEngine.getInstance().Close();
        SoftEngine.getInstance().Deinit();
        ScanInitThread.start();

    }

    public static void startScan(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                long currTime = System.currentTimeMillis() ;
                SoftEngine.getInstance(mContext).Open();
                SoftEngine.getInstance(mContext).StartDecode();
                Log.e(TAG, "触发扫码耗时 = " + (System.currentTimeMillis() - currTime));
                isRunnig = true ;
//                if (!keyDown) {
//                    isUp = true ;
//                    stopScan();
//                }
            }
        }).start();
    }


    public static void stopScan(){
        SoftEngine.getInstance(mContext).StopDecode();
        isRunnig = false ;
//        SoftEngine.getInstance(mContext).Close();
    }

    public static void setScanListener(ScanListener scanListener){
        mScanListener = scanListener;
    }

    public static void setScanParam(String id,String param, String value){
        SoftEngine.getInstance(mContext).ScanSet(id,param,value);
    }

    public static byte[] getImageLast(){
       return SoftEngine.getInstance().getLastImage();
    }

    public static SensorParam getSensorParam(int mode){
        return SoftEngine.getInstance(mContext).getSensorParam(mode);
    }

    public static void setSensorParam(int mode,SensorParam param){
        SoftEngine.getInstance(mContext).setSensorParam(mode,param);
    }

    public static WheelScanStatus getScanWheelTime(){
        int[] timeData = SoftEngine.getInstance(mContext).getScanWheelTime();
        WheelScanStatus wheelScanStatus = new WheelScanStatus(timeData[0],timeData[1]);
        Log.d(TAG,"getScanWheelTime: " + timeData[0] + "  " + timeData[1]);
        for(int i=0; i < wheelScanStatus.count; i++ ){
            OnceStatus onceStatus =new OnceStatus(timeData[2+ i*3],timeData[3+ i*3],timeData[4 + i*3]);
            wheelScanStatus.mList.add(onceStatus);
        }
        return wheelScanStatus;
    }

    private static final int SCANNER_IOCTRL_I2C = 0x0156;

    public static int scanI2cIOCtrl(int param1, Object obj){
        return  SoftEngine.getInstance(mContext).setSoftEngineIOCtrlEx(SCANNER_IOCTRL_I2C, param1, obj);
    }

    public static int scanI2cRead(int addr,int length, int[] data){
        int[] send_data= new int[length +2];
        send_data[0] = 0x00;
        send_data[1] = (byte) length;
        int ret = SoftEngine.getInstance(mContext).setSoftEngineIOCtrlEx(SCANNER_IOCTRL_I2C,addr,send_data);
        Log.d(TAG,"scanI2cRead: " + send_data[2] + "  " + send_data[3]);
        if(data != null){
            for(int i = 0;i<data.length && i<length;i++){
                data[i] = send_data[2+i];
            }
        }
        return  ret;
    }

    public static int scanI2cWrite(int addr,int length, int[] data){
        int[] send_data= new int[length + 2];
        send_data[0] = 0x01;
        send_data[1] = (byte) length;
        if(data != null){
            for(int i = 0;i<data.length && i<length;i++){
                send_data[2+i] = data[i];
            }
        }

        int ret = SoftEngine.getInstance(mContext).setSoftEngineIOCtrlEx(SCANNER_IOCTRL_I2C,addr,send_data);

        return  ret;
    }

    public static void scanSetDecodeImage(byte[] data,int length){
        SoftEngine.getInstance(mContext).setSoftEngineIOCtrlEx(SCAN_SET_DECODE_IMAGE, length, data);
    }

    public static void scanSetLog(boolean status) {
        if(status){
            SoftEngine.getInstance(mContext).setSoftEngineIOCtrlEx(SCAN_SET_LOG_LEVEL,0xFF,null);
        } else {
            SoftEngine.getInstance(mContext).setSoftEngineIOCtrlEx(SCAN_SET_LOG_LEVEL,0x00,null);
        }
    }

    public static int  scanUpdate(char[] data, int length){
        return SoftEngine.getInstance(mContext).setSoftEngineIOCtrlEx(SCAN_SET_SCAN_UPDATE, length, data);
    }

    public interface ScanListener{
        public void  onScanListener(Message msg);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 将扫描结果以广播的形式发回
     */
    private void sendScanResult(byte[] data, byte codeID) {
        Intent intent = new Intent();
        intent.putExtra("data", data);
        intent.putExtra("code_id", codeID);
        intent.setAction(SCAN_RESULT);
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



    /**
     * 用于接收扫描各种指令和关闭服务的指令
     */
    class ScanBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //存储相机标识
            boolean isCamera = intent.getBooleanExtra("iscamera", false);
            getSharedPreferences("HwSoftScan", Context.MODE_PRIVATE).edit().putBoolean("iscamera", isCamera).apply();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String action = intent.getAction();
            //默认为输入框模式
//            int mode = intent$getIntExtra("mode", 1);
            //按键是否按下
            //根据不同的action执行不同的操作
            if (ACTION_SCAN.equals(action)) {
                //创建扫描线程,当前不使用相机和已经初始化
                if (initStatus && !isCamera) {
                    startScan();
                }

            }
            //彻底关闭服务
            else if (ACTION_KILL_SCAN.equals(action)) {
                /*                */
                deInit();
                ScanService.this.stopSelf();
                //存储开启状态
                getSharedPreferences("HwSoftScan", Context.MODE_PRIVATE).edit().putBoolean("isInit", false).apply();

            }
            //关闭服务
            else if (ACTION_CLOSE_SCAN.equals(action)) {
                /*
                Log.v("Huang, ScanBroadcast", "ACTION_CLOSE_SCAN");
                //huang add 20180903
                if (Util.mIsPad && isCamera) {
                    stopService(new Intent(SoftScanService.this, FloatService.class));
                }
                //end
                if (mDecoder != null) {
                    try {
//                        mDecoder$stopScanning();
                        boolean isOpen = prefs.getBoolean("switch_scan_service", false);
                        if (!isOpen || isCamera) {
                            mDecoder.disconnectDecoderLibrary();
                            mDecoder = null;
                        }
                    } catch (DecoderException e) {
                        e.printStackTrace();
                    }
                }
                if (wm != null) {
                    wm.removeView(mFloatLayout);
                    wm = null;
                }
                //设置扫描服务开头为不可用
//                SharedPreferences$Editor edit = prefs$edit() ;
//                edit$putBoolean("switch_scan_service", false);
//                edit$commit();
                //存储开启状态
                getSharedPreferences("HwSoftScan", Context.MODE_PRIVATE).edit().putBoolean("isInit", false).apply();
                */
            }
            //设置参数
            else if (ACTION_SCAN_CONFIG.equals(action)) {
                /*
                if (mDecoder != null) {
                    setSymbologySettings();
                    try {
                        setOcrSettings();
                        setScanningSettings();//设置扫描灯光
                    } catch (DecoderException e) {
                        e.printStackTrace();
                    }
                }
*/
            }
            //扫描初始化
            else if (ACTION_SCAN_INIT.equals(action)) {

                if (isCamera){
                    getSharedPreferences("HwSoftScan", Context.MODE_PRIVATE).edit().putBoolean("iscamera", false).apply();
                    boolean isOpen = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("switch_scan_service", false);
                    if (!isOpen){
                        return;
                    }
                }else{
                    //设置模式为广播模式0
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString("inputConfig", "0");
                    edit.apply();
                }
                if(!initStatus){
                    ScanInitThread.start();
                }
                //存储开启状态
                getSharedPreferences("HwSoftScan", Context.MODE_PRIVATE).edit().putBoolean("isInit", true).apply();
            }
            //设置输入模式
            else if (ACTION_SET_SCAN_MODE.equals(action)) {
//                timeOut = intent$getIntExtra("timeout", 3000);
                //0为广播模式
                int mode = intent.getIntExtra("mode", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("inputConfig", "" + mode);
                edit.apply();
//                setSymbologySettings();
            }
            //设置扫码超时
            else if (ACTION_SCAN_TIME.equals(action)) {
                Log.e("Huang, ScanBroadcast", ACTION_SCAN_TIME);
                String time = intent.getStringExtra("time");
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("decode_time_limit", time);
                edit.apply();
//                decode_time_limit

            }

            //设置扫码结果字符编码
            else if (ACTION_SCAN_CHAR_SET.equals(action)) {
//                Log.v("Huang, ScanBroadcast", "ACTION_SCAN_TIME");
                Log.e("Huang, ScanBroadcast", ACTION_SCAN_CHAR_SET);
                String char_set = intent.getStringExtra("char_set");
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("result_char_set", char_set);
                edit.apply();
//                decode_time_limit

            }
            //设置码制
            else if (ACTION_ENABLE_SYM.equals(action)) {
                String symbology = intent.getStringExtra("symbology");
                boolean isOpen = intent.getBooleanExtra("enable", false);
                SharedPreferences.Editor edit = prefs.edit();
                if(symbology != null){
                    edit.putBoolean(symbology, isOpen);
                    edit.apply();
//                    setSymbologySettings();
                }

            }
            //关闭码制
            else if (ACTION_DISENABLE_SYM.equals(action)) {
                Log.v("Huang, ScanBroadcast", "ACTION_DISENABLE_SYM");
            }
            //按键控制设置
            else if (ACTION_KEY_SET.equals(action)){
                Log.v("Huang, ScanBroadcast", "ACTION_KEY_SET");
                String keyName = intent.getStringExtra("keyName");
                SharedPreferences.Editor edit = mPrefs.edit();
                if (keyName.contains("key_f1")) {
                    boolean f1Enable = intent.getBooleanExtra("key_f1", true);
                    edit.putBoolean("key_f1", f1Enable);
                }
                if (keyName.contains("key_f2")) {
                    boolean f2Enable = intent.getBooleanExtra("key_f2", true);
                    edit.putBoolean("key_f2", f2Enable);
                }
                if (keyName.contains("key_f3")) {
                    boolean f3Enable = intent.getBooleanExtra("key_f3", true);
                    edit.putBoolean("key_f3", f3Enable);
                }
                if (keyName.contains("key_f4")) {
                    boolean f4Enable = intent.getBooleanExtra("key_f4", true);
                    edit.putBoolean("key_f4", f4Enable);
                }
                if (keyName.contains("key_f5")) {
                    boolean f5Enable = intent.getBooleanExtra("key_f5", true);
                    edit.putBoolean("key_f5", f5Enable);
                }
                if (keyName.contains("key_f6")) {
                    boolean f6Enable = intent.getBooleanExtra("key_f6", true);
                    edit.putBoolean("key_f6", f6Enable);
                }
                if (keyName.contains("key_f7")) {
                    boolean f7Enable = intent.getBooleanExtra("key_f7", true);
                    edit.putBoolean("key_f7", f7Enable);
                }
                edit.apply();

            }
        }
    }


}
