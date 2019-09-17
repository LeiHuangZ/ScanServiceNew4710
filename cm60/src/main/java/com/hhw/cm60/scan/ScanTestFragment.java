package com.hhw.cm60.scan;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dawn.newlandscan.R;

/**
 * @author HuangLei 1252065297@qq.com
 * <code>
 * Create At 2019/4/27 11:05
 * Update By 更新者
 * Update At 2019/4/27 11:05
 * </code>
 */
public class ScanTestFragment extends Fragment {

    private View mViewContent;

    public ScanTestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (mViewContent == null) {
            mViewContent = inflater.inflate(R.layout.fragment_scan_test, container, false);
        }
        return mViewContent;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new ScanUtil(this.getActivity());
    }
}
