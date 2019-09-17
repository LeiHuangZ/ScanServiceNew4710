package com.dawn.newlandscan;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dawn.decoderapijni.WheelScanStatus;
import com.dawn.decoderapijni.ScanService;
import com.dawn.newlandscan.widget.TopToolbar;

public class DebugTimeActivity extends Activity implements ScanService.ScanListener, View.OnClickListener {

    private static final String TAG = DebugTimeActivity.class.getCanonicalName();

    private TopToolbar topToolbar;

    private TextView tv_debug_scan_time;

    static SoundPool mSoundPool;
    static int soundId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_time);
        initView();
        initData();
    }

    public void initView(){

        tv_debug_scan_time = (TextView) findViewById(R.id.tv_debug_scan_time);
        tv_debug_scan_time.setMovementMethod(ScrollingMovementMethod.getInstance());
        topToolbar = (TopToolbar) findViewById(R.id.tb_topToolBar);
        topToolbar.setLeftTitleDrawable(R.drawable.ic_toolbar_back);
        topToolbar.setRightTitleDrawable(R.drawable.ic_toolbar_help);
        topToolbar.setMainTitle(R.string.menu_item_debug_time);
        topToolbar.setMenuToolBarListener(new TopToolbar.MenuToolBarListener() {
            @Override
            public void onToolBarClickLeft(View v) {
                finish();
            }

            @Override
            public void onToolBarClickRight(View v) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.debug_scan_fab);
        fab.setOnClickListener(this);
    }

    public void initData(){
        mSoundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 10);
        soundId = mSoundPool.load(getApplicationContext(),R.raw.dingdj5, 10);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ScanService.setScanListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ScanService.stopScan();
        ScanService.setScanListener(null);
    }





    @Override
    public void onScanListener(Message msg) {
        mSoundPool.play(soundId, 1, 1, 0, 0, (float) 1.0); // 播放声音
        scanStatus = false;
        showTimeResult();
    }

    static boolean scanStatus = false;

    public void fabClickEvent(){
        if(!scanStatus){
            ScanService.startScan();
            scanStatus = true;
        } else {
            ScanService.stopScan();
            showTimeResult();
            scanStatus = false;
        }
    }

    public void showTimeResult(){
        WheelScanStatus scanStatus = ScanService.getScanWheelTime();
        String showText = "count:" + scanStatus.count + "\r\n" +
                "time:" + scanStatus.wheelDecodeTime/1000 + "ms\r\n";

        for(int i=0; i< scanStatus.mList.size();i++){
            showText = showText + scanStatus.mList.get(i).toString() + "\r\n";
        }

        tv_debug_scan_time.setText(showText);
        tv_debug_scan_time.scrollTo(0,0);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.debug_scan_fab:
                Log.d(TAG,"onClick:" + scanStatus);
                fabClickEvent();
                break;
                default:
                    break;
        }
    }
}
