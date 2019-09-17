package com.ssn.se4710;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * @author huang
 * 用于显示悬浮窗
 */
public class FloatService extends Service {
    private WindowManager wm;

    private float mTouchX;
    private float mTouchY;
    private float x;
    private float y;
    private float mStartX;
    private float mStartY;

    LinearLayout mFloatLayout;
    WindowManager.LayoutParams wmParams;
    public FloatService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createFloatWindow();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createFloatWindow() {
        wmParams = new WindowManager.LayoutParams();
        wm = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        assert wm != null;
        Display display = wm.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);


        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.START | Gravity.TOP;
        wmParams.x = dm.widthPixels;
        wmParams.y = dm.heightPixels / 2;
        wmParams.width = 100;
        wmParams.height = 100;
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_window, null);
        mFloatLayout.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                x = event.getRawX();
                y = event.getRawY();
                switch (event.getAction()) {
                    //down
                    case 0:
                        Log.e("", "ACTION_DOWN");
                        mTouchX = event.getX();
                        mTouchY = event.getY();
                        mStartX = x;
                        mStartY = y;

                        mFloatLayout.setBackground(getResources().getDrawable(R.drawable.bg_yellow));
                        break;
                    //move
                    case 2:

                        if ((x - mStartX) < 5 && (y - mStartY) < 5) {

                        } else {
                            updateView();
                        }
                        break;
                    //up
                    case 1:
                        mFloatLayout.setBackground(getResources().getDrawable(R.drawable.bg_red));
                        if ((x - mStartX) < 5 && (y - mStartY) < 5) {
                            Intent intent = new Intent();
                            intent.setAction("com.rfid.SCAN_CMD");
                            sendBroadcast(intent);
                        } else {
                            Log.e("onclice", "finish++++");
                            updateView();
                            mFloatLayout.setBackground(getResources().getDrawable(R.drawable.bg_red));
                            mTouchX = mTouchY = 0;
                        }
                        break;
                    default:

                        break;
                }
                return true;
            }
        });
        wm.addView(mFloatLayout, wmParams);
    }

    private void updateView() {
        wmParams.x = (int) (x - mTouchX);
        wmParams.y = (int) (y - mTouchY);
        wm.updateViewLayout(mFloatLayout, wmParams);
    }

    @Override
    public void onDestroy() {
        wm.removeViewImmediate(mFloatLayout);
        super.onDestroy();
    }
}
