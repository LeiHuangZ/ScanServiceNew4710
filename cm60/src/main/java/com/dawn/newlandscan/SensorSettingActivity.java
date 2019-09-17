package com.dawn.newlandscan;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dawn.decoderapijni.SensorParam;
import com.dawn.decoderapijni.ScanService;

public class SensorSettingActivity extends Activity implements View.OnClickListener {

    private static final String TAG = SensorSettingActivity.class.getCanonicalName();

    private EditText CaptureModeEditText;
    private EditText AdjustImgsEditText;
    private EditText SkipImgsEditText;
    private EditText RotationModeEditText;
    private EditText MinExpValueEditText;
    private EditText MaxExpValueEditText;
    private EditText MinGainValueEditText;
    private EditText MaxGainValuesEditText;
    private EditText TargetLumaValueEditText;

    private Button savaButton;
    private Button cancelButton;

    private SensorParam sensorParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_setting);
        initView();
        initData();
    }


    public void initView(){
        CaptureModeEditText = (EditText) findViewById(R.id.modeEditText);
        AdjustImgsEditText = (EditText) findViewById(R.id.AdjustImgsEditText);
        SkipImgsEditText = (EditText) findViewById(R.id.SkipImgsEditText);
        RotationModeEditText = (EditText) findViewById(R.id.RotationModeEditText);
        MinExpValueEditText = (EditText) findViewById(R.id.MinExpValueEditText);
        MaxExpValueEditText = (EditText) findViewById(R.id.MaxExpValueEditText);
        MinGainValueEditText = (EditText) findViewById(R.id.MinGainValueEditText);
        MaxGainValuesEditText = (EditText) findViewById(R.id.MaxGainValueEditText);
        TargetLumaValueEditText = (EditText) findViewById(R.id.TargetLumaValueEditText);

        savaButton = (Button) findViewById(R.id.saveParam);
        savaButton.setOnClickListener(this);
        cancelButton = (Button) findViewById(R.id.cancelParam);
        cancelButton.setOnClickListener(this);
    }

    public void initData(){
        sensorParam  = ScanService.getSensorParam(0);
        if(sensorParam == null){
            return;
        }
        CaptureModeEditText.setText(String.valueOf(sensorParam.getCaptureMode()));
        AdjustImgsEditText.setText(String.valueOf(sensorParam.getAdjustImgs()));
        SkipImgsEditText.setText(String.valueOf(sensorParam.getSkipImgs()));
        RotationModeEditText.setText(String.valueOf(sensorParam.getRotationMode()));
        MinExpValueEditText.setText(String.valueOf(sensorParam.getMinExpValue()));
        MaxExpValueEditText.setText(String.valueOf(sensorParam.getMaxExpValue()));
        MinGainValueEditText.setText(String.valueOf(sensorParam.getMinGainValue()));
        MaxGainValuesEditText.setText(String.valueOf(sensorParam.getMaxGainValue()));
        TargetLumaValueEditText.setText(String.valueOf(sensorParam.getTargetLumaValue()));
    }

    public void updataParam(){
        SensorParam param =new SensorParam();
        param.setCaptureMode(Integer.valueOf(CaptureModeEditText.getText().toString()));
        param.setAdjustImgs(Integer.valueOf(AdjustImgsEditText.getText().toString()));
        param.setSkipImgs(Integer.valueOf(SkipImgsEditText.getText().toString()));
        param.setRotationMode(Integer.valueOf(RotationModeEditText.getText().toString()));
        param.setMinExpValue(Integer.valueOf(MinExpValueEditText.getText().toString()));
        param.setMaxExpValue(Integer.valueOf(MaxExpValueEditText.getText().toString()));
        param.setMinGainValue(Integer.valueOf(MinGainValueEditText.getText().toString()));
        param.setMaxGainValue(Integer.valueOf(MaxGainValuesEditText.getText().toString()));
        param.setTargetLumaValue(Integer.valueOf(TargetLumaValueEditText.getText().toString()));
        ScanService.setSensorParam(0,param);
//        ScanService.scanReInit();//pang temp
        Toast.makeText(getApplicationContext(),"保存参数成功",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.saveParam:
                updataParam();
                break;
                default:
                    break;
        }
    }
}
