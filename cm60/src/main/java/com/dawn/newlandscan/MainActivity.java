package com.dawn.newlandscan;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dawn.decoderapijni.ScanService;
import com.dawn.newlandscan.widget.TopToolbar;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, ScanService.ScanListener {

    private static final String TAG = "NewLandScan";
    private Button btn_scan;
    private TextView tv_scanResult;
    private TextView tv_scanType;
    private TextView tv_scanTime;
    private TextView tv_sanTen;
    private final int SINGLE_MODE = 0;
    private final int CONTINUE_MODE = 1;

    private int mDecodeMode = SINGLE_MODE;
    private Random random = new Random();
    private final int MSG_SEND_SCAN_RESULT = 1000;
    private final int MSG_START_PREVIEW = 1002;
    private static long decodeTime = 0;					// Time for decode

    static SoundPool mSoundPool;
    static int soundId = 1;
    static int SoundCount = 1;

    private TopToolbar topToolbar;

    private Intent serviceIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        stopService(serviceIntent);
        ScanService.deInit();
        super.onDestroy();

    }

    private void initView(){

        serviceIntent = new Intent(MainActivity.this, ScanService.class);
        startService(serviceIntent);

        tv_scanResult = (TextView) findViewById(R.id.tv_scan_result);
        tv_scanResult.setMovementMethod(ScrollingMovementMethod.getInstance());
        tv_scanTime = (TextView) findViewById(R.id.tv_scan_time);
        tv_scanType = (TextView) findViewById(R.id.tv_scan_type);
        tv_sanTen = (TextView) findViewById(R.id.tv_san_len);
        btn_scan = (Button) findViewById(R.id.btn_scan);
        btn_scan.setOnTouchListener(this);


        topToolbar = (TopToolbar) findViewById(R.id.tb_topToolBar);
        topToolbar.setLeftTitleDrawable(R.drawable.ic_toolbar_picture);
        topToolbar.setRightTitleDrawable(R.drawable.ic_toolbar_setting);
        topToolbar.setMainTitle(R.string.main_page_title);
        topToolbar.setMenuToolBarListener(new TopToolbar.MenuToolBarListener() {
            @Override
            public void onToolBarClickLeft(View v) {
                Intent imageShowIntent = new Intent(MainActivity.this,ImageShowActivity.class);
                startActivity(imageShowIntent);
            }

            @Override
            public void onToolBarClickRight(View v) {
                Intent settingIntent = new Intent(MainActivity.this,SettingActivity.class);
                startActivity(settingIntent);
            }
        });
    }

    private void initData(){
        mSoundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 10);
        soundId = mSoundPool.load(getApplicationContext(),R.raw.dingdj5, 10);
        ScanService.scanInit(getApplicationContext());
        ScanService.setScanListener(this);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN :
                decodeTime = System.currentTimeMillis();
                ScanService.startScan();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                ScanService.stopScan();
                break;

            default:
                break;
        }

        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == 301){
            int action = event.getAction();
            switch (action){
                case MotionEvent.ACTION_DOWN :
                    decodeTime = System.currentTimeMillis();
                    ScanService.startScan();
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    ScanService.stopScan();
                    break;

                default:
                    break;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ScanService.setScanListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ScanService.setScanListener(null);
    }

    @Override
    public void onScanListener(Message msg) {

        mSoundPool.play(soundId, 1, 1, 0, 0, (float) 1.0); // 播放声音
        String strResult;
        String strAim;
        byte[] data = (byte[])msg.obj;

        int i = 0;
        for(i=0;data[i] != 0;i++){ }
        strAim = new String((byte[]) msg.obj, 0,i);
        Log.d(TAG,"Aim:" + strAim);
        strResult = new String((byte[]) msg.obj, 128,msg.arg2-128);
        Log.d(TAG,"Result:" + msg.arg2 + " body:" + strResult);


        if (strResult != null)
        {
            tv_scanResult.setText(strResult);
            tv_scanResult.setTextColor(Color.argb(255, random.nextInt(256),
                    random.nextInt(256), random.nextInt(256)));
            tv_scanType.setText(strAim);
            tv_scanTime.setText(String.valueOf(System.currentTimeMillis() - decodeTime));
            tv_sanTen.setText("" + strResult.length());
            decodeTime = System.currentTimeMillis();
        }
    }
}
