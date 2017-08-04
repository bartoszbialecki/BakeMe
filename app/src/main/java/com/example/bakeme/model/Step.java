package com.example.bakeme.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Step {
    // region VARIABLES
    @SerializedName("id")
    @Expose
    private Integer mId;

    @SerializedName("shortDescription")
    @Expose
    private String mShortDescription;

    @SerializedName("description")
    @Expose
    private String mDescription;

    @SerializedName("videoURL")
    @Expose
    private String mVideoURL;

    @SerializedName("thumbnailURL")
    @Expose
    private String mThumbnailURL;
    // endregion

    // region GETTERS AND SETTERS
    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        this.mId = id;
    }

    public String getShortDescription() {
        return mShortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.mShortDescription = shortDescription;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public String getVideoURL() {
        return mVideoURL;
    }

    public void setVideoURL(String videoURL) {
        this.mVideoURL = videoURL;
    }

    public String getThumbnailURL() {
        return mThumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.mThumbnailURL = thumbnailURL;
    }
    // endregion
}
