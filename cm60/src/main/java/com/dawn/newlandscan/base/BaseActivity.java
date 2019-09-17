package com.dawn.newlandscan.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.dawn.newlandscan.R;
import com.dawn.newlandscan.widget.TopToolbar;

public class BaseActivity extends Activity implements TopToolbar.MenuToolBarListener {

    private TopToolbar topToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId());

        baseInitView();

        initView(savedInstanceState);
        initData(savedInstanceState);
    }

    private void baseInitView(){
        topToolbar = (TopToolbar) findViewById(R.id.tb_topToolBar);

        topToolbar.setMenuToolBarListener(this);
    }

    public int layoutId(){
        return 0;
    }

    public void initView(Bundle savedInstanceState){

    }

    public void initData(Bundle savedInstanceState){

    }

    public void initTitle(int id){
        topToolbar.setMainTitle(id);
    }

    public void initDrawable(int leftId, int rightId){
        if(leftId != 0){
            topToolbar.setLeftTitleDrawable(leftId);
        }  else {
            topToolbar.setLeftTitleVisiable(View.GONE);
        }

        if(rightId != 0){
            topToolbar.setRightTitleDrawable(rightId);
        } else {
            topToolbar.setRightTitleVisiable(View.GONE);
        }
    }

    @Override
    public void onToolBarClickLeft(View v) {

    }

    @Override
    public void onToolBarClickRight(View v) {

    }
}
