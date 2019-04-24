package com.souja.lib.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

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

  /*  public static DatePickerDialog getDatePickerDialog(Context context, TextView tvDate) {
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
    }*/

//    public interface HourDialogListener {
//        void onTimeSelect(boolean am, int hour);
//    }

/*
    public static AlertDialog getHourPickerDialog(Context context, HourDialogListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_choose_hour, null);
        ScreenUtil.initScale(contentView);
        builder.setView(contentView);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        RadioGroup rgTime = (RadioGroup) contentView.findViewById(R.id.rg_time);
        rgTime.check(R.id.rb_am);
        ListView lvHours = (ListView) contentView.findViewById(R.id.lv_hours);
        HoursAdapter adapter = new HoursAdapter(context);
        lvHours.setAdapter(adapter);
        lvHours.setOnItemClickListener((parent, view, position, id) -> {
            if (listener != null)
                listener.onTimeSelect(rgTime.getCheckedRadioButtonId() == R.id.rb_am,
                        adapter.getItem(position));
            dialog.dismiss();
        });

//        Window window = dialog.getWindow();
//        if (window != null) {
//            window.setGravity(Gravity.CENTER);
//        }
//
//        WindowManager m = ((Activity) context).getWindowManager();
//        Display d = m.getDefaultDisplay(); //为获取屏幕宽、高
//        WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
//        p.width = d.getWidth(); //宽度设置为屏幕
//        dialog.getWindow().setAttributes(p); //设置生效
        return dialog;
    }
*/

/*
    private static class HoursAdapter extends BaseAdapter {
        private List<Integer> mList;
        private Context mContext;

        public HoursAdapter(Context context) {
            mContext = context;
            mList = new ArrayList<>();
            for (int i = 1; i < 13; i++) {
                mList.add(i);
            }
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Integer getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HolderHour mHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_hour, null);
                mHolder = new HolderHour(convertView);
                convertView.setTag(mHolder);
            } else mHolder = (HolderHour) convertView.getTag();

            mHolder.tvHour.setText(mList.get(position) + "");

            return convertView;
        }
    }
*/

/*
    private static class HolderHour {
        TextView tvHour;

        public HolderHour(View view) {
            ScreenUtil.initScale(view);
            tvHour = (TextView) view.findViewById(R.id.tv_hour);
        }
    }
*/


}