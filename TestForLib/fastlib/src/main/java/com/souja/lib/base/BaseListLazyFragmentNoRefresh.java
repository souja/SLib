package com.souja.lib.base;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;

import com.souja.lib.R;
import com.souja.lib.inter.IHttpCallBack;
import com.souja.lib.inter.IListPage;
import com.souja.lib.models.ODataPage;
import com.souja.lib.utils.SHttpUtil;
import com.souja.lib.utils.ScreenUtil;

import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;

import java.util.ArrayList;

public abstract class BaseListLazyFragmentNoRefresh<T> extends BaseLazyFragment implements IListPage<T> {

    private boolean bEmptyControl = false;

    public void setEmptyControl(boolean b) {
        bEmptyControl = b;
    }

    public int pageIndex = 1, pageAmount = 1;
    public ArrayList<T> baseList;

    @Override
    public void onRequestFinish(boolean success) {
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

    private void initViews() {
        recyclerView = _contentView.findViewById(R.id.recyclerView);
    }

    @Override
    public void onFirstUserVisible() {
        _contentView = LayoutInflater.from(mBaseActivity).inflate(R.layout.frag_list_lazy_no_refresh, null);
        ScreenUtil.initScale(_contentView);
        setContentView(_contentView);
        initViews();

        recyclerView.setLayoutManager(getLayoutMgr());
        baseList = new ArrayList<>();
        setAdapter(recyclerView, baseList);
        getList(false);
    }

    public void getList(boolean retry) {
        String url = getRequestUrl(pageIndex);
        if (TextUtils.isEmpty(url)) return;

        if (retry) setRetryDefaultTip();

        addRequest(SHttpUtil.Request(null, getRequestUrl(pageIndex),
                isGet() ? HttpMethod.GET : HttpMethod.POST,
                getRequestParams(), getResultClass(), new IHttpCallBack<T>() {
                    @Override
                    public void OnSuccess(String msg, ODataPage page, ArrayList<T> data) {
                        if (pageIndex == 1) {
                            baseList.clear();
                            pageAmount = page.getTotalPages();
                            setTotalCount(page.getTotal());
                        }

                        if (data.size() > 0) {
                            baseList.addAll(data);
                        }
                        if (pageIndex == 1 && data.size() == 0) {
                            if (!bEmptyControl) ShowEmptyView();
                            else ShowContentView();
                        } else ShowContentView();
                        notifyDatasetChanged();
                        onRequestFinish(true);
                    }

                    @Override
                    public void OnFailure(String msg) {
                        if (!bEmptyControl && isProgressing()) {
                            setMClick(() -> getList(true));
                            setErrMsgRetry(msg);
                        } else {
                            ShowContentView();
                            showToast(msg);
                            if (pageIndex > 1) pageIndex--;
                        }
                        onRequestFinish(false);
                    }
                }));
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
}
