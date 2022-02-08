package com.bedessee.salesca.product;


import com.google.gson.annotations.SerializedName;

public class CustSpecPrice {

    @SerializedName("CUST_NUM")
    private String mCustNum;

    @SerializedName("PROD#")
    private String mProdNum;

    @SerializedName("PRICE")
    private String mPrice;

    @SerializedName("UNIT PRICE")
    private String mUnitPrice;

    @SerializedName("1")
    private String m1;

    @SerializedName("LEVEL1PRICE")
    private String mLevel1Price;

    @SerializedName("2")
    private String m2;

    @SerializedName("LEVEL2PRICE")
    private String mLevel2Price;

    @SerializedName("3")
    private String m3;

    @SerializedName("LEVEL3PRICE")
    private String mLevel3Price;

    @SerializedName("NOTE01")
    private String mNote01;

    @SerializedName("NOTE02")
    private String mNote02;

    @SerializedName("NOTE03")
    private String mNote03;

    @SerializedName("NOTE04")
    private String mNote04;

    @SerializedName("NOTE05")
    private String mNote05;

    public String getM1() {
        return m1;
    }

    public void setM1(String m1) {
        this.m1 = m1;
    }

    public String getM2() {
        return m2;
    }

    public void setM2(String m2) {
        this.m2 = m2;
    }

    public String getM3() {
        return m3;
    }

    public void setM3(String m3) {
        this.m3 = m3;
    }

    public String getCustNum() {
        return mCustNum;
    }

    public void setCustNum(String custNum) {
        mCustNum = custNum;
    }

    public String getLevel1Price() {
        return mLevel1Price;
    }

    public void setLevel1Price(String level1Price) {
        mLevel1Price = level1Price;
    }

    public String getLevel2Price() {
        return mLevel2Price;
    }

    public void setLevel2Price(String level2Price) {
        mLevel2Price = level2Price;
    }

    public String getLevel3Price() {
        return mLevel3Price;
    }

    public void setLevel3Price(String level3Price) {
        mLevel3Price = level3Price;
    }

    public String getNote01() {
        return mNote01;
    }

    public void setNote01(String note01) {
        mNote01 = note01;
    }

    public String getNote02() {
        return mNote02;
    }

    public void setNote02(String note02) {
        mNote02 = note02;
    }

    public String getNote03() {
        return mNote03;
    }

    public void setNote03(String note03) {
        mNote03 = note03;
    }

    public String getNote04() {
        return mNote04;
    }

    public void setNote04(String note04) {
        mNote04 = note04;
    }

    public String getNote05() {
        return mNote05;
    }

    public void setNote05(String note05) {
        mNote05 = note05;
    }

    public String getPrice() {
        return mPrice;
    }

    public void setPrice(String price) {
        mPrice = price;
    }

    public String getProdNum() {
        return mProdNum;
    }

    public void setProdNum(String prodNum) {
        mProdNum = prodNum;
    }

    public String getUnitPrice() {
        return mUnitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        mUnitPrice = unitPrice;
    }


    //        "CUST_NUM":"9999-99",
//        "PROD#":"A0001",
//        "PRICE":"48.00",
//        "UNIT PRICE":"2.00",
//        "1":"1",
//        "LEVEL1PRICE":"45.00",
//        "2":"2",
//        "LEVEL2PRICE":"44.00",
//        "3":"3",
//        "LEVEL3PRICE":"43.00",
//        "NOTE01":"A1001 - note1",
//        "NOTE02":"A1001 - note2",
//        "NOTE03":"A1001 - note3",
//        "NOTE04":"A1001 - note4",
//        "NOTE05":"A1001 - note5",


}
