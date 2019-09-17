package com.dawn.newlandscan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.dawn.newlandscan.adapter.MenuItem;
import com.dawn.newlandscan.adapter.MenuItemAdapter;
import com.dawn.newlandscan.widget.TopToolbar;

import java.util.ArrayList;
import java.util.List;

public class DeviceSettingActivity extends Activity implements AdapterView.OnItemClickListener {

    private static final String TAG = DeviceSettingActivity.class.getCanonicalName();

    private TopToolbar topToolbar;
    private MenuItemAdapter mMenuItemAdapter;
    private ListView gv_menuItem;
    private List<MenuItem> mMenuItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_setting);
        initView();
        initData();
    }

    public void initView(){

        gv_menuItem = (ListView) findViewById(R.id.gv_deviceSettingMenu);
        gv_menuItem.setOnItemClickListener(this);

        topToolbar = (TopToolbar) findViewById(R.id.tb_topToolBar);
        topToolbar.setRightTitleVisiable(View.GONE);
        topToolbar.setMainTitle(R.string.deviceSetting_page_title);
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
        mMenuItemList = new ArrayList<>();
        String[] itemTexts = {
                getResources().getString(R.string.menu_item_code_set),
                getResources().getString(R.string.menu_item_sensor_set),
                getResources().getString(R.string.menu_item_sensor_update),
        };
        int[] itemDrawables = {
                R.drawable.ic_menu_code_set,
                R.drawable.ic_menu_sensor_set,
                R.drawable.ic_menu_update,
        };

        if(itemTexts != null){
            for(int i = 0; i < itemTexts.length; i++){
                MenuItem item = new MenuItem(itemDrawables[i], itemTexts[i], 0);
                mMenuItemList.add(item);
            }
        }
        mMenuItemAdapter = new MenuItemAdapter(getApplicationContext(), mMenuItemList);

        gv_menuItem.setAdapter(mMenuItemAdapter);
    }

    public static class MenuItemClick {
        public static final int SET_CODE = 0;
        public static final int SET_SENSOR = 1;
        public static final int SET_UPDATE  = 2;
    }

    public void startCodeSetActivity(){
        Intent codeSetIntent = new Intent(DeviceSettingActivity.this, CodeSetActivity.class);
        startActivity(codeSetIntent);
    }
    public void startSensorSetActivity(){
        Intent sensorSetIntent = new Intent(DeviceSettingActivity.this, SensorSettingActivity.class);
        startActivity(sensorSetIntent);
    }

    public void startDeviceUpdateActivity(){
        Intent deviceUpdatIntent = new Intent(DeviceSettingActivity.this, DeviceUpdateActivity.class);
        startActivity(deviceUpdatIntent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case MenuItemClick.SET_CODE:
                startCodeSetActivity();
                break;
            case MenuItemClick.SET_SENSOR:
                startSensorSetActivity();
                break;
            case MenuItemClick.SET_UPDATE:
                startDeviceUpdateActivity();
                break;
            default:
                break;


        }
    }
}
