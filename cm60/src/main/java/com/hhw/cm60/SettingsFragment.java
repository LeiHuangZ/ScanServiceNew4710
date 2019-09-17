package com.hhw.cm60;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dawn.newlandscan.R;

/**
 * @author HuangLei 1252065297@qq.com
 * <code>
 * Create At 2019/4/27 11:14
 * Update By 更新者
 * Update At 2019/4/27 11:14
 * </code>
 */
public class SettingsFragment extends Fragment {

    private View mViewContent;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (mViewContent == null) {
            mViewContent =  inflater.inflate(R.layout.fragment_general_set, container, false);
        }
        return mViewContent;
    }

}
