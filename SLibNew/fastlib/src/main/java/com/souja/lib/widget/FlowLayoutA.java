package com.souja.lib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.souja.lib.R;
import com.souja.lib.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * MyFlowLayout
 * Created by Yangdz on 2015/3/12.
 */

public class FlowLayoutA extends ViewGroup {
    private boolean
            /**
             * 自动计算所有间距
             */
            autoPadding = false,
    /**
     * 自动计算垂直间距
     */
    autoVerPadding = false,
    /**
     * 自动计算水平间距
     */
    autoHorPadding = false,
    /**
     * 垂直居中
     */
    centerVertical = false,
    /**
     * 水平居中
     */
    centerHorizontal = false;

    private int
            /**
             * 一行的列数
             */
            columns,
    /**
     * 容器（即MyFlowLayout）所有方向的padding
     */
    mPadding,
    /**
     * 容器的paddingLeft
     */
    mlPadding,
    /**
     * 容器的的paddingTop
     */
    mtPadding,

    /**
     * 容器的的paddingTop
     */
    mrPadding,
    /**
     * 容器的的paddingBottom
     */
    mbPadding,
    /**
     * 行间距
     */
    vLineMargin,
    /**
     * child水平间距
     */
    hLineMargin,
    /**
     * 最小高度
     */
    minHeight = 0;

    //最终测量的宽度
    private int mFinalWidth;

    public FlowLayoutA(Context context) {
        this(context, null);
    }

    public FlowLayoutA(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayoutA(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initSelf(context, attrs);
    }

    private int maxLine;

    private void initSelf(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlowLayoutA);
        try {
            minHeight = a.getDimensionPixelSize(R.styleable.FlowLayoutA_fla_min_height, 0);
            columns = a.getInt(R.styleable.FlowLayoutA_fla_columns, 0);
            autoPadding = a.getBoolean(R.styleable.FlowLayoutA_fla_auto_padding, false);
            autoVerPadding = a.getBoolean(R.styleable.FlowLayoutA_fla_auto_ver_padding, false);
            autoHorPadding = a.getBoolean(R.styleable.FlowLayoutA_fla_auto_hor_padding, false);
            centerVertical = a.getBoolean(R.styleable.FlowLayoutA_fla_center_vertical, false);
            centerHorizontal = a.getBoolean(R.styleable.FlowLayoutA_fla_center_horizontal, false);
            mPadding = a.getDimensionPixelSize(R.styleable.FlowLayoutA_fla_padding, 0);
            if (mPadding > 0) {
                mlPadding = mtPadding = mrPadding = mbPadding = mPadding;
            } else {
                mlPadding = a.getDimensionPixelSize(R.styleable.FlowLayoutA_fla_left_padding, 0);
                mtPadding = a.getDimensionPixelSize(R.styleable.FlowLayoutA_fla_top_padding, 0);
                mrPadding = a.getDimensionPixelSize(R.styleable.FlowLayoutA_fla_right_padding, 0);
                mbPadding = a.getDimensionPixelSize(R.styleable.FlowLayoutA_fla_bottom_padding, 0);
            }
            vLineMargin = a.getDimensionPixelSize(R.styleable.FlowLayoutA_fla_ver_margin, 0);
            hLineMargin = a.getDimensionPixelSize(R.styleable.FlowLayoutA_fla_hor_margin, 0);
            maxLine = a.getInteger(R.styleable.FlowLayoutA_fla_max_line, -1);
        } finally {
            a.recycle();
        }
    }

//    private int fixIndex = -1;
//    private boolean bFit;
//    private boolean bFixed;

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        LogUtil.e("onMeasure:" + getChildCount());
        // 获得它的父容器为它设置的测量模式和大小
        mFinalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int parentHorPadding = mlPadding + mrPadding,
                parentVerPadding = mtPadding + mbPadding;

//        LogUtil.e("sizeWidth=" + mFinalWidth);

        int finalWidth, finalHeight;//MyFlowLayout最终占据的宽和高
        int width = 0, height = 0,// 如果是warp_content情况下，记录宽和高
                lineWidth = 0,//记录每一行的宽度，width不断取最大宽度
                lineHeight = 0;//每一行的高度，累加至height

        int tempWidth = mFinalWidth;
//        LogUtil.e("temp width:" + tempWidth);

        int position = 0;//可见的child的index
        int childCount = getChildCount();
//        int line = 1;

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);// 测量每一个child的宽和高
                int childWidth = child.getMeasuredWidth();//当前child的宽度
                int childHeight = child.getMeasuredHeight();
//                LogUtil.e("child w" + childWidth + "&h" + childHeight);
                if (i > 0) {
                    childWidth += hLineMargin;//只给非第一个child设置水平间距
                    tempWidth -= hLineMargin;
//                    LogUtil.e("tempWidth--:" + tempWidth);
                }
                int curWidth = lineWidth + childWidth;
                //累加当前child的宽度
                if (curWidth > tempWidth ||//如果超出最大宽度，则保存当前最大宽度并累加height，然后换行
                        //如果设置了一行显示的列数，如果达到了所设定的columns值则换行
                        (columns != 0 && position >= columns && position % columns == 0)) {

                    width = Math.max(width, lineWidth);// 和之前的行的宽度比较，取最大的
                    height += lineHeight;// 叠加换行前最高的child的高度
                    height += vLineMargin;// 叠加行间距

                    // 记录下一行开始的宽度和高度
                    lineWidth = childWidth;
                    lineHeight = childHeight;
                    tempWidth = mFinalWidth;
//                    line++;
//                    LogUtil.e("measure换行，重置temp width:" + tempWidth + ",lineHeight:" + lineHeight);

/*
                    //如果设置了最大行数maxLine,则判断当前line是否达到临界值
                    if (maxLine > 0 && line > maxLine && !bFixed) {
                        LogUtil.e("达到maxLine,quit measure");

                        int lastWidth = lineWidth + 60;
                        if (lastWidth > tempWidth) {//如果加上LastView后宽度超标，则移除上一次添加的child
                            LogUtil.e("加上Last超出");
                            View childPre = getChildAt(i - 1);
                            measureChild(childPre, widthMeasureSpec, heightMeasureSpec);// 测量每一个child的宽和高
                            int childWidthPre = childPre.getMeasuredWidth();//当前child的宽度
                            lineWidth -= childWidthPre;
                            width = Math.max(width, lineWidth);
                        } else {
                            LogUtil.e("加上Last合适");
                            width = lastWidth;
                            bFit = true;
                        }
                        fixIndex = i;
                        break;
                    }
*/
                } else {
//                    LogUtil.e("measure不换行,lineHeight:" + lineHeight);
                    lineWidth += childWidth;//把当前child宽度累加给行宽
                    lineHeight = Math.max(lineHeight, childHeight);//取当前最高child的高度作为行高
                }

                if (i == childCount - 1) {
//                    LogUtil.e("measure最后一个");
                    width = Math.max(width, lineWidth);
                    height += lineHeight;
                }
                position++;
            }
        }

        finalWidth = (modeWidth == MeasureSpec.EXACTLY ? mFinalWidth : width) + parentHorPadding;
        finalHeight = (modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height) + parentVerPadding;
        finalWidth = finalWidth > mFinalWidth ? mFinalWidth : finalWidth;

        //如果设置了最小高度，当总高度小于最小高度时，取最小高度的值
        if (finalHeight < minHeight) finalHeight = minHeight;
//        LogUtil.e("finalHeight:" + finalHeight);
        setMeasuredDimension(finalWidth, finalHeight);
//        if (fixIndex > 0)
//            resetViews();
    }

    @Override
    public void addView(View child) {
        super.addView(child);

    }

    //e.g. childCount=10  fixIndex=8
    //bFit=true removeCount=1
    //bFit=false removeCount=2
   /* public void resetViews() {
        int removeCount = getChildCount() - fixIndex;
        if (bFit) {
            removeCount += 1;
        }
        for (int i = 0; i < removeCount; i++) {
            removeViewAt(getChildCount() - 1);
        }
        bFixed = true;
    }*/

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int maxWidth = getMeasuredWidth();
        int maxHeight = getMeasuredHeight();

        int childCount = getChildCount();

        int averageVerMargin = 0;//CENTER-VERTICAL时，margin-top平均值
        int mPainterPosX = left;  //绘图坐标x
        int mPainterPosY = 0;  //绘图坐标y
        int maxChildHeightInLine = 0;//每一行中最高的child的高度
        int totalLineHeight = 0;//所有行的高度之和
        int position = 0; //child index
        int line = 1;//布局行数

        maxWidth -= (mlPadding + mrPadding);//可用的总宽度要减去容器的水平padding

        mPainterPosY += mtPadding;

        List<List<View>> children = new ArrayList<>(); //容器所有的child
        List<View> horLineViews = new ArrayList<>(); //一横行的child
        List<Integer> tops = new ArrayList<>();//开始绘制每一行时的坐标y

        tops.add(mPainterPosY);//先把第一行的加进去~

//        LogUtil.e("left=" + left);
//        LogUtil.e("mFinalWidth=" + mFinalWidth);
        int maxX = left + mFinalWidth - mrPadding;
//        LogUtil.e("maxX=" + maxX);

        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            childView.setTag(i);

            if (childView.getVisibility() != View.GONE) {
                int width = childView.getMeasuredWidth();
                int height = childView.getMeasuredHeight();
//                LogUtil.e("childWidth:" + width);

                if (i > 0) {
                    mPainterPosX += hLineMargin;
                    maxX -= hLineMargin;
                }
//                LogUtil.e("mPx " + mPainterPosX);
                int curX = mPainterPosX + width;
//                LogUtil.e("curX " + curX);
                if (curX > maxX || //如果剩余的空间不够，则移到下一行开始位置
                        (columns != 0 && position >= columns && position % columns == 0)) {
//                    LogUtil.e("换行");
                    maxX = left + mFinalWidth - mrPadding;//重置最大x
//                    LogUtil.e("重置x:" + maxX);
                    children.add(horLineViews);

                    horLineViews = new ArrayList<>();
                    horLineViews.add(childView);

                    //记录新行开始的x和y
                    mPainterPosX = left;
                    mPainterPosY += maxChildHeightInLine;
                    mPainterPosY += vLineMargin;
                    totalLineHeight += maxChildHeightInLine;
                    maxChildHeightInLine = height;
//                    LogUtil.e("paint y:" + mPainterPosY);
                    tops.add(mPainterPosY);
                    line++;
//                    if (maxLine > 0 && line > maxLine) break;
                } else {
//                    LogUtil.e("不换行");
                    horLineViews.add(childView);
                    maxChildHeightInLine = Math.max(height, maxChildHeightInLine);
                }

                if (i == childCount - 1) {
//                    LogUtil.e("最后一个");
                    children.add(horLineViews);
                    mPainterPosY += maxChildHeightInLine;
                    tops.add(mPainterPosY);
                    totalLineHeight += maxChildHeightInLine;
                    int size = centerVertical ? 2 : children.size() + 1;
                    averageVerMargin = (maxHeight - totalLineHeight) / size;
                }
                mPainterPosX += width;
                position++;
            }
        }

        //所有行中，每个view的平均marginLeft（autoPadding 和 autoHorPadding时需要）
        int[] leftMargins = new int[children.size()];

        //只有设置了这几个属性后才会计算
        if (centerHorizontal || autoPadding || autoHorPadding) {
            //计算每一行的每个view的平均margin-left
            for (int i = 0; i < children.size(); i++) {
                List<View> rowViews = children.get(i); //获取第i行的所有view

                int col;//计算当前行有多少列
                if (columns == 0) {
                    col = rowViews.size() - 1;
                } else {
                    col = columns - 1;
                }

                //如果计算结果中列数为0（即这行只有1个view），为了计算的时候不为0，默认2列
                if (col == 0) col = 2;

                int lineWidth_ = 0;//计算当前行，所有view的宽度
                for (int j = 0; j < rowViews.size(); j++) {
                    lineWidth_ += rowViews.get(j).getMeasuredWidth();
                }
                //当前行的平均marginLeft=（总宽度-当前行所有view宽度）÷ 列数
                leftMargins[i] = (maxWidth - lineWidth_) / col;
            }

        }


        //绘图时x，y坐标
//        int mPx = left + mlPadding, mPy;
        int mPx = mlPadding, mPy;
//        LogUtil.e("绘图mPx:" + mPx);
        for (int i = 0; i < children.size(); i++) {
            List<View> oneLine = children.get(i);
            mPy = tops.get(i);
//            LogUtil.e("绘图mPy:" + mPy);

            if (centerVertical) mPy += averageVerMargin;
            else if ((autoPadding || autoVerPadding) && vLineMargin <= 0)
                mPy += (averageVerMargin + averageVerMargin * i);

            for (int j = 0; j < oneLine.size(); j++) {
                View v = oneLine.get(j);
                int width = v.getMeasuredWidth();
                int height = v.getMeasuredHeight();

                if (width == 0) width = (int) (60f * ScreenUtil.mScale);
                if (height == 0) height = (int) (72f * ScreenUtil.mScale);
//                LogUtil.e("view w&h:" + width + "&" + height);
                if (centerHorizontal) {
                    if (j == 0) mPx += leftMargins[i];
                } else if (autoPadding || autoHorPadding) {
                    if (j > 0) mPx += leftMargins[i];
                }
                if (hLineMargin > 0) {
                    if (j > 0) mPx += hLineMargin;
                }
//                LogUtil.e("mpx " + mPx);
                //执行ChildView的绘制
                v.layout(mPx, mPy, mPx + width, mPy + height);
                //记录当前已经绘制到的横坐标位置
                mPx += width;
//                LogUtil.e("mPx2:" + mPx);
            }
            mPx = mlPadding;
//            LogUtil.e("mPx3:" + mPx);
        }
    }

}