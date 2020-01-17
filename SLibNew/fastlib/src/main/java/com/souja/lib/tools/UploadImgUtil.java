package com.souja.lib.tools;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.souja.lib.inter.CompressImgCallBack;
import com.souja.lib.inter.IHttpCallBack;
import com.souja.lib.models.BaseModel;
import com.souja.lib.models.ODataPage;
import com.souja.lib.models.OImageBase;
import com.souja.lib.utils.FilePath;
import com.souja.lib.utils.FileUtil;
import com.souja.lib.utils.GsonUtil;
import com.souja.lib.utils.LibConstants;
import com.souja.lib.utils.MGlobal;
import com.souja.lib.utils.MTool;
import com.souja.lib.utils.SHttpUtil;
import com.souja.lib.utils.ScreenUtil;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 上传图片工具类
 * created by Ydz on 19/03/12
 */
public class UploadImgUtil {


    public interface RequestListener {

        void onSuccessAll(ArrayList<List<OImageBase>> netUrls);
    }

    public interface CompressListener {

        void onSkipNetImg(OImageBase netImgBean);

        void onSuccess(String oriPath, String comPath);

        void onFail(String oriPath);
    }

    public interface UploadListener {
        void onSuccess(String compressedPath, String netUrl);

        void onReset();

        void onError(String compressedPath);

        void onSkipNetImg(OImageBase imgBean);

        void onPathError(OImageBase imgBean);
    }

    private RequestListener mRequestListener;
    private UploadListener mUploadListener;
    private CompressListener mCompressListener;

    private Context mContext;
    private AlertDialog _mDialog;
    private TextView _tvProgressTip;
    private ProgressBar _progressBar;//上传进度条

    private String[] bzCodes;//上传图片业务码

    private ArrayList<ArrayList<OImageBase>> mListImgPath;//初始选择的图片路径
    private ArrayList<List<OImageBase>> imgPathCompressed;//图片压缩后的文件路径
    private ArrayList<List<OImageBase>> imgPathMarked;//图片添加水印后的文件路径
    private ArrayList<List<OImageBase>> relativeUrls;//图片上传成功后的相对路径
    private List<Callback.Cancelable> requests;

    private int uploadIndex;//上传图片索引

    private int fatherIndex = 0;//图片父索引
    private int childIndex;//图片子索引：第fatherIndex组第childIndex张图片

    private UploadParam mUploadParam;//上传图片配置
    private OSS mOSS;
    private boolean updateOss;
    public boolean bSubed;//是否提交过请求
    private String uploadConfigUrl;

    private float totalProgress;//总进度=>100*图片张数
    private int imgIndex = 0;//图片上传索引

    private Boolean[] waterMarkFlags;//是否需要添加水印标识

    private boolean bMarkImgs;


    public UploadImgUtil(String uploadConfigUrl) {
        this.uploadConfigUrl = uploadConfigUrl;
    }

    public void bind(Context context, String bzCode) {
        bind(context, new String[]{bzCode}, null, null);
    }

    public void bind(Context context, String bzCode, RequestListener listener) {
        bind(context, new String[]{bzCode}, null, listener);
    }

    public void bind(Context context, String[] bzCode, RequestListener listener) {
        bind(context, bzCode, null, listener);
    }

    /**
     * @param bzCode 图片组相应的业务码
     */
    public void bind(Context context, String[] bzCode, Boolean[] marks, RequestListener listener) {
        mContext = context;
        bzCodes = bzCode;
        _mDialog = getDialog();
        mRequestListener = listener;
        imgPathCompressed = new ArrayList<>();
        imgPathMarked = new ArrayList<>();
        mListImgPath = new ArrayList<>();
        relativeUrls = new ArrayList<>();
        waterMarkFlags = marks;


        for (int i = 0; i < bzCode.length; i++) {
            imgPathCompressed.add(new ArrayList<>());
            imgPathMarked.add(new ArrayList<>());
            relativeUrls.add(new ArrayList<>());
            mListImgPath.add(new ArrayList<>());
        }
    }

    //开始执行阿里云oss上传
    private void startUploadTask(final List<OImageBase> compressedImgObjs) {
        LogUtil.e("urls.size():" + compressedImgObjs.size() + " uploadIndex:" + uploadIndex);
        if (compressedImgObjs.size() <= 0) {
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

        final OImageBase compressedImgBean = compressedImgObjs.get(0);
        if (MTool.isNetBean(compressedImgBean)) {
            LogUtil.e("移除网络图片，不上传：" + MTool.getUrl(compressedImgBean));
            //添加到返回给UI的路径列表中，以便后续保存数据到后台
            relativeUrls.get(uploadIndex).add(compressedImgBean);
            if (mUploadListener != null) mUploadListener.onSkipNetImg(compressedImgBean);
            popAndContinue(compressedImgObjs, false);
            return;
        }
        if (MTool.isEmpty(compressedImgBean.getLocalPath())) {
            LogUtil.e("移除1张" + compressedImgBean.getLocalPath());
            if (mUploadListener != null) mUploadListener.onPathError(compressedImgBean);
            popAndContinue(compressedImgObjs, false);
            return;
        }

        File file = new File(compressedImgBean.getLocalPath());
        if (!file.exists() || !file.isFile()) {
            LogUtil.e("路径错误：" + compressedImgBean.getLocalPath());
            if (mUploadListener != null) mUploadListener.onPathError(compressedImgBean);
            popAndContinue(compressedImgObjs, false);
            return;
        }

        String compressedImgPath = compressedImgBean.getLocalPath();
        //当前图片的业务配置
        String objectKey = bzCodes[uploadIndex] + "/" + mUploadParam.getDir() + file.getName();
        LogUtil.e("[objectKey]" + objectKey);
        LogUtil.e("[path]" + compressedImgPath);
        relativeUrls.get(uploadIndex).add(OImageBase.createFromRelative(objectKey));//现在要给相对路径
        if (mOSS == null || updateOss) {
            initOss(mUploadParam);
        }
        PutObjectRequest put = new PutObjectRequest(mUploadParam.getBucketName(), objectKey, compressedImgPath);
        put.setProgressCallback((request, currentSize, totalSize) -> {
            LogUtil.e("progress " + currentSize + "/" + totalSize);
            if (mContext != null) {
                ((Activity) mContext).runOnUiThread(() ->
                        updateProgress(((float) currentSize / (float) totalSize) * 100f));
            }
        });
        try {
            PutObjectResult result = mOSS.putObject(put);
            LogUtil.e("uploadSuccess:" + GsonUtil.objToJson(result));
            if (mUploadListener != null) mUploadListener.onSuccess(compressedImgPath, objectKey);
            popAndContinue(compressedImgObjs, true);
        } catch (ClientException e) {
            e.printStackTrace();
            onErr(compressedImgPath);
        } catch (ServiceException e) {
            e.printStackTrace();
            onErr(compressedImgPath);
        }
    }

    //开始压缩、上传
    public void start() {
        boolean empty = true;
        float progress = 0f;
        for (List<OImageBase> list : mListImgPath) {
            if (list.size() > 0) {
                empty = false;
                progress += 100f * list.size();
            }
        }
        if (empty) {
            LogUtil.e("没有可处理的图片");
            if (mRequestListener != null)
                mRequestListener.onSuccessAll(relativeUrls);
            return;
        }
        LogUtil.e("总进度：" + progress);
        if (!_mDialog.isShowing())
            _mDialog.show();
        setTotalProgress(progress);
        if (bSubed) {
            mHandler.sendEmptyMessage(44);
        } else {
            setMsg("图片处理中...");
            mHandler.sendEmptyMessage(22);
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 22:
                    LogUtil.e("判断是否需要添加水印");
                    if (waterMarkFlags == null || waterMarkFlags.length == 0) {
                        //不需要添加水印，开始压缩
                        mHandler.sendEmptyMessage(222);
                    } else {
                        if (waterMarkFlags[fatherIndex]) {
                            LogUtil.e("第" + (fatherIndex + 1) + "组图片需要添加水印");

                            mHandler.sendEmptyMessage(33);
                        } else {
                            LogUtil.e("第" + (fatherIndex + 1) + "组图片不需要添加水印..then compress it!");

                            mHandler.sendEmptyMessage(222);
                        }
                    }
                    break;
                case 33:
                    LogUtil.e("执行添加水印");
                    if (mListImgPath.get(fatherIndex).size() > 0) {
                        OImageBase imgBean = mListImgPath.get(fatherIndex).get(childIndex);
                        if (TextUtils.isEmpty(imgBean.getAbsoluteUrl())) {//非网络图片才执行水印添加操作
                            String toMarkPath = imgBean.getLocalPath();
                            LogUtil.e("待添加水印图片路径：" + toMarkPath);
                            String markedPath = MTool.addRemark(mContext, toMarkPath, "医连医");
                            LogUtil.e("水印添加完成图片路径：" + markedPath);

                            imgPathMarked.get(fatherIndex).add(OImageBase.createFromLocal(markedPath));
                        } else {
                            LogUtil.e("网络图片，跳过水印");
                            imgPathMarked.get(fatherIndex).add(imgBean);
                        }
                        childIndex++;

                        if (childIndex > mListImgPath.get(fatherIndex).size() - 1) {
                            LogUtil.e("本组图片水印处理完成：" + (fatherIndex + 1));

                            LogUtil.e("压缩本组图片:" + (fatherIndex + 1));
                            bMarkImgs = true;
                            childIndex = 0;
                            mHandler.sendEmptyMessage(222);

                        } else {
                            LogUtil.e("处理下一张图片水印");
                            mHandler.sendEmptyMessage(33);
                        }

                    } else {
                        checkHandleIndex();
                    }
                    break;
                case 222:
                    LogUtil.e("执行图片压缩");
                    boolean canContinue = bMarkImgs ? childIndex < imgPathMarked.get(fatherIndex).size()
                            : childIndex < mListImgPath.get(fatherIndex).size();

                    if (canContinue) {
                        LogUtil.e("压缩第" + (fatherIndex + 1) + "组图片");
                        OImageBase imgBean2 = bMarkImgs ? imgPathMarked.get(fatherIndex).get(childIndex)
                                : mListImgPath.get(fatherIndex).get(childIndex);
                        if (!TextUtils.isEmpty(imgBean2.getAbsoluteUrl())
                                || !TextUtils.isEmpty(imgBean2.getPictureUrl())
                                || !TextUtils.isEmpty(imgBean2.getImageUrl())) {
                            LogUtil.e("跳过网络图片：" + imgBean2.getAbsoluteUrl());
                            if (mCompressListener != null)
                                mCompressListener.onSkipNetImg(imgBean2);
                            compressedAndContinue(imgBean2);
                        } else {
                            LogUtil.e("压缩图片：" + (fatherIndex + 1)
                                    + "==" + childIndex + "==" + imgBean2.getLocalPath());
                            compressImgs(imgBean2);
                        }
                    } else {
                        LogUtil.e("第" + (fatherIndex + 1) + "组图片已全部压缩");
                        childIndex = 0;//重置childIndex
                        bMarkImgs = false;
                        checkHandleIndex();
                    }
                    break;
                case 44://获取上传参数配置
                    getConfig();
                    break;
            }

            return false;
        }
    });

    //压缩
    private void compressImgs(OImageBase imgBean) {
        MTool.compressImage(mContext, imgBean.getLocalPath(), filePath -> {
                    LogUtil.e("onRename " + filePath);
                    return MTool.getUuidFileName();
                },
                256,
                new CompressImgCallBack() {
                    @Override
                    public void onSuccess(File file) {
                        LogUtil.e("压缩成功 " + fatherIndex + "-" + childIndex + " " + file.getAbsolutePath());
                        LogUtil.e("压缩后大小:" + MTool.getKb((double) file.length()));
                        String compressedPath = file.getAbsolutePath();
                        if (mCompressListener != null)
                            mCompressListener.onSuccess(imgBean.getLocalPath(), compressedPath);
                        compressedAndContinue(OImageBase.createFromLocal(compressedPath));
                    }

                    @Override
                    public void onFail(String oriFilePath, String msg) {
                        LogUtil.e("压缩处理失败:" + oriFilePath + ",msg:" + msg);
//                        String saveFilePath = FilePath.getCompressedPath();
//                        File oriFile = new File(oriFilePath);
//                        File tempFile = new File(saveFilePath, MTool.getUuidFileName());
//                        FileUtil.copyFile(oriFile, tempFile);
                        if (mCompressListener != null)
                            mCompressListener.onFail(imgBean.getLocalPath());
                        compressedAndContinue(imgBean);
                    }

                    @Override
                    public void onSkip(String filePath) {
                        imgBean.setLocalPath(filePath);
                        if (mCompressListener != null)
                            mCompressListener.onSkipNetImg(imgBean);
                        compressedAndContinue(imgBean);
                    }
                });
    }

    private void compressedAndContinue(OImageBase bean) {
        childIndex++;
        imgPathCompressed.get(fatherIndex).add(bean);
        mHandler.sendEmptyMessage(222);
    }

    private void checkHandleIndex() {
        fatherIndex++;
        childIndex = 0;
        if (fatherIndex > mListImgPath.size() - 1) {
            LogUtil.e("图片处理完成，开始上传...");
            fatherIndex = 0;
            mHandler.sendEmptyMessage(44);
        } else {
            LogUtil.e("处理下一组水印");
            mHandler.sendEmptyMessage(22);
        }
    }

    private void getConfig() {
        LogUtil.e("获取上传图片配置参数");
        addRequest(SHttpUtil.Get(null, uploadConfigUrl, new RequestParams(), UploadParam.class,
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
        for (List<OImageBase> list : relativeUrls) {
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

    private void onErr(String localPath) {
        if (mUploadListener != null) mUploadListener.onError(localPath);
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
                if (mRequestListener != null)
                    mRequestListener.onSuccessAll(relativeUrls);
            });
        }
    }

    private void popAndContinue(List<OImageBase> urls, boolean success) {
        urls.remove(0);
        startUploadTask(urls);
    }

    private void addRequest(Callback.Cancelable req) {
        if (requests == null) requests = new ArrayList<>();
        requests.add(req);
    }

    private void clearTempFiles() {
        if (imgPathCompressed == null) return;

        FileUtil.deleteDir(new File(FilePath.getCompressedPath()));
        FileUtil.deleteDir(FilePath.getWaterMarkPath());
    }

    private void cancelRequest() {
        if (requests == null || requests.size() == 0) return;
        for (Callback.Cancelable req : requests) {
            if (req != null && !req.isCancelled()) req.cancel();
        }
    }

    public boolean isEmptyList(int index) {
        return MTool.isEmptyList(getLocalUrls(index));
    }

    public void hideProgress() {
        if (_mDialog != null && _mDialog.isShowing())
            _mDialog.dismiss();
    }

    public void initSelected(int index, Collection<OImageBase> selected) {
        if (index <= mListImgPath.size() - 1) {
            if (!MTool.isEmptyList(mListImgPath.get(index))) {
                mListImgPath.get(index).clear();
            }
            if (selected != null)
                mListImgPath.get(index).addAll(selected);
        }
    }

    private List<OImageBase> getLocalUrls(int index) {
        if (mListImgPath == null || mListImgPath.size() == 0 || index > mListImgPath.size() - 1)
            return new ArrayList<>();
        return mListImgPath.get(index);
    }

    public void resetSubmittedStatus() {
        if (bSubed)
            bSubed = false;
        if (mUploadListener != null) mUploadListener.onReset();
    }

    public void onDestroy() {
        MGlobal.get().delAction(LibConstants.COMMON.RX_CHOOSE_PHOTO);
        cancelRequest();
        new Thread(() -> clearTempFiles()).start();
    }

    private AlertDialog getDialog() {
        createDialog(null);
        return _mDialog;
    }

    private void createDialog(@Nullable String msg) {
        if (_mDialog == null) {
            _mDialog = new AlertDialog.Builder(mContext, com.souja.lib.R.style.CustomProgressDialog).create();
            View loadView = LayoutInflater.from(mContext).inflate(com.souja.lib.R.layout.lib_upload_dialog, null);
            ScreenUtil.initScale(loadView);
            _mDialog.setView(loadView, 0, 0, 0, 0);
            _mDialog.setCanceledOnTouchOutside(false);
            _tvProgressTip = loadView.findViewById(com.souja.lib.R.id.tv_tip);
            _progressBar = loadView.findViewById(com.souja.lib.R.id.progress_bar);
        }
        _tvProgressTip.setText(TextUtils.isEmpty(msg) ? "请稍候..." : msg);
    }

    private void resetProgress() {
        if (_progressBar.getVisibility() != View.INVISIBLE)
            _progressBar.setVisibility(View.INVISIBLE);
        _progressBar.setProgress(0);
        imgIndex = 0;
    }

    private void setTotalProgress(float total) {
        totalProgress = total;
    }

    private void updateProgress(float progress) {
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

    public void clearSelected() {
        if (!MTool.isEmptyList(mListImgPath))
            for (ArrayList<OImageBase> list : mListImgPath) {
                list.clear();
            }
    }

    public void setRequestListener(RequestListener requestListener) {
        mRequestListener = requestListener;
    }

    public void setUploadListener(UploadListener uploadListener) {
        mUploadListener = uploadListener;
    }

    public void setCompressListener(CompressListener compressListener) {
        mCompressListener = compressListener;
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
