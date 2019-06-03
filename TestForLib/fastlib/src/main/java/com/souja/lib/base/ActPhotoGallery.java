package com.souja.lib.base;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.Button;

import com.souja.lib.models.MediaBean;
import com.souja.lib.models.RxCropInfo;
import com.souja.lib.models.RxEditImg;
import com.souja.lib.models.RxImgPath;
import com.souja.lib.models.SelectImgOptions;
import com.souja.lib.utils.FileUtil;
import com.souja.lib.utils.LibConstants;
import com.souja.lib.utils.MTool;
import com.souja.lib.utils.PermissionUtil;
import com.souja.lib.utils.SPHelper;
import com.souja.lib.widget.TitleBar;

import org.xutils.common.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.functions.Consumer;


/**
 * ClassName:ActPhotoGallery <br/>
 * Date:     2014-9-19 下午3:20:59 <br/>
 *
 * @author WangYue
 * @since JDK 1.6
 */
public class ActPhotoGallery extends ActBase {

    private static final int REQUEST_TAKE_PHONE = 10101;

    private TitleBar mTitleBar;
    private Button mBtnSelectDir;
    private Button mBtnPreview;
    private RecyclerView mRecyclerView;
    private DirLayout mDirLayout;

    public HashMap<String, List<String>> mPathMap; //key-文件夹名称，value-图片路径List
    public List<String> allImageList;   //所有图片的路径
    public ArrayList<String> selectedPathList;  //已选择的图片路径
    public PhotoAdapter mAdapter;//图片列表适配器

    private Handler handler = new Handler();
    private File cameraFile;//点击拍照时的图片路径
    public String tempPath;//拍照图片的temp路径

    public int maxCount;  //可选图片数量
    private String strComplete;//完成、完成（a/n）
    private boolean bCrop,//选择图片后是否需要裁剪（一般只会出现在maxCount=1的情况下）
            skipEnable;//裁剪界面中是否显示“跳过裁剪”按钮
    private int cropX,//剪裁的x
            cropY;//剪裁的y

    private String defaultDir;//上一次选择的默认路径
    private List<MediaBean> mDirList;//相册目录List
    private DirAdapter mDirAdapter;//目录

    private static ActPhotoGallery instance;

    public static void open(Context context, int max, ArrayList<String> selected) {
        Intent it = new Intent(context, ActPhotoGallery.class);
        it.putExtra(SelectImgOptions.IMAGES_MAX_SELECT_COUNT, max);
        if (selected != null && selected.size() > 0)
            it.putExtra(SelectImgOptions.IMAGE_PATH_LIST_SELECTED, selected);
        context.startActivity(it);
    }

    public static void open(Context context, SelectImgOptions options) {
        Intent it = new Intent(context, ActPhotoGallery.class);
        it.putExtra(SelectImgOptions.IMAGES_MAX_SELECT_COUNT, options.max);
        if (options.selectedImgListPath != null && options.selectedImgListPath.size() > 0)
            it.putExtra(SelectImgOptions.IMAGE_PATH_LIST_SELECTED, options.selectedImgListPath);
        if (options.crop) {
            it.putExtra(SelectImgOptions.IMAGES_CROP, true);
            it.putExtra(SelectImgOptions.IMAGES_SKIP_CROP, options.skip);
            if (options.x != 0 && options.y != 0) {
                it.putExtra(SelectImgOptions.IMAGES_CROP_X, options.x);
                it.putExtra(SelectImgOptions.IMAGES_CROP_Y, options.y);
            }
        }
        context.startActivity(it);
    }

    @Override
    public int setViewRes() {
        return com.souja.lib.R.layout.act_select_media;
    }

    @Override
    public void initMain() {
        instance = this;
        strComplete = getResources().getString(com.souja.lib.R.string.complete);
        defaultDir = SPHelper.getString(SelectImgOptions.GALLERY_LAST);
        initViews();
        initIntent();
        initListeners();
        initListView();
        getImages();
    }

    private void initViews() {
        mTitleBar = findViewById(com.souja.lib.R.id.m_title);
        mBtnSelectDir = findViewById(com.souja.lib.R.id.btn_selectDir);
        mBtnPreview = findViewById(com.souja.lib.R.id.btn_preview);
        mRecyclerView = findViewById(com.souja.lib.R.id.media_list);
        mDirLayout = findViewById(com.souja.lib.R.id.dir_layout);
    }


    private void initIntent() {
        Intent intent = getIntent();
        maxCount = intent.getIntExtra(SelectImgOptions.IMAGES_MAX_SELECT_COUNT, 4);
        bCrop = intent.getBooleanExtra(SelectImgOptions.IMAGES_CROP, false);
        if (bCrop) {
            skipEnable = intent.getBooleanExtra(SelectImgOptions.IMAGES_SKIP_CROP, true);

            cropX = intent.getIntExtra("x", -1);
            if (cropX > 0) {
                cropY = intent.getIntExtra("y", -1);
            }
            Consumer cropHeadIcon = (Consumer<RxCropInfo>) cropInfo -> {
                String path = cropInfo.cropFilePath;
                selectedPathList.clear();
                selectedPathList.add(path);
                cropInfo.mActivity.finish();
                goBack();
            };
            addAction(LibConstants.COMMON.CROP_IMG, cropHeadIcon);
        }
        if (intent.getStringArrayListExtra(SelectImgOptions.IMAGE_PATH_LIST_SELECTED) != null) {
            //存放调用此类的类传递过来的图片路径
            selectedPathList = intent.getStringArrayListExtra(SelectImgOptions.IMAGE_PATH_LIST_SELECTED);
            refreshNum();
        } else
            selectedPathList = new ArrayList<>();
    }

    private void initListeners() {
        mTitleBar.setLeftClick(v -> onBackPressed());//返回
        mTitleBar.setRightClick(v -> {//完成
            setResultBack();
        });
        mBtnSelectDir.setOnClickListener(view -> {//选择图片目录
            if (!mDirLayout.isShowing()) {
                mDirLayout.show();
            } else {
                mDirLayout.dismiss();
            }
        });
        mBtnPreview.setOnClickListener(v -> {//预览
            if (selectedPathList.size() > 0) {
                ActivityGallery.open(_this, 0, maxCount, true);
            }
        });

        //编辑（涂鸦等）产生了新的图片
        Consumer onEditImg = (Consumer<RxEditImg>) img -> {
            //添加到图库
            AddPicToScan(img.editPath);
            if (selectedPathList.contains(img.oriPath)) {
                selectedPathList.remove(img.oriPath);
            }
            if (selectedPathList.size() < maxCount) {
                selectedPathList.add(img.editPath);
            }
            String curFolder = mDirList.get(mDirAdapter.getDirIndex()).getFolderName();
            LogUtil.e("curFolder : " + curFolder);
            mPathMap.get(curFolder).add(img.editPath);//添加到数据源
            //添加到当前图片目录
            mAdapter.addPath(img.editPath);
        };
        addAction(LibConstants.COMMON.RX_EDIT_IMG, onEditImg);
    }

    private void initListView() {
        mPathMap = new HashMap<>();
        allImageList = new ArrayList<>();
        mAdapter = new PhotoAdapter(this, new ArrayList<>(), position -> {
            if (position == 0) {//拍照
                if (maxCount > 1 && selectedPathList.size() >= maxCount) {
                    showToast("已达最大可选择张数");
                    return;
                }

                checkCameraPermission();
            } else {
                ActivityGallery.open(_this, position - 1, maxCount, false);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    //利用ContentProvider扫描手机中的图片，此方法在运行在子线程中
    private void getImages() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            showToast(com.souja.lib.R.string.sdcard_nosize);
            return;
        }

        // 显示进度条
        new Thread(() -> {
            scanData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            //通知Handler扫描图片完成
            mPathMap.put("所有图片", allImageList);
            handler.post(() -> {
                if (!TextUtils.isEmpty(defaultDir)) {
                    changeDir(defaultDir);
                } else
                    changeDir("所有图片");
                mDirList = MTool.subMediaGroup(mPathMap, false);
                initDirList();
            });
        }).start();
    }


    PermissionUtil mPermissionUtil;

    String[] cameraPermission = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    private void checkCameraPermission() {
        if (mPermissionUtil == null) mPermissionUtil = new PermissionUtil(_this);
        mPermissionUtil.addPermissions(cameraPermission);
        mPermissionUtil.setListener(new PermissionUtil.PermissionCheckListener() {
            @Override
            public void ok() {
                turnCamera();
            }

            @Override
            public void notOk() {
                showToast("请同意相机权限后再继续");
            }
        });
        mPermissionUtil.check();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PermissionUtil.REQUEST_PHONE_PERMISSIONS:
                mPermissionUtil.handleResults(grantResults);
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void turnCamera() {
        try {
            cameraFile = FileUtil.setUpPhotoFile();

            LogUtil.e("cameraFile path=" + cameraFile.getAbsolutePath()
                    + ",exist=" + cameraFile.exists());

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            Uri imageUri = FileProvider.getUriForFile(_this, LibConstants.packageName, cameraFile);
            Uri imageUri = Uri.fromFile(cameraFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, REQUEST_TAKE_PHONE);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            showToast("相机设备异常");
        }
    }

    public void refreshNum() {
        if (selectedPathList.size() == 0) {
            mTitleBar.setRightMenuText(strComplete);
            mBtnPreview.setTextColor(Color.parseColor("#666666"));
            mBtnPreview.setText("预览");
        } else {
            mTitleBar.setRightMenuText(strComplete + "(" + selectedPathList.size() + "/" + maxCount + ")");
            mBtnPreview.setTextColor(Color.parseColor("#CFCFCF"));
            mBtnPreview.setText(String.valueOf("预览(" + selectedPathList.size() + ")"));
        }
    }

    private void scanData(Uri uri) {
        ContentResolver mContentResolver = getContentResolver();
        Cursor mCursor = mContentResolver.query(uri, null,
                MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED + " DESC");
        while (mCursor.moveToNext()) {
            // 获取图片的路径
            String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            String tempPath = path.substring(path.lastIndexOf(".")).toLowerCase();
            File f = new File(path);
            if (!f.exists() || f.length() <= 0) {
                LogUtil.e("图片读取失败或者length为0 " + path);
            } else if (tempPath.equals(".bk") ||
                    (!tempPath.equals(".jpg") && !tempPath.equals(".png") && !tempPath.equals(".jpeg"))) {
                LogUtil.e("图片路径无效 " + path);
            } else if (path.contains(LibConstants.APP_NAME.toLowerCase()) || path.contains("counselor")
                    || path.contains("myzt") || path.contains("ihisun")
                    || path.contains("ymb")) {
                LogUtil.e("跳过应用文件 " + path);
            } else {
                allImageList.add(path);
                // 获取该图片的父路径名
                String parentName = new File(path).getParentFile()
                        .getName();
                // 根据父路径名将图片放入到mGruopMap中
                if (!mPathMap.containsKey(parentName)) {
                    List<String> childList = new ArrayList<>();
                    childList.add(path);
                    mPathMap.put(parentName, childList);
                } else {
                    mPathMap.get(parentName).add(path);
                }
            }
        }
        mCursor.close();
    }

    private void goCrop() {
        ActCrop.open(_this, skipEnable, cropX, cropY <= 0 ? cropX : cropY, selectedPathList.get(0));
    }

    private void initDirList() {
        mDirAdapter = new DirAdapter(_this, mDirList, defaultDir, false, mediaBean -> {
            String key = mediaBean.getFolderName();
            changeDir(key);
            mDirLayout.dismiss();
        });
        mDirLayout.setAdapter(mDirAdapter);
    }

    private void changeDir(String key) {
        mTitleBar.setTitle(key);
        mAdapter.setPhotoPathList(mPathMap.get(key));
        mRecyclerView.scrollTo(0, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (REQUEST_TAKE_PHONE == requestCode) { //相机
                if (cameraFile == null) return;
                tempPath = cameraFile.getAbsolutePath();
                AddPicToScan(tempPath);
                LogUtil.e("tempPath=" + tempPath + ",cameraFile exist=" + cameraFile.exists());
                if (maxCount == 1) selectedPathList.clear();
                selectedPathList.add(tempPath);
                refreshNum();
                allImageList.add(0, tempPath);
                String curFolder = mDirList.get(mDirAdapter.getDirIndex()).getFolderName();
                if (curFolder.equals("所有图片")) {
                    mAdapter.notifyDataSetChanged();
                }
//                setResultBack();
            } else if (LibConstants.COMMON.REQ_IMAGE_GALLERY == requestCode) { //画廊
                Bundle b = data.getExtras();
                if (b == null) return;
                ArrayList<String> newList = b.getStringArrayList(SelectImgOptions.IMAGE_PATH_LIST_SELECTED);
                if (newList == null) return;
                this.selectedPathList = newList;
                refreshNum();
                if (selectedPathList.size() > 0 && bCrop) {
                    goCrop();
                } else {
                    boolean needfinish = b.getBoolean("needFinish", false);
                    if (needfinish) {
                        setResultBack();
                    } else {
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        } else {
            if (requestCode == REQUEST_TAKE_PHONE) {//相机拍照
                LogUtil.e("相机拍照取消");
                if (cameraFile != null && cameraFile.exists())
                    cameraFile.delete();
            }
        }
    }

    private void setResultBack() {
        if (selectedPathList.size() > 0 && bCrop) {
            goCrop();
        } else {
            goBack();
        }
    }

    private void goBack() {
        if (containsKey(LibConstants.COMMON.RX_CHOOSE_PHOTO)) {
            addSubscription(new RxImgPath(this, selectedPathList),
                    getAction(LibConstants.COMMON.RX_CHOOSE_PHOTO));
        } else {
            LogUtil.e("未设置回调");
            finish();
        }
    }

    private void AddPicToScan(String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
//        Uri contentUri = FileProvider.getUriForFile(_this, LibConstants.packageName, f);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public static ActPhotoGallery getInstance() {
        return instance;
    }

    @Override
    public void onDestroy() {
        instance = null;
        delAction(LibConstants.COMMON.CROP_IMG);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mDirLayout.isShowing()) {
            mDirLayout.dismiss();
        } else
            super.onBackPressed();
    }
}