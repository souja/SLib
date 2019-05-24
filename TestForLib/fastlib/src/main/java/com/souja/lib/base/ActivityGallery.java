package com.souja.lib.base;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.souja.lib.models.RxEditImg;
import com.souja.lib.models.SelectImgOptions;
import com.souja.lib.utils.FilePath;
import com.souja.lib.utils.LibConstants;
import com.souja.lib.utils.MGlobal;
import com.souja.lib.utils.MTool;
import com.souja.lib.utils.PermissionUtil;
import com.souja.lib.widget.HackyViewPager;

import org.xutils.common.util.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.kareluo.imaging.IMGEditActivity;
import uk.co.senab.photoview.PhotoView;

/**
 * ClassName:ActivityGallery (类似微信的图片左右选择器) <br/>
 * Date: 2014年9月9日 下午3:19:45 <br/>
 *
 * @author WangYue
 * @since JDK 1.6
 */
public class ActivityGallery extends ActBase {

    private HackyViewPager viewPager;
    private TextView textView_num;
    private Button btn_complete;
    private AppCompatCheckBox checkBox;
    private AppCompatButton btnEditImg;

    private File editFile;
    private MyPagerAdapter adapter;

    private List<String> mListAll = new ArrayList<>();
    private List<String> mListSelected = new ArrayList<>();
    private int index = 0;
    private int count, maxCount;
    private Point mPoint = new Point();
    private boolean needFinish;
    private String strComplete;//完成，完成（a/n）
    private String curSelectedPath;//编辑照片前的图片路径
    //    private List<Integer> errorList = new ArrayList<>();//加载出错的图片路径列表


    public static void open(AppCompatActivity context, int index, int max, boolean handleSelected) {
        Intent it = new Intent(context, ActivityGallery.class)
                .putExtra(SelectImgOptions.IMAGES_INDEX, index)
                .putExtra(SelectImgOptions.IMAGES_MAX_SELECT_COUNT, max)
                .putExtra(SelectImgOptions.IMAGES_HANDLE_ONLY_SELECTED, handleSelected);
        context.startActivityForResult(it, LibConstants.COMMON.REQ_IMAGE_GALLERY);
    }

    @Override
    public int setViewRes() {
        return com.souja.lib.R.layout.activity_gallery;
    }

    @Override
    public void initMain() {
        initViews();
        initIntent();
        initVp();
        initListeners();
    }

    private void initViews() {
        checkBox = findViewById(com.souja.lib.R.id.gallery_checkbox);
        btn_complete = findViewById(com.souja.lib.R.id.gallery_complate);
        textView_num = findViewById(com.souja.lib.R.id.gallery_nums);
        viewPager = findViewById(com.souja.lib.R.id.gallery_vp);
        btnEditImg = findViewById(com.souja.lib.R.id.btn_edit);
        Resources res = getResources();
        strComplete = res.getString(com.souja.lib.R.string.complete);
    }

    private void initIntent() {
        Intent it = getIntent();
        boolean bOnlySelected = it.getBooleanExtra(SelectImgOptions.IMAGES_HANDLE_ONLY_SELECTED, false);
        mListSelected.addAll(ActPhotoGallery.getInstance().selectedPathList);
        if (bOnlySelected) {
            mListAll.addAll(ActPhotoGallery.getInstance().selectedPathList);
        } else {
            mListAll.addAll(ActPhotoGallery.getInstance().mAdapter.getPathList());
        }
//        mListAll = it.getStringArrayListExtra(SelectImgOptions.IMAGES_PATH_LIST_ALL);
//        mListSelected = it.getStringArrayListExtra(SelectImgOptions.IMAGE_PATH_LIST_SELECTED);
        maxCount = it.getIntExtra(SelectImgOptions.IMAGES_MAX_SELECT_COUNT, 1);
        index = it.getIntExtra(SelectImgOptions.IMAGES_INDEX, 0);
        if (mListSelected != null && !mListSelected.isEmpty()) {
            btn_complete.setText(String.valueOf(strComplete + "(" + mListSelected.size() + "/" + maxCount + ")"));
        }
    }

    private void initListeners() {
        //返回
        findViewById(com.souja.lib.R.id.backbutton).setOnClickListener(v -> {
            needFinish = false;
            SetResult();
        });
        //完成
        btn_complete.setOnClickListener(v -> {
//            needFinish = true;
            needFinish = false;
            SetResult();
        });
        //编辑
        btnEditImg.setOnClickListener(v -> {
            curSelectedPath = mListAll.get(viewPager.getCurrentItem());
            LogUtil.e("current selected path:" + curSelectedPath);
            File selectedFile = new File(curSelectedPath);
            LogUtil.e("file exist:" + selectedFile.exists());
            if (selectedFile.exists()) {
                checkEditPermission(selectedFile);
            } else {
                showToast("图片加载失败");
            }
        });
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (errorList.contains(index)) {
//                if (buttonView.getVisibility() == View.VISIBLE) {
//                    showToast("图片加载失败，不可选择");
//                    buttonView.setChecked(false);
//                }
//            } else {
            if (!mListSelected.contains(mListAll.get(index))
                    && mListSelected.size() < maxCount) {
                if (isChecked) {
                    mListSelected.add(mListAll.get(index));
                }
            } else if (mListSelected.contains(mListAll.get(index))) {
                if (!isChecked) {
                    mListSelected.remove(mListAll.get(index));
                }
            } else {
                // 超过4个 toast
                buttonView.setChecked(false);
            }
            if (mListSelected.size() > 0)
                btn_complete.setText(String.valueOf(strComplete + "(" + mListSelected.size() + "/" + maxCount + ")"));
            else
                btn_complete.setText(strComplete);
//            }
        });
    }

    PermissionUtil mPermissionUtil;

    String[] editPhotoPermission = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
    };

    private void checkEditPermission(File selectedFile) {
        if (mPermissionUtil == null) mPermissionUtil = new PermissionUtil(ActivityGallery.this);
        mPermissionUtil.addPermissions(editPhotoPermission);
        mPermissionUtil.setListener(new PermissionUtil.PermissionCheckListener() {
            @Override
            public void ok() {
                LogUtil.e("selectedFile absolute path:" + selectedFile.getAbsolutePath());
                Uri uri = Uri.fromFile(selectedFile);
                editFile = new File(FilePath.getTempPicturePath() + "/editedimg"
                        + System.currentTimeMillis() + ".jpg.bk");
                startActivityForResult(
                        new Intent(ActivityGallery.this, IMGEditActivity.class)
                                .putExtra("IMAGE_URI", uri)
                                .putExtra("IMAGE_SAVE_PATH", editFile.getAbsolutePath()),
                        LibConstants.COMMON.REQ_IMAGE_EDIT);
            }

            @Override
            public void notOk() {
                MTool.Toast(ActivityGallery.this, "请同意相关权限后再继续");
            }
        });
        mPermissionUtil.check();
    }

    private void initVp() {
        int mVp_h = 0;
        int mVp_w = MGlobal.get().getDeviceWidth();
        updateImgCount();
        mPoint.set(mVp_w, mVp_h);
        adapter = new MyPagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int posi) {
                index = posi;
                textView_num.setText(String.valueOf((posi + 1) + "/" + count));
                isCheck(posi);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int posi) {
            }
        });
        updateCurItemSelectStatus();
    }

    private void updateImgCount() {
        count = mListAll.size();
        textView_num.setText(String.valueOf((index + 1) + "/" + count));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LibConstants.COMMON.REQ_IMAGE_EDIT) {
            if (resultCode == RESULT_OK) {
                if (mListSelected.contains(curSelectedPath)) {
                    mListSelected.remove(curSelectedPath);
                }
                String editPath = editFile.getAbsolutePath();
                if (mListSelected.size() < maxCount) {
                    mListSelected.add(editPath);
//                    checkBox.setChecked(true);
                }
                addSubscription(new RxEditImg(curSelectedPath, editPath), getAction(LibConstants.COMMON.RX_EDIT_IMG));
                mListAll.add(viewPager.getCurrentItem(), editPath);
                adapter.notifyDataSetChanged();
                checkBox.setChecked(true);
                updateImgCount();
            }
        }
    }

    private void SetResult() {
        Intent intent = new Intent(_this, ActPhotoGallery.class);
        Bundle b = new Bundle();
        b.putStringArrayList(SelectImgOptions.IMAGE_PATH_LIST_SELECTED, (ArrayList<String>) mListSelected);
        b.putBoolean("needFinish", needFinish);
        intent.putExtras(b);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void updateCurItemSelectStatus() {
        viewPager.setCurrentItem(index);
        isCheck(index);
    }

    private void isCheck(int posi) {
        if (mListSelected.contains(mListAll.get(posi)))
            checkBox.setChecked(true);
        else
            checkBox.setChecked(false);
    }

    @Override
    public void onBackPressed() {
        needFinish = false;
        SetResult();
    }

    /**
     * ViewPager适配器
     */
    public class MyPagerAdapter extends PagerAdapter {
        private Context mContext;

        public MyPagerAdapter(Context context) {
            mContext = context;
            mRequestManager = Glide.with(mContext);
        }

        private RequestManager mRequestManager;

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int arg1, Object arg2) {
            container.removeView((View) arg2);
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public int getCount() {
            return mListAll.size();
        }

        @Override
        public Object instantiateItem(final ViewGroup container, final int posi) {
            String path = mListAll.get(posi);
            PhotoView imageView = new PhotoView(_this);
            mRequestManager.load(path).into(imageView);
            container.addView(imageView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            return imageView;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }
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
}
