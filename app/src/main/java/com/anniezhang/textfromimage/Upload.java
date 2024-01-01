package com.anniezhang.textfromimage;

public class Upload {
    private String imageUrl;
    private String imageName;
    private String confidence;
    private String date;
    public Upload() {

    }
    public Upload(String imageName, String confidence, String date, String imageUrl) {
        this.imageName = imageName;
        this.imageUrl = imageUrl;
        this.confidence = confidence;
        this.date = date;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public String getImageName() {
        return imageName;
    }
    public String getConfidence() {
        return confidence;
    }
    public String getDate() {
        return date;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }
    public void setDate(String date) {
        this.date = date;
    }
}