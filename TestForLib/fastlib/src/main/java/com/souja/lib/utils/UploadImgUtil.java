package com.souja.lib.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.souja.lib.R;
import com.souja.lib.adapter.AdapterImgs;
import com.souja.lib.base.ActPhotoGallery;
import com.souja.lib.inter.CompressImgCallBack;
import com.souja.lib.inter.IHttpCallBack;
import com.souja.lib.models.BaseModel;
import com.souja.lib.models.ODataPage;
import com.souja.lib.models.RxImgPath;
import com.souja.lib.models.SelectImgOptions;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.reactivex.functions.Consumer;

/**
 * 上传图片工具类
 * created by Ydz on 19/03/12
 */
public class UploadImgUtil {

    private Context mContext;
    private AlertDialog _mDialog;
    private TextView _tvProgressTip;
    private ProgressBar _progressBar;//上传进度条

    private NestedScrollView mScrollView;
    private AdapterImgs[] mAdapters;
    private SelectImgOptions[] mChooseImgOptions;
    private ArrayList<List<String>> imgPathCompressed;//图片压缩后的文件路径
    private ArrayList<ArrayList<String>> mListImgPath;//图片路径
    private ArrayList<List<String>> relativeUrls;//图片上传成功后的相对路径
    private List<Callback.Cancelable> requests;
    private String[] bzCodes;//上传图片业务码
    private int compressFatherIndex = 0;//图片压缩父索引：第compressFatherIndex组图片，值范围<=recyclerViews.length
    private int compressChildIndex;//压缩子索引：第compressFatherIndex组第compressChildIndex张图片
    private int uploadIndex;//上传图片索引

    private UploadParam mUploadParam;//上传图片配置
    private OSS mOSS;
    private SelectPicCallBack mCallBack;
    private RequestListener mRequestListener;
    private boolean updateOss;
    private boolean bSubed;//是否提交过请求
    private String uploadConfigUrl;

    private float totalProgress;//总进度=>100*图片张数
    private int imgIndex = 0;//图片上传索引

    public UploadImgUtil(String uploadConfigUrl) {
        this.uploadConfigUrl = uploadConfigUrl;
    }

    public void bind(Context context, String bzCode, int max, RequestListener listener, RecyclerView... recyclerViews) {
        bind(context, bzCode, new SelectImgOptions(max), listener, recyclerViews);
    }

    public void bind(Context context, String bzCode, SelectImgOptions options, RequestListener listener, RecyclerView... recyclerViews) {
        bind(context, new String[]{bzCode}, new SelectImgOptions[]{options}, listener, recyclerViews);
    }

    /**
     * @param bzCode  图片组相应的业务码
     * @param options 选择图片相关参数（是否裁剪、xy比例等）
     */
    public void bind(Context context, String[] bzCode, SelectImgOptions[] options, RequestListener listener,
                     RecyclerView... recyclerViews) {
        mContext = context;
        bzCodes = bzCode;
        mChooseImgOptions = options;
        _mDialog = getDialog();
        mRequestListener = listener;
        imgPathCompressed = new ArrayList<>();
        mListImgPath = new ArrayList();
        relativeUrls = new ArrayList<>();

        mAdapters = new AdapterImgs[recyclerViews.length];

        for (int i = 0; i < recyclerViews.length; i++) {
            imgPathCompressed.add(new ArrayList<>());
            relativeUrls.add(new ArrayList<>());
            ArrayList<String> path = new ArrayList<>();
            mListImgPath.add(path);
            int finalI = i;
            mAdapters[i] = new AdapterImgs(mContext, path, new AdapterImgs.MListener() {
                @Override
                public void onAdd() {
                    choosePhotos(finalI);
                }

                @Override
                public void onDelete(int position) {
                    String path = mListImgPath.get(finalI).get(position);
                    mListImgPath.get(finalI).remove(position);
                    mAdapters[finalI].notifyDataSetChanged();
                    bSubed = false;
                    mRequestListener.notifyImgChanged(path);
                }
            });
            mAdapters[i].setMaxCount(options[i].max);
            recyclerViews[i].setAdapter(mAdapters[i]);
            recyclerViews[i].setNestedScrollingEnabled(false);
        }
    }

    //开始压缩、上传
    public void start() {
        boolean empty = true;
        float progress = 0f;
        for (List<String> list : mListImgPath) {
            if (list.size() > 0) {
                empty = false;
                progress += 100f * list.size();
            }
        }
        if (empty) {
            LogUtil.e("没有可处理的图片");
            mRequestListener.onSuccess(relativeUrls);
            return;
        }
        LogUtil.e("总进度：" + progress);
        if (!_mDialog.isShowing())
            _mDialog.show();
        setTotalProgress(progress);
        if (bSubed) {
            mHandler.sendEmptyMessage(22);
        } else {
            if (!mRequestListener.check()) {
                _mDialog.dismiss();
                return;
            }
            setMsg("图片处理中...");
            mHandler.sendEmptyMessage(11);
        }
    }

    //压缩
    private void compressImgs(String oriPath) {
        MTool.compressImage(mContext, oriPath, filePath -> {
                    LogUtil.e("onRename " + filePath);
                    return getFileName();
                },
                new CompressImgCallBack() {
                    @Override
                    public void onSuccess(File file) {
                        LogUtil.e("压缩成功 " + compressFatherIndex + "-" + compressChildIndex + " " + file.getAbsolutePath());
                        LogUtil.e("压缩后大小:" + MTool.getKb((double) file.length()));
                        continueCompress(file.getAbsolutePath());
                    }

                    @Override
                    public void onFail(String oriFilePath, String msg) {
                        String saveFilePath = FilePath.getCompressedPath();
                        File oriFile = new File(oriFilePath);
                        File tempFile = new File(saveFilePath, oriFile.getName());
                        FileUtil.copyFile(oriFile, tempFile);
                        continueCompress(oriFilePath);
                    }

                    @Override
                    public void onSkip(String filePath) {
                        continueCompress(filePath);
                    }
                });
    }

    private void continueCompress(String filePath) {
        imgPathCompressed.get(compressFatherIndex).add(filePath);
        compressChildIndex++;
        mHandler.sendEmptyMessage(11);
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 11://按组依次压缩
                    if (compressChildIndex >= mListImgPath.get(compressFatherIndex).size()) {
                        LogUtil.e("第" + (compressFatherIndex + 1) + "组图片已全部压缩:");
                        compressChildIndex = 0;//重置childIndex
                        if (compressFatherIndex >= mListImgPath.size() - 1) {
                            compressFatherIndex = 0;//重置fatherIndex
                            LogUtil.e("所有组图片都已压缩");
                            mHandler.sendEmptyMessage(22);
                        } else {
                            compressFatherIndex++;
                            LogUtil.e("开始压缩第" + (compressFatherIndex + 1) + "组图片");
                            mHandler.sendEmptyMessage(11);
                        }
                    } else {
                        String imgPath = mListImgPath.get(compressFatherIndex).get(compressChildIndex);
                        if (imgPath.contains("http:") || imgPath.contains("https:")) {
                            LogUtil.e("跳过网络图片：" + imgPath);
                            continueCompress(imgPath);
                        } else {
                            LogUtil.e("压缩图片：" + compressFatherIndex + "==" + compressChildIndex + "==" + imgPath);
                            compressImgs(imgPath);
                        }
                    }
                    break;
                case 22://（压缩完成 || 压缩过&&提交过&&没有修改过）=> 获取配置参数
                    getConfig();
                    break;
            }

            return false;
        }
    });

    private void getConfig() {
        LogUtil.e("获取上传图片配置参数");
        addRequest(SHttpUtil.Post(null, uploadConfigUrl, new RequestParams(), UploadParam.class,
                new IHttpCallBack<UploadParam>() {
                    @Override
                    public void OnSuccess(String msg, ODataPage page, ArrayList<UploadParam> data) {
                        if (data.size() > 0) {
                            LogUtil.e("获取配置参数成功");
                            mUploadParam = data.get(0);
                            updateOss = true;
                            if (bSubed)//如果提交过数据，并且没有修改过任何内容，则使用之前的数据直接提交
                                handleOnUploadImgSuccess();
                            else {
                                reset();
                                prepareUpload();
                            }
                        } else {
                            _mDialog.dismiss();
                            MTool.Toast(mContext, "获取配置信息失败");
                        }
                    }

                    @Override
                    public void OnFailure(String msg) {
                        _mDialog.dismiss();
                        MTool.Toast(mContext, msg);
                    }
                }));
    }

    private void reset() {
        bSubed = false;
        uploadIndex = 0;
        for (List<String> list : relativeUrls) {
            list.clear();
        }
    }

    private void prepareUpload() {
        bSubed = true;
        LogUtil.e("开始上传第" + (uploadIndex + 1) + "组图片");
        new Thread(() -> startUploadTask(imgPathCompressed.get(uploadIndex))).start();
    }

    private void initOss(UploadParam uploadParam) {
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(
                uploadParam.getAccessKeyId(),
                uploadParam.getAccessKeySecret(),
                uploadParam.getSecurityToken());
        mOSS = new OSSClient(mContext, uploadParam.getEndPoint(), credentialProvider);
        updateOss = false;
    }

    private void startUploadTask(final List<String> urls) {
        LogUtil.e(urls.size() + " " + uploadIndex + " " + imgPathCompressed.size());
        if (urls.size() <= 0) {
            if (uploadIndex >= imgPathCompressed.size() - 1) {
                LogUtil.e("所有组图片都已上传");
                uploadIndex = 0;
                handleOnUploadImgSuccess();
            } else {
                LogUtil.e("上传下一组...");
                uploadIndex++;
                prepareUpload();
            }
            return;
        }

        final String localPath = urls.get(0);
        if (MTool.isNetUrlPath(localPath)) {
            LogUtil.e("移除网络图片，不上传：" + localPath);
            //添加到返回给UI的路径列表中，以便后续保存数据到后台
            relativeUrls.get(uploadIndex).add(localPath);
            popAndContinue(urls);
            return;
        }
        if (MTool.isEmpty(localPath)) {
            LogUtil.e("移除1张" + localPath);
            popAndContinue(urls);
            return;
        }

        File file = new File(localPath);
        if (!file.exists() || !file.isFile()) {
            LogUtil.e("路径错误：" + localPath);
            popAndContinue(urls);
            return;
        }

        //当前图片的业务配置
        String objectKey = bzCodes[uploadIndex] + "/" + mUploadParam.getDir() + file.getName();
        LogUtil.e("[objectKey]" + objectKey);
        LogUtil.e("[path]" + localPath);
        relativeUrls.get(uploadIndex).add(objectKey);//现在要给相对路径
        if (mOSS == null || updateOss) {
            initOss(mUploadParam);
        }
        PutObjectRequest put = new PutObjectRequest(mUploadParam.getBucketName(), objectKey, localPath);
        put.setProgressCallback((request, currentSize, totalSize) -> {
            LogUtil.e("progress " + currentSize + "/" + totalSize);
            if (mContext != null) {
                ((Activity) mContext).runOnUiThread(() ->
                        updateProgress(((float) currentSize / (float) totalSize) * 100f));
            }
        });
        try {
            mOSS.putObject(put);
            popAndContinue(urls);
        } catch (ClientException e) {
            e.printStackTrace();
            onErr(localPath);
        } catch (ServiceException e) {
            e.printStackTrace();
            onErr(localPath);
        }
    }

    private void onErr(String localPath) {
        bSubed = false;
        if (mContext != null) {
            ((Activity) mContext).runOnUiThread(() -> {
                _mDialog.dismiss();
                resetProgress();
                MTool.Toast(mContext, "上传失败：" + localPath);
            });
        }
    }

    private void handleOnUploadImgSuccess() {
        if (mContext != null) {
            ((Activity) mContext).runOnUiThread(() -> {
                resetProgress();
                setMsg("保存数据中...");
                mRequestListener.onSuccess(relativeUrls);
            });
        }
    }

    private void popAndContinue(List<String> urls) {
        urls.remove(0);
        startUploadTask(urls);
    }

    private void addRequest(Callback.Cancelable req) {
        if (requests == null) requests = new ArrayList<>();
        requests.add(req);
    }

    private void clearTempFiles() {
        if (imgPathCompressed == null) return;
        for (List<String> files : imgPathCompressed) {
            for (String fPath : files) {
                File file = new File(fPath);
                if (file.exists()) {
                    LogUtil.e("删除缓存图片:" + file.getPath());
                    file.delete();
                }
            }
        }
    }

    private void cancelRequest() {
        if (requests == null || requests.size() == 0) return;
        for (Callback.Cancelable req : requests) {
            if (req != null && !req.isCancelled()) req.cancel();
        }
    }

    private String getFileName() {
        String uuid = UUID.randomUUID().toString();
        String newFileName = uuid + ".bk";
        LogUtil.e("[upload file name]" + newFileName);
        return newFileName;
    }

    public void setScrollView(NestedScrollView scrollView) {
        mScrollView = scrollView;
    }

    public boolean isEmptyList(int index) {
        return MTool.isEmptyList(getLocalUrls(index));
    }

    public void hideProgress() {
        if (_mDialog != null && _mDialog.isShowing())
            _mDialog.dismiss();
    }

    public void initSelected(int index, List<String> selected) {
        mListImgPath.get(index).addAll(selected);
        mAdapters[index].notifyDataSetChanged();
    }

    public List<String> getLocalUrls(int index) {
        if (mListImgPath == null || mListImgPath.size() == 0 || index > mListImgPath.size() - 1)
            return new ArrayList<>();
        return mListImgPath.get(index);
    }

    public void resetSub() {
        bSubed = false;
    }

    //添加照片
    public void choosePhotos(int choosePhotoIndex) {
        LogUtil.e("choosePhotoIndex:" + choosePhotoIndex);

        Consumer chooseImgs = (Consumer<RxImgPath>) model -> {
            model.mActivity.finish();
            bSubed = false;
            mRequestListener.notifyImgChanged(null);
            if (model.pathList.size() > 0) {
                mListImgPath.get(choosePhotoIndex).clear();
                mListImgPath.get(choosePhotoIndex).addAll(model.pathList);
                mAdapters[choosePhotoIndex].notifyDataSetChanged();
                if (mScrollView != null)
                    mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                if (mCallBack != null) mCallBack.onPicSelect(model.pathList, choosePhotoIndex);
            }
            MGlobal.get().delAction(LibConstants.COMMON.RX_CHOOSE_PHOTO);
        };
        MGlobal.get().addAction(LibConstants.COMMON.RX_CHOOSE_PHOTO, chooseImgs);
        ActPhotoGallery.open(mContext, mChooseImgOptions[choosePhotoIndex].setSelected(mListImgPath.get(choosePhotoIndex)));
    }

    public void setSelectPicCallBack(SelectPicCallBack callBack) {
        mCallBack = callBack;
    }

    public void onDestroy() {
        MGlobal.get().delAction(LibConstants.COMMON.RX_CHOOSE_PHOTO);
        cancelRequest();
        new Thread(() -> clearTempFiles()).start();
    }

    public AlertDialog getDialog() {
        createDialog(null);
        return _mDialog;
    }

    public AlertDialog getDialog(String msg) {
        createDialog(msg);
        return _mDialog;
    }

    private void createDialog(@Nullable String msg) {
        if (_mDialog == null) {
            _mDialog = new AlertDialog.Builder(mContext, R.style.CustomProgressDialog).create();
            View loadView = LayoutInflater.from(mContext).inflate(R.layout.lib_upload_dialog, null);
            ScreenUtil.initScale(loadView);
            _mDialog.setView(loadView, 0, 0, 0, 0);
            _mDialog.setCanceledOnTouchOutside(false);
            _tvProgressTip = loadView.findViewById(R.id.tv_tip);
            _progressBar = loadView.findViewById(R.id.progress_bar);
        }
        _tvProgressTip.setText(TextUtils.isEmpty(msg) ? "请稍候..." : msg);
    }

    public void resetProgress() {
        if (_progressBar.getVisibility() != View.INVISIBLE)
            _progressBar.setVisibility(View.INVISIBLE);
        _progressBar.setProgress(0);
        imgIndex = 0;
    }

    public void setTotalProgress(float total) {
        totalProgress = total;
    }

    public void updateProgress(float progress) {
        if (_progressBar.getVisibility() != View.VISIBLE) {
            _progressBar.setVisibility(View.VISIBLE);
            setMsg("图片上传中...");
        }
        float mCurProgress = progress + imgIndex * 100;
        if (progress == 100) imgIndex++;
        int p = (int) ((mCurProgress / totalProgress) * 100f);
        _progressBar.setProgress(p);
    }

    private void setMsg(String tip) {
        _tvProgressTip.setText(tip);
    }


    public interface RequestListener {
        boolean check();

        void notifyImgChanged(String path);

        void onSuccess(ArrayList<List<String>> netUrls);
    }

    public interface SelectPicCallBack {
        void onPicSelect(ArrayList<String> imgUrls, int index);
    }

    public static class UploadParam extends BaseModel {

        private String SecurityToken;
        private String bucketName;
        private String showPreUrl;//上传成功后图片加载的前缀
        private String AccessKeyId;
        private String AccessKeySecret;
        private String Expiration;
        private String endPoint;
        private String dir;//code+dir+fileName

        public String getSecurityToken() {
            return SecurityToken;
        }

        public void setSecurityToken(String SecurityToken) {
            this.SecurityToken = SecurityToken;
        }

        public String getBucketName() {
            return bucketName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }

        public String getShowPreUrl() {
            return showPreUrl;
        }

        public void setShowPreUrl(String showPreUrl) {
            this.showPreUrl = showPreUrl;
        }

        public String getAccessKeyId() {
            return AccessKeyId;
        }

        public void setAccessKeyId(String AccessKeyId) {
            this.AccessKeyId = AccessKeyId;
        }

        public String getAccessKeySecret() {
            return AccessKeySecret;
        }

        public void setAccessKeySecret(String AccessKeySecret) {
            this.AccessKeySecret = AccessKeySecret;
        }

        public String getExpiration() {
            return Expiration;
        }

        public void setExpiration(String Expiration) {
            this.Expiration = Expiration;
        }

        public String getDir() {
            return dir;
        }

        public void setDir(String dir) {
            this.dir = dir;
        }

        public String getEndPoint() {
            return endPoint;
        }

        public void setEndPoint(String endPoint) {
            this.endPoint = endPoint;
        }
    }
}
