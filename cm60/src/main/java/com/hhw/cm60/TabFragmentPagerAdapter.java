package com.hhw.cm60;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hhw.cm60.scan.ScanTestFragment;

/**
 * @author HuangLei 1252065297@qq.com
 * @CreateDate 2019/4/27 14:49
 * @UpdateUser 更新者
 * @UpdateDate 2019/4/27 14:49
 */
public class TabFragmentPagerAdapter extends FragmentPagerAdapter {
    /**
     * fragment的数量
     */
    int nNumOfTabs;
    public TabFragmentPagerAdapter(FragmentManager fm, int nNumOfTabs)
    {
        super(fm);
        this.nNumOfTabs=nNumOfTabs;
    }

    /**
     * 重写getItem方法
     *
     * @param position 指定的位置
     * @return 特定的Fragment
     */
    @Override
    public Fragment getItem(int position) {
        switch(position)
        {
            case 0:
                return new ScanTestFragment();
            case 1:
                return new SettingsFragment();
            case 2:
                return new CodeSetFragment();
            default:
                break;
        }
        return null;
    }

    /**
     * 重写getCount方法
     *
     * @return fragment的数量
     */
    @Override
    public int getCount() {
        return nNumOfTabs;
    }
}
