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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Fragment with @SmartRefreshLayout and @RecyclerView
 *
 * @author Ydz
 */
public abstract class BaseListFragmentNew<T> extends BaseFragmentNew implements IListPage<T> {

    @BindView(R2.id.smartRefresh)
    public SmartRefreshLayout _refreshLayout;
    @BindView(R2.id.recyclerView)
    RecyclerView _recyclerView;

    public int pageIndex = 1, pageAmount = 1, totalCount = 0, extCount = 0;


    @Override
    protected int setupLayoutRes() {
        return R.layout.layout_refresh_recycler;
    }

    @Override
    protected void initMain() {
        ButterKnife.bind(this, _rootView);

        _refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (pageIndex < pageAmount) {
                    getListNextPage();
                }
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                updateList();
            }
        });
        _recyclerView.setLayoutManager(setLayoutMgr());
        setAdapter(_recyclerView);
        initArguments();
        getList();
    }

    public void getList() {
        String url = setRequestUrl(pageIndex);
        if (TextUtils.isEmpty(url)) return;

        addRequest(SHttpUtil.Request(null, url, setMethod(), setRequestParams(""),
                getTClass(), new IHttpCallBack<T>() {
                    @Override
                    public void OnSuccess(String msg, ODataPage page, ArrayList<T> data) {
                        _refreshLayout.finishRefresh();
                        _refreshLayout.finishLoadMore();

                        if (pageIndex == 1) {
                            ((MBaseAdapter) _recyclerView.getAdapter()).clearDataSet();
                            pageAmount = page.getTotalPages();
                            totalCount = page.getTotal();
                            extCount = page.getExtTotal();
                        }

                        ((MBaseAdapter) _recyclerView.getAdapter()).addList(data);

                        _refreshLayout.setEnableLoadMore(pageIndex < pageAmount);

                        onRequestFinish(true, msg);
                    }

                    @Override
                    public void OnFailure(String msg) {
                        _refreshLayout.finishRefresh();
                        _refreshLayout.finishLoadMore();
                        if (pageIndex > 1) pageIndex--;
                        showToast(msg);
                        onRequestFinish(false, msg);
                    }
                }));
    }


    public void initArguments() {
    }

    public RecyclerView.LayoutManager setLayoutMgr() {
        //默认返回LinearLayoutManger，如需其他类型 重写此方法
        return new LinearLayoutManager(_baseActivity);
    }


    protected void updateList() {
        pageIndex = 1;
        getList();
    }

    protected void getListNextPage() {
        pageIndex++;
        getList();
    }

    public void notifyDataSetChanged() {
        _recyclerView.getAdapter().notifyDataSetChanged();
    }

    public int getTotalCount() {
        return totalCount;
    }
}
