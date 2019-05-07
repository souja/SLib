package com.souja.lib.inter;

import android.os.AsyncTask;

import org.xutils.common.util.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask extends AsyncTask<Void, Integer, Integer> {

    public interface LoadCallBack {
        void onPreExecute();

        void onProgressUpdate(Integer progress);

        void onFail();

        void onInstall();
    }

    private LoadCallBack mCallBack;

    private String mApkDownloadUrl;
    private File mNewApkInstaller;

    public DownloadTask(String apkDownloadUrl, File newApkInstaller, LoadCallBack callBack) {
        mApkDownloadUrl = apkDownloadUrl;
        mCallBack = callBack;
        mNewApkInstaller = newApkInstaller;
    }

    @Override
    public void onPreExecute() {
        mCallBack.onPreExecute();
    }

    @Override
    public Integer doInBackground(Void... params) {
        URL url;
        HttpURLConnection connection = null;
        InputStream in = null;
        FileOutputStream out = null;
        try {
            url = new URL(mApkDownloadUrl);
            connection = (HttpURLConnection) url.openConnection();

            in = connection.getInputStream();
            long fileLength = connection.getContentLength();

            out = new FileOutputStream(mNewApkInstaller);//为指定的文件路径创建文件输出流
            byte[] buffer = new byte[1024 * 1024];
            int len = 0;
            long readLength = 0;
            while ((len = in.read(buffer)) != -1) {

                out.write(buffer, 0, len);//从buffer的第0位开始读取len长度的字节到输出流
                readLength += len;
                int curProgress = (int) (((float) readLength / fileLength) * 100);
                publishProgress(curProgress);
                if (readLength >= fileLength) {
                    LogUtil.e("finish downloading,readLength:" + readLength + ",totalLength:" + fileLength);
                    break;
                }
            }
            out.flush();
            //是否安装
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    @Override
    public void onProgressUpdate(Integer... values) {
        mCallBack.onProgressUpdate(values[0]);
    }

    @Override
    public void onPostExecute(Integer i) {
        if (i == null) {
            if (mNewApkInstaller.exists()) {
                try {
                    mNewApkInstaller.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            mCallBack.onFail();

        } else {
            mCallBack.onInstall();
        }
    }
}
