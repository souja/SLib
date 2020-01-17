package com.souja.lib.models;

/**
 * another image model
 */
public class OImageBase extends BaseModel {
    private String absoluteUrl;//load with this
    private String relativeUrl;
    private String imageUrl;
    private String pictureUrl;
    private String localPath;
    private int type;
    private int id;
    private String remark;
    private int point;
    private int sort;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public OImageBase() {
    }

    public static OImageBase createFromLocal(String localPath) {
        OImageBase bean = new OImageBase();
        bean.localPath = localPath;
        return bean;
    }

    public static OImageBase createFromRelative(String relativeUrl) {
        OImageBase bean = new OImageBase();
        bean.relativeUrl = relativeUrl;
        return bean;
    }

    public static OImageBase createFromAbs(String absoluteUrl) {
        OImageBase bean = new OImageBase();
        bean.absoluteUrl = absoluteUrl;
        return bean;
    }
    public static OImageBase createFromAbsRel(String absoluteUrl, String relativeUrl) {
        OImageBase bean = new OImageBase();
        bean.absoluteUrl = absoluteUrl;
        bean.relativeUrl = relativeUrl;
        return bean;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRelativeUrl() {
        return relativeUrl;
    }

    public void setRelativeUrl(String relativeUrl) {
        this.relativeUrl = relativeUrl;
    }

    public String getAbsoluteUrl() {
        return absoluteUrl;
    }

    public void setAbsoluteUrl(String absoluteUrl) {
        this.absoluteUrl = absoluteUrl;
    }
}
