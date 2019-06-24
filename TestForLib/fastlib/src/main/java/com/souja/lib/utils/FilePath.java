package com.souja.lib.utils;


import android.os.Environment;

import com.souja.lib.SLib;

import java.io.File;

public class FilePath {

    //im日志缓存路径
    public static String getLogPath() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + LibConstants.APP_NAME + File.separator + "00IMLog/";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static String getWebCachePath() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + LibConstants.APP_NAME + File.separator + "sonic";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static String getAvatarPath() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + LibConstants.APP_NAME + File.separator + "avatar";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    //商机，产品图片，第一张，缓存，用于分享
    public static String getBizImgSharePath() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + LibConstants.APP_NAME + File.separator + "biz";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static String getGlidePath() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + LibConstants.APP_NAME + File.separator + "glideCache";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static String getCameraPicturePath() {
        String path = Environment.getExternalStorageDirectory()
                + File.separator + LibConstants.APP_NAME + File.separator + "camera";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static String getTempPicturePath() {
        String path = Environment.getExternalStorageDirectory()
                + File.separator + LibConstants.APP_NAME + File.separator + "temp";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static String getDownloadPath() {
        String path = Environment.getExternalStorageDirectory()
                + File.separator + LibConstants.APP_NAME + File.separator + "download";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static String getDatabasePath() {
        String path = Environment.getExternalStorageDirectory()
                + File.separator + LibConstants.APP_NAME + File.separator + "db";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static String getTemplateBaseDir() {
        String path = Environment.getExternalStorageDirectory()
                + File.separator + LibConstants.APP_NAME + "/templates/";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static String getCompressMarkPath() {
        String path = Environment.getExternalStorageDirectory()
                + File.separator + LibConstants.APP_NAME + "/compressMark";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static String getTemplateDir(int templateId) {
        String path = getTemplateBaseDir() + templateId + "/";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static String getSavingZipPath(int templateId) {
        String path = Environment.getExternalStorageDirectory()
                + File.separator + LibConstants.APP_NAME + "/templates/";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        path = path + templateId + ".zip";
        return path;
    }

    //获取营销模板图的路径
    public static String getTemplateImgPath(int templateId, String bgImgName) {
        return getTemplateDir(templateId) + bgImgName;
    }

    public static String getCompressedPath() {
        String path = Environment.getExternalStorageDirectory()
                + File.separator + LibConstants.APP_NAME + File.separator + "compressed";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static File getWaterMarkPath() {
        String path = Environment.getExternalStorageDirectory()
                + File.separator + LibConstants.APP_NAME + File.separator + "waterMarked";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static void deleteTpDirs(int templateId) {
        delAllFile(getTemplateDir(templateId));
    }

    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //test ignore
    public static String getTestImgPath() {
        String path = Environment.getExternalStorageDirectory()
                + File.separator + LibConstants.APP_NAME + File.separator + "testImgs";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }
}
