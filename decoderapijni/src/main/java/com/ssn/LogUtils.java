package com.ssn;

import android.util.Log;

/**
 * @author HuangLei 1252065297@qq.com
 * @CreateDate 2019/1/17 11:36
 * @UpdateUser 更新者
 * @UpdateDate 2019/1/17 11:36
 */
public class LogUtils {
    private static final boolean DEBUG = true;

    public static void v(String className, String content) {
        if (DEBUG) {
            Log.v("Huang, se4710", className + ", " + content);
        }
    }

    public static void d(String className, String content) {
        if (DEBUG) {
            Log.d("Huang, se4710", className + ", " + content);
        }
    }

    public static void i(String className, String content) {
        if (DEBUG) {
            Log.i("Huang, se4710", className + ", " + content);
        }
    }

    public static void w(String className, String content) {
        if (DEBUG) {
            Log.w("Huang, se4710", className + ", " + content);
        }
    }

    public static void e(String className, String content) {
        if (DEBUG) {
            Log.e("Huang", className + ", " + content);
        }
    }
}
