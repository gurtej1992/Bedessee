package com.bedessee.sales.update.json;

import android.app.Activity;
import android.content.Context;

import com.bedessee.sales.admin.AppInfo;
import com.bedessee.sales.sharedprefs.SharedPrefsManager;
import com.bedessee.sales.update.UpdateActivity;
import com.bedessee.sales.utilities.Utilities;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * TODO: Document me...
 */
public class UpdateAppInfo extends BaseJsonUpdate {

    private Context mContext;

    public UpdateAppInfo(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public String getFilename() {
        return "app_info.json";
    }


    @Override
    public UpdateActivity.UpdateType getUpdateType() {
        return UpdateActivity.UpdateType.APP_INFO;
    }

//    @Override
//    protected Void doInBackground(String... params) {
//        publishProgress("100", "");
//        return null;
//    }

    @Override
    protected void onPostExecute(Void v) {
        if (result != null) {
            try {

                final JSONObject jsonObject = new JSONArray(result).getJSONObject(0);

                final AppInfo appInfo = new Gson().fromJson(jsonObject.toString(), AppInfo.class);

                final SharedPrefsManager prefsManager = new SharedPrefsManager(mContext);

                prefsManager.setOrderEmailRecips(appInfo.getOrderReceivedEmailAddress());
                prefsManager.setLinkToProdImages(appInfo.getProductImagesLink());
                prefsManager.setLinkToLargeProdImages(appInfo.getProductLargeImagesLink());
                prefsManager.setLinkToBrandLogoImages(appInfo.getBrandLogoImagesLink());
                prefsManager.setLinkToSellSheetImages(appInfo.getSalesSheetImagesLink());
                prefsManager.setLinkToCustomerStatements(appInfo.getCustStatementTxtFilesLink());
                prefsManager.setLinkToCustomerStatementsSmall(appInfo.getCustStmtSmallTxtFilesLink());
                prefsManager.setLinkToCustomerSalesStats(appInfo.getCustSalesStatsTxtFilesLink());
                prefsManager.setLinkToCustomerAccountsJson(appInfo.getCustomerAccountsJsonLink());
                prefsManager.setLinkToProductsJson(appInfo.getProductInfoJsonLink());
                prefsManager.setLinkToBrandsJson(appInfo.getBrandsInfoJsonLink());
                prefsManager.setStatementEmailSubject(appInfo.getStatementEmailSubject());
                prefsManager.setUseNewLikeLogic(appInfo.getUseProductLikeLogic());
                prefsManager.setFadePercentage(appInfo.getFadePercentage());
                prefsManager.setCountBrands(appInfo.getTotalCountBrandsJsonFile());
                prefsManager.setCountCategories(appInfo.getTotalCountCategoriesJsonFile());
                prefsManager.setCountProducts(appInfo.getTotalCountProductsJsonFile());
                prefsManager.setCountSidePanel(appInfo.getTotalCountSidePanelJsonFile());
                prefsManager.setCountSls(appInfo.getTotalCountSlsJsonFile());
                prefsManager.setApkPath(appInfo.getAppApkFilePath());
                prefsManager.setAdminPin(appInfo.getAdminPin());
                prefsManager.setShowTypeInProductGrid(appInfo.getShowTypeInProductsGrid().equals("YES"));
                prefsManager.setShowUomInProductGrid(appInfo.getShowUomInProductsGrid().equals("YES"));
                prefsManager.setCloseAfterUpdate(appInfo.getCloseAppAfterUpdate() != null && appInfo.getCloseAppAfterUpdate().equals("YES"));
                prefsManager.setForceDailyUpdate(appInfo.getForceDailyUpdate().equals("YES"));
                prefsManager.setLastDailyUpdate(appInfo.getLastDailyUpdate());
                mListener.onComplete();

            } catch (JSONException e) {
                Utilities.longToast(mContext, "APP_INFO JSON FILE ERROR");
                ((Activity) mContext).finish();
            }
        }
    }
}