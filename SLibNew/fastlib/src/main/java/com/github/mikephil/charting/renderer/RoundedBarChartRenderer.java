package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.highlight.Range;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class RoundedBarChartRenderer extends BarChartRenderer {

    public RoundedBarChartRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    private float mRadius = 5f;

    public void setmRadius(float mRadius) {
        this.mRadius = mRadius;
    }

    @Override
    protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {

        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        mShadowPaint.setColor(dataSet.getBarShadowColor());

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();


        // initialize the buffer
        BarBuffer buffer = mBarBuffers[index];
        buffer.setPhases(phaseX, phaseY);
        buffer.setDataSet(index);
        buffer.setBarWidth(mChart.getBarData().getBarWidth());
        buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()));

        buffer.feed(dataSet);

        trans.pointValuesToPixel(buffer.buffer);

        // if multiple colors
        if (dataSet.getColors().size() > 1) {
            for (int j = 0; j < buffer.size(); j += 4) {
                if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
                    continue;
                if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                    break;
                if (mChart.isDrawBarShadowEnabled()) {
                    if (mRadius > 0)
                        c.drawRoundRect(new RectF(buffer.buffer[j], mViewPortHandler.contentTop(),
                                buffer.buffer[j + 2],
                                mViewPortHandler.contentBottom()), mRadius, mRadius, mShadowPaint);
                    else
                        c.drawRect(buffer.buffer[j], mViewPortHandler.contentTop(),
                                buffer.buffer[j + 2],
                                mViewPortHandler.contentBottom(), mShadowPaint);
                }
                // Set the color for the currently drawn value. If the index
                // is
                // out of bounds, reuse colors.
                mRenderPaint.setColor(dataSet.getColor(j / 4));
                if (mRadius > 0) {
                    Log.e("DRAWING", "index " + j / 4 + "," + (j / 4 % 2));
                    RectF rectF = new RectF(buffer.buffer[j],
                            buffer.buffer[j + 1],
                            buffer.buffer[j + 2],
                            buffer.buffer[j + 3]);
                    c.drawRect(rectF, mRenderPaint);
//                    mRenderPaint.setColor(Color.RED);
                    if (j / 4 % 2 == 0) {//向下
                        rectF.top = rectF.bottom - 18;
                        rectF.bottom = rectF.top + 30;
                        c.drawArc(rectF, 0, 180, false, mRenderPaint);
                    } else {//向上
                        rectF.top -= 15;
                        rectF.bottom = rectF.top + 35;
                        c.drawArc(rectF, 0, -180, false, mRenderPaint);
                    }
//                    c.drawRoundRect(rectF, mRadius, mRadius, mRenderPaint);
                } else
                    c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                            buffer.buffer[j + 3], mRenderPaint);
            }
        } else {
            mRenderPaint.setColor(dataSet.getColor());
            for (int j = 0; j < buffer.size(); j += 4) {
                if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
                    continue;
                if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                    break;
                if (mChart.isDrawBarShadowEnabled()) {
                    if (mRadius > 0) {
                        c.drawRoundRect(new RectF(buffer.buffer[j], mViewPortHandler.contentTop(),
                                buffer.buffer[j + 2],
                                mViewPortHandler.contentBottom()), mRadius, mRadius, mShadowPaint);
                    } else
                        c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                                buffer.buffer[j + 3], mRenderPaint);
                }
                if (mRadius > 0) {
                    RectF rectF = new RectF(buffer.buffer[j],
                            buffer.buffer[j + 1],
                            buffer.buffer[j + 2],
                            buffer.buffer[j + 3]);
                    c.drawRoundRect(rectF, mRadius, mRadius, mRenderPaint);
//                    c.drawRect(rectF, mRenderPaint);
//                    c.drawArc(rectF, 0, -180, false, mRenderPaint);
                } else
                    c.drawRect(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                            buffer.buffer[j + 3], mRenderPaint);
            }
        }
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {
        BarData barData = mChart.getBarData();
        for (Highlight high : indices) {
            IBarDataSet set = barData.getDataSetByIndex(high.getDataSetIndex());
            if (set == null || !set.isHighlightEnabled())
                continue;
            BarEntry e = set.getEntryForXValue(high.getX(), high.getY());
            if (!isInBoundsX(e, set))
                continue;
            Transformer trans = mChart.getTransformer(set.getAxisDependency());
            mHighlightPaint.setColor(set.getHighLightColor());
            mHighlightPaint.setAlpha(set.getHighLightAlpha());
            boolean isStack = (high.getStackIndex() >= 0 && e.isStacked()) ? true : false;
            final float y1;
            final float y2;
            if (isStack) {
                if (mChart.isHighlightFullBarEnabled()) {
                    y1 = e.getPositiveSum();
                    y2 = -e.getNegativeSum();
                } else {
                    Range range = e.getRanges()[high.getStackIndex()];
                    y1 = range.from;
                    y2 = range.to;
                }
            } else {
                y1 = e.getY();
                y2 = 0.f;
            }
            prepareBarHighlight(e.getX(), y1, y2, barData.getBarWidth() / 2f, trans);
            setHighlightDrawPos(high, mBarRect);
            c.drawRect(mBarRect, mHighlightPaint);

            float t1 = mBarRect.top - 15;
            float b1 = t1 + 35;
            RectF f1 = new RectF(mBarRect.left, t1, mBarRect.right, b1);
            c.drawArc(f1, 0, -180, false, mHighlightPaint);

            float t2 = mBarRect.bottom - 15;
            float b2 = t2 + 25;
            RectF f2 = new RectF(mBarRect.left, t2, mBarRect.right, b2);
            c.drawArc(f2, 0, 180, false, mHighlightPaint);
        }
    }
}
