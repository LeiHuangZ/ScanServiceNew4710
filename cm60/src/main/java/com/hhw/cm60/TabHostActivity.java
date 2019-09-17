package com.hhw.cm60;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.dawn.newlandscan.R;
import com.dawn.newlandscan.widget.TopToolbar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author HuangLei 1252065297@qq.com
 * <code>
 * Create At 2019/4/27 10:53
 * Update By 更新者
 * Update At 2019/4/27 10:53
 * </code>
 */
public class TabHostActivity extends AppCompatActivity implements TopToolbar.MenuToolBarListener, TabLayout.OnTabSelectedListener {

    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.tab_topToolBar)
    TopToolbar mTabTopToolBar;

    private AlertDialog mHelpDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_host);
        ButterKnife.bind(this);

        initView();
    }

    @Override
    protected void onDestroy() {
        if (mHelpDialog.isShowing()) {
            mHelpDialog.dismiss();
        }
        super.onDestroy();
    }

    private void initView() {
        // 设置TopToolBar标题栏图片去除
        mTabTopToolBar.setLeftTitleVisiable(View.GONE);
        // 设置TopToolBar标题栏右边图片内容
        mTabTopToolBar.setRightTitleDrawable(R.drawable.ic_toolbar_help);
        // 获取应用版本号名称
        String versionName = "";
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = " v" + packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        // 设置TopToolBar标题栏内容（显示版本号名称）
        mTabTopToolBar.setMainTitle(getString(R.string.app_name) + versionName);
        //设置TopToolBar标题栏点击监听
        mTabTopToolBar.setMenuToolBarListener(this);
        // 添加Tab标题
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.scan_tab_title)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.settings_tab_title)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.code_tab_title)));
        // 添加Fragment进ViewPager
        final PagerAdapter adapter = new TabFragmentPagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
        mViewPager.setAdapter(adapter);
        //为ViewPager添加页面改变监听
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        //为TabLayout添加Tab选择监听
        mTabLayout.addOnTabSelectedListener(this);
        // 关于应用
        mHelpDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.about))
                .setMessage(this.getString(R.string.description)).create();
    }

    /**---------------------------------------------TopToolbar 标题栏点击监听-----------------------------------------------*/
    @Override
    public void onToolBarClickLeft(View v) {
    }
    @Override
    public void onToolBarClickRight(View v) {
        mHelpDialog.show();
    }
    /**---------------------------------------------TabLayout 选择监听-----------------------------------------------*/
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        // 显示对应Fragment
        mViewPager.setCurrentItem(tab.getPosition());
    }
    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }
    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }
}
