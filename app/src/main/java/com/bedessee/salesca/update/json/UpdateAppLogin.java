package com.bedessee.salesca.update.json;

import android.app.Activity;
import android.content.Context;

import com.bedessee.salesca.admin.AppInfo;
import com.bedessee.salesca.sharedprefs.SharedPrefsManager;
import com.bedessee.salesca.update.UpdateActivity;
import com.bedessee.salesca.utilities.Utilities;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * TODO: Document me...
 */
public class UpdateAppLogin extends BaseJsonUpdate {

    private Context mContext;

    public UpdateAppLogin(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public String getFilename() {
        return "app_info.json";
    }


    @Override
    public UpdateActivity.UpdateType getUpdateType() {
        return UpdateActivity.UpdateType.LOGIN;
    }


    @Override
    protected void onPostExecute(Void v) {
        if (result != null) {
            try {

                final JSONObject jsonObject = new JSONArray(result).getJSONObject(0);

                final AppInfo appInfo = new Gson().fromJson(jsonObject.toString(), AppInfo.class);

                SharedPrefsManager prefsManager = new SharedPrefsManager(mContext);

                prefsManager.setOrderEmailRecips(appInfo.getOrderReceivedEmailAddress());
                prefsManager.setLinkToProdImages(appInfo.getProductImagesLink());
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

                mListener.onComplete();

            } catch (JSONException e) {
                Utilities.longToast(mContext, "APP_INFO JSON FILE ERROR");
                ((Activity) mContext).finish();
            }
        }

    }

}