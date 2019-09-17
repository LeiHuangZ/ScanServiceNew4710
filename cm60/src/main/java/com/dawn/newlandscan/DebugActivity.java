package com.dawn.newlandscan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dawn.newlandscan.adapter.MenuItem;
import com.dawn.newlandscan.adapter.MenuItemAdapter;
import com.dawn.newlandscan.widget.TopToolbar;

import java.util.ArrayList;
import java.util.List;

public class DebugActivity extends Activity implements AdapterView.OnItemClickListener {

    private static final String TAG = DebugActivity.class.getCanonicalName();

    private TopToolbar topToolbar;

    private List<MenuItem> mMenuItemList;
    private MenuItemAdapter mMenuItemAdapter;
    private ListView gv_menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_debug_time);
        initView();
        initData();
    }

    public void initView(){

        gv_menuItem = (ListView) findViewById(R.id.gv_scanDebugMenu);
        gv_menuItem.setOnItemClickListener(this);

        topToolbar = (TopToolbar) findViewById(R.id.tb_topToolBar);
        topToolbar.setLeftTitleDrawable(R.drawable.ic_toolbar_back);
        topToolbar.setRightTitleDrawable(R.drawable.ic_toolbar_help);
        topToolbar.setMainTitle(R.string.setting_icon_debug_text);
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
                getResources().getString(R.string.menu_item_debug_time),
                getResources().getString(R.string.menu_item_debug_i2c),
                getResources().getString(R.string.menu_item_debug_scan),
                getResources().getString(R.string.menu_item_debug_log),
                getResources().getString(R.string.menu_item_debug_camera),
        };
        int[] itemDrawables = {
                R.drawable.ic_menu_debug_time,
                R.drawable.ic_menu_debug_i2c,
                R.drawable.ic_menu_debug_scan,
                R.drawable.ic_menu_debug_log,
                R.drawable.ic_menu_debug_camera,
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
        public static final int DEBUG_TIME = 0;
        public static final int DEBUG_I2C = 1;
        public static final int DEBUG_SCAN = 2;
        public static final int DEBUG_LOG = 3;
        public static final int DEBUG_CAMERA = 4;
    }

    private void startDebugTimeActivity(){
        Intent debugTimeIntent = new Intent(DebugActivity.this,DebugTimeActivity.class);
        startActivity(debugTimeIntent);
    }

    private void startDebugI2CActivity(){
        Intent debugI2CIntent = new Intent(DebugActivity.this,DebugI2CActivity.class);
        startActivity(debugI2CIntent);
    }

    private void startDebugScanActivity(){
        Intent debugScanIntent = new Intent(DebugActivity.this,DebugScanActivity.class);
        startActivity(debugScanIntent);
    }

    private void startDebugLogActivity(){
        Intent debugLogIntent = new Intent(DebugActivity.this,DebugLogActivity.class);
        startActivity(debugLogIntent);
    }

    private void startDebugCameraActivity(){
        Intent debugCameraIntent = new Intent(DebugActivity.this, DebugCameraActivity.class);
        startActivity(debugCameraIntent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case MenuItemClick.DEBUG_TIME:
                startDebugTimeActivity();
                break;
            case MenuItemClick.DEBUG_I2C:
                startDebugI2CActivity();
                break;
            case MenuItemClick.DEBUG_SCAN:
                startDebugScanActivity();
                break;
            case MenuItemClick.DEBUG_LOG:
                startDebugLogActivity();
                break;
            case MenuItemClick.DEBUG_CAMERA:
                startDebugCameraActivity();
                break;
                default:
                    break;
        }
    }
}
