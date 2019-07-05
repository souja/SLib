package com.souja.lib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.Checkable;

import com.souja.lib.R;


public class MCheckableImageView extends AppCompatImageView implements Checkable {

    public MCheckableImageView(Context context) {
        this(context, null);
    }

    public MCheckableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MCheckableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MCheckableImageView);
        boolean checked = a.getBoolean(R.styleable.MCheckableImageView_bCheck, false);
        if (checked) {
            setChecked(true);
        }
    }

    private boolean mChecked;
    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};

    @Override
    public int[] onCreateDrawableState(final int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void setChecked(final boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            refreshDrawableState();
        }
    }
}
