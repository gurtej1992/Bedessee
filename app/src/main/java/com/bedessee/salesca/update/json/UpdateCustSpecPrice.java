package com.bedessee.salesca.update.json;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.bedessee.salesca.product.CustSpecPrice;
import com.bedessee.salesca.provider.Contract;
import com.bedessee.salesca.provider.ProviderUtils;
import com.bedessee.salesca.update.UpdateActivity;
import com.bedessee.salesca.utilities.Utilities;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * AsyncTask used to update all Salesman and Store data.x
 */
public class UpdateCustSpecPrice extends BaseJsonUpdate {

    private Context mContext;

    public UpdateCustSpecPrice(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public String getFilename() {
        return "cust_spec_price_list.json";
    }


    @Override
    public UpdateActivity.UpdateType getUpdateType() {
        return UpdateActivity.UpdateType.CUST_SPEC_PRICE_LIST;
    }


//    @Override
//    protected Void doInBackground(String... params) {
//        publishProgress("100", "");
//        return super.doInBackground(params);
//    }


    @Override
    protected void onPostExecute(Void v) {
        if(result != null) {


            final ContentResolver contentResolver = mContext.getContentResolver();

            try {

                mContext.getContentResolver().delete(Contract.CustSpecPrice.CONTENT_URI, null, null);

                final JSONArray jArray = new JSONArray(result);

                final int length = jArray.length();

                if (length > 0) {
                    contentResolver.delete(Contract.CustSpecPrice.CONTENT_URI, null, null);
                }

                final ContentValues[] contentValues = new ContentValues[length];

                for (int i = 0; i < length; i++) {

                    final JSONObject jsonObject = jArray.getJSONObject(i);

                    final CustSpecPrice custSpecPrice = new Gson().fromJson(jsonObject.toString(), CustSpecPrice.class);

                    contentValues[i] = ProviderUtils.custSpecPriceToContentValues(custSpecPrice);
                }

                contentResolver.bulkInsert(Contract.CustSpecPrice.CONTENT_URI, contentValues);

                mListener.onComplete();

            } catch (JSONException e) {
                Utilities.longToast(mContext, "CUST SPEC PRICE LIST JSON FILE ERROR");
                ((Activity) mContext).finish();
            }
        }

    }

}
