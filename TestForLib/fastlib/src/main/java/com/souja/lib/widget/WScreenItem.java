package com.souja.lib.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.souja.lib.R;

/**
 * Created by Ydz on 2017/5/26 0026.
 */

public class WScreenItem extends LinearLayout {
    public WScreenItem(Context context) {
        super(context);
        init(context, null);
    }

    public WScreenItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public WScreenItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    TextView tvName;
    ImageView ivArrowUp;
    ImageView ivArrowDown;
    LinearLayout layoutArrow;

    private int txtFocus, txtBlur;
    private String name;
    private boolean bNoArrow;

    private void initViews() {
        tvName = findViewById(R.id.tv_name);
        ivArrowUp = findViewById(R.id.iv_arrowUp);
        ivArrowDown = findViewById(R.id.iv_arrowDown);
        layoutArrow = findViewById(R.id.layout_arrow);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.widget_screen_item, this);
        initViews();
        setClickable(true);
        Resources res = getResources();
        txtFocus = res.getColor(R.color.main_color);
        txtBlur = res.getColor(R.color.grey_566267);


        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WScreenItem);
            try {
                name = a.getString(R.styleable.WScreenItem_mItemName);
                if (name != null && !name.isEmpty())
                    tvName.setText(name);
                bNoArrow = a.getBoolean(R.styleable.WScreenItem_mNoArrow, false);
                if (bNoArrow) {
                    layoutArrow.setVisibility(GONE);
                }
            } finally {
                a.recycle();
            }
        }
    }

    /**
     * 0-未选中；1-选中，降序；2-选中，升序
     */
    private int status = 0;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
        handleOnStatusChange();
    }

    //改变选中和未选中
    public void toggleFocus() {
        if (status == 0) {
            setStatus(1);
        } else {
            setStatus(0);
        }
    }

    //改变排序方式
    public void toggleWay() {
        if (status == 1) {
            setStatus(2);
        } else {
            setStatus(1);
        }
    }

    private void handleOnStatusChange() {
        switch (status) {
            case 1://降序
                ivArrowDown.setBackgroundResource(R.drawable.ic_arrow_down_focus);
                ivArrowUp.setBackgroundResource(R.drawable.ic_arrow_up_blur);
                tvName.setTextColor(txtFocus);
                break;
            case 2://升序
                ivArrowDown.setBackgroundResource(R.drawable.ic_arrow_down_blur);
                ivArrowUp.setBackgroundResource(R.drawable.ic_arrow_up_focus);
                tvName.setTextColor(txtFocus);
                break;
            default://未选中
                ivArrowDown.setBackgroundResource(R.drawable.ic_arrow_down_blur);
                ivArrowUp.setBackgroundResource(R.drawable.ic_arrow_up_blur);
                tvName.setTextColor(txtBlur);
        }

    }
}