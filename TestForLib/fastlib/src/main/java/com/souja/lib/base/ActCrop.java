package com.souja.lib.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.souja.lib.R;
import com.souja.lib.models.RxCropInfo;
import com.souja.lib.utils.FilePath;
import com.souja.lib.utils.LibConstants;
import com.souja.lib.utils.MBitmapUtil;
import com.souja.lib.utils.MDateUtils;
import com.souja.lib.utils.MTool;
import com.souja.lib.widget.MCropCoverView;
import com.souja.lib.widget.TouchImageView;

import java.io.File;
import java.util.concurrent.ExecutionException;


public class ActCrop extends ActBase {

    private TouchImageView ivPhoto;
    private MCropCoverView ivRect;
    private TextView tvSkip;
    private TextView tvFinish;

    private String imgPath;
    private boolean skipEnable;
    private Bitmap bitmap;

    public static void open(Context context, boolean skipEnable, int cropX, int cropY, String imgPath) {
        context.startActivity(new Intent(context, ActCrop.class)
                .putExtra("skip", skipEnable)
                .putExtra("x", cropX)
                .putExtra("y", cropY <= 0 ? cropX : cropY)
                .putExtra("path", imgPath));
    }

    @Override
    public int setViewRes() {
        return R.layout.act_crop;
    }

    private void initViews(){
        ivPhoto = findViewById(R.id.iv_photo);
        ivRect = findViewById(R.id.iv_rect);
        tvSkip = findViewById(R.id.tv_skip);
        tvFinish = findViewById(R.id.tv_finish);
    }

    @Override
    public void initMain() {
        MTool.setStatusBarFullTransparent(getWindow());
        MTool.setStatusBarTextColor(getWindow(), true);
        initViews();
        Intent it = getIntent();
        imgPath = it.getStringExtra("path");
        int ratioType = it.getIntExtra("type", 1);
        skipEnable = it.getBooleanExtra("skip", true);
        int width = it.getIntExtra("x", -1);
        int height = it.getIntExtra("y", -1);
        if (width != -1) {
            ivRect.setWidthHeight(width, height == -1 ? width : height);
        } else
            ivRect.setRatioType(ratioType);

        //跳过，不剪裁
        tvSkip.setOnClickListener(v -> {
            if (containsKey(LibConstants.CROP_IMG))
                addSubscription(new RxCropInfo(_this, imgPath),
                        getAction(LibConstants.CROP_IMG));
            else onBackPressed();
        });
        //完成裁剪
        tvFinish.setOnClickListener(v -> {
            File savedFile = MBitmapUtil.saveCroppedBmpToFile(getCroppedBmp(),
                    FilePath.getTempPicturePath(), MDateUtils.getCurrentDate2() + ".jpg.bk");
            if (containsKey(LibConstants.CROP_IMG)) {
                addSubscription(new RxCropInfo(_this, savedFile.getAbsolutePath()),
                        getAction(LibConstants.CROP_IMG));
            } else onBackPressed();
        });
        new Thread(() -> {
            try {
                bitmap = Glide.with(_this)
                        .asBitmap()
                        .load(imgPath)
                        .submit()
                        .get();
                runOnUiThread(() -> {
                    ivPhoto.setupBmp(bitmap);
                    tvFinish.setVisibility(View.VISIBLE);
                    if (skipEnable)
                        tvSkip.setVisibility(View.VISIBLE);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private Bitmap getCroppedBmp() {
        int h = ivPhoto.getMeasuredHeight();
        Bitmap bitmap = Bitmap.createBitmap(ivPhoto.getMeasuredWidth(), h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        ivPhoto.draw(canvas);

        Bitmap bitmapR = Bitmap.createBitmap(ivRect.getCropWidth(), ivRect.getCropHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvasR = new Canvas(bitmapR);
        Rect src = new Rect(ivRect.getMLeft(), ivRect.getMTop(), ivRect.getMRight(), ivRect.getMBot());
        Rect dsc = new Rect(0, 0, ivRect.getCropWidth(), ivRect.getCropHeight());
        canvasR.drawBitmap(bitmap, src, dsc, null);

        return bitmapR;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
