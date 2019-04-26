package com.souja.lib.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

public class MDialog extends AlertDialog {
    protected MDialog(@NonNull Context context) {
        super(context);
        init(context);
    }

    protected MDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected MDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context context) {
        
    }


}
