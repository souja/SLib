package com.souja.lib.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.souja.lib.utils.MGlobal;
import com.souja.lib.utils.ScreenUtil;

import org.xutils.common.Callback;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by Souja on 2018/3/12 0012.
 */

public abstract class ActBase extends AppCompatActivity {

    /**
     * 设置页面视图Resource
     * e.g. R.layout.act_test
     * */
    protected abstract int setViewRes();

    //页面逻辑处理
    protected abstract void initMain();

    public void showToast(String msg) {
        if (msg == null || msg.contains("onNext")) return;
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showToast(int msgRes) {
        Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
    }

    public void showToast(String msg, int duration) {
        if (msg == null || msg.contains("onNext")) return;
        Toast.makeText(this, msg, duration).show();
    }

    //跳转页面
    public void NEXT(Intent it) {
        startActivity(it);
    }

    //跳转页面
    public void GO(Class dclass) {
        startActivity(new Intent(getApplicationContext(), dclass));
    }

    //添加请求
    public void addRequest(Callback.Cancelable req) {
        if (mCancelables == null) mCancelables = new ArrayList<>();
        mCancelables.add(req);
    }

    /**
     * “添加订阅”
     * Consumer是简易版的Observer，他有多重重载，可以自定义你需要处理的信息
     * 这里调用的是只接受onNext消息的方法，只提供一个回调接口accept
     * http://reactivex.io/RxJava/javadoc/io/reactivex/functions/Consumer.html
     */
    public void addAction(int actionCode, Consumer<Object> action) {
        if (!containsKey(actionCode)) {
            MGlobal.get().addAction(actionCode, action);
            if (actions == null) actions = new ArrayList<>();
            actions.add(actionCode);
        }
    }

    public Consumer<Object> getAction(int key) {
        return MGlobal.get().getAction(key);
    }

    public void delAction(int actionCode) {
        MGlobal.get().delAction(actionCode);
    }

    public boolean containsKey(int actionCode) {
        return MGlobal.get().containsKey(actionCode);
    }

    //“发送订阅”
    public void addSubscription(Object obj, Consumer<Object> consumer) {
        if (consumer != null)
            mDisposable = Flowable.just(obj).subscribe(consumer);
    }

    public void addSubscription(int key, Object o) {
        if (containsKey(key))
            addSubscription(o, getAction(key));
    }

    public void addSubscription(int key) {
        if (containsKey(key))
            addSubscription("", getAction(key));
    }

    protected InputMethodManager inputMethodManager;
    private List<Callback.Cancelable> mCancelables;
    private List<Integer> actions;
    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleOnCreate(savedInstanceState);

        ScreenUtil.setScale(this);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (setViewRes() != 0)
            setContentView(setViewRes());
        initMain();
    }

    protected void handleOnCreate(Bundle savedInstanceState) {
    }

    @Override
    public void setContentView(int layoutResID) {
        View v = getLayoutInflater().inflate(layoutResID, null);
        ScreenUtil.initScale(v);
        super.setContentView(v);
    }

    @Override
    protected void onDestroy() {
        if (mCancelables != null) {
            for (Callback.Cancelable req : mCancelables) {
                if (req != null && !req.isCancelled()) req.cancel();
            }
        }
        if (actions != null) {
            for (int actionCode : actions) {
                MGlobal.get().delAction(actionCode);
            }
        }
        if (mDisposable != null) {
            if (!mDisposable.isDisposed())
                mDisposable.dispose();
        }
        mDisposable = null;
        super.onDestroy();
    }

    public boolean isHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public void requestInput() {
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }

        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    protected void hideSoftKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
