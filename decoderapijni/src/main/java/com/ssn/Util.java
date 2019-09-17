package com.ssn;

/**
 * Created by Administrator on 2018/4/9.
 */

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.ssn.decoderapijni.R;

public class Util {
    /**
     * 是否为平板（H711），用于控制悬浮窗是否显示
     */
    public static final boolean mIsPad = false;

    public static SoundPool sp ;
    public static Map<Integer, Integer> suondMap;
    public static Context context;

    //初始化声音池
    public static void initSoundPool(Context context){
        Util.context = context;
        sp = new SoundPool(1, AudioManager.STREAM_SYSTEM, 10);
        suondMap = new HashMap<Integer, Integer>();
//        suondMap.put(1, sp.load(context, R.raw.msg, 1));
        suondMap.put(1, sp.load(context, R.raw.dingdj5, 10));
    }

    //播放声音池声音
    public static  void play(int sound, int number){
        AudioManager am = (AudioManager)Util.context.getSystemService(Context.AUDIO_SERVICE);
        //获取系统最大音量
        int maxVolume= am != null ? am.getStreamVolume(AudioManager.STREAM_SYSTEM) : 15;
        sp.play(
                //播放的音乐Id
                suondMap.get(sound),
                //左声道音量
                maxVolume,
                //右声道音量
                maxVolume,
                //优先级，0为最低
                1,
                //循环次数，0无不循环，-1无永远循环
                number,
                //回放速度，值在0.5-2.0之间，1为正常速度
                1);
    }
}