package com.bedessee.salesca.update;

import  android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bedessee.salesca.R;
import com.bedessee.salesca.login.Login;
import com.bedessee.salesca.mixpanel.MixPanelManager;
import com.bedessee.salesca.sharedprefs.SharedPrefsManager;
import com.bedessee.salesca.update.json.BaseJsonUpdate;
import com.bedessee.salesca.update.json.UpdateAppInfo;
import com.bedessee.salesca.update.json.UpdateBrands;
import com.bedessee.salesca.update.json.UpdateCategories;
import com.bedessee.salesca.update.json.UpdateCustSpecPrice;
import com.bedessee.salesca.update.json.UpdateProducts;
import com.bedessee.salesca.update.json.UpdateSalesmenStores;
import com.bedessee.salesca.update.json.UpdateSideMenu;
import com.bedessee.salesca.update.json.UpdateUsers;
import com.bedessee.salesca.update.json.deserializer.UpdateReportsMenu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Activity used for updating all data. In order for this to work, an
 * {@link UpdateType}
 * must be passed in via {@link Intent#putExtra(String, Serializable)} using
 * {@link UpdateActivity#sUpdateTypeKey} as the key.
 */
public class UpdateActivity extends Activity {
    public static Intent newIntent(Context context) {
        final Intent intent = new Intent(context, UpdateActivity.class);
        final ArrayList<UpdateActivity.UpdateType> updateTypes = new ArrayList<>(7);
        updateTypes.add(UpdateActivity.UpdateType.APP_INFO);
        updateTypes.add(UpdateActivity.UpdateType.USERS);
        updateTypes.add(UpdateActivity.UpdateType.CUST_SPEC_PRICE_LIST);
        updateTypes.add(UpdateActivity.UpdateType.SIDE_MENU);
        updateTypes.add(UpdateActivity.UpdateType.BRANDS);
        updateTypes.add(UpdateActivity.UpdateType.CATEGORY);
        updateTypes.add(UpdateActivity.UpdateType.SALESMEN_STORES);
        updateTypes.add(UpdateActivity.UpdateType.PRODUCT_INFO);
        updateTypes.add(UpdateActivity.UpdateType.REPORTS_MENU);
        intent.putExtra(UpdateActivity.sUpdateTypeKey, updateTypes);
        return intent;
    }

    final public static int REQUEST_CODE = 1;
    final public static String KEY_UPDATE_DIR = "key_update_dir";
    private String mDir = "";
Boolean update=false;
    private int mCurrentUpdateCount = 0;
    private int mTotalUpdateCount = 0;

    /**
     * Enum to determine what to update. This is passed in from MainActivity when starting
     * this Activity.
     */
    public enum UpdateType {

        PRODUCT_INFO("Product info"),
        SALESMEN_STORES("Stores list"),
        BRANDS("Brands info"),
        APP_INFO("App info"),
        USERS("Users"),
        SIDE_MENU("Side Menu"),
        CATEGORY("Category info"),
        REPORTS_MENU("Reports Menu"),
        LOGIN("Login"),
        CUST_SPEC_PRICE_LIST("Customer Specific Price List"),
        NO_LOGIN("No Login");

        private final String mDescription;

        UpdateType(String description) {
            mDescription = description;
        }

        public String getDescription() {
            return mDescription;
        }
    }

    /**
     * Update queue.
     */
    private ArrayList<UpdateType> mUpdateTypesList;

    /**
     * Key used for passing in an {@link UpdateActivity.UpdateType}
     */
    public static String sUpdateTypeKey = "update_type_key";

    private static UpdatingAdapter updatingAdapter = new UpdatingAdapter();

    /**
     * DownloadManager.
     */
    public static DownloadManager sDownloadManager;

    SharedPreferences sharedPrefsManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        SharedPreferences sh = getSharedPreferences("setting", Context.MODE_PRIVATE);
        String orient= sh.getString("orientation","landscape");
        if(orient.equals("landscape")){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else {
           setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
       sharedPrefsManager=getSharedPreferences("selectedfile",Context.MODE_PRIVATE);
        update=sharedPrefsManager.getBoolean("update",false);
        updatingAdapter = new UpdatingAdapter();
        initViews();

        new Thread(new Runnable() {
            @Override
            public void run() {
                MixPanelManager.trackScreenView(UpdateActivity.this, "Daily Update screen");

                final Bundle extras = getIntent().getExtras();

                if (extras != null && !extras.isEmpty()) {

                    if (extras.containsKey(sUpdateTypeKey)) {
                        //noinspection unchecked
                        mUpdateTypesList = (ArrayList<UpdateType>) extras.get(sUpdateTypeKey);
                    } else {
                        mUpdateTypesList = new ArrayList<>();

                            mUpdateTypesList.add(UpdateActivity.UpdateType.APP_INFO);
                            mUpdateTypesList.add(UpdateType.USERS);
                            mUpdateTypesList.add(UpdateType.CUST_SPEC_PRICE_LIST);
                            mUpdateTypesList.add(UpdateType.SIDE_MENU);
                            mUpdateTypesList.add(UpdateType.BRANDS);
                            mUpdateTypesList.add(UpdateType.CATEGORY);
                            mUpdateTypesList.add(UpdateType.SALESMEN_STORES);

                        mUpdateTypesList.add(UpdateType.PRODUCT_INFO);

                            mUpdateTypesList.add(UpdateType.REPORTS_MENU);

                    }

                    if (extras.containsKey(KEY_UPDATE_DIR)) {
                        mDir = extras.getString(KEY_UPDATE_DIR);
                    }

                }

                sDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

                mTotalUpdateCount = mUpdateTypesList.size();

                downloadQueuedUpdate(mUpdateTypesList.get(0));
            }
        }).start();
    }


    private void downloadQueuedUpdate(final UpdateType updateType) {

        mCurrentUpdateCount++;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updatingAdapter.add(new UpdateInfo(updateType, ((Double) (100 * ((double) mCurrentUpdateCount / (double) mTotalUpdateCount))).intValue(), ""));
                updatingAdapter.notifyDataSetChanged();
            }
        });

        switch (updateType) {

            case PRODUCT_INFO:
                /* Download product info */

                final UpdateProducts updateProducts = new UpdateProducts(UpdateActivity.this);
                updateProducts.setOnJsonDownloadCompleteListener(new BaseJsonUpdate.OnDownloadJsonCompleteListener() {
                    @Override
                    public void onComplete() {
                        setResult(1);
                        allClear();
                    }
                });
                updateProducts.execute(mDir);
                break;

            case SALESMEN_STORES:


                    final UpdateSalesmenStores updateSalesmenStores = new UpdateSalesmenStores(this);
                    updateSalesmenStores.setOnJsonDownloadCompleteListener(new BaseJsonUpdate.OnDownloadJsonCompleteListener() {
                        @Override
                        public void onComplete() {
                            setResult(100);
                            allClear();
                        }
                    });
                    updateSalesmenStores.execute(mDir);

                break;

            case BRANDS:

                    final UpdateBrands updateBrands = new UpdateBrands(this);
                    updateBrands.setOnJsonDownloadCompleteListener(new BaseJsonUpdate.OnDownloadJsonCompleteListener() {
                        @Override
                        public void onComplete() {
                            setResult(1);
                            allClear();
                        }
                    });
                    updateBrands.execute(mDir);

                break;

            case USERS:

                    final UpdateUsers updateUsers = new UpdateUsers(this);
                    updateUsers.setOnUpdateUsersCompleteListener(new UpdateUsers.OnUpdateUsersCompleteListener() {
                        @Override
                        public void onComplete() {

                            setResult(1);
                            allClear();
                        }

                        @Override
                        public void onError() {

                        }
                    });
                    updateUsers.execute(mDir);

                break;

            case CATEGORY:

                    final UpdateCategories updateCategories = new UpdateCategories(this);
                    updateCategories.setOnJsonDownloadCompleteListener(new BaseJsonUpdate.OnDownloadJsonCompleteListener() {
                        @Override
                        public void onComplete() {
                            setResult(1);
                            allClear();
                        }
                    });
                    updateCategories.execute(mDir);

                break;

            case APP_INFO:

                    runUpdate(new UpdateAppInfo(this));

                break;

            case SIDE_MENU:

                    runUpdate(new UpdateSideMenu(this));

                break;

            case CUST_SPEC_PRICE_LIST:

                    runUpdate(new UpdateCustSpecPrice(this));

                break;

            case REPORTS_MENU:

                    runUpdate(new UpdateReportsMenu(this));

                break;

            default:
                break;
        }
    }


    private void runUpdate(@NonNull final BaseJsonUpdate jsonUpdate) {
        jsonUpdate.setOnJsonDownloadCompleteListener(new BaseJsonUpdate.OnDownloadJsonCompleteListener() {
            @Override
            public void onComplete() {
                setResult(1);
                allClear();
            }
        });
        jsonUpdate.execute(mDir);
    }


    public void allClear() {

        /* Remove the 0th element, which is the just-downloaded update */
        if(!mUpdateTypesList.isEmpty()) {
            mUpdateTypesList.remove(0);
        }

        if(!mUpdateTypesList.isEmpty()) {
            downloadQueuedUpdate(mUpdateTypesList.get(0));
        } else {
            final Button button = findViewById(R.id.btn_close);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (new SharedPrefsManager(UpdateActivity.this).getCloseAfterUpdate()) {
                        setResult(Activity.RESULT_CANCELED);
                        finishAffinity();
                    } else {
                        setResult(Activity.RESULT_OK);
                        new SharedPrefsManager(UpdateActivity.this).removeLoggedInUser();
                        Intent intent=new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);
                        finish();

                    }
                  sharedPrefsManager.edit().putBoolean("update",false).apply();
                }
            });
            button.setEnabled(true);
        }
    }


    private void initViews() {
        ListView mTitle = (ListView) findViewById(R.id.number_pad_title);
        mTitle.setAdapter(updatingAdapter);
    }


    /**
     * Override back functionality to do nothing.
     */
    @Override
    public void onBackPressed() { }


    /**
     * Sets progress.
     *
     * @param progress Text to set
     */
    public static void setProgress(UpdateType updateType, int progress, String created) {
        updatingAdapter.update(new UpdateInfo(updateType, progress, created));
        updatingAdapter.notifyDataSetChanged();
    }

    public static class UpdateInfo{
        UpdateType updateType;
        int progress;
        String created;

        public UpdateInfo(UpdateType updateType, int progress, String created) {
            this.updateType = updateType;
            this.progress = progress;
            this.created = created;
        }
    }


    private static class UpdatingAdapter extends BaseAdapter {

        ArrayList<UpdateInfo> infos = new ArrayList<>();
        HashMap<UpdateType, Integer> updateTypeHashMap = new HashMap<>(UpdateType.values().length);

        public void add(UpdateInfo updateInfo) {

            if (updateTypeHashMap.containsKey(updateInfo.updateType)) {
                update(updateInfo);
            } else {
                updateTypeHashMap.put(updateInfo.updateType, infos.size());
                infos.add(updateInfo);
            }
        }


        public void update(UpdateInfo updateInfo) {
            if (updateInfo != null && updateInfo.updateType != null && infos != null && updateTypeHashMap != null && updateTypeHashMap.containsKey(updateInfo.updateType)) {
                infos.set(updateTypeHashMap.get(updateInfo.updateType), updateInfo);
            }
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            @SuppressLint("ViewHolder")
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.updating_line_item, parent, false);

            ((TextView)view.findViewById(R.id.number_pad_title)).setText(infos.get(position).updateType.getDescription());
            int pro = infos.get(position).progress;
            if(pro > 100){
                pro = 100;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ProgressBar)view.findViewById(R.id.progressbar)).setProgressTintList(ColorStateList.valueOf(Color.RED));
            }
            ((TextView)view.findViewById(R.id.progress)).setText(pro + "%");
            if (TextUtils.isEmpty(infos.get(position).created)) {
                view.findViewById(R.id.created).setVisibility(View.GONE);
            } else {
                ((TextView) view.findViewById(R.id.created)).setText(infos.get(position).created);
                view.findViewById(R.id.created).setVisibility(View.VISIBLE);
            }
            ((ProgressBar)view.findViewById(R.id.progressbar)).setProgress(infos.get(position).progress);

            return view;

            //when it shows progress it show duplocate progress and titles. Some progress
        }

        @Override
        public int getCount() {
            return infos.size();
        }

        @Override
        public UpdateInfo getItem(int position) {
            return infos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }
}