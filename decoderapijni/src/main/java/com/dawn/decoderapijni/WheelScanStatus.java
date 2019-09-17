package com.dawn.decoderapijni;

import java.util.ArrayList;
import java.util.List;

public class WheelScanStatus {

    public int count;
    public int wheelDecodeTime;
    public List<OnceStatus> mList;

    public WheelScanStatus(int count, int wheelDecodeTime) {
        this.count = count;
        this.wheelDecodeTime = wheelDecodeTime;
        mList = new ArrayList<>();
    }

    public void addOnceTime(OnceStatus onceStatus){
        mList.add(onceStatus);
    }


    @Override
    public String toString() {
        return "WheelScanStatus{" +
                "count=" + count +
                ", wheelDecodeTime=" + wheelDecodeTime +
                ", mList=" + mList +
                '}';
    }
}
