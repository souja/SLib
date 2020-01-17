package com.souja.lib.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

import org.xutils.common.util.LogUtil;
import org.xutils.x;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MBitmapUtil {

    public static String compressImage(Bitmap image, String name) {
        if (image == null) return "";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        //循环判断如果压缩后图片是否大于500kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > 1024) {
            LogUtil.e("cur len:" + baos.toByteArray().length / 1024 + ",go on compress...");
            baos.reset();
            options -= 10;//每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }
        LogUtil.e("cur len:" + baos.toByteArray().length / 1024 + ",compressed...");
//        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
//        getFile(baos.toByteArray());
//        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return getFile(baos.toByteArray(), name);
    }

    /**
     * @param len 需要压缩到的目标,kb
     */
    public static String compressImage(Bitmap image, String name, int len) {
        if (image == null) return "";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        //循环判断如果压缩后图片是否大于500kb,大于继续压缩
        double targetLen = (double) len;
        double totalLen = (double) baos.toByteArray().length / 1024d;
        if (totalLen > targetLen) {
            LogUtil.e("totalLen=" + totalLen);
            int option = (int) (targetLen / totalLen * 100d);
            LogUtil.e("option:" + option);
            if (option == 0) option = 1;
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, option, baos);
        }
        //这里压缩options%，把压缩后的数据存放到baos中
//        while (baos.toByteArray().length / 1024 > len) {
//            LogUtil.e("cur len:" + baos.toByteArray().length / 1024 + ",go on compress...");
//            baos.reset();
//            options -= 20;//每次都减少10
//            LogUtil.e("cur option:" + options);
//            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
//        }
        LogUtil.e("cur len:" + baos.toByteArray().length / 1024 + ",compressed...");
//        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
//        getFile(baos.toByteArray());
//        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return getFile(baos.toByteArray(), name);
    }

   /* public static String saveCroppedImage(Bitmap bmp, String path) {
//        String path = FilePath.getTempPicturePath() + "/" + System.currentTimeMillis() + ".jpg.bk";
        File file = new File(path);
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }*/

    /**
     * 根据byte数组，生成文件
     */
    public static String getFile(byte[] bfile) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            String path = FilePath.getTempPicturePath() + "/" + System.currentTimeMillis() + ".jpg.bk";
            file = new File(path);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 根据byte数组，生成文件
     */
    public static String getFile(byte[] bfile, String name) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            String path = FilePath.getTempPicturePath() + "/" + name + ".jpg.bk";
            file = new File(path);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }

    //返回保存的路径
    public static File saveCroppedBmpToFile(Bitmap bmp, String dirPath, String imgName) {
        File file = new File(dirPath, imgName);
        if (file.exists()) file.delete();
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    //返回保存的路径
    public static String saveBitmapToTempFile(Bitmap bmp) {
        String fileName = System.currentTimeMillis() + ".jpg.bk";
        // 首先保存图片
        File appDir = new File(FilePath.getTempPicturePath());
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        File file = new File(appDir, fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    public static File saveMarkedBmpToFile(Bitmap bmp, String saveName) {
        // 首先保存图片
        File appDir = FilePath.getWaterMarkPath();
        File file = new File(appDir, saveName);


//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//
//        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//        bmp.recycle();
//
//        FileOutputStream fos;
//        try {
//            fos = new FileOutputStream(file);
//            fos.write(stream.toByteArray());
//            fos.flush();
//            fos.close();
//            stream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


    private static int computeSize(int srcWidth, int srcHeight) {
        srcWidth = srcWidth % 2 == 1 ? srcWidth + 1 : srcWidth;
        srcHeight = srcHeight % 2 == 1 ? srcHeight + 1 : srcHeight;

        int longSide = Math.max(srcWidth, srcHeight);
        int shortSide = Math.min(srcWidth, srcHeight);

        float scale = ((float) shortSide / longSide);
        if (scale <= 1 && scale > 0.5625) {
            if (longSide < 1664) {
                return 1;
            } else if (longSide < 4990) {
                return 2;
            } else if (longSide > 4990 && longSide < 10240) {
                return 4;
            } else {
                return longSide / 1280 == 0 ? 1 : longSide / 1280;
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            return longSide / 1280 == 0 ? 1 : longSide / 1280;
        } else {
            return (int) Math.ceil(longSide / (1280.0 / scale));
        }
    }

    public static String Bitmap2StrByBase64(Bitmap bit) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 100, bos);//参数100表示不压缩
        byte[] bytes = bos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static Bitmap compressBitmapH(String imgUrl, int size, int quality) {
        // 创建bitMap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgUrl, options);
        int height = options.outHeight;
        int width = options.outWidth;
        LogUtil.e("原来宽高：" + width + ",height:" + height);
        float scale;
        int reqW, reqH;
        if (height > width) {
            LogUtil.e("高度大于宽度，以高度计算缩放率");
            scale = 1200f * ScreenUtil.mScale / (float) height;
            reqW = (int) (scale * width);
            reqH = (int) (scale * height);
        } else {
            LogUtil.e("宽度大于高度，以宽度计算缩放率");
            scale = 1080f * ScreenUtil.mScale / (float) width;
            reqW = (int) (scale * width);
            reqH = (int) (scale * height);
        }

        LogUtil.e("缩放率：" + scale + " 缩放后宽度：" + reqW + " 缩放后高度：" + reqH);
        // 在内存中创建bitmap对象，这个对象按照缩放比例创建的
        options.inSampleSize = calculateInSampleSize(options, reqW, reqH);

        options.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeFile(imgUrl, options);
        return compressImageB(Bitmap.createScaledBitmap(bm, reqW, reqH, false), size, quality);
    }


    /**
     * 计算像素压缩的缩放比例
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     */
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        LogUtil.e("calculateInSampleSize height:" + height + ",width:" + width + ",reqW:" + reqWidth + ",reqH:" + reqHeight);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }


    /**
     * 质量压缩图片，图片占用内存减小，像素数不变，常用于上传
     *
     * @param image
     * @param size    期望图片的大小，单位为kb
     * @param options 图片压缩的质量，取值1-100，越小表示压缩的越厉害,如输入30，表示压缩70%
     */
    private static Bitmap compressImageB(Bitmap image, int size, int options) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > size) {
            options -= 10;// 每次都减少10
            baos.reset();// 重置baos即清空baos
            // 这里压缩options%，把压缩后的数据存放到baos中
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        // 把压缩后的数据baos存放到ByteArrayInputStream中
        byte[] result = baos.toByteArray();
        LogUtil.e("压缩后大小为 " + result.length / 1024 + "kb");
        ByteArrayInputStream isBm = new ByteArrayInputStream(result);
        // 把ByteArrayInputStream数据生成图片
        return BitmapFactory.decodeStream(isBm, null, null);
    }

    /**
     * 合成bitmap和点到新的bitmap
     *
     * @param savingPath 保存的本地路径
     * @param bmpOrigin  要绘制的bitmap
     * @param pointFS    要绘制的点
     * @param startX     X坐标偏移量
     * @param startY     Y坐标偏移量
     * @return 合成后的bitmap
     */
/*
    public static Bitmap saveBitmapToLocal(String savingPath, Bitmap bmpOrigin, ArrayList<PointF> pointFS, float startX, float startY) {
        // 得到图片的宽、高
        int width_bg = bmpOrigin.getWidth();
        int height_bg = bmpOrigin.getHeight();

        // 创建一个你需要尺寸的Bitmap
        Bitmap mSavedBitmap = Bitmap.createBitmap(width_bg, height_bg, Bitmap.Config.RGB_565);
        // 用这个Bitmap生成一个Canvas,然后canvas就会把内容绘制到上面这个bitmap中
        Canvas mCanvas = new Canvas(mSavedBitmap);

        // 绘制背景图片
        mCanvas.drawBitmap(bmpOrigin, 0.0f, 0.0f, null);

        Paint mPaintDot = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintDot.setColor(Color.WHITE);
        //绘制所有点
        for (PointF point : pointFS) {
            point.x -= startX;
            point.y -= startY;
            mCanvas.drawCircle(point.x, point.y, 8, mPaintDot);
        }

        // 保存绘图为本地图片
        mCanvas.save();
//        mCanvas.save(Canvas.ALL_SAVE_FLAG);
        mCanvas.restore();

        File file = new File(savingPath);
        LogUtil.e("SavingPath:" + file.getAbsolutePath());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            mSavedBitmap.compress(Bitmap.CompressFormat.PNG, 50, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogUtil.e("保存本地");

        return mSavedBitmap;
    }
*/

    /**
     * 把两个位图覆盖合成为一个位图，以底层位图的长宽为基准
     *
     * @param backBitmap  在底部的位图
     * @param frontBitmap 盖在上面的位图
     * @return
     */
    public static Bitmap mergeBitmap(Bitmap backBitmap, Bitmap frontBitmap) {

        if (backBitmap == null || backBitmap.isRecycled()
                || frontBitmap == null || frontBitmap.isRecycled()) {
//            Log.e("mergeBitmap", "backBitmap=" + backBitmap + ";frontBitmap=" + frontBitmap);
            return null;
        }
        Bitmap bitmap = backBitmap.copy(Bitmap.Config.RGB_565, true);
        Canvas canvas = new Canvas(bitmap);
        Rect baseRect = new Rect(0, 0, backBitmap.getWidth(), backBitmap.getHeight());
        Rect frontRect = new Rect(0, 0, frontBitmap.getWidth(), frontBitmap.getHeight());
        canvas.drawBitmap(frontBitmap, frontRect, baseRect, null);
        return bitmap;
    }

    public static Bitmap getMarkTextBitmap(Context gContext, String gText, int width, int height, boolean is4Showing) {
        float textSize;
        float inter;
        if (is4Showing) {
            textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, gContext.getResources().getDisplayMetrics());
            inter = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, gContext.getResources().getDisplayMetrics());
        } else {
            textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 54, gContext.getResources().getDisplayMetrics());
            inter = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, gContext.getResources().getDisplayMetrics());
        }

        int sideLength;
        if (width > height) {
            sideLength = (int) Math.sqrt(2 * (width * width));
        } else {
            sideLength = (int) Math.sqrt(2 * (height * height));
        }


        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Rect rect = new Rect();
        paint.setTextSize(textSize);
        //获取文字长度和宽度
        paint.getTextBounds(gText, 0, gText.length(), rect);

        int strwid = rect.width();
        int strhei = rect.height();

        Bitmap markBitmap = null;
        try {
            markBitmap = Bitmap.createBitmap(sideLength, sideLength, Bitmap.Config.ARGB_4444);
            Canvas canvas = new Canvas(markBitmap);
            //创建透明画布
            canvas.drawColor(Color.TRANSPARENT);

            paint.setColor(Color.BLACK);
            paint.setAlpha((int) (0.1 * 255f));
            // 获取跟清晰的图像采样
            paint.setDither(true);
            paint.setFilterBitmap(true);

            //先平移，再旋转才不会有空白，使整个图片充满
            if (width > height) {
                canvas.translate(width - sideLength - inter, sideLength - width + inter);
            } else {
                canvas.translate(height - sideLength - inter, sideLength - height + inter);
            }

            //将该文字图片逆时针方向倾斜45度
            canvas.rotate(-45);

            for (int i = 0; i <= sideLength; ) {
                int count = 0;
                for (int j = 0; j <= sideLength; count++) {
                    if (count % 2 == 0) {
                        canvas.drawText(gText, i, j, paint);
                    } else {
                        //偶数行进行错开
                        canvas.drawText(gText, i + strwid / 2, j, paint);
                    }
                    j = (int) (j + inter + strhei);
                }
                i = (int) (i + strwid + inter);
            }
            canvas.save();//Canvas.ALL_SAVE_FLAG
//  ACache.get(gContext).put(gText, markBitmap);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            if (markBitmap != null && !markBitmap.isRecycled()) {
                markBitmap.recycle();
                markBitmap = null;
            }
        }

        return markBitmap;
    }


    public static Bitmap getMarkTextBitmap(String gText, int width, int height, int alpha, boolean needDegree) {
        if (gText == null) gText = " ";
        float textSize = 36 * ScreenUtil.mScale;
        float inter = 45 * ScreenUtil.mScale;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Rect rect = new Rect();
        paint.setTextSize(textSize);
        //获取文字长度和宽度
        paint.getTextBounds(gText, 0, gText.length(), rect);

        int strwid = rect.width();
        int strhei = rect.height();

        int sideLength;
        int longerSize = width > height ? width : height;
        if (needDegree) {
            sideLength = (int) Math.sqrt(2 * (longerSize * longerSize));
        } else sideLength = width > height ? width : height;

        Bitmap markBitmap = null;
        try {
            markBitmap = Bitmap.createBitmap(sideLength, sideLength, Bitmap.Config.ARGB_4444);
            Canvas canvas = new Canvas(markBitmap);
            //创建透明画布
            canvas.drawColor(Color.TRANSPARENT);

            paint.setColor(x.isDebug() ? Color.RED : Color.WHITE);
            paint.setAlpha(alpha);
            // 获取跟清晰的图像采样
            paint.setDither(true);
            paint.setFilterBitmap(true);
            if (needDegree) {
                canvas.rotate(45);

                int moveSize = (int) Math.sqrt(2 * ((0.5 * longerSize) * (0.5 * longerSize)));
                canvas.translate(0, -moveSize);
            }
            for (int x = 0; x <= sideLength; ) {
                int n = 0;//第n行
                for (int y = needDegree ? strhei : (int) inter; y <= sideLength; n++) {
                    if (n % 2 == 0) {
                        canvas.drawText(gText, x, y, paint);
                    } else {
                        //偶数行进行错开
                        canvas.drawText(gText, x + strwid / 2, y, paint);
                    }
                    y = (int) (y + inter + strhei);
                }
                x = (int) (x + strwid + inter);
            }
            canvas.save();
//            canvas.save(Canvas.ALL_SAVE_FLAG);
        } catch (OutOfMemoryError e) {
            if (markBitmap != null && !markBitmap.isRecycled()) {
                markBitmap.recycle();
                markBitmap = null;
            }
        }
        return markBitmap;
    }

    /**
     * 截取scrollview的屏幕
     *
     * @param scrollView
     * @return
     */
    public static Bitmap getBitmapByScrollView(ScrollView scrollView) {
        int h = 0;
        Bitmap bitmap = null;
        // 获取scrollview实际高度
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
            scrollView.getChildAt(i).setBackgroundColor(
                    Color.parseColor("#ffffff"));
        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        return bitmap;
    }

    /**
     * 截取scrollview的屏幕
     *
     * @param scrollView
     * @return
     */
    public static Bitmap getBitmapByScrollView(NestedScrollView scrollView) {
        int h = 0;
        Bitmap bitmap = null;
        // 获取scrollview实际高度
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
            scrollView.getChildAt(i).setBackgroundColor(
                    Color.parseColor("#ffffff"));
        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        return bitmap;
    }

    public static Bitmap getBitmapByView(View view) {
        int h = view.getMeasuredHeight();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        view.draw(canvas);
        return bitmap;
    }

    public static void saveBmp2Gallery(Context context, Bitmap bmp, String picName) {

        String fileName = null;
        //系统相册目录
        String galleryPath = Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                + File.separator + "Camera" + File.separator;


        // 声明文件对象
        File file = null;
        // 声明输出流
        FileOutputStream outStream = null;

        try {
            // 如果有目标文件，直接获得文件对象，否则创建一个以filename为名称的文件
            file = new File(galleryPath, picName + ".jpg.bk");

            // 获得文件相对路径
            fileName = file.toString();
            // 获得输出流，如果文件中有内容，追加内容
            outStream = new FileOutputStream(fileName);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (file != null) {
            //通知相册更新
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    bmp, fileName, null);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri;
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(context, LibConstants.FILE_PROVIDER, file);
            } else
                uri = Uri.fromFile(file);
            intent.setData(uri);
            context.sendBroadcast(intent);

            MTool.Toast(context, "图片保存成功");
        }

    }

    /**
     * ListView 截屏
     *
     * @param listView
     * @param context
     * @return
     */
    public static Bitmap createBitmap(ListView listView, Context context) {
        int titleHeight, width, height, rootHeight = 0;
        Bitmap bitmap;
        Canvas canvas;
        int yPos = 0;
        int listItemNum;
        List<View> childViews = null;

        width = getDisplayMetrics(context)[0];//宽度等于屏幕宽

        ListAdapter listAdapter = listView.getAdapter();
        listItemNum = listAdapter.getCount();
        childViews = new ArrayList<View>(listItemNum);
        View itemView;
        //计算整体高度:
        for (int pos = 0; pos < listItemNum; ++pos) {
            itemView = listAdapter.getView(pos, null, listView);
            //measure过程
            itemView.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            childViews.add(itemView);
            rootHeight += itemView.getMeasuredHeight();
        }

        height = rootHeight;
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(listView.getWidth(), height, Bitmap.Config.RGB_565);
        //bitmap = BitmapUtil.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);

        Bitmap itemBitmap;
        int childHeight;
        //把每个ItemView生成图片，并画到背景画布上
        for (int pos = 0; pos < childViews.size(); ++pos) {
            itemView = childViews.get(pos);
            childHeight = itemView.getMeasuredHeight();
            itemBitmap = viewToBitmap(itemView, width, childHeight);
            if (itemBitmap != null) {
                canvas.drawBitmap(itemBitmap, 0, yPos, null);
            }
            yPos = childHeight + yPos;
        }

        canvas.save();
//        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return bitmap;
    }

    /**
     * 获取屏幕分辨率
     *
     * @param context
     * @return
     */
    public static final Integer[] getDisplayMetrics(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getApplicationContext().getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        return new Integer[]{screenWidth, screenHeight};
    }

    private static Bitmap viewToBitmap(View view, int viewWidth, int viewHeight) {
        view.layout(0, 0, viewWidth, viewHeight);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }


    /**
     * 把View对象转换成bitmap
     */
    public static Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }


    /**
     * 获取Layout截图
     *
     * @return 所需区域的截图
     */
    public static Bitmap getBitmap(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();  //启用DrawingCache并创建位图
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache()); //创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
        view.setDrawingCacheEnabled(false);  //禁用DrawingCahce否则会影响性能
        return bitmap;
    }


    /*
     * 把两个位图覆盖合成为一个位图，左右拼接
     *
     * @param leftBitmap
     * @param rightBitmap
     * @param isBaseMax   是否以宽度大的位图为准，true则小图等比拉伸，false则大图等比压缩
     * @return
     */
   /* public static Bitmap mergeBmpLR(Bitmap leftBitmap, Bitmap rightBitmap, boolean isBaseMax) {

        if (leftBitmap == null || leftBitmap.isRecycled()
                || rightBitmap == null || rightBitmap.isRecycled()) {
//            Log.e("mergeBitmapLR", "leftBitmap=" + leftBitmap + ";rightBitmap=" + rightBitmap);
            return null;
        }
        int height = 0; // 拼接后的高度，按照参数取大或取小
        if (isBaseMax) {
            height = leftBitmap.getHeight() > rightBitmap.getHeight() ? leftBitmap.getHeight() : rightBitmap.getHeight();
        } else {
            height = leftBitmap.getHeight() < rightBitmap.getHeight() ? leftBitmap.getHeight() : rightBitmap.getHeight();
        }

        // 缩放之后的bitmap
        Bitmap tempBitmapL = leftBitmap;
        Bitmap tempBitmapR = rightBitmap;

        if (leftBitmap.getHeight() != height) {
            tempBitmapL = Bitmap.createScaledBitmap(leftBitmap, (int) (leftBitmap.getWidth() * 1f / leftBitmap.getHeight() * height), height, false);
        } else if (rightBitmap.getHeight() != height) {
            tempBitmapR = Bitmap.createScaledBitmap(rightBitmap, (int) (rightBitmap.getWidth() * 1f / rightBitmap.getHeight() * height), height, false);
        }

        // 拼接后的宽度
        int width = tempBitmapL.getWidth() + tempBitmapR.getWidth();

        // 定义输出的bitmap
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);

        // 缩放后两个bitmap需要绘制的参数
        Rect leftRect = new Rect(0, 0, tempBitmapL.getWidth(), tempBitmapL.getHeight());
        Rect rightRect = new Rect(0, 0, tempBitmapR.getWidth(), tempBitmapR.getHeight());

        // 右边图需要绘制的位置，往右边偏移左边图的宽度，高度是相同的
        Rect rightRectT = new Rect(tempBitmapL.getWidth(), 0, width, height);

        canvas.drawBitmap(tempBitmapL, leftRect, leftRect, null);
        canvas.drawBitmap(tempBitmapR, rightRect, rightRectT, null);
        return bitmap;
    }*/


    /*
     * 把两个位图覆盖合成为一个位图，上下拼接
     *
     * @param topBitmap
     * @param bottomBitmap
     * @param isBaseMax    是否以高度大的位图为准，true则小图等比拉伸，false则大图等比压缩
     * @return
     */
    /*public static Bitmap mergeBmpTB(Bitmap topBitmap, Bitmap bottomBitmap, boolean isBaseMax) {

        if (topBitmap == null || topBitmap.isRecycled()
                || bottomBitmap == null || bottomBitmap.isRecycled()) {
//            Log.e("mergeBmpTB", "topBitmap=" + topBitmap + ";bottomBitmap=" + bottomBitmap);
            return null;
        }
        int width = 0;
        if (isBaseMax) {
            width = topBitmap.getWidth() > bottomBitmap.getWidth() ? topBitmap.getWidth() : bottomBitmap.getWidth();
        } else {
            width = topBitmap.getWidth() < bottomBitmap.getWidth() ? topBitmap.getWidth() : bottomBitmap.getWidth();
        }
        Bitmap tempBitmapT = topBitmap;
        Bitmap tempBitmapB = bottomBitmap;

        if (topBitmap.getWidth() != width) {
            tempBitmapT = Bitmap.createScaledBitmap(topBitmap, width, (int) (topBitmap.getHeight() * 1f / topBitmap.getWidth() * width), false);
        } else if (bottomBitmap.getWidth() != width) {
            tempBitmapB = Bitmap.createScaledBitmap(bottomBitmap, width, (int) (bottomBitmap.getHeight() * 1f / bottomBitmap.getWidth() * width), false);
        }

        int height = tempBitmapT.getHeight() + tempBitmapB.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);

        Rect topRect = new Rect(0, 0, tempBitmapT.getWidth(), tempBitmapT.getHeight());
        Rect bottomRect = new Rect(0, 0, tempBitmapB.getWidth(), tempBitmapB.getHeight());

        Rect bottomRectT = new Rect(0, tempBitmapT.getHeight(), width, height);

        canvas.drawBitmap(tempBitmapT, topRect, topRect, null);
        canvas.drawBitmap(tempBitmapB, bottomRect, bottomRectT, null);
        return bitmap;
    }*/

/*
    public static Bitmap createWaterMaskBitmap(Bitmap src) {
        if (src == null) {
            return null;
        }
        String markText = "美业智图APP";
        int width = src.getWidth();
        int height = src.getHeight();
        //创建一个bitmap
        Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);// 创建一个新的和SRC长度宽度一样的位图
        //将该图片作为画布
        Canvas canvas = new Canvas(newBitmap);
        //在画布 0，0坐标上开始绘制原始图片
        canvas.drawBitmap(src, 0, 0, null);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setTextSize(42f * ScreenUtil.mScale);
        paint.setShadowLayer(5, 5, 5, Color.GRAY);
        Rect rect = new Rect();
        paint.getTextBounds(markText, 0, markText.length(), rect);

        int strwid = rect.width();
        int strhei = rect.height();

        int startX = width - strwid - 30;
        int startY = strhei + 15;
        canvas.drawText(markText, startX, startY, paint);

        // 保存
        canvas.save(Canvas.ALL_SAVE_FLAG);
        // 存储
        canvas.restore();
        return newBitmap;
    }
*/

}
