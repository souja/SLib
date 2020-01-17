package com.souja.lib.widget;

import android.content.Context;
import android.view.View;
import android.widget.Checkable;

import com.souja.lib.R;


public class MCheckableView extends View implements Checkable {
    public MCheckableView(Context context) {
        super(context);
        setBackgroundResource(R.drawable.shape_indicator_uncheck);
    }

    private boolean mChecked;

    @Override
    public void setChecked(boolean checked) {
        mChecked = checked;
        setupBg();
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        mChecked = !mChecked;
        setupBg();
    }

    private void setupBg() {
        if (mChecked) setBackgroundResource(R.drawable.shape_indicator_checked);
        else setBackgroundResource(R.drawable.shape_indicator_uncheck);
    }
}
