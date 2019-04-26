package com.souja.lib.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.souja.lib.R;

import org.xutils.common.util.LogUtil;


/**
 * titlebar
 * Created by ydz on 2017/03/08.
 */
public class TitleBar extends RelativeLayout {

    private ImageButton mLeftImageBtn,//left image btn
            mRightImageBtn,//right image btn
            mRightImageBtnB;//another right image btn ,to the left of mRightImageBtn

    private TextView mTitle,//title text
            mLeftTextBtn,//left text btn
            mRightTextBtn;//right text btn
    private Context mContext;

    public TitleBar(Context context) {
        this(context, null);
        mContext = context;
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        int colorBlack = mContext.getResources().getColor(R.color.black_33);
        int colorMain = mContext.getResources().getColor(R.color.lib_main_color);

        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.TitleBar);
        View titleLayout = LayoutInflater.from(mContext).inflate(R.layout.layout_titlebar, this);
        mTitle = titleLayout.findViewById(R.id.tv_pageTitle);
        mLeftTextBtn = titleLayout.findViewById(R.id.tv_leftBtn);
        mLeftImageBtn = titleLayout.findViewById(R.id.ib_leftBtn);
        mRightTextBtn = titleLayout.findViewById(R.id.tv_rightBtn);
        mRightImageBtn = titleLayout.findViewById(R.id.ib_rightBtn);
        mRightImageBtnB = titleLayout.findViewById(R.id.ib_rightBtnB);
        View mLineBot = titleLayout.findViewById(R.id.v_lineBot);

        try {
            //max title length
            int maxLength = a.getInteger(R.styleable.TitleBar_mMaxTitleLength, 0);
            if (maxLength > 0)
                mTitle.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
            //title
            String titleValue = a.getString(R.styleable.TitleBar_mTitle);
            mTitle.setText(titleValue != null ? titleValue : "");
            int titleColor = a.getColor(R.styleable.TitleBar_mTitleColor, -123);
            if (titleColor != -123) mTitle.setTextColor(titleColor);
            else mTitle.setTextColor(colorBlack);

            //left menu
            boolean defaultBackIcon = a.getBoolean(R.styleable.TitleBar_mDefaultBack, true);
            //left text menu
            String leftText = a.getString(R.styleable.TitleBar_mLeftText);
            //left image menu
            int leftSrc = a.getResourceId(R.styleable.TitleBar_mLeftSrc, -123);
            if (leftText != null && leftText.length() > 0) {//left text btn
                mLeftTextBtn.setVisibility(VISIBLE);
                mLeftTextBtn.setText(leftText);
            } else if (leftSrc != -123) {
                //left image btn
                mLeftImageBtn.setVisibility(VISIBLE);
                mLeftImageBtn.setImageResource(leftSrc);
            } else if (defaultBackIcon) {
                mLeftImageBtn.setVisibility(VISIBLE);
                mLeftImageBtn.setImageResource(R.drawable.ic_back_blue);
            }
            //点击左边的按钮，关闭页面
            boolean goBack = a.getBoolean(R.styleable.TitleBar_mGoBack, true);
            if (goBack) {
                mLeftImageBtn.setOnClickListener(view -> ((Activity) getContext()).finish());
            }
            //底部分割线，默认不显示
            boolean noLine = a.getBoolean(R.styleable.TitleBar_mNoLine, false);
            if (noLine) {
                mLineBot.setVisibility(INVISIBLE);
            }
            //right text menu
            String rightText = a.getString(R.styleable.TitleBar_mRightText);
            //right image menu
            int rightSrc = a.getResourceId(R.styleable.TitleBar_mRightSrc, 0);
            int rightSrcB = a.getResourceId(R.styleable.TitleBar_mRightSrcB, 0);
            if (rightText != null) {//left text btn
                mRightTextBtn.setVisibility(VISIBLE);
                mRightTextBtn.setText(rightText);
            } else if (rightSrc > 0) {//right image btn
                mRightImageBtn.setVisibility(VISIBLE);
                mRightImageBtn.setImageResource(rightSrc);
            }
            if (rightSrcB > 0) {//another image btn
                mRightImageBtnB.setVisibility(VISIBLE);
                mRightImageBtnB.setImageResource(rightSrcB);
            }
            //right text menu color
            int rTxtColor = a.getColor(R.styleable.TitleBar_mRightTextColor, -123);
            if (rTxtColor != -123) mRightTextBtn.setTextColor(rTxtColor);
            else mRightTextBtn.setTextColor(colorMain);

            //titlebar background color
            int titleBgColor = a.getColor(R.styleable.TitleBar_mBgColor, -1);

            int titleBgRes = a.getResourceId(R.styleable.TitleBar_mBgRes, -1);
            if (titleBgColor == -1 && titleBgRes == -1) setBackgroundColor(Color.WHITE);
            else {
                if (titleBgColor != -1) setBackgroundColor(titleBgColor);
                if (titleBgRes != -1) setBackgroundResource(titleBgRes);
            }
            boolean defineId = a.getBoolean(R.styleable.TitleBar_mDefineId, false);
            if (!defineId) setId(R.id.m_title);
        } finally {
            a.recycle();
        }
    }


    public String getTitle() {
        return mTitle.getText().toString();
    }

    //====title====
    public void setTitle(String title) {
        if (title != null)
            mTitle.setText(title);
    }

    //====left imgbtn menu====
    public void hideLeftImageBtn() {
        if (mLeftImageBtn.getVisibility() != GONE)
            mLeftImageBtn.setVisibility(GONE);
    }

    public void showLeftImageBtn() {
        if (mLeftImageBtn.getVisibility() != VISIBLE)
            mLeftImageBtn.setVisibility(VISIBLE);
    }

    public void setLeftSrc(int res) {
        if (mLeftImageBtn.getVisibility() != VISIBLE) mLeftImageBtn.setVisibility(VISIBLE);
        mLeftImageBtn.setImageResource(res);
    }

    //====left text menu====
    public void hideLeftTextMenu() {
        if (mLeftTextBtn.getVisibility() != GONE)
            mLeftTextBtn.setVisibility(GONE);
    }

    public void showLeftTextMenu() {
        if (mLeftTextBtn.getVisibility() != VISIBLE)
            mLeftTextBtn.setVisibility(VISIBLE);
    }

    public void setLeftText(String text) {
        mLeftTextBtn.setText(text);
    }

    public void setLeftClick(OnClickListener listener) {
        if (mLeftTextBtn.getVisibility() != GONE && listener != null) {
            mLeftTextBtn.setClickable(true);
            mLeftTextBtn.setOnClickListener(listener);
        } else if (mLeftImageBtn.getVisibility() != GONE && listener != null) {
            mLeftImageBtn.setOnClickListener(listener);
        } else {
            LogUtil.e("btn is not visible or listener is null");
        }
    }

    public void setLeftTextListener(OnClickListener listener) {
        mLeftTextBtn.setClickable(true);
        mLeftTextBtn.setOnClickListener(listener);
    }

    //====right imgbtn menu====
    public void setRightSrc(int res) {
        mRightImageBtn.setImageResource(res);
        mRightImageBtn.setVisibility(VISIBLE);
    }

    public void showRightMenu() {
        if (mRightImageBtn.getVisibility() != VISIBLE)
            mRightImageBtn.setVisibility(VISIBLE);
    }

    //====right text menu====
    public TitleBar setRightMenuText(String text) {
        if (mRightTextBtn.getVisibility() != VISIBLE)
            mRightTextBtn.setVisibility(VISIBLE);
        mRightTextBtn.setText(text);
        return this;
    }

    public void hideRightTextMenu() {
        if (mRightTextBtn.getVisibility() != GONE)
            mRightTextBtn.setVisibility(GONE);
    }

    public void showRightTextMenu() {
        if (mRightTextBtn.getVisibility() != VISIBLE)
            mRightTextBtn.setVisibility(VISIBLE);
    }

    public TextView getRightTextBtn() {
        return mRightTextBtn;
    }

    public void setRightClick(OnClickListener listener) {
        if (mRightTextBtn.getVisibility() != GONE && listener != null) {
            mRightTextBtn.setClickable(true);
            mRightTextBtn.setOnClickListener(listener);
        } else if (mRightImageBtn.getVisibility() != GONE && listener != null) {
            mRightImageBtn.setOnClickListener(listener);
        } else {
            LogUtil.e("btn is not visible or listener is null");
        }
    }

    public void setAllRightClick(OnClickListener listener) {
        mRightTextBtn.setOnClickListener(listener);
        mRightImageBtn.setOnClickListener(listener);
    }

    //====right imgbtnb menu====
    public void setSndMenuSrc(int res) {
        mRightImageBtnB.setImageResource(res);
        mRightImageBtnB.setVisibility(VISIBLE);
    }

    public void setSndMenuClick(OnClickListener listener) {
        if (mRightImageBtnB.getVisibility() != GONE && listener != null) {
            mRightImageBtnB.setOnClickListener(listener);
        } else {
            LogUtil.e("btnb is not visible or listener is null");
        }
    }

    public void hideSndMenu() {
        mRightImageBtnB.setVisibility(GONE);
    }

    public void hideRightMenu() {
        mRightImageBtn.setVisibility(GONE);
    }

    public void show() {
        if (getVisibility() != VISIBLE)
            setVisibility(VISIBLE);
    }

    public ImageButton getRightImageBtn() {
        return mRightImageBtn;
    }
}
