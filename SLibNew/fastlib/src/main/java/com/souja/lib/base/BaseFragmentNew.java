package com.souja.lib.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AlertDialog;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.souja.lib.R;
import com.souja.lib.inter.IHttpCallBack;
import com.souja.lib.utils.MGlobal;
import com.souja.lib.utils.MTool;
import com.souja.lib.utils.SHttpUtil;
import com.souja.lib.utils.ScreenUtil;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


/**
 * Base Fragment
 * @author Ydz
 * 2020-01-13
 */
public abstract class BaseFragmentNew extends Fragment {


    protected abstract int setupLayoutRes();

    protected abstract void initMain();

    protected View _rootView;

    public FragmentActivity _baseActivity;

    private List<Callback.Cancelable> _cancelableList;
    private List<Integer> _actionList;

    private InputMethodManager _inputMethodManager;
    private Disposable _disposable;

    private AlertDialog _mDialog;
    private TextView _tvProgressTip;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _baseActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _rootView = inflater.inflate(setupLayoutRes(), null);
        ScreenUtil.initScale(_rootView);
        initMain();
        return _rootView;
    }

    public AlertDialog getDialog() {
        createDialog(null);
        return _mDialog;
    }

    public AlertDialog getDialog(String msg) {
        createDialog(msg);
        return _mDialog;
    }

    private void createDialog(@Nullable String msg) {
        if (_mDialog == null) {
            _mDialog = new AlertDialog.Builder(_baseActivity, R.style.CustomProgressDialog).create();
            View loadView = LayoutInflater.from(_baseActivity).inflate(R.layout.m_dialog_new, null);
            ScreenUtil.initScale(loadView);
            _mDialog.setView(loadView, 0, 0, 0, 0);
            _mDialog.setCanceledOnTouchOutside(false);
            _tvProgressTip = loadView.findViewById(R.id.tvTip);
        }
        _tvProgressTip.setText(TextUtils.isEmpty(msg) ? "请稍候..." : msg);
    }


    public void showToast(String msg) {
        MTool.Toast(_baseActivity, msg);
    }

    public void showToast(int msgRes) {
        MTool.Toast(_baseActivity, msgRes);
    }

    public void NEXT(Intent it) {
        _baseActivity.startActivity(it);
    }

    public void addAction(int actionCode, Consumer<Object> action) {
        if (!MGlobal.get().containsKey(actionCode)) {
            MGlobal.get().addAction(actionCode, action);
            if (_actionList == null) _actionList = new ArrayList<>();
            _actionList.add(actionCode);
        }
    }

    public Consumer<Object> getAction(int key) {
        return MGlobal.get().getAction(key);
    }

    public void delAction(int actionCode) {
        MGlobal.get().delAction(actionCode);
    }


    public InputMethodManager getInputMethodManager() {
        if (_inputMethodManager == null)
            _inputMethodManager = (InputMethodManager) _baseActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        return _inputMethodManager;
    }

    public void addRequest(Callback.Cancelable req) {
        if (_cancelableList == null) _cancelableList = new ArrayList<>();
        _cancelableList.add(req);
    }

    public void GO(Class c) {
        startActivity(new Intent(_baseActivity, c));
    }

    public String setPageTitle() {
        return null;
    }


    public void addSubscription(Object obj, Consumer<Object> consumer) {
        _disposable = Flowable.just(obj).subscribe(consumer);
    }

    @Override
    public void onDestroy() {
        if (_disposable != null) {
            if (!_disposable.isDisposed())
                _disposable.dispose();
        }
        _disposable = null;
        if (_cancelableList != null) {
            for (Callback.Cancelable req : _cancelableList) {
                if (req != null && !req.isCancelled()) req.cancel();
            }
        }
        if (_actionList != null) {
            for (int actionCode : _actionList) {
                MGlobal.get().delAction(actionCode);
            }
        }
        try {
            super.onDestroy();
        } catch (NullPointerException e) {
            LogUtil.e("sb androidx fragment");
            //Fragment.java中调用Detech方法时未判空
//            if (!mChildFragmentManager.isDestroyed()) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }


    //##################=================HTTP Methods====>>>

    //========================Post
    public <T> void Post(AlertDialog dialog, String url, RequestParams params,
                         final Class<T> dataClass, IHttpCallBack callBack) {
        addRequest(SHttpUtil.Post(dialog, url, params, dataClass, callBack));
    }

    //不需要解析返回数据模型
    public void Post(AlertDialog dialog, String url, RequestParams params,
                     IHttpCallBack callBack) {
        addRequest(SHttpUtil.Post(dialog, url, params, Object.class, callBack));
    }

    //不需要Dialog
    public <T> void Post(String url, RequestParams params, final Class<T> dataClass, IHttpCallBack callBack) {
        Post(null, url, params, dataClass, callBack);
    }

    //不需要请求参数
    public <T> void Post(AlertDialog dialog, String url, final Class<T> dataClass, IHttpCallBack callBack) {
        Post(dialog, url, new RequestParams(), dataClass, callBack);
    }

    //不需要（Dialog 和 解析返回数据模型）
    public void Post(String url, RequestParams params, IHttpCallBack callBack) {
        Post(null, url, params, Object.class, callBack);
    }

    //不需要（Dialog 和 请求参数）
    public <T> void Post(String url, final Class<T> dataClass, IHttpCallBack callBack) {
        Post(null, url, new RequestParams(), dataClass, callBack);
    }

    //========================Get
    public <T> void Get(AlertDialog dialog, String url, RequestParams params,
                        final Class<T> dataClass, IHttpCallBack callBack) {
        addRequest(SHttpUtil.Get(dialog, url, params, dataClass, callBack));
    }

    public <T> void Get(AlertDialog dialog, String url, final Class<T> dataClass, IHttpCallBack callBack) {
        Get(dialog, url, new RequestParams(), dataClass, callBack);
    }

    public void Get(String url, RequestParams params, IHttpCallBack callBack) {
        Get(null, url, params, Object.class, callBack);
    }

    public <T> void Get(String url, Class<T> dataClass, IHttpCallBack<T> callBack) {
        Get(null, url, new RequestParams(), dataClass, callBack);
    }

    public <T> void Get(String url, RequestParams params,
                        final Class<T> dataClass, IHttpCallBack callBack) {
        Get(null, url, params, dataClass, callBack);
    }

    public void Get(AlertDialog dialog, String url, IHttpCallBack callBack) {
        Get(dialog, url, new RequestParams(), Object.class, callBack);
    }

    //========================Delete
    public <T> void Delete(AlertDialog dialog, String url, RequestParams params,
                           final Class<T> dataClass, IHttpCallBack callBack) {
        addRequest(SHttpUtil.Delete(dialog, url, params, dataClass, callBack));
    }

    public void Delete(String url, RequestParams params, IHttpCallBack callBack) {
        Delete(null, url, params, Object.class, callBack);
    }

    public <T> void Delete(String url, RequestParams params,
                           final Class<T> dataClass, IHttpCallBack callBack) {
        Delete(null, url, params, dataClass, callBack);
    }

    public void Delete(AlertDialog dialog, String url, IHttpCallBack callBack) {
        Delete(dialog, url, new RequestParams(), Object.class, callBack);
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
//    public void onDetach() {
//        super.onDetach();
//        try {
//            Field childFragmentManager = Fragment.class
//                    .getDeclaredField("mChildFragmentManager");
//            childFragmentManager.setAccessible(true);
//            childFragmentManager.set(this, null);
//        } catch (NoSuchFieldException e) {
//            throw new RuntimeException(e);
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }

    /**
     * fragment可见（切换回来或者onResume）
     */
//    public void onUserVisible() {
//
//    }


    /**
     * 第一次fragment不可见（不建议在此处理事件）
     */
//    public void onFirstUserInvisible() {
//
//    }

    /**
     * fragment不可见（切换掉或者onPause）
     */
//    public void onUserInvisible() {
//
//    }
}
