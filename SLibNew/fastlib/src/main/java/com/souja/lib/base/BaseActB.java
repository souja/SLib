package com.souja.lib.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.souja.lib.utils.MTool;
import com.souja.lib.utils.ScreenUtil;

/**
 * 缩放+透明状态栏
 * */
public abstract class BaseActB extends AppCompatActivity {

    public BaseActB _this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _this = this;
        MTool.setStatusBarFullTransparent(getWindow());
        MTool.setStatusBarTextColor(getWindow(), true);

        ScreenUtil.setScale(this);
        if (setViewRes() != 0)
            setContentView(setViewRes());
        initMain();
    }

    protected abstract int setViewRes();

    protected abstract void initMain();

    @Override
    public void setContentView(int layoutResID) {
        View v = getLayoutInflater().inflate(layoutResID, null);
        ScreenUtil.initScale(v);
        super.setContentView(v);
    }

    public void NEXT(Intent it) {
        startActivity(it);
    }

    public void showToast(String msg) {
        if (msg == null || msg.contains("onNext")) return;
        Toast toast = Toast.makeText(this, null, Toast.LENGTH_SHORT);
        toast.setText(msg);
        toast.show();
    }

    public void showToast(int msgRes) {
        Toast toast = Toast.makeText(this, null, Toast.LENGTH_SHORT);
        toast.setText(msgRes);
        toast.show();
    }

}
