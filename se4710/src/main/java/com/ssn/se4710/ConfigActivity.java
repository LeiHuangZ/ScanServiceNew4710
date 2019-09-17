package com.ssn.se4710;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * @author HuangLei 1252065297@qq.com
 * @CreateDate 2019/1/18 10:07
 * @UpdateUser 更新者
 * @UpdateDate 2019/1/18 10:07
 * 配置界面
 */
public class ConfigActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(android.R.id.content, new ConfigFragment());
        fragmentTransaction.commit();
    }
}
