package com.dawn.newlandscan;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.dawn.newlandscan.base.BaseActivity;
import com.dawn.decoderapijni.ScanService;

public class DebugLogActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {


    private static final String TAG = DebugLogActivity.class.getCanonicalName();

    private Switch sw_log_setting;

    @Override
    public int layoutId() {
        return R.layout.activity_debugf_log;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        this.initTitle(R.string.menu_item_debug_log);
        this.initDrawable(R.drawable.ic_toolbar_back, 0);

        sw_log_setting = (Switch) findViewById(R.id.sw_log_setting);
        sw_log_setting.setOnCheckedChangeListener(this);

    }

    @Override
    public void onToolBarClickLeft(View v) {
        finish();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            ScanService.scanSetLog(true);
        } else {
            ScanService.scanSetLog(false);
        }
    }
}
