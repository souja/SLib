package com.souja.lib.models;

public class OImageBase extends BaseModel {
    /**
     * id : 1
     * bbsId : 1
     * pictureUrl : http://10.13.80.10:8082/doctor/openImage/getBbsImage/1
     * sort : 1
     */

    private int id;
    private String pictureUrl;
    private String imageUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}