package com.souja.lib.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import com.souja.lib.R;

public class MDialog extends AlertDialog {
    public MDialog(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    public MDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.m_dialog_new,null);
    }


}
