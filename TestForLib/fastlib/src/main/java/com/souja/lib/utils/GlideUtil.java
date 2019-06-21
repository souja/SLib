package com.souja.lib.utils;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.souja.lib.R;
import com.souja.lib.models.OImageBase;
import com.souja.lib.widget.PopZoomGallery;
import com.souja.lib.widget.ZoomImageModel;

import org.xutils.common.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Souja on 2018/7/23 0023.
 */

public class GlideUtil {

    public static void loadHeadIcon(Context context, String url, ImageView target) {
        if (TextUtils.isEmpty(url)) target.setImageResource(R.drawable.lib_person_default);
        else load(context, url, target, R.drawable.lib_person_default);
    }

    public static void loadHospitalIcon(Context context, String url, ImageView target) {
        if (TextUtils.isEmpty(url)) target.setImageResource(R.drawable.lib_hos_default_icon);
        else load(context, url, target, R.drawable.lib_hos_default_icon);
    }

    public static void load(Context context, String path, ImageView target) {
        load(context, path, target, -1);
    }

    public static void loadDefault(Context context, String path, ImageView target) {
        load(context, path, target, R.drawable.ic_loading);
    }

    public static void loadDefaultLong(Context context, String path, ImageView target) {
        load(context, path, target, R.drawable.lib_default_long);
    }

    public static void loadDefaultRec(Context context, String url, ImageView target) {
        load(context, url, target, R.drawable.lib_default_rec);
    }

    public static void load(Context context, String url, ImageView target, int placeholder) {
        RequestOptions requestOptions = new RequestOptions();
        if (placeholder != -1) requestOptions.placeholder(placeholder);
        Glide.with(context)
                .load(url)
                .apply(requestOptions)
                .into(target);
    }

    public static void load(Context context, int imgRes, ImageView target) {
        Glide.with(context)
                .load(imgRes)
                .into(target);
    }

    public static void loadDefaultRound(Context context, String url, ImageView target) {
        loadRound(context, url, target, 5, R.drawable.lib_default_long, -1);

    }

    public static void loadRound(Context context, String url, ImageView target, int radius, int placeholder) {
        loadRound(context, url, target, radius, placeholder, -1);
    }

    public static void loadRound(Context context, String url, ImageView target, int radius, int placeholder, int size) {
        if (radius <= 0) {
            load(context, url, target, placeholder);
            return;
        }
        RoundedCorners roundedCorners = new RoundedCorners(radius);
        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners);
        if (placeholder != -1)
            options.placeholder(placeholder);
        if (size > 0) {
            options.override(size, size);
        }
        Glide.with(context)
                .load(url)
                .apply(options)
                .into(target);
    }

    public static void showPopImages(Context context, View v, List<String> urls, String imgPath) {
        int index = 0;
        ArrayList<ZoomImageModel> zoomImageArrayList = new ArrayList<>();
        for (int i = 0; i < urls.size(); i++) {

            String url = urls.get(i);
            if (imgPath.equals(url)) {
                index = i;
            }
            LogUtil.d("pop url " + url);
            ZoomImageModel imageScale = new ZoomImageModel();
            int[] xy = new int[2];
            v.getLocationInWindow(xy);
            imageScale.rect = new Rect(xy[0], xy[1], xy[0] + v.getWidth(), xy[1] + v.getHeight());
            imageScale.smallImagePath = url;
            imageScale.bigImagePath = url;
            zoomImageArrayList.add(imageScale);
        }
        PopZoomGallery popZoomGallery = new PopZoomGallery(context, zoomImageArrayList,
                (container, position, view, model) -> {
                    String url = model.bigImagePath;
                    Glide.with(context).load(url).into(view);
                });
        popZoomGallery.showPop(v, index);
    }

    public static void showPopImages(Context context, View v, List<String> urls, int index) {
        ArrayList<ZoomImageModel> zoomImageArrayList = new ArrayList<>();
        for (int i = 0; i < urls.size(); i++) {
            String url = urls.get(i);
            LogUtil.d("pop url " + url);
            ZoomImageModel imageScale = new ZoomImageModel();
            int[] xy = new int[2];
            v.getLocationInWindow(xy);
            imageScale.rect = new Rect(xy[0], xy[1], xy[0] + v.getWidth(), xy[1] + v.getHeight());
            imageScale.smallImagePath = url;
            imageScale.bigImagePath = url;
            zoomImageArrayList.add(imageScale);
        }
        PopZoomGallery popZoomGallery = new PopZoomGallery(context, zoomImageArrayList,
                (container, position, view, model) -> {
                    String url = model.bigImagePath;
                    if (url.contains("http:") || url.contains("https:"))
                        Glide.with(context).load(url).into(view);
                    else
                        Glide.with(context).load(url).into(view);
                });
        popZoomGallery.showPop(v, index);
    }

    public static void showPopImgs(Context context, View v, List<OImageBase> list, String imgUrl) {
        int index = 0;
        ArrayList<ZoomImageModel> zoomImageArrayList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            OImageBase img = list.get(i);
            String url = img.getPictureUrl();
            if (TextUtils.isEmpty(url)) url = img.getImageUrl();
            if (imgUrl.equals(url)) {
                index = i;
            }
            LogUtil.d("pop url " + url);
            ZoomImageModel imageScale = new ZoomImageModel();
            int[] xy = new int[2];
            v.getLocationInWindow(xy);
            imageScale.rect = new Rect(xy[0], xy[1], xy[0] + v.getWidth(), xy[1] + v.getHeight());
            imageScale.smallImagePath = url;
            imageScale.bigImagePath = url;
            zoomImageArrayList.add(imageScale);
        }
        PopZoomGallery popZoomGallery = new PopZoomGallery(context, zoomImageArrayList,
                (container, position, view, model) -> {
                    String url = model.bigImagePath;
                    Glide.with(context).load(url).into(view);
                });
        popZoomGallery.showPop(v, index);
    }

}
