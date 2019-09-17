package com.dawn.newlandscan;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.dawn.newlandscan.base.BaseActivity;
import com.dawn.decoderapijni.ScanService;

public class DebugScanActivity extends BaseActivity implements View.OnClickListener, ScanService.ScanListener{

    private static final String TAG = DebugScanActivity.class.getCanonicalName();

    private static final int SINGLE_MODE = 0;
    private static final int CONTINUOUS_MODE = 1;

    FloatingActionButton fab;

    private boolean scanStatus = false;
    private int scanMode = SINGLE_MODE;
    private boolean scanMusic = false;
    static SoundPool mSoundPool;
    static int soundId = 1;

    private CheckBox checkBox_mode;
    private CheckBox checkBox_music;


    private TextView tv_debug_scan_detail;

    @Override
    public int layoutId() {
        return R.layout.activity_debug_scan;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        this.initTitle(R.string.menu_item_debug_scan);
        this.initDrawable(R.drawable.ic_toolbar_back,0);

        fab = (FloatingActionButton) findViewById(R.id.debug_scan_fab);
        fab.setOnClickListener(this);

        checkBox_mode = (CheckBox) findViewById(R.id.cb_continuous);
        checkBox_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    scanMode = CONTINUOUS_MODE ;
                } else {
                    scanMode = SINGLE_MODE ;
                }
            }
        });

        checkBox_music = (CheckBox) findViewById(R.id.cb_music);
        checkBox_music.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    scanMusic = true ;
                } else {
                    scanMusic = false ;
                }
            }
        });

        tv_debug_scan_detail = (TextView) findViewById(R.id.tv_debug_scan_detail);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mSoundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 10);
        soundId = mSoundPool.load(getApplicationContext(),R.raw.dingdj5, 10);
    }

    @Override
    public void onToolBarClickLeft(View v) {
        finish();
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


    private void startScan(){
        fab.setImageResource(R.drawable.ic_menu_stop);
        ScanService.startScan();
        scanStatus = true;
    }

    private void stopScan(){
        ScanService.stopScan();
        fab.setImageResource(R.drawable.ic_media_pause);
        scanStatus = false;
    }

    @Override
    public void onClick(View v) {

        if(scanStatus){
            stopScan();
        } else {
            startScan();
        }

    }

    private int count = 0;
    @Override
    public void onScanListener(Message msg) {
        if(scanMusic){
            mSoundPool.play(soundId, 1, 1, 0, 0, (float) 1.0); // 播放声音
        }
        count++;
        tv_debug_scan_detail.setText("count:" + count);
        if(scanMode == CONTINUOUS_MODE){
            startScan();
        } else {
            stopScan();
        }
    }


}
