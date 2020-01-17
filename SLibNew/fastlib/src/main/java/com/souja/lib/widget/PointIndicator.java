package com.souja.lib.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.souja.lib.R;

/**
 * Created by wangyue on 15-4-9.
 */
public class PointIndicator extends View {
    private static final int NORMAL_COLOR = R.color.grey_75;
    private static final int SELECT_COLOR = R.color.grey_ee;
    private static final int DEFAULT_SPACING = 20;

    private Paint mPointPaint;
    private int mPointNO;
    private int mSpacing;
    private int mCurrentNO;

    private ViewPager mViewPager;

    private ViewPager.OnPageChangeListener mOnPageChangeListener;

    public PointIndicator(Context context) {
        this(context, null);
    }

    public PointIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPointPaint = new Paint();
        mPointPaint.setAntiAlias(true);
        mPointPaint.setStyle(Paint.Style.FILL);

        mPointNO = 1;
        mSpacing = DEFAULT_SPACING;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //只有一张图片的时候 不绘制
        if(mPointNO <= 1){
            return;
        }

        int cY = (getMeasuredHeight()-getPaddingTop()-getPaddingBottom())/2;
        int ratio = cY;
        int firstCX = getMeasuredWidth()/2 - ratio*(mPointNO-1) - mSpacing/2*(mPointNO-1);

        for (int i=0 ; i< mPointNO; i++){
            int currentCX = firstCX + mSpacing*i + ratio*2*i;
            if(i == mCurrentNO){
                drawPoint(canvas,true,currentCX,cY,cY);
            }else {
                drawPoint(canvas,false,currentCX,cY,cY);
            }
        }
    }

    private void drawPoint(Canvas canvas, boolean selected, float x, float y, float ratio){
        if(selected){
            mPointPaint.setColor(getResources().getColor(SELECT_COLOR));
            canvas.drawCircle(x, y, ratio, mPointPaint);
        }else {
            mPointPaint.setColor(getResources().getColor(NORMAL_COLOR));
            canvas.drawCircle(x, y, ratio/2, mPointPaint);
        }
    }

    public void setViewPager(ViewPager viewPager){
        mViewPager = viewPager;
        PagerAdapter adapter = viewPager.getAdapter();
        if(adapter == null){
            throw new IllegalArgumentException("viewPager adapter is null");
        }
        if(adapter.getCount() == 0){
            return;
        }

        notifyDataSetChanged();

    }

    public void notifyDataSetChanged(){
        if(mViewPager.getAdapter().getCount() <= 1){
            return;
        }

        mPointNO = mViewPager.getAdapter().getCount();
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(mOnPageChangeListener!= null){
                    mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if(mOnPageChangeListener != null){
                    mOnPageChangeListener.onPageSelected(position);
                }
                mCurrentNO = mViewPager.getCurrentItem();
                postInvalidate();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(mOnPageChangeListener != null){
                    mOnPageChangeListener.onPageScrollStateChanged(state);
                }
            }
        });
        postInvalidate();
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener){
        mOnPageChangeListener = onPageChangeListener;
    }

}
