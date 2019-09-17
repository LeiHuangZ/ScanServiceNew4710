package com.dawn.newlandscan;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dawn.decoderapijni.ScanService;
import com.dawn.newlandscan.widget.TopToolbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DeviceUpdateActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = DeviceUpdateActivity.class.getCanonicalName();

    private TopToolbar topToolbar;
    private Button btn_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_update);
        initView();
        initData();
    }

    public void initView(){

        topToolbar = (TopToolbar) findViewById(R.id.tb_topToolBar);
        topToolbar.setRightTitleVisiable(View.GONE);
        topToolbar.setMainTitle(R.string.menu_item_sensor_update);
        topToolbar.setMenuToolBarListener(new TopToolbar.MenuToolBarListener() {
            @Override
            public void onToolBarClickLeft(View v) {
                finish();
            }

            @Override
            public void onToolBarClickRight(View v) {

            }
        });

        btn_update = (Button) findViewById(R.id.btn_update);
        btn_update.setOnClickListener(this);

    }

    public void initData(){

    }

    private void startUpdate(){
        AssetManager assetManager = getApplicationContext().getAssets();

        try {
//            InputStream inputStream = assetManager.open("Krnl_CM60_V1.00.002.bin");
            InputStream inputStream = assetManager.open("CM60_S.bin");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "ISO-8859-1");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            int length;
            char[] updateChar = new char[45*1024];
            length = bufferedReader.read(updateChar,0,updateChar.length);
            bufferedReader.close();
            inputStreamReader.close();
            Log.d(TAG,"length:" + length + "  " + updateChar[5]);
            int ret = ScanService.scanUpdate(updateChar,length);
            if(ret != 0){
                Toast.makeText(getApplicationContext(),"升级 成功 重启设备.....", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(),"升级 失败", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_update:
                startUpdate();
                break;

                default:
                    break;
        }
    }
}
