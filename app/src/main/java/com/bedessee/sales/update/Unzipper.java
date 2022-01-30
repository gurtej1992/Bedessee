package com.bedessee.sales.update;//package com.bedessee.sales20.update;
//
//import android.content.Context;
//import android.os.AsyncTask;
//
//import com.bedessee.sales20.utilities.ProtectedLog;
//import com.bedessee.sales20.utilities.Utilities;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipFile;
//import java.util.zip.ZipInputStream;
//
///**
// * Utility class containing only an AsyncTask to unzip files.
// */
//public class Unzipper extends AsyncTask<Void, Integer, Integer> {
//
//    private String mPathZipFile;
//    private String mPathDestination;
//    private int mPercentage = 0;
//    private ZipEntry mZipEntry = null;
//
//    private OnUnzipCompleteListener mOnUnzipCompleteListener;
//
//    public Unzipper(final Context context, final String pathDestination, final String pathZipFile) {
//        mPathZipFile = pathZipFile;
//        mPathDestination = pathDestination;
//        confirmDirectory(null);
//    }
//
//    public void setOnUnzipCompleteListener(OnUnzipCompleteListener onUnzipCompleteListener) {
//        mOnUnzipCompleteListener = onUnzipCompleteListener;
//    }
//
//    @Override
//    protected Integer doInBackground(Void... params) {
//        try {
//            ZipFile zipFile = new ZipFile(mPathZipFile);
//            UpdateActivity.setProgressBarMax(zipFile.size());
//            FileInputStream fileInputStream = new FileInputStream(mPathZipFile);
//            ZipInputStream zipInputStream = new ZipInputStream(fileInputStream);
//
//            while ((mZipEntry = zipInputStream.getNextEntry()) != null) {
//
//                ProtectedLog.v("Decompress", "Unzipping " + mZipEntry.getName());
//
//                if (mZipEntry.isDirectory()) {
//                    confirmDirectory(mZipEntry.getName());
//                } else {
//                    mPercentage++;
//                    publishProgress(mPercentage);
//
//                    FileOutputStream fileOutputStream = new FileOutputStream(mPathDestination + mZipEntry.getName());
//
//                    for (int i = zipInputStream.read(); i != -1; i = zipInputStream.read()) {
//                        fileOutputStream.write(i);
//                    }
//                    zipInputStream.closeEntry();
//                    fileOutputStream.close();
//                }
//            }
//            zipInputStream.close();
//        } catch (Exception e) {
//            ProtectedLog.e("Decompress", "unzip", e);
//        }
//
//        return mPathZipFile.length();
//    }
//
//    protected void onProgressUpdate(Integer... progress) {
//        if(mZipEntry != null) {
//            UpdateActivity.setProgress("extracting " + mZipEntry.getName(), mPercentage);
//        }
//    }
//
//    @Override
//    protected void onPostExecute(Integer integer) {
//        super.onPostExecute(integer);
//
//        //Delete file after unzipping
//        Utilities.deleteFile(mPathZipFile);
//
//        if(mOnUnzipCompleteListener != null) {
//            mOnUnzipCompleteListener.onComplete();
//        }
//    }
//
//    private void confirmDirectory(String directory) {
//        File file = new File(mPathDestination + directory);
//        if (!file.isDirectory()) {
//            file.mkdirs();
//        }
//    }
//
//    public interface OnUnzipCompleteListener {
//        public void onComplete();
//    }
//
//}