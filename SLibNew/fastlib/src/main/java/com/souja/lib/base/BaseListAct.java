package com.souja.lib.base;

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
import com.souja.lib.widget.TitleBar;

import java.util.ArrayList;

import butterknife.BindView;


/**
 * 列表类型页面基类，如“我的招聘”列表页面
 * 页面结构：TitleBar + SmartRefresh + RecyclerView + LoadingDialog
 */
public abstract class BaseListAct<T> extends ActBaseLoading implements IListPage<T> {

    @BindView(R2.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R2.id.smartRefresh)
    SmartRefreshLayout mSmartRefresh;


    public int pageIndex = 1, pageAmount = 1;

    private int totalCount = 0;

    @Override
    public int setupContentRes() {
        return R.layout.refresh_recyclerview;
    }

    @Override
    public void initContentView() {
        setupTitle(mTitleBar);
        mRecyclerView.setLayoutManager(getLayoutMgr());
        setAdapter(mRecyclerView);
        mSmartRefresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (pageIndex < pageAmount) {
                    pageIndex++;
                    getList(false);
                }
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                updateList(false);
            }
        });
        getList(false);
    }

    //设置页面标题
    public abstract void setupTitle(TitleBar titleBar);

    //设置适配器布局类型，如LinearLayoutManager（线性布局）、GridLayoutManager（网格布局）。
    public RecyclerView.LayoutManager getLayoutMgr() {
        //默认返回LinearLayoutManger，如需其他类型 重写此方法进行修改
        return new LinearLayoutManager(_this);
    }

    public void notifyDatasetChanged() {
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    public void updateList(boolean autorefresh) {
        if (autorefresh) mSmartRefresh.autoRefresh();
        pageIndex = 1;
        getList(false);
    }

    public void setTotalCount(int count) {
        totalCount = count;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void getList(boolean retry) {
        String requestUrl = setRequestUrl(pageIndex);
        if (TextUtils.isEmpty(requestUrl)) return;

        if (retry) showLoading();

        addRequest(SHttpUtil.Request(null, requestUrl, setMethod(), setRequestParams(""),
                getTClass(), new IHttpCallBack<T>() {
                    @Override
                    public void OnSuccess(String msg, ODataPage page, ArrayList<T> data) {
                        mSmartRefresh.finishRefresh();
                        mSmartRefresh.finishLoadMore();
                        if (pageIndex == 1) {
                            ((MBaseAdapter) mRecyclerView.getAdapter()).clearDataSet();
                            pageAmount = page.getTotalPages();
                            setTotalCount(page.getTotal());
                        }

                        ((MBaseAdapter) mRecyclerView.getAdapter()).reset(data);
                        mSmartRefresh.setEnableLoadMore(pageIndex < pageAmount);
                        hideLoading();
                        notifyDatasetChanged();
                        onRequestFinish(true, msg);
                    }

                    @Override
                    public void OnFailure(String msg) {
                        if (isDialogShowing()) {
                            whiteBg.setOnClickListener(v -> getList(true));
                            showErrorView(msg);
                        } else {
                            mSmartRefresh.finishRefresh();
                            mSmartRefresh.finishLoadMore();
                            showToast(msg);
                            if (pageIndex > 1) pageIndex--;
                        }
                        onRequestFinish(false, msg);
                    }
                }));
    }

}
