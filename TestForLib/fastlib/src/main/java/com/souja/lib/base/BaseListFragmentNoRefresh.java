package com.souja.lib.base;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.souja.lib.R;
import com.souja.lib.inter.IHttpCallBack;
import com.souja.lib.inter.IListPage;
import com.souja.lib.models.ODataPage;
import com.souja.lib.utils.SHttpUtil;
import com.souja.lib.widget.MLoadingDialog;

import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;

import java.util.ArrayList;

public abstract class BaseListFragmentNoRefresh<T> extends BaseFragment implements IListPage<T> {

    public int pageIndex = 1, pageAmount = 1;
    public ArrayList<T> baseList;

    private boolean bEmptyControl = false;

    public void setEmptyControl(boolean b) {
        bEmptyControl = b;
    }

    @Override
    public void onRequestFinish(boolean b) {
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


    public void updateList() {
        pageIndex = 1;
        getList(false);
    }

    public void loadMore() {
        recyclerView.stopScroll();
        recyclerView.stopNestedScroll();
        if (pageIndex < pageAmount) {
            pageIndex++;
            getList(false);
        }
    }

    public void hideLoading() {
        mLoadingDialog.dismiss();
    }

    public T getItem(int i) {
        return baseList.get(i);
    }

    public RecyclerView.Adapter getAdapter() {
        return recyclerView.getAdapter();
    }

    public void notifyDatasetChanged() {
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    public RecyclerView recyclerView;
    public MLoadingDialog mLoadingDialog;


    @Override
    public int setupLayoutRes() {
        return R.layout.frag_list_no_refresh;
    }

    private void initViews() {
        recyclerView = _rootView.findViewById(R.id.recyclerView);
        mLoadingDialog = _rootView.findViewById(R.id.m_loading);
    }

    @Override
    public void initMain() {
        initViews();
        recyclerView.setLayoutManager(getLayoutMgr());
        baseList = new ArrayList<>();
        setAdapter(recyclerView, baseList);
        getList(false);
    }

    public void getList(boolean retry) {
        String url = getRequestUrl(pageIndex);
        if (TextUtils.isEmpty(url)) return;
        if (retry) mLoadingDialog.setRetryDefaultTip();
        addRequest(SHttpUtil.Request(null, url, isGet() ? HttpMethod.GET : HttpMethod.POST,
                getRequestParams(), getResultClass(), new IHttpCallBack<T>() {
                    @Override
                    public void OnSuccess(String msg, ODataPage page, ArrayList<T> data) {
                        if (pageIndex == 1) {
                            baseList.clear();
                            pageAmount = page.getTotalPages();
                        }

                        if (data.size() > 0) {
                            baseList.addAll(data);
                        }
                        if (pageIndex == 1 && data.size() == 0) {
                            if (!bEmptyControl) mLoadingDialog.showEmptyView();
                            else hideLoading();
                        } else hideLoading();
                        notifyDatasetChanged();
                        onRequestFinish(true);
                    }

                    @Override
                    public void OnFailure(String msg) {
                        if (!bEmptyControl && mLoadingDialog.isShowing()) {
                            mLoadingDialog.setMClick(() -> getList(true));
                            mLoadingDialog.setErrMsgRetry(msg);
                        } else {
                            hideLoading();
                            showToast(msg);
                            if (pageIndex > 1) pageIndex--;
                        }
                        onRequestFinish(false);
                    }
                }));
    }
}
