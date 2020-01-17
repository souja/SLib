package com.souja.lib.tools;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.souja.lib.R;

public class HiddenAnimUtils {
    private View hideView;//需要展开隐藏的布局，开关控件
    private ImageView arrowView;

    /**
     * 构造器(可根据自己需要修改传参)
     *
     * @param hideView  需要隐藏或显示的布局view
     * @param arrowView 按钮开关的view
     */
    public static HiddenAnimUtils newInstance(View hideView, ImageView arrowView) {
        return new HiddenAnimUtils(hideView, arrowView);
    }

    private HiddenAnimUtils(View hideView, ImageView arrowView) {
        this.hideView = hideView;
        this.arrowView = arrowView;
    }

    /**
     * 开关
     */
    public void toggle() {
        startArrowAnimation();
        startViewAnimation();
    }

    /**
     * arrow rotate
     */
    private void startArrowAnimation() {
        //旋转动画
        RotateAnimation animation;
        boolean openUp = View.GONE == hideView.getVisibility();
        if (openUp) {
            animation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        } else {
            animation = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        }
        animation.setDuration(100);//设置动画持续时间
        animation.setInterpolator(new LinearInterpolator());
//        animation.setRepeatMode(Animation.REVERSE);//设置反方向执行
//        animation.setFillAfter(true);//动画执行完后是否停留在执行完的状态
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (openUp) {
                    arrowView.setImageResource(R.drawable.ic_a_s_up);
                }else{
                    arrowView.setImageResource(R.drawable.ic_a_s_down);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        arrowView.startAnimation(animation);
    }


    /**
     * view scale
     */
    private void startViewAnimation() {
        //旋转动画
        ScaleAnimation animation;
        if (View.GONE == hideView.getVisibility()) {
            animation = new ScaleAnimation(1, 1, 0, 1);
        } else {
            animation = new ScaleAnimation(1, 1, 1, 0);
        }
        animation.setDuration(100);//设置动画持续时间
        animation.setInterpolator(new LinearInterpolator());
//        animation.setRepeatMode(Animation.RELATIVE_TO_SELF);//设置反方向执行
        animation.setFillAfter(true);//动画执行完后是否停留在执行完的状态

        if (View.GONE == hideView.getVisibility()) {
            hideView.setVisibility(View.VISIBLE);
            hideView.startAnimation(animation);
        } else {
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    hideView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            hideView.startAnimation(animation);
        }
    }
}
