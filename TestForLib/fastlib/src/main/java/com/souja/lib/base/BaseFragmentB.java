/**
 * Project Name:Ouni
 * File Name:BaseImageCropFragment.java
 * Package Name:com.ouni.ui.activity
 * Date:2014年6月22日下午3:36:33
 * Copyright (c) 2014, chenzhou1025@126.com All Rights Reserved.
 */

package com.souja.lib.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.souja.lib.utils.MTool;

import java.lang.reflect.Field;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


/**
 * ClassName:BaseImageCropFragment <br/>
 * Date: 2014年6月22日 下午3:36:33 <br/>
 *
 * @author WangYue
 * @see
 * @since JDK 1.6
 */
public abstract class BaseFragmentB extends Fragment {

    public FragmentActivity mBaseActivity;
    public FragmentManager mFragmentManager;
    private boolean isPrepared;
    protected View _rootView;
    protected InputMethodManager inputMethodManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _rootView = inflater.inflate(setupLayoutRes(), null, false);
        initMain();
        return _rootView;
    }

    public abstract int setupLayoutRes();

    public abstract void initMain();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaseActivity = getActivity();
        inputMethodManager = (InputMethodManager) mBaseActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        mFragmentManager = getChildFragmentManager();
    }

    public void showToast(String msg) {
        MTool.Toast(mBaseActivity, msg);
    }

    public void showToast(int msgRes) {
        MTool.Toast(mBaseActivity, msgRes);
    }

    /**
     * This seems to be a bug in the newly added support for nested fragments.
     * Basically, the child FragmentManager ends up with a broken internal state
     * when it is detached from the activity. A short-term workaround that fixed
     * it for me is to add the following to onDetach() of every Fragment which
     * you call getChildFragmentManager() on:
     * <p>
     * 不加会报一下错误
     * java.lang.IllegalStateException: No activity at
     * android.support.v4.app.FragmentManagerImpl
     * .moveToState(FragmentManager.java:1075) at
     * android.support.v4.app.FragmentManagerImpl
     * .moveToState(FragmentManager.java:1070)
     **/
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 第一次onResume中的调用onUserVisible避免操作与onFirstUserVisible操作重复
     */
    private boolean isFirstResume = true;

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstResume) {
            isFirstResume = false;
            return;
        }
        if (getUserVisibleHint()) {
            onUserVisible();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            onUserInvisible();
        }
    }

    private boolean isFirstVisible = true;
    private boolean isFirstInvisible = true;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            if (isFirstVisible) {
                isFirstVisible = false;
                initPrepare();
            } else {
                onUserVisible();
            }
        } else {
            if (isFirstInvisible) {
                isFirstInvisible = false;
                onFirstUserInvisible();
            } else {
                onUserInvisible();
            }
        }
    }

    public synchronized void initPrepare() {
        if (isPrepared) {
            onFirstUserVisible();
        } else {
            isPrepared = true;
        }
    }

    /**
     * 第一次fragment可见（进行初始化工作）
     */
    public void onFirstUserVisible() {

    }

    /**
     * fragment可见（切换回来或者onResume）
     */
    public void onUserVisible() {

    }

    /**
     * 第一次fragment不可见（不建议在此处理事件）
     */
    public void onFirstUserInvisible() {

    }

    /**
     * fragment不可见（切换掉或者onPause）
     */
    public void onUserInvisible() {

    }

    private Disposable mDisposable;

    public void addSubscription(Object obj, Consumer<Object> consumer) {
        mDisposable = Flowable.just(obj).subscribe(consumer);
    }

    @Override
    public void onDestroy() {
        if (mDisposable != null) {
            if (!mDisposable.isDisposed())
                mDisposable.dispose();
        }
        mDisposable = null;
        super.onDestroy();
    }

    public void NEXT(Intent it) {
        mBaseActivity.startActivity(it);
    }

}
