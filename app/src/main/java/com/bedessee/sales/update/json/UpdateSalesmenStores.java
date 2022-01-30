package com.bedessee.sales.update.json;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.bedessee.sales.provider.Contract;
import com.bedessee.sales.provider.ProviderUtils;
import com.bedessee.sales.salesmanstore.SalesmanStore;
import com.bedessee.sales.sharedprefs.SharedPrefsManager;
import com.bedessee.sales.update.UpdateActivity;
import com.bedessee.sales.update.json.deserializer.SalesmanStoreDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * AsyncTask used to update all Salesman and Store data.
 */
public class UpdateSalesmenStores extends AsyncTask<String, String, Void> {

    final Context mContext;
    final String mSugarSyncDir;

    protected BaseJsonUpdate.OnDownloadJsonCompleteListener mListener;


    public UpdateSalesmenStores(Context context) {
        mContext = context;
        final SharedPrefsManager sharedPrefs = new SharedPrefsManager(context);
        mSugarSyncDir = sharedPrefs.getSugarSyncDir();
    }


    public String getFilename() {
        return "sls_person_list_of_accounts.json";
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
            while (reader.hasNext()) {

                if (numProducts == 0) {
                    contentResolver.delete(Contract.SalesmanStore.CONTENT_URI, null, null);
                }

                final GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(SalesmanStore.class, new SalesmanStoreDeserializer());
                final Gson gson = gsonBuilder.create();
                final SalesmanStore salesmanStore = gson.fromJson(reader, SalesmanStore.class);
                final ContentValues contentValues = ProviderUtils.salesmanStoreToContentValues(salesmanStore.getSalesman(), salesmanStore.getStore());
                contentResolver.insert(Contract.SalesmanStore.CONTENT_URI, contentValues);

                numProducts++;

                final double totalProducts = new SharedPrefsManager(mContext).getCountSls();

                final String progress;
                if (totalProducts > 0) {
                    progress = String.valueOf(Math.round((numProducts / totalProducts) * 100));
                } else {
                    progress = "";
                }
                publishProgress(progress, "FILE CREATED ON: " + salesmanStore.getStore().getFileCreatedOn());
            }
            reader.endArray();
            reader.close();

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
//        final String [] ss = values[0].split("\n");
        final String s1 = values[0];
        final String s2 = values[1];

        UpdateActivity.setProgress(UpdateActivity.UpdateType.SALESMEN_STORES, Integer.parseInt(s1), s2);

    }


}
