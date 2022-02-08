package com.bedessee.salesca.product.category;

import com.google.gson.annotations.SerializedName;
/**
 * TODO: Document me...
 */
public class Category2 {

    @SerializedName("ACTIVE")
    private String mIsActive;

    @SerializedName("PROD FIRST LETTER")
    private String mChar;

    @SerializedName("DESCRIPTION")
    private String mDescription;

    @SerializedName("FILE CREATED ON")
    private String mCreatedOn;

    public Category2(String isActive, String aChar, String description) {
        mIsActive = isActive;
        mChar = aChar;
        mDescription = description;
    }

    public String isActive() {
        return mIsActive;
    }

    public void setActive(String isActive) {
        mIsActive = isActive;
    }

    public String getChar() {
        return mChar;
    }

    public void setChar(String aChar) {
        mChar = aChar;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getmCreatedOn() {
        return mCreatedOn;
    }

    public void setmCreatedOn(String mCreatedOn) {
        this.mCreatedOn = mCreatedOn;
    }
}