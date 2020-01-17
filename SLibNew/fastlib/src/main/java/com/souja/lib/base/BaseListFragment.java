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
import com.souja.lib.R2;
import com.souja.lib.inter.IHttpCallBack;
import com.souja.lib.inter.IListPage;
import com.souja.lib.models.ODataPage;
import com.souja.lib.utils.SHttpUtil;
import com.souja.lib.widget.MLoadingDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseListFragment<T> extends BaseFragment implements IListPage<T> {

    @BindView(R2.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R2.id.smartRefresh)
    SmartRefreshLayout smartRefresh;
    @BindView(R2.id.m_loading)
    MLoadingDialog mLoadingDialog;

    public static final String KEY_REFRESH = "refreshFlag";

    public int pageIndex = 1, pageAmount = 1;
    private boolean enableRefresh = true;
    private boolean bEmptyControl = false;
    private int totalCount = 0;

    public void setEmptyControl(boolean b) {
        bEmptyControl = b;
    }

    public RecyclerView.LayoutManager setLayoutMgr() {
        //默认返回LinearLayoutManger，如需其他类型 重写此方法
        return new LinearLayoutManager(mBaseActivity);
    }

    public void updateList() {
        pageIndex = 1;
        getList(false);
    }

    public void hideLoading() {
        mLoadingDialog.dismiss();
    }

    public T getItem(int i) {
        return (T) ((MBaseAdapter) mRecyclerView.getAdapter()).getItem(i);
    }

    public RecyclerView.Adapter getAdapter() {
        return mRecyclerView.getAdapter();
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

    @Override
    public int setupLayoutRes() {
        return R.layout.frag_list;
    }

    @Override
    public void initMain() {
        ButterKnife.bind(this, _rootView);
        Bundle bd = getArguments();
        if (bd != null) {
            enableRefresh = bd.getBoolean(KEY_REFRESH, true);
        }
        if (!enableRefresh) {
            smartRefresh.setEnableRefresh(false);
            smartRefresh.setEnableLoadMore(false);
        } else {
            smartRefresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
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
        if (retry) mLoadingDialog.setRetryDefaultTip();
        addRequest(SHttpUtil.Request(null, url, setMethod(), setRequestParams(""),
                getTClass(), new IHttpCallBack<T>() {
                    @Override
                    public void OnSuccess(String msg, ODataPage page, ArrayList<T> data) {
                        smartRefresh.finishRefresh();
                        smartRefresh.finishLoadMore();
                        if (pageIndex == 1) {
                            ((MBaseAdapter) mRecyclerView.getAdapter()).clearDataSet();
                            pageAmount = page.getTotalPages();
                            setTotalCount(page.getTotal());
                        }

                        ((MBaseAdapter) mRecyclerView.getAdapter()).reset(data);
                        smartRefresh.setEnableLoadMore(pageIndex < pageAmount);
                        if (pageIndex == 1 && data.size() == 0) {
                            if (!bEmptyControl) mLoadingDialog.showEmptyView();
                            else hideLoading();
                        } else hideLoading();
                        notifyDatasetChanged();
                        onRequestFinish(true, msg);
                    }

                    @Override
                    public void OnFailure(String msg) {
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.setMClick(v -> getList(true));
                            mLoadingDialog.setErrMsgRetry(msg);
                        } else {
                            smartRefresh.finishRefresh();
                            smartRefresh.finishLoadMore();
                            showToast(msg);
                            if (pageIndex > 1) pageIndex--;
                        }
                        onRequestFinish(false, msg);
                    }
                }));
    }

}
