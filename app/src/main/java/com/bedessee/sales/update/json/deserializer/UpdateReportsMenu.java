package com.bedessee.sales.update.json.deserializer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.bedessee.sales.provider.Contract;
import com.bedessee.sales.provider.ProviderUtils;
import com.bedessee.sales.reportsmenu.ReportsMenu;
import com.bedessee.sales.update.UpdateActivity;
import com.bedessee.sales.update.json.BaseJsonUpdate;
import com.bedessee.sales.utilities.Utilities;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class UpdateReportsMenu extends BaseJsonUpdate {

    public UpdateReportsMenu(Context context) {
        super(context);
    }

    @Override
    public String getFilename() {
        return "bottom_left_reports_menu.json";
    }

    @Override
    protected UpdateActivity.UpdateType getUpdateType() {
        return UpdateActivity.UpdateType.REPORTS_MENU;
    }


    @Override
    protected void onPostExecute(Void v) {
        if(result != null) {

            final ContentResolver contentResolver = mContext.getContentResolver();

            try {
                final Gson gson = new GsonBuilder().serializeNulls().create();

                final JSONArray jArray = new JSONArray(result);

                final int length = jArray.length();

                if (length > 0) {
                    mContext.getContentResolver().delete(Contract.ReportsMenu.CONTENT_URI, null, null);
                }

                final ContentValues[] contentValues = new ContentValues[length];

                for (int i = 0; i < length; i++) {

                    final JSONObject jsonObject = jArray.getJSONObject(i);

                    final ReportsMenu reportsMenu = gson.fromJson(jsonObject.toString(), ReportsMenu.class);

                    contentValues[i] = ProviderUtils.reportsMenuToContentValues(reportsMenu);
                }

                contentResolver.delete(Contract.ReportsMenu.CONTENT_URI, null, null);
                contentResolver.bulkInsert(Contract.ReportsMenu.CONTENT_URI, contentValues);

                mListener.onComplete();

            } catch (JSONException e) {
                Utilities.longToast(mContext, "REPORTS MENU JSON FILE ERROR");
                ((Activity) mContext).finish();
            }
        }

    }

}
