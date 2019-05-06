package com.souja.lib.base;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.souja.lib.R;
import com.souja.lib.inter.IHttpCallBack;
import com.souja.lib.inter.IListPage;
import com.souja.lib.models.ODataPage;
import com.souja.lib.utils.SHttpUtil;
import com.souja.lib.widget.MLoadingDialog;
import com.souja.lib.widget.TitleBar;

import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;

import java.util.ArrayList;


/**
 * 列表类型页面基类，如“我的招聘”列表页面
 * 页面结构：TitleBar + SmartRefresh + RecyclerView + LoadingDialog
 */
public abstract class BaseListAct<T> extends ActBase implements IListPage<T> {

    //设置页面标题
    protected abstract void setupTitle(TitleBar titleBar);

    //请求类型，默认POST
    protected boolean isGet() {
        //如果请求方法是GET，重写此方法，return true;
        return false;
    }

    //在获取到列表数据并且ui更新完毕后，如果有自己的一些其他处理，重写此方法进行
    protected void notifyAdapter() {

    }

    //设置接口请求参数
    protected RequestParams getRequestParams() {
        //默认无附加参数，如需附加参数，重写此方法进行添加
        return new RequestParams();
    }

    //设置适配器布局类型，如LinearLayoutManager（线性布局）、GridLayoutManager（网格布局）。
    protected RecyclerView.LayoutManager getLayoutMgr() {
        //默认返回LinearLayoutManger，如需其他类型 重写此方法进行修改
        return new LinearLayoutManager(_this);
    }

    protected void hideLoading() {
        mLoadingDialog.dismiss();
    }

    protected void updateList() {
        pageIndex = 1;
        getList(false);
    }

    public void setContentBg(int color) {
        body.setBackgroundColor(color);
    }

    protected View body;
    protected TitleBar mTitleBar;
    protected MLoadingDialog mLoadingDialog;
    protected SmartRefreshLayout mSmartRefresh;
    protected RecyclerView mRecyclerView;

    protected ArrayList<T> baseList;
    protected int pageIndex = 1, pageAmount = 1;

    @Override
    protected int setViewRes() {
        return R.layout.act_base_list_page;
    }

    private void initViews() {
        body = findViewById(R.id.body);
        body = findViewById(R.id.m_title);
        mLoadingDialog = findViewById(R.id.m_loading);
        mSmartRefresh = findViewById(R.id.smartRefresh);
        mRecyclerView = findViewById(R.id.recyclerView);
    }

    @Override
    protected void initMain() {
        initViews();
        setupTitle(mTitleBar);
        baseList = new ArrayList<>();
        mRecyclerView.setLayoutManager(getLayoutMgr());
        setAdapter(mRecyclerView, baseList);
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
                updateList();
            }
        });
        getList(false);
    }

    protected void getList(boolean retry) {
        String requestUrl = getRequestUrl(pageIndex);
        if (TextUtils.isEmpty(requestUrl)) return;

        if (retry) mLoadingDialog.setRetryDefaultTip();

        addRequest(SHttpUtil.Request(null, requestUrl, isGet() ? HttpMethod.GET : HttpMethod.POST,
                getRequestParams(), getResultClass(), new IHttpCallBack<T>() {
                    @Override
                    public void OnSuccess(String msg, ODataPage page, ArrayList<T> data) {
                        mSmartRefresh.finishRefresh();
                        mSmartRefresh.finishLoadMore();
                        if (pageIndex == 1) {
                            baseList.clear();
                            pageAmount = page.getTotalPages();
                        }

                        if (data.size() > 0) {
                            baseList.addAll(data);
                        }
                        mSmartRefresh.setEnableLoadMore(pageIndex < pageAmount);
                        if (pageIndex == 1 && data.size() == 0) mLoadingDialog.showEmptyView();
                        else mLoadingDialog.dismiss();
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                        notifyAdapter();
                    }

                    @Override
                    public void OnFailure(String msg) {
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.setErrMsgRetry(msg);
                            mLoadingDialog.setMClick(() -> getList(true));
                        } else {
                            mSmartRefresh.finishRefresh();
                            mSmartRefresh.finishLoadMore();
                            showToast(msg);
                            if (pageIndex > 1) pageIndex--;
                        }
                    }
                }));
    }
}
