package com.dawn.newlandscan.widget;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v7.widget.Toolbar ;

import com.dawn.newlandscan.R;


public class TopToolbar extends Toolbar implements View.OnClickListener{

    private ImageButton btn_left;

    private TextView tv_title;

    private ImageButton btn_reght;

    private MenuToolBarListener menuToolBarListener;

    public TopToolbar(Context context) {
        this(context,null);
    }

    public TopToolbar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TopToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_top_toolbar, this);
        btn_left = (ImageButton) findViewById(R.id.btn_htb_left);
        tv_title = (TextView) findViewById(R.id.tv_htb_title);
        btn_reght = (ImageButton) findViewById(R.id.btn_htb_right);

        btn_left.setOnClickListener(this);
        btn_reght.setOnClickListener(this);

    }


    //设置中间title的内容
    public void setMainTitle(int text) {
        this.setTitle(" ");
        tv_title.setText(text);
    }

    //设置中间title的内容
    public void setMainTitle(String text) {
        this.setTitle(" ");
        tv_title.setText(text);
    }

    //设置中间title的内容文字的颜色
    public void setMainTitleColor(int color) {
        tv_title.setTextColor(color);
    }


    //设置title左边图标
    public void setLeftTitleDrawable(int id) {
        btn_left.setImageResource(id);
    }

    //设置title右边图标
    public void setLeftTitleVisiable(int visiable) {
        btn_left.setVisibility(visiable);
    }

    //设置title右边图标
    public void setRightTitleDrawable(int id) {
        btn_reght.setImageResource(id);
    }

    //设置title右边图标
    public void setRightTitleVisiable(int visiable) {
        btn_reght.setVisibility(visiable);
    }

    @Override
    public void onClick(View v) {

        if(menuToolBarListener == null)return;

        switch (v.getId()){
            case R.id.btn_htb_left:
                menuToolBarListener.onToolBarClickLeft(v);
                break;
            case R.id.btn_htb_right:
                menuToolBarListener.onToolBarClickRight(v);
                break;

            default:
                break;
        }
    }

    public void setMenuToolBarListener(MenuToolBarListener listener){
        menuToolBarListener = listener;
    }

    public interface MenuToolBarListener {
        public void onToolBarClickLeft(View v);
        public void onToolBarClickRight(View v);
    }

}
