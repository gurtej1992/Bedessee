package com.bedessee.salesca.update.json;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.bedessee.salesca.sharedprefs.SharedPrefsManager;
import com.bedessee.salesca.update.UpdateActivity;
import com.bedessee.salesca.utilities.Utilities;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * TODO: Document me...
 */
public abstract class BaseJsonUpdate extends AsyncTask<String, String, Void> {

    protected Context mContext;
    protected String result;
    protected OnDownloadJsonCompleteListener mListener;
    protected Gson mGson;


    protected BaseJsonUpdate(Context context) {
        mContext = context;
        mGson = new Gson();
    }

    public void setOnJsonDownloadCompleteListener(OnDownloadJsonCompleteListener listener) {
        mListener = listener;
    }

    public abstract String getFilename();

    @Override
    protected Void doInBackground(String... params) {

        try {

            String dirPath;
            String dirSubPath = "";

            if (params.length > 0) {

                final String customFilepath = params[0];

                if (!TextUtils.isEmpty(customFilepath)) {
                    dirSubPath = customFilepath + "/";
                }
            }

            final SharedPrefsManager sharedPrefs = new SharedPrefsManager(mContext);
            final String sugarSyncPath = sharedPrefs.getSugarSyncDir();

            dirPath = sugarSyncPath + "/data/" + dirSubPath;

            final String filepath = dirPath + getFilename();

            final File file = new File(filepath);

            final FileInputStream ois = new FileInputStream(file);
            final BufferedInputStream is = new BufferedInputStream(ois);

            int size = is.available();
            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();
            ois.close();

            result = new String(buffer, StandardCharsets.UTF_8);

            publishProgress("100", "");

        } catch (FileNotFoundException e) {
            if(mContext instanceof Activity) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    public void run() {
                        Utilities.longToast(mContext, getFilename() + " FILE NOT FOUND");
                        Utilities.longToast(mContext, "HAVE YOU SET UP A DATA FOLDER?");

                    }
                });
                ((Activity) mContext).finish();
            }
        } catch (UnsupportedEncodingException e) {

            ((Activity)mContext).runOnUiThread(new Runnable() {
                public void run() {
                    Utilities.longToast(mContext, "UNSUPPORTED ENCODING EXCEPTION ---CONTACT YUSUF");
                }
            });
            ((Activity)mContext).finish();
        } catch (IOException e) {

            ((Activity)mContext).runOnUiThread(new Runnable() {
                public void run() {
                    Utilities.longToast(mContext, "ERROR READING FILE " + getFilename() + " ---CONTACT HENRY OR YUSUF");
                }
            });
            ((Activity)mContext).finish();
        }
        return null;
    }


    @Override
    protected void onProgressUpdate(String... values) {
        UpdateActivity.setProgress(getUpdateType(), 100, "");
    }

    protected abstract UpdateActivity.UpdateType getUpdateType();

    public interface OnDownloadJsonCompleteListener {
        void onComplete();
    }
}