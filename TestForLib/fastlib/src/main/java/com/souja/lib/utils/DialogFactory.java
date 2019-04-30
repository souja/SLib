package com.souja.lib.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Yangdz on 2016/8/11 0011.
 */
public class DialogFactory {

    public static PopupWindow getPopupWindow(View contentView, Drawable windowBg) {
        ScreenUtil.initScale(contentView);
        PopupWindow popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindow.setBackgroundDrawable(windowBg);
        return popupWindow;
    }


    public static AlertDialog NewDialog(Context context, String msg,
                                        String textConfirm,
                                        DialogInterface.OnClickListener listenerConfirm,
                                        String textCancel,
                                        DialogInterface.OnClickListener listenerCancel) {
        return NewDialog(context, null, msg, textConfirm, listenerConfirm, textCancel, listenerCancel, null, null);
    }

    public static AlertDialog NewDialog(Context context, String title, String msg,
                                        String textConfirm,
                                        DialogInterface.OnClickListener listenerConfirm,
                                        String textCancel,
                                        DialogInterface.OnClickListener listenerCancel) {
        return NewDialog(context, title, msg, textConfirm, listenerConfirm, textCancel, listenerCancel, null, null);
    }

    public static AlertDialog NewDialog(Context context, String title, String msg,
                                        String textConfirm,
                                        DialogInterface.OnClickListener listenerConfirm,
                                        String textCancel,
                                        DialogInterface.OnClickListener listenerCancel,
                                        String textNeutral,
                                        DialogInterface.OnClickListener listenerNeutral) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null) builder.setTitle(title);
        if (msg != null) builder.setMessage(msg);
        if (textConfirm != null) builder.setPositiveButton(textConfirm, listenerConfirm);
        if (textCancel != null) builder.setNegativeButton(textCancel, listenerCancel);
        if (textNeutral != null) builder.setNeutralButton(textNeutral, listenerNeutral);
        return builder.create();
    }

    public static AlertDialog SimpleDialog(Context context, String title, String msg, DialogInterface.OnClickListener listenerConfirm) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null) builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("确定", listenerConfirm);
        return builder.create();
    }

    public static AlertDialog SimpleDialog(Context context, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null) builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("确定", ((dialog1, which) -> dialog1.dismiss()));
        return builder.create();
    }

    public static AlertDialog SimpleDialog(Context context, String msg, DialogInterface.OnClickListener listenerConfirm) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setPositiveButton("确定", listenerConfirm);
        return builder.create();
    }


    public static DatePickerDialog getDatePickerDialog(Context context, TextView tvDate) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        DatePickerDialog dialog = new DatePickerDialog(context, (view, year, monthOfYear, dayOfMonth) -> {
            String month, day;
            monthOfYear++;
            if (monthOfYear < 10) {
                month = "0" + monthOfYear;
            } else {
                month = String.valueOf(monthOfYear);
            }
            if (dayOfMonth < 10) {
                day = "0" + dayOfMonth;
            } else {
                day = String.valueOf(dayOfMonth);
            }
            String dateStr = String.valueOf(year) + "-" + month + "-" + day;
            tvDate.setText(dateStr);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        return dialog;
    }

    public static DatePickerDialog getDatePickerDialog(Context context, DatePickerDialog.OnDateSetListener listener) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        DatePickerDialog dialog = new DatePickerDialog(context, listener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        return dialog;
    }

}