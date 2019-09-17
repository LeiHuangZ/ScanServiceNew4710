package com.dawn.newlandscan;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.dawn.decoderapijni.ScanService;
import com.dawn.newlandscan.widget.TopToolbar;

public class ImageShowActivity extends Activity implements ScanService.ScanListener {

    private static final String TAG = ImageShowActivity.class.getCanonicalName();

    private TopToolbar topToolbar;
    private ImageView imageView;
    static SoundPool mSoundPool;
    static int soundId = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_show);
        initVew();
        initData();
    }

    public void initVew(){
        imageView = (ImageView) findViewById(R.id.iv_showLastImage);
        topToolbar = (TopToolbar) findViewById(R.id.tb_topToolBar);
        topToolbar.setMainTitle(R.string.imageShow_page_title);
        topToolbar.setRightTitleDrawable(R.drawable.ic_toolbar_scan);
        topToolbar.setMenuToolBarListener(new TopToolbar.MenuToolBarListener() {
            @Override
            public void onToolBarClickLeft(View v) {
                finish();
            }

            @Override
            public void onToolBarClickRight(View v) {
                ScanService.startScan();
            }
        });
    }

    public void initData(){
        mSoundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 10);
        soundId = mSoundPool.load(getApplicationContext(),R.raw.dingdj5, 10);
        showImage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ScanService.setScanListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ScanService.stopScan();
        ScanService.setScanListener(null);
    }

    public void showImage(){
        byte[] image = ScanService.getImageLast();
        if(image ==null){
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeByteArray(image,0,image.length);
        if(bitmap == null){
            return;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(90);
        bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void onScanListener(Message msg) {
        mSoundPool.play(soundId, 1, 1, 0, 0, (float) 1.0); // 播放声音
        showImage();
    }
}
