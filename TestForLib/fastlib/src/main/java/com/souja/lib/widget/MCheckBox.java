package com.souja.lib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.souja.lib.R;

/**
 * 图片、文字，checked属性的layout
 * Created by yangdz on 2016/2/23.
 */
public class MCheckBox extends LinearLayout implements Checkable {
    public MCheckBox(Context context) {
        this(context, null, 0);
    }

    public MCheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private CheckBox checkBox;
    private TextView tvTitle;
    //    private TextView vMsg;
    private View vMsg;
    private boolean mChecked, mSelfControl, noBg;
    private int textColor, textColorFocus;
    private String text, textFocus;

    private void init(Context c, AttributeSet attrs) {
        LayoutInflater.from(c).inflate(R.layout.widget_checkbox, this);
        checkBox = findViewById(R.id.mcbBox);
        tvTitle = findViewById(R.id.mcbTxt);
        vMsg = findViewById(R.id.v_msg);

        setClickable(true);
        int imgWidth, imgHeight;
        Drawable cbBackground;

        TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.MCheckBox);
        try {
            noBg = a.getBoolean(R.styleable.MCheckBox_noBg, false);
            mSelfControl = a.getBoolean(R.styleable.MCheckBox_mSelfControl, false);
            mChecked = a.getBoolean(R.styleable.MCheckBox_mChecked, false);
            imgWidth = a.getDimensionPixelSize(R.styleable.MCheckBox_mDrawableWidth, 0);
            imgHeight = a.getDimensionPixelSize(R.styleable.MCheckBox_mDrawableHeight, 0);
            cbBackground = a.getDrawable(R.styleable.MCheckBox_mDrawable);
            text = a.getString(R.styleable.MCheckBox_mText);
            textFocus = a.getString(R.styleable.MCheckBox_mTextFocus);
            textColor = a.getColor(R.styleable.MCheckBox_mTxtColor,
                    getResources().getColor(R.color.lib_main_color));
            textColorFocus = a.getColor(R.styleable.MCheckBox_mTxtColorFocus, -1);
        } finally {
            a.recycle();
        }

        LayoutParams paramsBox = new LayoutParams(imgWidth, imgHeight);
        checkBox.setLayoutParams(paramsBox);
        checkBox.setBackgroundDrawable(cbBackground);
        checkBox.setClickable(false);

        tvTitle.setText(text != null ? text : "");
        tvTitle.setTextColor(textColor);

        setChecked(mChecked);
    }

    public void refreshBg() {
        if (mChecked) {
            if (!noBg) setBackgroundResource(R.drawable.shape_focus);
            if (textFocus != null) tvTitle.setText(textFocus);
            if (textColorFocus != -1) tvTitle.setTextColor(textColorFocus);
            else tvTitle.setTextColor(Color.WHITE);
        } else {
            if (!noBg) setBackgroundResource(R.drawable.shape_blur);
            tvTitle.setTextColor(textColor);
            if (textFocus != null) tvTitle.setText(text);
        }
    }

    /*public void isMall() {
        LayoutParams params = (LayoutParams) vMsg.getLayoutParams();
        params.rightMargin = (int) (90 * ScreenUtil.mScale);
        vMsg.setLayoutParams(params);
    }*/

    @Override
    public void setChecked(boolean checked) {
        mChecked = checked;
        refreshBg();
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public boolean performClick() {
        toggle();
        return super.performClick();
    }

    @Override
    public void toggle() {
        if (!mSelfControl) {
            mChecked = !mChecked;
            setChecked(mChecked);
            checkBox.setChecked(mChecked);
        }
    }

    public void doFocus() {
        setChecked(true);
        checkBox.setChecked(true);
    }

    public void doBlur() {
        setChecked(false);
        checkBox.setChecked(false);
    }

//    public void setMsgCount(int count) {
//        if (count > 0)
//            vMsg.setVisibility(VISIBLE);
//        else
//            vMsg.setVisibility(GONE);
//        vMsg.setText("" + count);
//    }

    public void showMsg() {
        vMsg.setVisibility(VISIBLE);
    }

    public void hideMsg() {
        vMsg.setVisibility(INVISIBLE);
    }

}
