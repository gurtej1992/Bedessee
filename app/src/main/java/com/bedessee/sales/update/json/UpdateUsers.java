package com.bedessee.sales.update.json;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.bedessee.sales.provider.Contract;
import com.bedessee.sales.provider.ProviderUtils;
import com.bedessee.sales.salesman.Salesman;
import com.bedessee.sales.salesman.SalesmanManager;
import com.bedessee.sales.sharedprefs.SharedPrefsManager;
import com.bedessee.sales.update.UpdateActivity;
import com.bedessee.sales.utilities.ProtectedLog;
import com.bedessee.sales.utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class UpdateUsers extends BaseJsonUpdate {

    OnUpdateUsersCompleteListener mListener;

    private Context mContext;

    public UpdateUsers(Context context) {
        super(context);
        mContext = context;
    }

    public void setOnUpdateUsersCompleteListener(OnUpdateUsersCompleteListener listener) {
        mListener = listener;
    }

    @Override
    public String getFilename() {
        return "app_login.json";
    }

    @Override
    public UpdateActivity.UpdateType getUpdateType() {
        return UpdateActivity.UpdateType.USERS;
    }


    protected void onPostExecute(Void v) {

        if (result == null) {
            Utilities.longToast(mContext, "There was an error locating the list of valid users. Have you set up a data folder?");
            final SharedPrefsManager sharedPrefsManager = new SharedPrefsManager(mContext);
            sharedPrefsManager.setSugarSyncDir(null);
            if(mContext instanceof Activity) {
                ((Activity)mContext).finish();
            }
        } else {

            try {
                JSONArray jArray = new JSONArray(result);

                if (jArray.length() > 0) {
                    mContext.getContentResolver().delete(Contract.User.CONTENT_URI, null, null);
                }

                for (int i = 0; i < jArray.length(); i++) {

                    final JSONObject jsonObject = jArray.getJSONObject(i);

                    final Salesman salesman = new Salesman(jsonObject.getString("name"), jsonObject.getString("email"));
                    salesman.setIsAdmin(jsonObject.getString("admin").equals("YES"));
                    final String prefix = jsonObject.getString("APP ORDER SUBJECT LINE");
                    salesman.setEmailPrefix(TextUtils.isEmpty(prefix) ? "" : prefix);

                    Log.d("Prefix", " " + salesman.getEmailPrefix());
                    Log.d("Salesman", " " + salesman.toString());

                    final Salesman currSalesman = new SharedPrefsManager(mContext).getCurrentSalesman();

                    if (currSalesman != null && currSalesman.getEmail().equals(salesman.getEmail())) {
                        new SharedPrefsManager(mContext).setCurrentEmailPrefix(salesman);
                        String currentEmailPrefix =  new SharedPrefsManager(mContext).getCurrentEmailPrefix();
                        Log.d("CurrentEmailPrefix", " " + currentEmailPrefix);

                        SalesmanManager.setCurrentSalesman(mContext, salesman);
                    }

                    final ContentValues contentValue = ProviderUtils.UserToContentValues(salesman);

                    final Cursor existsCursor = mContext.getContentResolver().query(Contract.User.CONTENT_URI, null, Contract.UserColumns.COLUMN_EMAIL + " = ?", new String[]{salesman.getEmail()}, null);
                    if (existsCursor != null) {
                        if (existsCursor.moveToFirst()) {
                            mContext.getContentResolver().update(Contract.User.CONTENT_URI, contentValue, Contract.UserColumns.COLUMN_EMAIL + " = ?", new String[]{salesman.getEmail()});
                        } else {
                            mContext.getContentResolver().insert(Contract.User.CONTENT_URI, contentValue);
                        }
                        existsCursor.close();
                    }
                }


                if (mListener != null) {
                    mListener.onComplete();
                }

            } catch(JSONException e) {
                ProtectedLog.e("JSONException", "Error: " + e.toString(), e);
                if (mListener != null) {
                    mListener.onError();
                }
                ((Activity) mContext).finish();
            }
        }

    }

    public interface OnUpdateUsersCompleteListener {
        void onComplete();
        void onError();
    }


}



