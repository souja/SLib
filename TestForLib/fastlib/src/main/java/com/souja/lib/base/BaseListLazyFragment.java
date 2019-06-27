package com.souja.lib.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.souja.lib.R;
import com.souja.lib.inter.IHttpCallBack;
import com.souja.lib.inter.IListPage;
import com.souja.lib.models.ODataPage;
import com.souja.lib.utils.SHttpUtil;
import com.souja.lib.utils.ScreenUtil;

import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;

import java.util.ArrayList;

public abstract class BaseListLazyFragment<T> extends BaseLazyFragment implements IListPage<T> {

    public static final String KEY_REFRESH = "refreshFlag";
    private boolean enableRefresh = true;
    private boolean bEmptyControl = false;

    public void setEmptyControl(boolean b) {
        bEmptyControl = b;
    }

    public int pageIndex = 1, pageAmount = 1;
    public ArrayList<T> baseList;

    public void notifyAdapter() {
        //如果有自己的处理，重写此方法
    }

    //请求类型，默认POST
    public boolean isGet() {
        //如果请求方法是GET，重写此方法，return true;
        return false;
    }

    public RequestParams getRequestParams() {
        //如果接口需要自定义参数，重写此方法
        return new RequestParams();
    }

    public RecyclerView.LayoutManager getLayoutMgr() {
        //默认返回LinearLayoutManger，如需其他类型 重写此方法
        return new LinearLayoutManager(mBaseActivity);
    }

    public void notifyDatasetChanged() {
        recyclerView.getAdapter().notifyDataSetChanged();
    }


    private int totalCount = 0;

    public void setTotalCount(int count) {
        totalCount = count;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public RecyclerView recyclerView;
    public SmartRefreshLayout mRefreshLayout;

    private void initViews() {
        recyclerView = _contentView.findViewById(R.id.recyclerView);
        mRefreshLayout = _contentView.findViewById(R.id.smartRefresh);
    }

    @Override
    public void onFirstUserVisible() {
        _contentView = LayoutInflater.from(mBaseActivity).inflate(R.layout.frag_list_lazy, null);
        ScreenUtil.initScale(_contentView);
        setContentView(_contentView);
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
        recyclerView.setLayoutManager(getLayoutMgr());
        baseList = new ArrayList<>();
        setAdapter(recyclerView, baseList);
        getList(false);
    }

    public void getList(boolean retry) {
        if (retry) setRetryDefaultTip();

        addRequest(SHttpUtil.Request(null, getRequestUrl(pageIndex),
                isGet() ? HttpMethod.GET : HttpMethod.POST,
                getRequestParams(), getResultClass(), new IHttpCallBack<T>() {
                    @Override
                    public void OnSuccess(String msg, ODataPage page, ArrayList<T> data) {
                        mRefreshLayout.finishRefresh();
                        mRefreshLayout.finishLoadMore();
                        if (pageIndex == 1) {
                            baseList.clear();
                            pageAmount = page.getTotalPages();
                            setTotalCount(page.getTotal());
                        }

                        if (data.size() > 0) {
                            baseList.addAll(data);
                        }
                        mRefreshLayout.setEnableLoadMore(pageIndex < pageAmount);
                        if (pageIndex == 1 && data.size() == 0) {
                            if (!bEmptyControl) ShowEmptyView();
                            else ShowContentView();
                        } else ShowContentView();
                        notifyDatasetChanged();
                        notifyAdapter();
                    }

                    @Override
                    public void OnFailure(String msg) {
                        if (isProgressing()) {
                            setMClick(() -> getList(true));
                            setErrMsgRetry(msg);
                        } else {
                            mRefreshLayout.finishRefresh();
                            mRefreshLayout.finishLoadMore();
                            showToast(msg);
                            if (pageIndex > 1) pageIndex--;
                        }
                    }
                }));
    }

    public void updateList() {
        pageIndex = 1;
        getList(false);
    }
}
