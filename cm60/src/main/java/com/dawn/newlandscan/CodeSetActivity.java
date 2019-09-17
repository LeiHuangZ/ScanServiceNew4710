package com.dawn.newlandscan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dawn.decoderapijni.EngineCode;
import com.dawn.decoderapijni.EngineCodeMenu;
import com.hhw.cm60.SoftScanService;
import com.dawn.newlandscan.adapter.CodeSetItem;
import com.dawn.newlandscan.adapter.CodeSetItemAdapter;
import com.dawn.newlandscan.widget.TopToolbar;

import java.util.ArrayList;
import java.util.List;

public class CodeSetActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private static final String TAG = "Huang, " + CodeSetActivity.class.getCanonicalName();

    private TopToolbar topToolbar;

    private ListView lv_codesetItem;
    private List<CodeSetItem> mCodeSetItemList;
    private CodeSetItemAdapter mCodeSetItemAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_set);
        initView();
        initData();
    }

    public void initView(){

        lv_codesetItem = (ListView) findViewById(R.id.gv_codeSetMenu);
        lv_codesetItem.setOnItemClickListener(this);

        topToolbar = (TopToolbar) findViewById(R.id.tb_topToolBar);
        topToolbar.setRightTitleVisiable(View.GONE);
        topToolbar.setMainTitle(R.string.code_set_title);
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
        mCodeSetItemList = new ArrayList<>();
        if(SoftScanService.engineCodeList == null)return;

        for(EngineCode engineCode : SoftScanService.engineCodeList){
            CodeSetItem item = new CodeSetItem(engineCode.getName(),engineCode.getEnable());
            mCodeSetItemList.add(item);
        }

        mCodeSetItemAdapter = new CodeSetItemAdapter(getApplicationContext(),mCodeSetItemList,this);
        lv_codesetItem.setAdapter(mCodeSetItemAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG,"onItemClick:" + mCodeSetItemList.get(position).getCodeName());
        String status;
        if(mCodeSetItemList.get(position).getEnable()!= null){
            if(mCodeSetItemList.get(position).getEnable().equals("0")){
                status = "1";
            } else {
                status = "0";
            }
            SoftScanService.engineCodeList.get(position).setEnable(status);
            mCodeSetItemList.get(position).setEnable(status);
            mCodeSetItemAdapter.notifyDataSetChanged();

            sendCmdBroadcast(mCodeSetItemList.get(position).getCodeName(),
                    EngineCodeMenu.CodeParam.ENABLE.getParamName(), status);
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick:" + (String) v.getTag());
        Intent paramSetIntent = new Intent(CodeSetActivity.this,ParamSetActivity.class);
        paramSetIntent.putExtra("Id",v.getTag().toString());
        startActivity(paramSetIntent);
    }

    /**
     * 通知Service改变码制设置
     *
     */
    private void sendCmdBroadcast(String id, String param, String value) {
        Intent configIntent = new Intent();
        configIntent.setAction(SoftScanService.ACTION_SCAN_PARAM);
        configIntent.putExtra("id", id);
        configIntent.putExtra("param", param);
        configIntent.putExtra("value", value);
        this.getApplicationContext().sendBroadcast(configIntent);
    }
}
