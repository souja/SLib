package com.souja.lib.base;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.souja.lib.R;
import com.souja.lib.inter.IHttpCallBack;
import com.souja.lib.inter.IListPage;
import com.souja.lib.models.ODataPage;
import com.souja.lib.utils.SHttpUtil;

import java.util.ArrayList;

public abstract class BaseListLazyFragment<T> extends BaseLazyFragment implements IListPage<T> {

    public static final String KEY_REFRESH = "refreshFlag";

    public void setEmptyControl(boolean b) {
        bEmptyControl = b;
    }

    public int pageIndex = 1, pageAmount = 1;
    private boolean enableRefresh = true;
    private boolean bEmptyControl = false;
    private int totalCount = 0;

    public RecyclerView mRecyclerView;
    public SmartRefreshLayout mRefreshLayout;

    public void updateList() {
        pageIndex = 1;
        getList(false);
    }

    public RecyclerView.LayoutManager setLayoutMgr() {
        //默认返回LinearLayoutManger，如需其他类型 重写此方法
        return new LinearLayoutManager(mBaseActivity);
    }

    public void notifyDatasetChanged() {
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    public void setTotalCount(int count) {
        totalCount = count;
    }

    public int getTotalCount() {
        return totalCount;
    }

    private void initViews() {
        mRecyclerView = _rootView.findViewById(R.id.recyclerView);
        mRefreshLayout = _rootView.findViewById(R.id.smartRefresh);
    }

    @Override
    public int setFragContentViewRes() {
        return R.layout.frag_list_lazy;
    }

    @Override
    public void initPage() {
        initViews();

        Bundle bd = getArguments();
        if (bd != null) {
            enableRefresh = bd.getBoolean(KEY_REFRESH, true);
        }
        if (!enableRefresh) {
            mRefreshLayout.setEnableRefresh(false);
            mRefreshLayout.setEnableLoadMore(false);
        } else {
            mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    if (pageIndex < pageAmount) {
                        pageIndex++;
                        getList(false);
                    }
                }

                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    updateList();
                }
            });
        }
        mRecyclerView.setLayoutManager(setLayoutMgr());
        setAdapter(mRecyclerView);
        getList(false);
    }

    public void getList(boolean retry) {
        String url = setRequestUrl(pageIndex);
        if (TextUtils.isEmpty(url)) return;

        if (retry) setRetryDefaultTip();

        addRequest(SHttpUtil.Request(null, url, setMethod(), setRequestParams(""),
                getTClass(), new IHttpCallBack<T>() {
                    @Override
                    public void OnSuccess(String msg, ODataPage page, ArrayList<T> data) {
                        mRefreshLayout.finishRefresh();
                        mRefreshLayout.finishLoadMore();
                        if (pageIndex == 1) {
                            ((MBaseAdapter) mRecyclerView.getAdapter()).clearDataSet();
                            pageAmount = page.getTotalPages();
                            setTotalCount(page.getTotal());
                        }

                        ((MBaseAdapter) mRecyclerView.getAdapter()).reset(data);
                        mRefreshLayout.setEnableLoadMore(pageIndex < pageAmount);
                        if (pageIndex == 1 && data.size() == 0) {
                            if (!bEmptyControl) ShowEmptyView();
                            else ShowContentView();
                        } else ShowContentView();
                        notifyDatasetChanged();
                        onRequestFinish(true, msg);
                    }

                    @Override
                    public void OnFailure(String msg) {
                        if (isProgressing()) {
                            setMClick(v -> getList(true));
                            setErrMsgRetry(msg);
                        } else {
                            mRefreshLayout.finishRefresh();
                            mRefreshLayout.finishLoadMore();
                            showToast(msg);
                            if (pageIndex > 1) pageIndex--;
                        }
                        onRequestFinish(false, msg);
                    }
                }));
    }
}
