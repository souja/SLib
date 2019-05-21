package com.souja.lib.widget.dragsort;

/**
 * 側滑以及長按拖动事件
 */
public interface OnMoveAndSwipedListener {
    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int pos);
}