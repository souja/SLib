package com.souja.lib.utils;

import org.xutils.x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileUtil {
    public static final String APP_NAME = x.isDebug() ? "011testHosFiles" : "myztHosFiles";

    public static File setUpPhotoFile() throws IOException {
        try {
            String imageFileName = "ymb_" + MDateUtils.getCurrentDate2();
            File albumF = getAlbumDir();
            return File.createTempFile(imageFileName, ".jpg.bk", albumF);
        } catch (IOException e) {
            return null;
        }
    }

    private static File getAlbumDir() {
        File file = new File(FilePath.getCameraPicturePath());
        file.mkdirs();// 创建文件夹
        return file;
    }

    /**
     * 获得文件后缀
     *
     * @param filename
     * @return (类似.mp4)
     */
    public static String getExt(String filename) {
        return filename.substring(filename.lastIndexOf('.')).toLowerCase();
    }


    /*
     * deleteDirectory:删除目录（文件夹）以及目录下的文件
     *
     * @param sPath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     * @author WangYue
     */
//    public static void deleteDirectory(String sPath) {
//        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
//        if (!sPath.endsWith(File.separator)) {
//            sPath = sPath + File.separator;
//        }
//        File dirFile = new File(sPath);
//        //如果dir对应的文件不存在，或者不是一个目录，则退出
//        if (!dirFile.exists() || !dirFile.isDirectory()) {
//            return;
//        }
//        //删除文件夹下的所有文件(包括子目录)
//        File[] files = dirFile.listFiles();
//        for (int i = 0; i < files.length; i++) {
//            //删除子文件
//            if (files[i].isFile()) {
//                deleteFile(files[i].getAbsolutePath());
//            } //删除子目录
//            else {
//                deleteDirectory(files[i].getAbsolutePath());
//            }
//        }
//    }

    /*
     * deleteFile:删除单个文件
     *
     * @param sPath 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     * @author WangYue
     */
//    private static void deleteFile(String sPath) {
//        File file = new File(sPath);
//        // 路径为文件且不为空则进行删除
//        if (file.isFile() && file.exists()) {
//            file.delete();
//        }
//    }

    /*
     * 检查磁盘空间是否大于10mb
     *
     * @return true 大于
     */
//    public static boolean isDiskAvailable() {
//        long size = getDiskAvailableSize();
//        return size > 10 * 1024 * 1024; // > 10bm
//    }

   /* public static String getHospitalSize(File file) {
        int size = (int) (file.length() / 1024);//KB
        int mSize = size / 1024;  //M
        if (mSize > 0)
            return mSize + "M";
        else return size + "KB";

    }*/

    public static void copyFile(File resfile, File tfile) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fis = new FileInputStream(resfile);
            fos = new FileOutputStream(tfile);
            in = fis.getChannel();// 得到对应的文件通道
            out = fos.getChannel();// 得到对应的文件通道
            long start = System.currentTimeMillis();
            in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道
            long end = System.currentTimeMillis();
            System.out.println("运行时间：" + (start - end) + "毫秒");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
                in.close();
                fos.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
