package com.dawn.newlandscan;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.dawn.decoderapijni.ScanService;
import com.dawn.newlandscan.widget.TopToolbar;

import java.util.Arrays;

public class DebugI2CActivity extends Activity implements RadioGroup.OnCheckedChangeListener, AdapterView.OnItemSelectedListener, View.OnClickListener {

    private static final String TAG = DebugI2CActivity.class.getCanonicalName();

    private TopToolbar topToolbar;
    private RadioGroup radioGroup;
    private LinearLayout layout_data;
    private Spinner sp_length;

    private EditText et_addr;
    private EditText et_data0;
    private EditText et_data1;
    private EditText et_data2;
    private EditText et_data3;

    private TextView tv_i2c_show;

    private Button btn_send_cmd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i2_cdebug);
        initView();
        initData();
    }

    private void initView(){
        topToolbar = (TopToolbar) findViewById(R.id.tb_topToolBar);
        topToolbar.setLeftTitleDrawable(R.drawable.ic_toolbar_back);
        topToolbar.setRightTitleDrawable(R.drawable.ic_toolbar_help);
        topToolbar.setMainTitle(R.string.menu_item_debug_i2c);
        topToolbar.setMenuToolBarListener(new TopToolbar.MenuToolBarListener() {
            @Override
            public void onToolBarClickLeft(View v) {
                finish();
            }

            @Override
            public void onToolBarClickRight(View v) {

            }
        });

        radioGroup = (RadioGroup) findViewById(R.id.rg_read_write);
        radioGroup.setOnCheckedChangeListener(this);
        layout_data = (LinearLayout) findViewById(R.id.layout_data);
        sp_length = (Spinner) findViewById(R.id.sp_length);
        sp_length.setOnItemSelectedListener(this);

        et_addr = (EditText) findViewById(R.id.et_addr);
        et_data0 = (EditText) findViewById(R.id.et_data0);
        et_data1 = (EditText) findViewById(R.id.et_data1);
        et_data2 = (EditText) findViewById(R.id.et_data2);
        et_data3 = (EditText) findViewById(R.id.et_data3);

        btn_send_cmd = (Button) findViewById(R.id.btn_send_cmd);
        btn_send_cmd.setOnClickListener(this);

        tv_i2c_show = (TextView) findViewById(R.id.tv_i2c_show);
    }

    private void initData(){

    }

    private int length = 1;
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        tv_i2c_show.setText("");
        switch (checkedId){
            case R.id.rbtn_write:
                layout_data.setVisibility(View.VISIBLE);
                showTextView(length);
                break;
            case R.id.rbtn_read:
                layout_data.setVisibility(View.GONE);
                break;
                default:
                    break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG,"position: " + position);
        showTextView(position+1);
        length = position + 1;
    }

    private void showTextView(int num){
        if(radioGroup.getCheckedRadioButtonId() == R.id.rbtn_write){
            if(num == 1){
                et_data0.setVisibility(View.VISIBLE);
                et_data1.setVisibility(View.GONE);
                et_data2.setVisibility(View.GONE);
                et_data3.setVisibility(View.GONE);
            } else if(num == 2){
                et_data0.setVisibility(View.VISIBLE);
                et_data1.setVisibility(View.VISIBLE);
                et_data2.setVisibility(View.GONE);
                et_data3.setVisibility(View.GONE);
            } else if(num ==3){
                et_data0.setVisibility(View.VISIBLE);
                et_data1.setVisibility(View.VISIBLE);
                et_data2.setVisibility(View.VISIBLE);
                et_data3.setVisibility(View.GONE);
            } else {
                et_data0.setVisibility(View.VISIBLE);
                et_data1.setVisibility(View.VISIBLE);
                et_data2.setVisibility(View.VISIBLE);
                et_data3.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void i2cReadData(){

        String string_data = et_addr.getText().toString();
        if(string_data.isEmpty()){
            return;
        }

        int addr = Integer.valueOf(string_data);
        int[] data = new int[length];
        int ret = ScanService.scanI2cRead(addr,length,data);
        tv_i2c_show.setText("i2cReadData->ret:" + ret + "\r\n" +
                "data:\r\n" + Arrays.toString(data));

        Log.d(TAG,"i2cReadData->ret:"+ ret+ "data:" + Arrays.toString(data));
    }

    private void i2cWriteData(){
        String data0 = et_data0.getText().toString();
        String data1 = et_data1.getText().toString();
        String data2 = et_data2.getText().toString();
        String data3 = et_data3.getText().toString();

        String string_data = et_addr.getText().toString();
        if(string_data.isEmpty()){
            return;
        }
        int addr = Integer.valueOf(string_data);

        int[] data = new int[length];
        switch (length) {
            case 4:
                if (!data3.isEmpty() ) {
                    data[3] = Integer.valueOf(data3);
                } else {
                    break;
                }
            case 3:
                if (!data2.isEmpty() ) {
                    data[2] = Integer.valueOf(data2);
                } else {
                    break;
                }
            case 2:
            {
                if (!data1.isEmpty() ) {
                    data[1] = Integer.valueOf(data1);
                } else {
                   break;
                }
            }
            case 1:
            {
                if (!data0.isEmpty()) {
                    data[0] = Integer.valueOf(data0);
                    ScanService.scanI2cWrite(addr,length,data);
                }
            }
                break;
                default:
                    break;
        }

    }

    private void sendCmd(){
        if(radioGroup.getCheckedRadioButtonId() == R.id.rbtn_read ) {
            i2cReadData();
        } else {
            i2cWriteData();
        }

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_send_cmd:
                sendCmd();
                break;
                default:
                    break;
        }
    }
}
