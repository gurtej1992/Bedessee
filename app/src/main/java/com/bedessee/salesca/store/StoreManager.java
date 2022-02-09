package com.bedessee.salesca.store;

import android.content.Context;

import com.google.gson.Gson;

/**
 * Simple class to manage current store.
 */
public class StoreManager {

    private static Store mCurrentStore;

    public static Store getCurrentStore() {
        return mCurrentStore;
    }

    public static void setCurrentStore(Context context, Store store) {
        mCurrentStore = store;
        Gson gson = new Gson();
        String json = gson.toJson(store);
        context.getSharedPreferences("store", Context.MODE_PRIVATE)
                .edit().putString("store", json).apply();
    }

    public static Store getLastStore(Context context) {
        String store = context.getSharedPreferences("store", Context.MODE_PRIVATE).getString("store", null);
        if (store != null) {
            return new Gson().fromJson(store, Store.class);
        }
        return null;
    }


    public static void clearCurrentStore() {
        mCurrentStore = null;
    }

    public static boolean isStoreSelected() {
        return mCurrentStore != null;
    }
}
