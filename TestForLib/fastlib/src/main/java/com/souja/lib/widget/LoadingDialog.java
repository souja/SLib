package com.souja.lib.widget;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.souja.lib.R;
import com.souja.lib.utils.ScreenUtil;


/**
 * 菊花...
 * Created by Yangdz on 2015/2/4.
 */
public class LoadingDialog extends ProgressDialog {

    private Context mContext;
    private TextView tvMsg;
    private String msg;

    public LoadingDialog(Context context) {
        super(context, R.style.CommProgressDialog);
        this.mContext = context;
    }

    public LoadingDialog(Context context, String msg) {
        super(context, R.style.CommProgressDialog);
        this.mContext = context;
        this.msg = msg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.progress_dialog, null);
        ScreenUtil.initScale(contentView);
        tvMsg = contentView.findViewById(R.id.content);
        if (msg != null) tvMsg.setText(msg);
        setCanceledOnTouchOutside(false);
        setContentView(contentView);

        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        window.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.drawable_transparent));
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.gravity = Gravity.BOTTOM;
        window.setAttributes(layoutParams);
    }

    public void setMsg(String msg) {
        tvMsg.setText(msg);
    }
}