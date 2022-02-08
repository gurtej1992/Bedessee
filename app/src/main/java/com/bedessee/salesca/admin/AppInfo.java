package com.bedessee.salesca.admin;

import com.google.gson.annotations.SerializedName;

/**
 * TODO: Document me...
 */
public class AppInfo {

    @SerializedName("SHORT NAME                        ")
    private String mShortName;

    @SerializedName("APP DESCRIPTION                   ")
    private String mAppDescription;

    @SerializedName("APP NAME                          ")
    private String mAppName;

    @SerializedName("LINK TO PRODUCT IMAGES            ")
    private String mProductImagesLink;

    @SerializedName("LINK TO PRODUCT LARGE IMAGES      ")
    private String mProductLargeImagesLink;

    @SerializedName("LINK TO BRAND LOGO IMAGES         ")
    private String mBrandLogoImagesLink;

    @SerializedName("LINK TO SALES SHEET IMAGES        ")
    private String mSalesSheetImagesLink;

    @SerializedName("LINK TO CUST STATEMENT  TXT FILES ")
    private String mCustStatementTxtFilesLink;

    @SerializedName("LINK TO CUST STMT SMALL TXT FILES ")
    private String mCustStmtSmallTxtFilesLink;

    @SerializedName("LINK TO CUST SALES SATS TXT FILES ")
    private String mCustSalesStatsTxtFilesLink;

    @SerializedName("LINK TO CUSTOMER ACCOUNTS    JSON ")
    private String mCustomerAccountsJsonLink;

    @SerializedName("LINK TO PRODUCT INFO         JSON ")
    private String mProductInfoJsonLink;

    @SerializedName("LINK TO BRANDS INFO          JSON ")
    private String mBrandsInfoJsonLink;

    @SerializedName("ORDER RECEIVED EMAIL ADDRESS      ")
    private String mOrderReceivedEmailAddress;

    @SerializedName("APP CURRENT VERSION               ")
    private String mAppCurrentVersion;

    @SerializedName("VERSION NOTES                     ")
    private String mVersionNotesFile;

    @SerializedName("THIS FILE CREATED                 ")
    private String mThisFileCreated;

    @SerializedName("STATEMENT OF ACCT BODY LINE MSG   ")
    private String mStatementEmailSubject;

    @SerializedName("USE PRODUCT LIKE MATCH LOGIC      ")
    private String mUseProductLikeLogic;

    @SerializedName("FADE PERCENT FOR PRODUCT POP UP   ")
    private String mFadePercentage;

    @SerializedName("IMAGE WORKING FOLDER              ")
    private String mImageWorkingFolder;

    @SerializedName("TOTAL COUNT SLS JSON FILE         ")
    private int mTotalCountSlsJsonFile;

    @SerializedName("TOTAL COUNT CAT JSON FILE         ")
    private int mTotalCountCategoriesJsonFile;

    @SerializedName("TOTAL COUNT PROD JSON FILE        ")
    private int mTotalCountProductsJsonFile;

    @SerializedName("TOTAL COUNT BRAND JSON FILE       ")
    private int mTotalCountBrandsJsonFile;

    @SerializedName("TOTAL COUNT SIDE PANEL JSON FILE  ")
    private int mTotalCountSidePanelJsonFile;

    @SerializedName("PRV                               ")
    private int mAdminCode;

    @SerializedName("APP APK FOLDER AND FILE NAME      ")
    private String mAppApkFilePath;

    @SerializedName("SHOW UOM LINE IN TILE SCREEN      ")
    private String mShowUomInProductsGrid;

    @SerializedName("SHOW PC CS IN TILE SCREEN         ")
    private String mShowTypeInProductsGrid;

    @SerializedName("CLOSE APP AFTER UPDATE")
    private String mCloseAppAfterUpdate;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("FORCE DAILY UPDATE")
    private String mForceDailyUpdate;

    @SerializedName("LAST DAILY UPDATE")
    private String mLastDailyUpdate;

    public String getAppCurrentVersion() {
        return mAppCurrentVersion;
    }

    public void setAppCurrentVersion(String appCurrentVersion) {
        mAppCurrentVersion = appCurrentVersion;
    }

    public String getAppDescription() {
        return mAppDescription;
    }

    public void setAppDescription(String appDescription) {
        mAppDescription = appDescription;
    }

    public String getAppName() {
        return mAppName;
    }

    public void setAppName(String appName) {
        mAppName = appName;
    }

    public String getBrandLogoImagesLink() {
        return mBrandLogoImagesLink;
    }

    public void setBrandLogoImagesLink(String brandLogoImagesLink) {
        mBrandLogoImagesLink = brandLogoImagesLink;
    }

    public String getBrandsInfoJsonLink() {
        return mBrandsInfoJsonLink;
    }

    public void setBrandsInfoJsonLink(String brandsInfoJsonLink) {
        mBrandsInfoJsonLink = brandsInfoJsonLink;
    }

    public String getCustomerAccountsJsonLink() {
        return mCustomerAccountsJsonLink;
    }

    public void setCustomerAccountsJsonLink(String customerAccountsJsonLink) {
        mCustomerAccountsJsonLink = customerAccountsJsonLink;
    }

    public String getCustSalesStatsTxtFilesLink() {
        return mCustSalesStatsTxtFilesLink;
    }

    public void setCustSalesStatsTxtFilesLink(String custSalesStatsTxtFilesLink) {
        mCustSalesStatsTxtFilesLink = custSalesStatsTxtFilesLink;
    }

    public String getCustStatementTxtFilesLink() {
        return mCustStatementTxtFilesLink;
    }

    public void setCustStatementTxtFilesLink(String custStatementTxtFilesLink) {
        mCustStatementTxtFilesLink = custStatementTxtFilesLink;
    }

    public String getCustStmtSmallTxtFilesLink() {
        return mCustStmtSmallTxtFilesLink;
    }

    public void setCustStmtSmallTxtFilesLink(String custStmtSmallTxtFilesLink) {
        mCustStmtSmallTxtFilesLink = custStmtSmallTxtFilesLink;
    }

    public String getOrderReceivedEmailAddress() {
        return mOrderReceivedEmailAddress;
    }

    public void setOrderReceivedEmailAddress(String orderReceivedEmailAddress) {
        mOrderReceivedEmailAddress = orderReceivedEmailAddress;
    }

    public String getProductImagesLink() {
        return mProductImagesLink;
    }

    public void setProductImagesLink(String productImagesLink) {
        mProductImagesLink = productImagesLink;
    }

    public String getProductInfoJsonLink() {
        return mProductInfoJsonLink;
    }

    public void setProductInfoJsonLink(String productInfoJsonLink) {
        mProductInfoJsonLink = productInfoJsonLink;
    }

    public String getSalesSheetImagesLink() {
        return mSalesSheetImagesLink;
    }

    public void setSalesSheetImagesLink(String salesSheetImagesLink) {
        mSalesSheetImagesLink = salesSheetImagesLink;
    }

    public String getShortName() {
        return mShortName;
    }

    public void setShortName(String shortName) {
        mShortName = shortName;
    }

    public String getThisFileCreated() {
        return mThisFileCreated;
    }

    public void setThisFileCreated(String thisFileCreated) {
        mThisFileCreated = thisFileCreated;
    }

    public String getVersionNotesFile() {
        return mVersionNotesFile;
    }

    public void setVersionNotesFile(String versionNotesFile) {
        mVersionNotesFile = versionNotesFile;
    }

    public String getUseProductLikeLogic() {
        return mUseProductLikeLogic;
    }

    public void setUseProductLikeLogic(String useProductLikeLogic) {
        mUseProductLikeLogic = useProductLikeLogic;
    }

    public String getStatementEmailSubject() {
        return mStatementEmailSubject;
    }

    public void setStatementEmailSubject(String statementEmailSubject) {
        mStatementEmailSubject = statementEmailSubject;
    }

    public String getFadePercentage() {
        return mFadePercentage;
    }

    public void setFadePercentage(String fadePercentage) {
        mFadePercentage = fadePercentage;
    }


    public int getAdminPin() {
        return mAdminCode;
    }

    public void setAdminCode(int adminCode) {
        mAdminCode = adminCode;
    }

    public String getAppApkFilePath() {
        return mAppApkFilePath;
    }

    public void setAppApkFilePath(String appApkFilePath) {
        mAppApkFilePath = appApkFilePath;
    }

    public String getImageWorkingFolder() {
        return mImageWorkingFolder;
    }

    public void setImageWorkingFolder(String imageWorkingFolder) {
        mImageWorkingFolder = imageWorkingFolder;
    }

    public int getTotalCountBrandsJsonFile() {
        return mTotalCountBrandsJsonFile;
    }

    public void setTotalCountBrandsJsonFile(int totalCountBrandsJsonFile) {
        mTotalCountBrandsJsonFile = totalCountBrandsJsonFile;
    }

    public int getTotalCountCategoriesJsonFile() {
        return mTotalCountCategoriesJsonFile;
    }

    public void setTotalCountCategoriesJsonFile(int totalCountCategoriesJsonFile) {
        mTotalCountCategoriesJsonFile = totalCountCategoriesJsonFile;
    }

    public int getTotalCountProductsJsonFile() {
        return mTotalCountProductsJsonFile;
    }

    public void setTotalCountProductsJsonFile(int totalCountProductsJsonFile) {
        mTotalCountProductsJsonFile = totalCountProductsJsonFile;
    }

    public int getTotalCountSidePanelJsonFile() {
        return mTotalCountSidePanelJsonFile;
    }

    public void setTotalCountSidePanelJsonFile(int totalCountSidePanelJsonFile) {
        mTotalCountSidePanelJsonFile = totalCountSidePanelJsonFile;
    }

    public int getTotalCountSlsJsonFile() {
        return mTotalCountSlsJsonFile;
    }

    public void setTotalCountSlsJsonFile(int totalCountSlsJsonFile) {
        mTotalCountSlsJsonFile = totalCountSlsJsonFile;
    }

    public String getShowUomInProductsGrid() {
        return mShowUomInProductsGrid;
    }

    public void setmShowUomInProductsGrid(String mShowUomInProductsGrid) {
        this.mShowUomInProductsGrid = mShowUomInProductsGrid;
    }

    public String getShowTypeInProductsGrid() {
        return mShowTypeInProductsGrid;
    }

    public void setmShowTypeInProductsGrid(String mShowTypeInProductsGrid) {
        this.mShowTypeInProductsGrid = mShowTypeInProductsGrid;
    }

    public String getmShortName() {
        return mShortName;
    }

    public void setmShortName(String mShortName) {
        this.mShortName = mShortName;
    }

    public String getmAppDescription() {
        return mAppDescription;
    }

    public void setmAppDescription(String mAppDescription) {
        this.mAppDescription = mAppDescription;
    }

    public String getmAppName() {
        return mAppName;
    }

    public void setmAppName(String mAppName) {
        this.mAppName = mAppName;
    }

    public String getmProductImagesLink() {
        return mProductImagesLink;
    }

    public void setmProductImagesLink(String mProductImagesLink) {
        this.mProductImagesLink = mProductImagesLink;
    }

    public String getmBrandLogoImagesLink() {
        return mBrandLogoImagesLink;
    }

    public void setmBrandLogoImagesLink(String mBrandLogoImagesLink) {
        this.mBrandLogoImagesLink = mBrandLogoImagesLink;
    }

    public String getmSalesSheetImagesLink() {
        return mSalesSheetImagesLink;
    }

    public void setmSalesSheetImagesLink(String mSalesSheetImagesLink) {
        this.mSalesSheetImagesLink = mSalesSheetImagesLink;
    }

    public String getmCustStatementTxtFilesLink() {
        return mCustStatementTxtFilesLink;
    }

    public void setmCustStatementTxtFilesLink(String mCustStatementTxtFilesLink) {
        this.mCustStatementTxtFilesLink = mCustStatementTxtFilesLink;
    }

    public String getmCustStmtSmallTxtFilesLink() {
        return mCustStmtSmallTxtFilesLink;
    }

    public void setmCustStmtSmallTxtFilesLink(String mCustStmtSmallTxtFilesLink) {
        this.mCustStmtSmallTxtFilesLink = mCustStmtSmallTxtFilesLink;
    }

    public String getmCustSalesStatsTxtFilesLink() {
        return mCustSalesStatsTxtFilesLink;
    }

    public void setmCustSalesStatsTxtFilesLink(String mCustSalesStatsTxtFilesLink) {
        this.mCustSalesStatsTxtFilesLink = mCustSalesStatsTxtFilesLink;
    }

    public String getmCustomerAccountsJsonLink() {
        return mCustomerAccountsJsonLink;
    }

    public void setmCustomerAccountsJsonLink(String mCustomerAccountsJsonLink) {
        this.mCustomerAccountsJsonLink = mCustomerAccountsJsonLink;
    }

    public String getmProductInfoJsonLink() {
        return mProductInfoJsonLink;
    }

    public void setmProductInfoJsonLink(String mProductInfoJsonLink) {
        this.mProductInfoJsonLink = mProductInfoJsonLink;
    }

    public String getmBrandsInfoJsonLink() {
        return mBrandsInfoJsonLink;
    }

    public void setmBrandsInfoJsonLink(String mBrandsInfoJsonLink) {
        this.mBrandsInfoJsonLink = mBrandsInfoJsonLink;
    }

    public String getmOrderReceivedEmailAddress() {
        return mOrderReceivedEmailAddress;
    }

    public void setmOrderReceivedEmailAddress(String mOrderReceivedEmailAddress) {
        this.mOrderReceivedEmailAddress = mOrderReceivedEmailAddress;
    }

    public String getmAppCurrentVersion() {
        return mAppCurrentVersion;
    }

    public void setmAppCurrentVersion(String mAppCurrentVersion) {
        this.mAppCurrentVersion = mAppCurrentVersion;
    }

    public String getmVersionNotesFile() {
        return mVersionNotesFile;
    }

    public void setmVersionNotesFile(String mVersionNotesFile) {
        this.mVersionNotesFile = mVersionNotesFile;
    }

    public String getmThisFileCreated() {
        return mThisFileCreated;
    }

    public void setmThisFileCreated(String mThisFileCreated) {
        this.mThisFileCreated = mThisFileCreated;
    }

    public String getmStatementEmailSubject() {
        return mStatementEmailSubject;
    }

    public void setmStatementEmailSubject(String mStatementEmailSubject) {
        this.mStatementEmailSubject = mStatementEmailSubject;
    }

    public String getmUseProductLikeLogic() {
        return mUseProductLikeLogic;
    }

    public void setmUseProductLikeLogic(String mUseProductLikeLogic) {
        this.mUseProductLikeLogic = mUseProductLikeLogic;
    }

    public String getmFadePercentage() {
        return mFadePercentage;
    }

    public void setmFadePercentage(String mFadePercentage) {
        this.mFadePercentage = mFadePercentage;
    }

    public String getmImageWorkingFolder() {
        return mImageWorkingFolder;
    }

    public void setmImageWorkingFolder(String mImageWorkingFolder) {
        this.mImageWorkingFolder = mImageWorkingFolder;
    }

    public int getmTotalCountSlsJsonFile() {
        return mTotalCountSlsJsonFile;
    }

    public void setmTotalCountSlsJsonFile(int mTotalCountSlsJsonFile) {
        this.mTotalCountSlsJsonFile = mTotalCountSlsJsonFile;
    }

    public int getmTotalCountCategoriesJsonFile() {
        return mTotalCountCategoriesJsonFile;
    }

    public void setmTotalCountCategoriesJsonFile(int mTotalCountCategoriesJsonFile) {
        this.mTotalCountCategoriesJsonFile = mTotalCountCategoriesJsonFile;
    }

    public int getmTotalCountProductsJsonFile() {
        return mTotalCountProductsJsonFile;
    }

    public void setmTotalCountProductsJsonFile(int mTotalCountProductsJsonFile) {
        this.mTotalCountProductsJsonFile = mTotalCountProductsJsonFile;
    }

    public int getmTotalCountBrandsJsonFile() {
        return mTotalCountBrandsJsonFile;
    }

    public void setmTotalCountBrandsJsonFile(int mTotalCountBrandsJsonFile) {
        this.mTotalCountBrandsJsonFile = mTotalCountBrandsJsonFile;
    }

    public int getmTotalCountSidePanelJsonFile() {
        return mTotalCountSidePanelJsonFile;
    }

    public void setmTotalCountSidePanelJsonFile(int mTotalCountSidePanelJsonFile) {
        this.mTotalCountSidePanelJsonFile = mTotalCountSidePanelJsonFile;
    }

    public int getmAdminCode() {
        return mAdminCode;
    }

    public void setmAdminCode(int mAdminCode) {
        this.mAdminCode = mAdminCode;
    }

    public String getmAppApkFilePath() {
        return mAppApkFilePath;
    }

    public void setmAppApkFilePath(String mAppApkFilePath) {
        this.mAppApkFilePath = mAppApkFilePath;
    }

    public String getmShowUomInProductsGrid() {
        return mShowUomInProductsGrid;
    }

    public String getmShowTypeInProductsGrid() {
        return mShowTypeInProductsGrid;
    }

    public String getCloseAppAfterUpdate() {
        return mCloseAppAfterUpdate;
    }

    public void setCloseAppAfterUpdate(String mCloseAppAfterUpdate) {
        this.mCloseAppAfterUpdate = mCloseAppAfterUpdate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProductLargeImagesLink() {
        return mProductLargeImagesLink;
    }

    public void setProductLargeImagesLink(String mProductLargeImagesLink) {
        this.mProductLargeImagesLink = mProductLargeImagesLink;
    }

    public String getForceDailyUpdate() {
        //this is only for old versions where value is null
        if (mForceDailyUpdate == null) return "NO";
        return mForceDailyUpdate;
    }

    public String getLastDailyUpdate() {
        return this.mLastDailyUpdate;
    }


}
