package com.bedessee.sales.store;

import android.text.TextUtils;

import java.io.Serializable;

public class Store implements Serializable {

    private String mName;
    private String mNumber;
    private String mAddress;
    private String mLastCollectDaysOld;
    private String mLastCollectDate;
    private String mLastCollectInvoice;
    private String mLastCollectAmount;
    private String mOutstandingBalanceDue;
    private String mStatementUrl;
    private boolean mShowPopup;
    private String mFileCreatedOn;
    private boolean mIsNewStoreAdded = false;
    private StoreCustomAttributes mCustomAttributes;
    private String mOpenAccountStatus = "N";
    private String mOpenDefaultReport = "N";

    public String getFileCreatedOn() {
        return mFileCreatedOn;
    }

    public void setFileCreatedOn(String mFileCreatedOn) {
        this.mFileCreatedOn = mFileCreatedOn;
    }

    public String getName() {
        return mName;
    }

    public String getBaseNumber() {
        return mNumber.split("-")[0];
    }

    public void setName(String name) {
        mName = name;
    }

    public String getNumber() {
        return mNumber;
    }

    public void setNumber(String number) {
        mNumber = number;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getLastCollectDaysOld() {
        return mLastCollectDaysOld;
    }

    public void setLastCollectDaysOld(String lastCollectDaysOld) {
        mLastCollectDaysOld = lastCollectDaysOld;
    }

    public String getLastCollectDate() {
        return mLastCollectDate;
    }

    public void setLastCollectDate(String lastCollectDate) {
        mLastCollectDate = lastCollectDate;
    }

    public String getLastCollectInvoice() {
        return mLastCollectInvoice;
    }

    public void setLastCollectInvoice(String lastCollectInvoice) {
        mLastCollectInvoice = lastCollectInvoice;
    }

    public String getLastCollectAmount() {
        return mLastCollectAmount;
    }

    public void setLastCollectAmount(String lastCollectAmount) {
        mLastCollectAmount = lastCollectAmount;
    }

    public String getOutstandingBalanceDue() {
        return TextUtils.isEmpty(mOutstandingBalanceDue) ? "0" : mOutstandingBalanceDue;
    }

    public void setOutstandingBalanceDue(String outstandingBalanceDue) {
        mOutstandingBalanceDue = outstandingBalanceDue;
    }

    public String getStatementUrl() {
        return mStatementUrl;
    }

    public void setStatementUrl(String statementUrl) {
        mStatementUrl = statementUrl;
    }

    public boolean isShowPopup() {
        return mShowPopup;
    }

    public void setShowPopup(boolean showPopup) {
        mShowPopup = showPopup;
    }

    public void setOpenAccountStatusPopUp(String value) {
        mOpenAccountStatus = value;
    }

    public void setOpenDefaultReport(String value) {
        mOpenDefaultReport = value;
    }

    public String getOpenAccountStatusPopUp() {
        return mOpenAccountStatus;
    }

    public String getOpenDefaultReport() {
        return mOpenDefaultReport;
    }

    public boolean isOpenAccountStatusPopUp(){
        return mOpenAccountStatus.equals("Y")
                || mOpenAccountStatus.equals("YES");
    }
    public boolean isOpenDefaultReport(){
        return mOpenDefaultReport.equals("Y")
                || mOpenDefaultReport.equals("YES");
    }

    @Override
    public String toString() {
        return "Store{" +
                "mName='" + mName + '\'' +
                ", mNumber='" + mNumber + '\'' +
                ", mAddress='" + mAddress + '\'' +
                ", mLastCollectDaysOld='" + mLastCollectDaysOld + '\'' +
                ", mLastCollectDate='" + mLastCollectDate + '\'' +
                ", mLastCollectInvoice='" + mLastCollectInvoice + '\'' +
                ", mLastCollectAmount='" + mLastCollectAmount + '\'' +
                ", mOutstandingBalanceDue='" + mOutstandingBalanceDue + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Store)) return false;

        Store store = (Store) o;

        return !(mNumber != null ? !mNumber.equals(store.mNumber) : store.mNumber != null);

    }

    @Override
    public int hashCode() {
        return mNumber != null ? mNumber.hashCode() : 0;
    }

    public boolean isNewStoreAdded() {
        return mIsNewStoreAdded;
    }

    public void setIsNewStoreAdded(boolean isNewStoreAdded) {
        this.mIsNewStoreAdded = isNewStoreAdded;
    }

    public StoreCustomAttributes getCustomAttributes() {
        return mCustomAttributes;
    }

    public void setCustomAttributes(StoreCustomAttributes customAttributes) {
        this.mCustomAttributes = customAttributes;
    }
}
