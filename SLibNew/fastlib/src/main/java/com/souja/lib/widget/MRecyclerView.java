package com.souja.lib.widget;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Yangdz on 2016/8/30 0030.
 */
public class MRecyclerView extends RecyclerView {
    public MRecyclerView(Context context) {
        super(context);
        init();
    }

    public MRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setHasFixedSize(true);
    }

    private OnLoadMoreListener mListener;
    private boolean loading = false;

    public void loadingComplete() {
        if (loading)
            loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mListener = listener;
        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                LogUtil.e("state:" + newState + ",SCROLL_STATE_IDLE:" + SCROLL_STATE_IDLE);
//                if (newState == SCROLL_STATE_IDLE)
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mListener != null) {
                    LayoutManager manager = recyclerView.getLayoutManager();
                    int lastVisibleItemPosition;
//                    if (manager instanceof LinearLayoutManager) {
//                    } else {
//                        lastVisibleItemPosition = ((GridLayoutManager) manager).findLastCompletelyVisibleItemPosition();
//                        LogUtil.e("G-lastVisibleItemPosition:" + lastVisibleItemPosition);
//                    }
                    lastVisibleItemPosition = ((LinearLayoutManager) manager).findLastCompletelyVisibleItemPosition();
//                    LogUtil.e("LastVisibleItemPosition:" + lastVisibleItemPosition);
                    int totalItemCount = getAdapter().getItemCount();
//                    LogUtil.e("totalItemCount:" + totalItemCount);
                    if (lastVisibleItemPosition == totalItemCount - 1) {
                        if (!loading) {
                            loading = true;
//                            LogUtil.e("loading next page");
                            mListener.onLoadMore();
                        }
                    }
                }

            }
        });
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
