package com.bedessee.sales.update.json;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.bedessee.sales.main.SideMenu;
import com.bedessee.sales.provider.Contract;
import com.bedessee.sales.provider.ProviderUtils;
import com.bedessee.sales.update.UpdateActivity;
import com.bedessee.sales.utilities.Utilities;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * AsyncTask used to update all Salesman and Store data.x
 */
public class UpdateSideMenu extends BaseJsonUpdate {

    private Context mContext;
    private Gson mGson;

    public UpdateSideMenu(Context context) {
        super(context);
        mContext = context;
        mGson = new Gson();
    }

    @Override
    public String getFilename() {
        return "product_status_side_menu.json";
    }

    @Override
    public UpdateActivity.UpdateType getUpdateType() {
        return UpdateActivity.UpdateType.SIDE_MENU;
    }

    @Override
    protected void onPostExecute(Void v) {
        if(result != null) {

            final ContentResolver contentResolver = mContext.getContentResolver();

            try {
                final JSONArray jArray = new JSONArray(result);

                final int length = jArray.length();

                if (length > 0) {
                    mContext.getContentResolver().delete(Contract.SideMenu.CONTENT_URI, null, null);
                }

                final ContentValues[] contentValues = new ContentValues[length];

                for (int i = 0; i < length; i++) {

                    final JSONObject jsonObject = jArray.getJSONObject(i);

                    final SideMenu sideMenu = mGson.fromJson(jsonObject.toString(), SideMenu.class);

                    contentValues[i] = ProviderUtils.sideMenuToContentValues(sideMenu);
                }

                contentResolver.delete(Contract.SideMenu.CONTENT_URI, null, null);
                contentResolver.bulkInsert(Contract.SideMenu.CONTENT_URI, contentValues);

                mListener.onComplete();

            } catch (JSONException e) {
                Utilities.longToast(mContext, "SIDE MENU JSON FILE ERROR");
                ((Activity) mContext).finish();
            }
        }
    }
}
