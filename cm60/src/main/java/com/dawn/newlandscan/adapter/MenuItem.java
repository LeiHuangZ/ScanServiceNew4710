package com.dawn.newlandscan.adapter;

import com.dawn.newlandscan.R;

public class MenuItem {
    long mUnreadCounts = 0;
    int mID = 0;
    int mDrawableID = 0;
    int mBgID = 0;
    int mTextID = 0;
    int mTipID = 0;
    String mTextName = null;
    String mTextTip = null;

    String mIconUrl = null;
    String mJumpUrl = null;

    public MenuItem() {
    }

    public MenuItem(int icon, String name, long count) {
        this.mDrawableID = icon;
        this.mTextName = name;
        this.mUnreadCounts = count;
    }

    public MenuItem(int id, int icon, String name, long count) {
        this.mID = id;
        this.mDrawableID = icon;
        this.mTextName = name;
        this.mUnreadCounts = count;
    }

    public int getID() {
        return mID;
    }

    public void setID(int id) {
        this.mID = id;
    }

    public long getCount() {
        return this.mUnreadCounts;
    }

    public int getIconID() {
        return this.mDrawableID;
    }

    public String getName() {
        return this.mTextName;
    }

    public String getTip() {
        return this.mTextTip;
    }

    public void setCount(long count) {
        this.mUnreadCounts = count;
    }

    public void setIconID(int id) {
        this.mDrawableID = id;
    }

    public void setName(String name) {
        this.mTextName = name;
    }

    public void setTip(String tip) {
        this.mTextTip = tip;
    }

    public int getBgID() {
        return mBgID;
    }

    public void setBgID(int mBgID) {
        this.mBgID = mBgID;
    }

    public int getTextID() {
        if (mTextID == 0)
            mTextID = R.string.menu_item_hint;
        return mTextID;
    }

    public void setTextID(int mTextID) {
        this.mTextID = mTextID;
    }

    public int getTipID() {
        if (mTipID == 0)
            mTipID = R.string.menu_item_hint;
        return mTipID;
    }

    public void setTipID(int mTipID) {
        this.mTipID = mTipID;
    }


    public String getIconUrl() {
        return mIconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.mIconUrl = iconUrl;
    }

    public String getJumpUrl() {
        return mJumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.mJumpUrl = jumpUrl;
    }

    @Override
    public String toString() {
        return "MenuItem [mUnreadCounts=" + mUnreadCounts + ", mID=" + mID
                + ", mDrawableID=" + mDrawableID + ", mBgID=" + mBgID
                + ", mTextID=" + mTextID + ", mTipID=" + mTipID
                + ", mTextName=" + mTextName + ", mTextTip=" + mTextTip
                + ", iconUrl=" + mIconUrl + ", jumpUrl=" + mJumpUrl + "]";
    }
}



