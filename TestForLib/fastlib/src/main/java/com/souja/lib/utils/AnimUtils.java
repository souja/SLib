package com.souja.lib.utils;

import android.view.View;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.IntEvaluator;
import com.nineoldandroids.animation.ValueAnimator;

/**
 * Created by wangyue on 2015.07.23.
 */
public class AnimUtils {

    /**
     *
     * @param target 目标view
     * @param start 初始高度
     * @param end 结束高度
     * @param duration 动画时间
     * @param listener 监听事件 ，can be null
     */
    public void startHeightAnimate(final View target, final int start, final int end, int duration, Animator.AnimatorListener listener) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(1, 100);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            //持有一个IntEvaluator对象，方便下面估值的时候使用
            private IntEvaluator mEvaluator = new IntEvaluator();

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                //获得当前动画的进度值，整型，1-100之间
                int currentValue = (Integer)animator.getAnimatedValue();
                //计算当前进度占整个动画过程的比例，浮点型，0-1之间
                float fraction = currentValue / 100f;
                //直接调用整型估值器通过比例计算出宽度，然后再设给view
                target.getLayoutParams().height = mEvaluator.evaluate(fraction, start, end);
                target.requestLayout();
            }
        });
        if(listener!=null)
            valueAnimator.addListener(listener);
        valueAnimator.setDuration(duration).start();
    }


}
