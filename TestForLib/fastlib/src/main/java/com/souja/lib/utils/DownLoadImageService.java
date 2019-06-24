package com.souja.lib.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.souja.lib.inter.ImageDownLoadCallBack;

import org.xutils.common.util.LogUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 图片下载
 */
public class DownLoadImageService implements Runnable {
    private String url;
    private Context context;
    private ImageDownLoadCallBack callBack;
    private File currentFile;
    private String imgName;

    public DownLoadImageService(Context context, String url, String imgName, ImageDownLoadCallBack callBack) {
        this.context = context;
        this.url = url;
        this.imgName = imgName;
        this.callBack = callBack;
    }

    private String savePath;

    public DownLoadImageService(Context context, String url, String imgName, String savePath, ImageDownLoadCallBack callBack) {
        this.context = context;
        this.url = url;
        this.imgName = imgName;
        this.callBack = callBack;
        this.savePath = savePath;
    }

    @Override
    public void run() {
        Bitmap bitmap = null;
        try {
            bitmap = Glide.with(context)
                    .asBitmap()
                    .load(url)
                    .submit()
                    .get();
            if (bitmap != null) {
                // 在这里执行图片保存方法
                saveImageToLocal(bitmap);
            } else callBack.onDownLoadFailed();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bitmap != null && currentFile.exists()) {
                callBack.onDownLoadSuccess(bitmap);
            } else {
                callBack.onDownLoadFailed();
            }
        }
    }

    private void saveImageToLocal(Bitmap bmp) {
        // 首先保存图片
        currentFile = new File(savePath != null ? savePath : FilePath.getDownloadPath(), imgName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(currentFile);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface DownloadCallBack {
        void onSuccess(Bitmap bitmap);

        void onFail();
    }

    public static void downloadImg(Context mContext, String rootPath, String imgUrl, String imgLocPath,
                                   DownloadCallBack callBack) {
        LogUtil.e("downloadImg url1：" + imgUrl);
        if (imgUrl == null) {
            if (callBack != null) callBack.onFail();
            return;
        }
        if (!imgUrl.contains("http")) imgUrl = SHttpUtil.M_HTTP + imgUrl;
        LogUtil.e("downloadImg url2：" + imgUrl);
        DownLoadImageService service = new DownLoadImageService(mContext,
                imgUrl,
                imgLocPath,
                rootPath,
                new ImageDownLoadCallBack() {

                    @Override
                    public void onDownLoadSuccess(Bitmap bitmap) {
                        LogUtil.e("图片下载成功：" + imgLocPath);
                        if (callBack != null) callBack.onSuccess(bitmap);
                    }

                    @Override
                    public void onDownLoadFailed() {
                        LogUtil.e("图片下载失败：" + imgLocPath);
                        if (callBack != null) callBack.onFail();
                    }
                });
        //启动图片下载线程
        new Thread(service).start();
    }

}