package com.souja.lib.widget;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.souja.lib.R;
import com.souja.lib.utils.ScreenUtil;

/**
 * ClassName
 * Created by Ydz on 2019/3/15 0015 15:21
 */
public class TransLoadingDialog extends ProgressDialog {

    public TransLoadingDialog(Context context) {
        super(context, R.style.trans_loading_dialog);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.trans_progress_dialog, null);
        ScreenUtil.initScale(contentView);
        setCanceledOnTouchOutside(false);
        setContentView(contentView);
    }

}


