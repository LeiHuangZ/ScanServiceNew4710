package com.dawn.newlandscan;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.dawn.newlandscan.widget.TopToolbar;

/**
 * @author HuangLei 1252065297@qq.com
 * @CreateDate 2019/1/18 10:07
 * @UpdateUser 更新者
 * @UpdateDate 2019/1/18 10:07
 * 配置界面
 */
public class ConfigActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        //添加标题栏
        TopToolbar topToolbar = (TopToolbar) findViewById(R.id.tb_topToolBar);
        topToolbar.setLeftTitleVisiable(View.GONE);
        topToolbar.setRightTitleVisiable(View.GONE);
        topToolbar.setMainTitle(R.string.app_name);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.config_content, new ConfigFragment());
        fragmentTransaction.commit();
    }
}