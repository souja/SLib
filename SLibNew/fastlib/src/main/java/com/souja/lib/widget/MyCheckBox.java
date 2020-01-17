package com.souja.lib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.souja.lib.R;

import org.xutils.common.util.LogUtil;

/**
 * 图片、文字，checked属性的layout
 * Created by yangdz on 2016/2/23.
 */
public class MyCheckBox extends FrameLayout implements Checkable {
    public MyCheckBox(Context context) {
        this(context, null, 0);
    }

    public MyCheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setSelfControl(boolean b) {
        mSelfControl = b;
    }

    private MCheckableImageView checkBox;
    private TextView textView;
    private TextView vMsg;
    private boolean mChecked, mSelfControl;
    private int textColor, textColorFocus;
    private String text, textFocus;
    private int msgCount;

    private void init(Context c, AttributeSet attrs) {
        LayoutInflater.from(c).inflate(R.layout.widget_checkbox, this);
        checkBox = findViewById(R.id.mcbBox);
        textView = findViewById(R.id.mcbTxt);
        vMsg = findViewById(R.id.v_msg);

        setClickable(true);
        int imgWidth, imgHeight;
        Drawable cbBackground;

        TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.MyCheckBox);
        try {
            mSelfControl = a.getBoolean(R.styleable.MyCheckBox_mcb_self_control, false);
            mChecked = a.getBoolean(R.styleable.MyCheckBox_mcb_checked, false);
            imgWidth = a.getDimensionPixelSize(R.styleable.MyCheckBox_mcb_drawable_width, 0);
            imgHeight = a.getDimensionPixelSize(R.styleable.MyCheckBox_mcb_drawable_height, 0);
            cbBackground = a.getDrawable(R.styleable.MyCheckBox_mcb_drawable);
            text = a.getString(R.styleable.MyCheckBox_mcb_text);
            textFocus = a.getString(R.styleable.MyCheckBox_mcb_text_focus);
            textColor = a.getColor(R.styleable.MyCheckBox_mcb_txt_color,
                    getResources().getColor(R.color.main_color));
            textColorFocus = a.getColor(R.styleable.MyCheckBox_mcb_txt_color_focus, -1);
        } finally {
            a.recycle();
        }

        LinearLayout.LayoutParams paramsBox = (LinearLayout.LayoutParams) checkBox.getLayoutParams();
        paramsBox.width = imgWidth;
        paramsBox.height = imgHeight;
        checkBox.setLayoutParams(paramsBox);
        checkBox.setBackgroundDrawable(cbBackground);

        textView.setText(text != null ? text : "");
        textView.setTextColor(textColor);
        if (mChecked)
            setChecked(true);
    }

    public void setupCheckBox(String menu, int bgRes, int imgWidth, int imgHeight, int imgTxtMargin,
                              int colorBlur, int colorFocus) {
        setText(menu);
        textColor = colorBlur;
        textView.setTextColor(textColor);
        textColorFocus = colorFocus;
        LinearLayout.LayoutParams paramsBox = (LinearLayout.LayoutParams) checkBox.getLayoutParams();
        paramsBox.width = imgWidth;
        paramsBox.height = imgHeight;
        checkBox.setLayoutParams(paramsBox);
        checkBox.setBackgroundDrawable(getContext().getResources().getDrawableForDensity(bgRes, 0));

        LinearLayout.LayoutParams paramsTxt = (LinearLayout.LayoutParams) textView.getLayoutParams();
        paramsTxt.topMargin = imgTxtMargin;
        textView.setLayoutParams(paramsTxt);
    }

    public void refreshBg() {
//        LogUtil.e("=======refreshBg=======");
        if (mChecked) {
            if (textFocus != null) textView.setText(textFocus);
            if (textColorFocus != -1) textView.setTextColor(textColorFocus);
            else textView.setTextColor(Color.WHITE);
        } else {
            textView.setTextColor(textColor);
            if (text != null) textView.setText(text);
        }
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

    @Override
    public void setChecked(boolean checked) {
//        LogUtil.e("=======setChecked:" + checked);
        mChecked = checked;
        refreshBg();
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }


    public void doFocus() {
        setChecked(true);
        checkBox.setChecked(true);
    }

    public void doBlur() {
        setChecked(false);
        checkBox.setChecked(false);
    }

    public void setMsgCount(int count) {
        msgCount = count;
        notifyCount();
    }

    private void notifyCount() {
        if (msgCount > 0)
            vMsg.setVisibility(VISIBLE);
        else
            vMsg.setVisibility(GONE);
        if (msgCount > 99)
            vMsg.setText("99+");
        else
            vMsg.setText("" + msgCount);
    }

    public void addMsgCount(int count) {
        msgCount += count;
        notifyCount();
    }

    public void hideMsg() {
        vMsg.setVisibility(INVISIBLE);
    }

    public void setText(String text) {
        this.text = text;
        textView.setText(this.text);
    }

//    public void setMsgCount(int count) {
//        if (count > 0)
//            vMsg.setVisibility(VISIBLE);
//        else
//            vMsg.setVisibility(GONE);
//        vMsg.setText("" + count);
//    }

//    public void showMsg() {
//        vMsg.setVisibility(VISIBLE);
//    }
}
