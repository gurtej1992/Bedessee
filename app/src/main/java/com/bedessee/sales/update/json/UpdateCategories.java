package com.bedessee.sales.update.json;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.bedessee.sales.product.category.Category2;
import com.bedessee.sales.provider.Contract;
import com.bedessee.sales.provider.ProviderUtils;
import com.bedessee.sales.sharedprefs.SharedPrefsManager;
import com.bedessee.sales.update.UpdateActivity;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * AsyncTask used to update all Salesman and Store data.
 */
public class UpdateCategories extends AsyncTask<String, String, Void> {

    private Context mContext;

    final String mSugarSyncDir;

    protected BaseJsonUpdate.OnDownloadJsonCompleteListener mListener;

    public UpdateCategories(Context context) {
        mContext = context;
        final SharedPrefsManager sharedPrefs = new SharedPrefsManager(context);
        mSugarSyncDir = sharedPrefs.getSugarSyncDir();
    }


    public String getFilename() {
        return "categories.json";
    }



    public void setOnJsonDownloadCompleteListener(BaseJsonUpdate.OnDownloadJsonCompleteListener listener) {
        mListener = listener;
    }

    @Override
    protected Void doInBackground(String... params) {


        String dirpath;
        String dirSubpath = "";

        if (params.length > 0) {

            final String customFilepath = params[0];

            if (!TextUtils.isEmpty(customFilepath)) {
                dirSubpath = customFilepath + "/";

            }
        }

        final SharedPrefsManager sharedPrefs = new SharedPrefsManager(mContext);
        final String sugarSyncPath = sharedPrefs.getSugarSyncDir();

        dirpath = sugarSyncPath + "/data/" + dirSubpath;

        final String filepath = dirpath + getFilename();

        final ContentResolver contentResolver = mContext.getContentResolver();

        try {

            final BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(new File(filepath)));

            final JsonReader reader = new JsonReader(new InputStreamReader(bufferedInputStream, "UTF-8"));
            reader.beginArray();
            int numProducts = 0;
            Category2 category = null;
            while (reader.hasNext()) {

                if (numProducts == 0) {
                    contentResolver.delete(Contract.Category.CONTENT_URI, null, null);
                }

                category = new Gson().fromJson(reader, Category2.class);

                final ContentValues contentValues = ProviderUtils.categoryToContentValues(category);
                contentResolver.insert(Contract.Category.CONTENT_URI, contentValues);

                numProducts++;

                final double totalProducts = (double) new SharedPrefsManager(mContext).getCountCategories();
                final String progress;
                if (totalProducts > 0) {
                    progress = String.valueOf(Math.round((numProducts / totalProducts) * 100));
                } else {
                    progress = "";
                }
                publishProgress(progress, "FILE CREATED ON: " + category.getmCreatedOn());
            }
            reader.endArray();
            reader.close();

            if (category != null) {
                publishProgress("100", "FILE CREATED ON: " + category.getmCreatedOn());
            }

        } catch (IOException ignore) { }

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (mListener != null) {
            mListener.onComplete();
        }
    }


    @Override
    protected void onProgressUpdate(String... values) {
        final String s1 = values[0];
        final String s2 = values[1];
        UpdateActivity.setProgress(UpdateActivity.UpdateType.CATEGORY, Integer.parseInt(s1), s2);
    }

}
