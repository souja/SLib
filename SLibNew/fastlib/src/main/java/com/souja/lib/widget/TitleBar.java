package com.souja.lib.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.souja.lib.R;

import org.xutils.common.util.LogUtil;


/**
 * titlebar
 * Created by ydz on 2017/03/08.
 */
public class TitleBar extends RelativeLayout {

    public TitleBar(Context context) {
        this(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }


    private ImageView mLeftImage,//left image btn
            mRightImage,//right image btn
            mRightImageB;//another right image btn ,to the left of mRightImage

    private TextView tvTitle,//title text
            leftMenuText,//left text btn
            rightMenuText;//right text btn

    private View underLine, leftMenu, rightMenu, rightMenuB;

    private void initViews() {
        tvTitle = findViewById(R.id.tv_pageTitle);
        leftMenuText = findViewById(R.id.tv_left);
        mLeftImage = findViewById(R.id.iv_Left);
        rightMenuText = findViewById(R.id.tv_right);
        mRightImage = findViewById(R.id.iv_right);
        mRightImageB = findViewById(R.id.iv_rightB);
        underLine = findViewById(R.id.v_lineBot);
        leftMenu = findViewById(R.id.left_menu);
        rightMenu = findViewById(R.id.right_menu);
        rightMenuB = findViewById(R.id.right_menuB);
    }

    private void init(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_titlebar, this);
        initViews();
        initProperties(attrs);
    }

    public void initProperties(AttributeSet attrs) {
        int colorBlack = getContext().getResources().getColor(R.color.black_33);
        int colorMain = getResources().getColor(R.color.main_color);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TitleBar);

        try {
            //Title Max length
            int maxLength = a.getInteger(R.styleable.TitleBar_tb_max_title_length, 0);
            if (maxLength > 0)
                tvTitle.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
            //Title
            String titleValue = a.getString(R.styleable.TitleBar_tb_title);
            tvTitle.setText(titleValue != null ? titleValue : "");
            int titleColor = a.getColor(R.styleable.TitleBar_tb_title_color, -123);
            if (titleColor != -123) tvTitle.setTextColor(titleColor);
            else tvTitle.setTextColor(colorBlack);

            //Left default image menu
            boolean defaultBackIcon = a.getBoolean(R.styleable.TitleBar_tb_default_back, true);
            //Left text menu
            String leftText = a.getString(R.styleable.TitleBar_tb_left_text);
            if (leftText != null && leftText.length() > 0) {//left text btn
                showLeftMenu();
                leftMenuText.setText(leftText);
            } else {
                //Left image menu
                int leftSrc = a.getResourceId(R.styleable.TitleBar_tb_left_src, -123);
                if (leftSrc != -123) {
                    //Left image btn
                    showLeftMenu();
                    mLeftImage.setImageResource(leftSrc);
                } else if (defaultBackIcon) {
                    showLeftMenu();
                    mLeftImage.setImageResource(R.drawable.ic_back_blue_bb);
                    //点击左边的按钮，关闭页面
                    boolean goBack = a.getBoolean(R.styleable.TitleBar_tb_go_back, true);
                    if (goBack) {
                        leftMenu.setOnClickListener(view -> ((Activity) getContext()).finish());
                    }
                }
            }
            //Right text menu color
            int rTxtColor = a.getColor(R.styleable.TitleBar_tb_right_text_color, -123);
            if (rTxtColor != -123) rightMenuText.setTextColor(rTxtColor);
            else rightMenuText.setTextColor(colorMain);
            //Right text menu
            String rightText = a.getString(R.styleable.TitleBar_tb_right_text);
            if (rightText != null) {//left text btn
                showRightMenu();
                rightMenuText.setText(rightText);
            } else {
                //Right image menu
                int rightSrc = a.getResourceId(R.styleable.TitleBar_tb_right_src, 0);
                if (rightSrc != 0) {//right image btn
                    showRightMenu();
                    mRightImage.setImageResource(rightSrc);
                }
            }
            //Right image menuB
            int rightSrcB = a.getResourceId(R.styleable.TitleBar_tb_right_src_b, 0);
            if (rightSrcB != 0) {//another image btn
                showRightSecondMenu();
                mRightImageB.setImageResource(rightSrcB);
            }
            //底部分割线，默认显示
            boolean noLine = a.getBoolean(R.styleable.TitleBar_tb_no_line, false);
            if (noLine) {
                underLine.setVisibility(INVISIBLE);
            }

            //TitleBar background color
            int titleBgColor = a.getColor(R.styleable.TitleBar_tb_bg_color, -1);
            int titleBgRes = a.getResourceId(R.styleable.TitleBar_tb_bg_res, -1);
            if (titleBgColor == -1 && titleBgRes == -1) setBackgroundColor(Color.WHITE);
            else {
                if (titleBgColor != -1) setBackgroundColor(titleBgColor);
                if (titleBgRes != -1) setBackgroundResource(titleBgRes);
            }
            //是否自定义控件id
            boolean defineId = a.getBoolean(R.styleable.TitleBar_tb_define_id, false);
            if (!defineId) setId(R.id.m_title);
        } finally {
            a.recycle();
        }
    }

    public void setUnderlineVisible(boolean visible) {
        underLine.setVisibility(visible ? VISIBLE : INVISIBLE);
    }

    //====Title====
    public void setTitle(String tvTitle) {
        if (tvTitle != null)
            this.tvTitle.setText(tvTitle);
    }

    public void setTitle(int titleRes) {
        if (titleRes != 0)
            this.tvTitle.setText(titleRes);
    }

    public String getTitle() {
        return tvTitle.getText().toString();
    }

    //==== Left menu====
    public void setLeftMenu(int res) {
        showLeftMenu();
        mLeftImage.setImageResource(res);
    }

    public void setLeftMenu(String text) {
        showLeftMenu();
        leftMenuText.setText(text);
    }

    public void setLeftClick(OnClickListener listener) {
        if (leftMenu.getVisibility() != GONE && listener != null) {
            leftMenu.setOnClickListener(listener);
        } else {
            LogUtil.e("btn is not visible or listener is null");
        }
    }

    public void showLeftMenu() {
        if (leftMenu.getVisibility() != VISIBLE)
            leftMenu.setVisibility(VISIBLE);
    }

    public void hideLeftMenu() {
        if (leftMenu.getVisibility() != GONE)
            leftMenu.setVisibility(GONE);
    }

    //====Right menu====
    public void setRightMenuImg(int res) {
        showRightMenu();
        mRightImage.setImageResource(res);
    }

    public void setRightMenuText(String text) {
        showRightMenu();
        rightMenuText.setText(text);
    }

    public void setRightMenuText(int text) {
        showRightMenu();
        rightMenuText.setText(text);
    }

    public void setRightClick(OnClickListener listener) {
        if (rightMenu.getVisibility() != GONE && listener != null) {
            rightMenu.setOnClickListener(listener);
        } else {
            LogUtil.e("btn is not visible or listener is null");
        }
    }

    public void showRightMenu() {
        if (rightMenu.getVisibility() != VISIBLE)
            rightMenu.setVisibility(VISIBLE);
    }

    public void hideRightMenu() {
        if (rightMenu.getVisibility() != GONE)
            rightMenu.setVisibility(GONE);
    }

    public TextView getRightMenuText() {
        return rightMenuText;
    }

    public ImageView getRightMenuImage() {
        return mRightImage;
    }

    //====Right menu b====
    public void setRightSecondMenu(int res) {
        mRightImageB.setImageResource(res);
    }

    public void setRightSecondMenuClick(OnClickListener listener) {
        if (rightMenuB.getVisibility() != GONE && listener != null) {
            rightMenuB.setOnClickListener(listener);
        } else {
            LogUtil.e("btnb is not visible or listener is null");
        }
    }

    public void showRightSecondMenu() {
        if (rightMenuB.getVisibility() != VISIBLE)
            rightMenuB.setVisibility(VISIBLE);
    }

    public void hideUnderLine() {
        underLine.setVisibility(GONE);
    }
//    public void hideRightMenuB() {
//        if (rightMenuB.getVisibility() != GONE)
//            rightMenuB.setVisibility(GONE);
//    }
//    public void show() {
//        if (getVisibility() != VISIBLE)
//            setVisibility(VISIBLE);
//    }
//    public void hide() {
//        if (getVisibility() != GONE)
//            setVisibility(GONE);
//    }

}
