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



    public static void loadDefaultLong(Context context, String path, ImageView target) {
        load(context, path, R.drawable.lib_img_default_grey, target);
    }

    public static void loadHeadIcon(Context context, String url, ImageView target) {
        if (MTool.isEmpty(url)) target.setImageResource(R.drawable.ic_p_default);
        else {
            load(context, url, R.drawable.ic_p_default, target);
        }
    }

    public static void loadHospitalIcon(Context context, String url, ImageView target) {
        if (MTool.isEmpty(url)) target.setImageResource(R.drawable.lib_hos_default_icon);
        else {
            load(context, url, R.drawable.lib_hos_default_icon, target);
        }
    }

    public static void load(Context context, String url, RequestOptions options, ImageView target) {
        if (context == null) return;
        Glide.with(context)
                .load(url)
                .apply(options)
                .into(target);
    }

    public static void load(Context context, String url, ImageView target) {
        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.lib_img_default_grey);
        load(context, url, requestOptions, target);
    }

    public static void load(Context context, String url, int placeholder, ImageView target) {
        RequestOptions requestOptions = new RequestOptions().placeholder(placeholder);
        load(context, url, requestOptions, target);
    }

    public static void load(Context context, String url, int width, int height, int placeholder, ImageView target) {
        RequestOptions requestOptions = new RequestOptions()
                .override(width, height)
                .placeholder(placeholder);
        Glide.with(context)
                .load(url)
                .apply(requestOptions)
                .into(target);
    }

    public static void loadRes(Context context, int imgRes, ImageView target) {
        Glide.with(context)
                .load(imgRes)
                .into(target);
    }

    public static void loadLocal(Context context, String path, ImageView target) {
        Glide.with(context)
                .load(path)
                .into(target);
    }

    public static void loadLocal(Context context, String path, int placeHolder, ImageView target) {
        RequestOptions requestOptions = new RequestOptions().placeholder(placeHolder);
        Glide.with(context)
                .load(path)
                .apply(requestOptions)
                .into(target);
    }


    public static void loadRound(Context context, String url, int radius, int placeholder, ImageView target) {
        RoundedCorners roundedCorners = new RoundedCorners(radius);
        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners)
                .override(222, 222)
                .placeholder(placeholder);
        Glide.with(context)
                .load(url)
                .apply(options)
                .into(target);
    }

    public static void loadRound(Context context, String url, int radius, int width, int height, int placeholder, ImageView target) {
        RoundedCorners roundedCorners = new RoundedCorners(radius);
        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners)
                .override(width, height)
                .placeholder(placeholder);
        Glide.with(context)
                .load(url)
                .apply(options)
                .into(target);
    }

    public static void loadRound(Context context, String url, int radius, int width, int placeholder, ImageView target) {
        loadRound(context, url, radius, width, width, placeholder, target);
    }

    public static void loadNetRoundImg(Context context, String url, ImageView target) {
        loadNetRoundImg(context, url, 5, R.drawable.lib_img_default_grey, target);
    }

    public static void loadNetRoundImg(Context context, String url, int radius, int placeholder, ImageView target) {
        RoundedCorners roundedCorners = new RoundedCorners(radius);
        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners)
                .override(222, 222)
                .placeholder(placeholder);
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
                    if (url.contains("http:") || url.contains("https:"))
                        Glide.with(context).load(url).into(view);
                    else
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


    public static void showPopImgs22(Context context, View v, List<OImageBase> list, int index) {
        ArrayList<ZoomImageModel> zoomImageArrayList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            OImageBase img = list.get(i);
            String url = img.getPictureUrl();
            if (TextUtils.isEmpty(url)) url = img.getImageUrl();
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


    public static RequestOptions DefaultPlaceHolderOption(int placeHolder) {
        return new RequestOptions().placeholder(placeHolder);
    }
}
