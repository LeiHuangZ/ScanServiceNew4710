package com.dawn.newlandscan;

import android.os.Bundle;
import android.view.View;

import com.dawn.decoderapijni.ScanCamera;
import com.dawn.newlandscan.base.BaseActivity;

public class DebugCameraActivity extends BaseActivity {

    private static final String TAG = DebugScanActivity.class.getCanonicalName();

    @Override
    public int layoutId() {
        return R.layout.activity_debug_camera;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        this.initTitle(R.string.menu_item_debug_camera);
        this.initDrawable(R.drawable.ic_toolbar_back, 0);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        ScanCamera.getInstance().cameraInit();
        ScanCamera.getInstance().cameraOpen(1);
        ScanCamera.getInstance().cameraStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScanCamera.getInstance().cameraStop();
        ScanCamera.getInstance().cameraClose();
    }

    @Override
    public void onToolBarClickLeft(View v) {
        finish();
    }


}
