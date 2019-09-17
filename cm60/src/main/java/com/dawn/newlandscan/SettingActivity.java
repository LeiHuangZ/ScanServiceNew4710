package com.dawn.newlandscan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.dawn.newlandscan.widget.TopToolbar;

public class SettingActivity extends Activity implements View.OnClickListener {

    private  static final String TAG = SettingActivity.class.getCanonicalName();

    private TopToolbar topToolbar;

    private ImageButton btn_device_setting;
    private ImageButton btn_device_info;
    private ImageButton btn_device_debug;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        initData();
    }

    public void initView(){

        btn_device_info = (ImageButton) findViewById(R.id.btn_device_info);
        btn_device_setting = (ImageButton) findViewById(R.id.btn_device_setting);
        btn_device_setting.setOnClickListener(this);
        btn_device_info.setOnClickListener(this);
        btn_device_debug = (ImageButton) findViewById(R.id.btn_device_debug);
        btn_device_debug.setOnClickListener(this);

        topToolbar = (TopToolbar) findViewById(R.id.tb_topToolBar);
        topToolbar.setLeftTitleDrawable(R.drawable.ic_toolbar_back);
        topToolbar.setRightTitleDrawable(R.drawable.ic_toolbar_help);
        topToolbar.setMainTitle(R.string.setting_page_title);
        topToolbar.setMenuToolBarListener(new TopToolbar.MenuToolBarListener() {
            @Override
            public void onToolBarClickLeft(View v) {
                finish();
            }

            @Override
            public void onToolBarClickRight(View v) {

            }
        });


    }

    public void initData(){

    }

    private void startDeviceSettingActivity(){
        Intent deviceSettingIntent = new Intent(SettingActivity.this,DeviceSettingActivity.class);
        startActivity(deviceSettingIntent);
    }

    public void startDeviceInfoActivity(){
        Intent deviceInfoIntent = new Intent(SettingActivity.this, DeviceInfoActivity.class);
        startActivity(deviceInfoIntent);
    }

    public void startScanDebugActivity(){
        Intent scanDebugIntent = new Intent(SettingActivity.this, DebugActivity.class);
        startActivity(scanDebugIntent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_device_setting:
                startDeviceSettingActivity();
                break;
            case R.id.btn_device_info:
                startDeviceInfoActivity();
                break;
            case R.id.btn_device_debug:
                startScanDebugActivity();
                break;
                default:
                    break;
        }
    }
}
