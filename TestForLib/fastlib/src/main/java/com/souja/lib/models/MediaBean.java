package com.souja.lib.models;

public class MediaBean {
    /**
     * 文件夹名
     */
    private String folderName;
    /**
     * 文件夹的第一张图片or视频路径
     */
    private String topMediaPath;
    /**
     * 文件夹中的图片or视频数量
     */
    private int mediaCount;

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getTopMediaPath() {
        return topMediaPath;
    }

    public void setTopMediaPath(String topMediaPath) {
        this.topMediaPath = topMediaPath;
    }

    public int getMediaCount() {
        return mediaCount;
    }

    public void setMediaCount(int mediaCount) {
        this.mediaCount = mediaCount;
    }
}
