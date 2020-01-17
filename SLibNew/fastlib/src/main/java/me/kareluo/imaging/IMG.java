package me.kareluo.imaging;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.souja.lib.R;

import java.util.Locale;

import me.kareluo.imaging.core.util.IMGScreen;

/**
 * Created by felix on 2017/12/26 下午1:34.
 */

public class IMG {

    public static final int REQ_IMAGE_EDIT = 2018;
    private static Context mApplicationContext;

    public static void initialize(Context context) {
        mApplicationContext = context.getApplicationContext();
    }

    public static String FILE_PROVIDER;

    public static class Config {
        private boolean isSave;
    }

    public static String formatStr(String format, Object... args) {
        return String.format(Locale.ENGLISH, format, args);
    }

    public static String formatStr(Locale locale, String format, Object... args) {
        return String.format(locale, format, args);
    }

    public static boolean isEmpty(String s) {
        if (null == s || s.length() == 0 || s.trim().length() == 0 || s.equals("null"))
            return true;
        return false;
    }


    public static AlertDialog createDialog(Activity context) {
        return createDialog(context, null);
    }

    public static AlertDialog createDialog(Activity context, String msg) {
        AlertDialog dialog = new AlertDialog.Builder(context, R.style.imgProgressDialog).create();
        View loadView = LayoutInflater.from(context).inflate(R.layout.img_progress_dialog, null);
        IMGScreen.setScale(context);
        IMGScreen.initScale(loadView);
        dialog.setView(loadView, 0, 0, 0, 0);
        dialog.setCanceledOnTouchOutside(false);
        if (!TextUtils.isEmpty(msg)) {
            TextView tvTip = loadView.findViewById(R.id.tvTip);
            tvTip.setText(msg);
        }
        return dialog;
    }
}
