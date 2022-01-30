package com.bedessee.sales.main;

import com.google.gson.annotations.SerializedName;

/**
 * Object model for the side menu to parse from json file
 */
public class SideMenu {
    @SerializedName("DEFAULT OPEN AFTER DAILY UPDATE")
    private String mOpenByDefault;

    @SerializedName("STATUS CODE")
    private String mStatusCode;

    @SerializedName("SIDE MENU DISPLAY")
    private String mMenuTitle;

    @SerializedName("MENU SORT")
    private String mSort;

    @SerializedName("COLOUR")
    private String mColour;

    @SerializedName("COUNT")
    private String mCount;


    public SideMenu(String openByDefault, String colour, String count, String menuTitle, String sort, String statusCode) {
        mOpenByDefault = openByDefault;
        mColour = colour;
        mCount = count;
        mMenuTitle = menuTitle;
        mSort = sort;
        mStatusCode = statusCode;
    }

    public String getColour() {
        return mColour;
    }

    public void setColour(String colour) {
        mColour = colour;
    }

    public String getCount() {
        return mCount;
    }

    public void setCount(String count) {
        mCount = count;
    }

    public String getMenuTitle() {
        return mMenuTitle;
    }

    public void setMenuTitle(String menuTitle) {
        mMenuTitle = menuTitle;
    }

    public String getSort() {
        return mSort;
    }

    public void setSort(String sort) {
        mSort = sort;
    }

    public String getStatusCode() {
        return mStatusCode;
    }

    public void setStatusCode(String statusCode) {
        mStatusCode = statusCode;
    }


    public String getOpenByDefault() {
        return mOpenByDefault;
    }

    public void setOpenByDefault(String openByDefault) {
        mOpenByDefault = openByDefault;
    }
}