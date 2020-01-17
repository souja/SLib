package com.souja.lib.base;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.Button;

import com.souja.lib.R;
import com.souja.lib.models.MediaBean;
import com.souja.lib.models.OImageBase;
import com.souja.lib.models.RxCropInfo;
import com.souja.lib.models.RxEditImg;
import com.souja.lib.models.RxImgPath;
import com.souja.lib.models.RxImgPathB;
import com.souja.lib.models.SelectImgOptions;
import com.souja.lib.utils.DialogFactory;
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

    private HashMap<String, ArrayList<String>> mPathMap; //key-文件夹名称，value-图片路径List
    private ArrayList<String> allImageList;   //所有图片的路径
    private ArrayList<OImageBase> selectedPathListB;  //已选择的图片路径
    private PhotoAdapter mPhotoAdapter;//图片列表适配器

    private String defaultDir;//上一次选择的默认路径
    private ArrayList<MediaBean> mDirList;//相册目录List
    private DirAdapter mDirAdapter;//目录

    private Handler handler = new Handler();
    private File cameraFile;//点击拍照时的图片路径

    private int maxCount;  //可选图片数量
    private String strComplete;//完成、完成（a/n）
    private boolean bCrop,//选择图片后是否需要裁剪（一般只会出现在maxCount=1的情况下）
            skipEnable;//裁剪界面中是否可以“跳过裁剪”
    private int cropX,//剪裁的x
            cropY;//剪裁的y
    private boolean bMode2;

    private static ActPhotoGallery instance;

    @Override
    public int setViewRes() {
        return R.layout.act_select_media;
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
        mTitleBar = findViewById(R.id.m_title);
        mBtnSelectDir = findViewById(R.id.btn_selectDir);
        mBtnPreview = findViewById(R.id.btn_preview);
        mRecyclerView = findViewById(R.id.media_list);
        mDirLayout = findViewById(R.id.dir_layout);
    }

    private void initIntent() {
        Intent intent = getIntent();
        maxCount = intent.getIntExtra(SelectImgOptions.IMAGES_MAX_SELECT_COUNT, 4);
        bMode2 = intent.getBooleanExtra("mode2", false);
        bCrop = intent.getBooleanExtra(SelectImgOptions.IMAGES_CROP, false);
        if (bCrop) {
            skipEnable = intent.getBooleanExtra(SelectImgOptions.IMAGES_SKIP_CROP, true);

            cropX = intent.getIntExtra("x", -1);
            if (cropX > 0) {
                cropY = intent.getIntExtra("y", -1);
            }
            Consumer cropHeadIcon = (Consumer<RxCropInfo>) cropInfo -> {
                selectedPathListB.clear();
                selectedPathListB.add(OImageBase.createFromLocal(cropInfo.cropFilePath));
                cropInfo.mActivity.finish();
                goBack();
            };
            addAction(LibConstants.COMMON.CROP_IMG, cropHeadIcon);
        }

        ArrayList<String> selectedPathList = intent.getStringArrayListExtra(SelectImgOptions.IMAGE_PATH_LIST_SELECTED);
        if (!MTool.isEmptyList(selectedPathList)) {
            //存放调用此类的类传递过来的图片路径
            List<String> tempList = new ArrayList<>();
            for (String path : selectedPathList) {
                if (!path.contains("http") && !new File(path).exists()) {
                    tempList.add(path);
                }
            }
            if (tempList.size() > 0) {
                for (String path : tempList)
                    selectedPathList.remove(path);
            }
            selectedPathListB = new ArrayList<>();
            for (int i = 0; i < selectedPathList.size(); i++) {
                OImageBase imgBean = OImageBase.createFromLocal(selectedPathList.get(i));
                selectedPathListB.add(imgBean);
            }
        }
//        }

        if (selectedPathListB == null) selectedPathListB = new ArrayList<>();
        else
            refreshNum();
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
            if (selectedPathListB.size() > 0) {
                ActivityGallery.open(_this, 0, maxCount, true);
            }
        });

        //编辑（涂鸦等）产生了新的图片
        Consumer onEditImg = (Consumer<RxEditImg>) img -> {
            //添加到图库
            AddPicToScan(img.editPath);

            int index = -1;
            for (int i = 0; i < selectedPathListB.size(); i++) {
                OImageBase imgBean = selectedPathListB.get(i);
                if (img.oriPath.equals(imgBean.getLocalPath())) {
                    index = i;
                    break;
                }
            }
            if (index != -1) selectedPathListB.remove(index);

            if (selectedPathListB.size() < maxCount) {
                selectedPathListB.add(OImageBase.createFromLocal(img.editPath));
            }

            String curFolder = mDirList.get(mDirAdapter.getDirIndex()).getFolderName();
            LogUtil.e("curFolder : " + curFolder);
            mPathMap.get(curFolder).add(img.editPath);//添加到数据源
            //添加到当前图片目录
            mPhotoAdapter.addPath(img.editPath);
        };
        addAction(LibConstants.COMMON.RX_EDIT_IMG, onEditImg);
    }

    private void initListView() {
        mPathMap = new HashMap<>();
        allImageList = new ArrayList<>();
        mPhotoAdapter = new PhotoAdapter(this, new ArrayList<>(), position -> {
            if (position == 0) {//拍照
                if (maxCount > 1 && selectedPathListB.size() >= maxCount) {
                    showToast("已达最大可选择张数");
                    return;
                }

                checkCameraPermission();
            } else {
                ActivityGallery.open(_this, position - 1, maxCount, false);
            }
        });
        mRecyclerView.setAdapter(mPhotoAdapter);
    }

    //利用ContentProvider扫描手机中的图片，此方法在运行在子线程中
    private void getImages() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            showToast(R.string.sdcard_nosize);
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


    private PermissionUtil mPermissionUtil;

    private String[] cameraPermission = new String[]{
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
                AlertDialog dialogRefused = DialogFactory.NewDialog(_this,
                        R.string.permission_camera, "去开启", (d, i) -> {
                            d.dismiss();
                            mPermissionUtil.jumpPermissionPage();
                        }, "取消", (d, i) -> d.dismiss());
                dialogRefused.setCanceledOnTouchOutside(false);
                dialogRefused.show();
            }

            @Override
            public void denied() {
                AlertDialog dialogDenied = DialogFactory.NewDialog(_this,
                        R.string.permission_denied, "去开启", (d, i) -> {
                            d.dismiss();
                            mPermissionUtil.jumpPermissionPage();
                        }, "取消", (d, i) -> d.dismiss());
                dialogDenied.setCanceledOnTouchOutside(false);
                dialogDenied.show();
            }
        });
        mPermissionUtil.checkPermissions();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PermissionUtil.REQUEST_PERMISSIONS:
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
            Uri uri;
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(_this, LibConstants.FILE_PROVIDER, cameraFile);
            } else
                uri = Uri.fromFile(cameraFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, REQUEST_TAKE_PHONE);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            showToast("相机设备异常");
        }
    }

    public void refreshNum() {
        if (selectedPathListB.size() == 0) {
            mTitleBar.setRightMenuText(strComplete);
            mBtnPreview.setTextColor(Color.parseColor("#666666"));
            mBtnPreview.setText("预览");
        } else {
            mTitleBar.setRightMenuText(strComplete + "(" + selectedPathListB.size() + "/" + maxCount + ")");
            mBtnPreview.setTextColor(Color.parseColor("#CFCFCF"));
            mBtnPreview.setText(String.valueOf("预览(" + selectedPathListB.size() + ")"));
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
//                LogUtil.e("图片读取失败或者length为0 " + path);
            } else if (tempPath.equals(".bk") ||
                    (!tempPath.equals(".jpg") && !tempPath.equals(".png") && !tempPath.equals(".jpeg"))) {
//                LogUtil.e("图片路径无效 " + path);
            } else if (path.contains(LibConstants.APP_NAME.toLowerCase()) || path.contains("counselor")
                    || path.contains("myzt") || path.contains("ihisun")
                    || path.contains("ymb")) {
//                LogUtil.e("跳过应用文件 " + path);
            } else {
                allImageList.add(path);
                // 获取该图片的父路径名
                String parentName = new File(path).getParentFile()
                        .getName();
                // 根据父路径名将图片放入到mGruopMap中
                if (!mPathMap.containsKey(parentName)) {
                    ArrayList<String> childList = new ArrayList<>();
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
        ActCrop.open(_this, skipEnable, cropX, cropY <= 0 ? cropX : cropY, selectedPathListB.get(0).getLocalPath());
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
        mPhotoAdapter.setPhotoPathList(mPathMap.get(key));
        mRecyclerView.scrollTo(0, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (REQUEST_TAKE_PHONE == requestCode) { //相机
                if (cameraFile == null) return;
                AddPicToScan(cameraFile.getAbsolutePath());
                LogUtil.e("tempPath=" + cameraFile.getAbsolutePath() + ",cameraFile exist=" + cameraFile.exists());
                if (maxCount == 1) selectedPathListB.clear();
                selectedPathListB.add(OImageBase.createFromLocal(cameraFile.getAbsolutePath()));
                refreshNum();
                allImageList.add(0, cameraFile.getAbsolutePath());
                String curFolder = mDirList.get(mDirAdapter.getDirIndex()).getFolderName();
                if (curFolder.equals("所有图片")) {
                    mPhotoAdapter.notifyDataSetChanged();
                }
//                setResultBack();
            } else if (LibConstants.COMMON.REQ_IMAGE_GALLERY == requestCode) { //画廊
                Bundle b = data.getExtras();
                if (b == null) return;
                ArrayList<String> newList = b.getStringArrayList(SelectImgOptions.IMAGE_PATH_LIST_SELECTED);
                if (newList == null) return;
                this.selectedPathListB = MTool.getUrlsB(newList);
                refreshNum();
                if (selectedPathListB.size() > 0 && bCrop) {
                    goCrop();
                } else {
                    boolean needfinish = b.getBoolean("needFinish", false);
                    if (needfinish) {
                        setResultBack();
                    } else {
                        mPhotoAdapter.notifyDataSetChanged();
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
        if (selectedPathListB.size() > 0 && bCrop) {
            goCrop();
        } else {
            goBack();
        }
    }

    private void goBack() {
        if (containsKey(LibConstants.COMMON.RX_CHOOSE_PHOTO)) {
            addSubscription(bMode2 ? new RxImgPathB(_this, selectedPathListB)
                            : new RxImgPath(_this, MTool.getUrls(selectedPathListB)),
                    getAction(LibConstants.COMMON.RX_CHOOSE_PHOTO));
        } else {
            LogUtil.e("未设置回调");
            finish();
        }
    }

    private void AddPicToScan(String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(_this, LibConstants.FILE_PROVIDER, f);
        } else
            uri = Uri.fromFile(f);
        mediaScanIntent.setData(uri);
        this.sendBroadcast(mediaScanIntent);
    }

    public void clearList() {
        selectedPathListB.clear();
    }

    public void add(String path) {
        selectedPathListB.add(OImageBase.createFromLocal(path));
    }

    public void remove(String path) {
        OImageBase bean = null;
        for (OImageBase imgBean : selectedPathListB) {
            if (imgBean.getLocalPath().equals(path)) {
                bean = imgBean;
                break;
            }
        }
        if (bean != null)
            selectedPathListB.remove(bean);
    }

    public boolean contains(String path) {
        boolean exist = false;
        for (OImageBase bean : selectedPathListB) {
            if (bean.getLocalPath() != null && bean.getLocalPath().equals(path)) {
                exist = true;
                break;
            }
        }
        return exist;
    }

    public int getListSize() {
        return selectedPathListB.size();
    }

    public int getMaxCount() {
        return maxCount;
    }

    public ArrayList<OImageBase> getSelectedPathListB() {
        return selectedPathListB;
    }

    public ArrayList<String> getAdapterList() {
        return mPhotoAdapter.getPathList();
    }

    public static ActPhotoGallery getInstance() {
        return instance;
    }

    public static void open(Context context, int max) {
        open(context, max, null);
    }

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

    public static void openB(Context context, SelectImgOptions options) {
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
        it.putExtra("mode2", true);
        context.startActivity(it);
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