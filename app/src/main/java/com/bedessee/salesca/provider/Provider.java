package com.bedessee.salesca.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.bedessee.salesca.sharedprefs.SharedPrefsManager;

import java.io.File;

import timber.log.Timber;

/**
 * TODO: Document me...
 */
public class Provider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    final static private int PRODUCT = 100;
    final static private int PRODUCT_ID = 101;

    final static private int BRAND = 200;
    final static private int BRAND_ID = 201;

    final static private int CATEGORY = 300;
    final static private int CATEGORY_ID = 301;

    final static private int USER = 400;
    final static private int USER_ID = 401;

    final static private int SALESMAN_STORE = 500;
    final static private int SALESMAN_STORE_ID = 501;

    final static private int SAVED_ITEM = 600;
    final static private int SAVED_ITEM_ID = 601;

    final static private int SAVED_ORDER = 700;
    final static private int SAVED_ORDER_ID = 701;

    final static private int SIDE_MENU = 800;
    final static private int SIDE_MENU_ID = 801;

    final static private int CUST_SPEC_PRICE = 900;
    final static private int CUST_SPEC_PRICE_ID = 901;

    final static private int REPORTS_MENU = 1000;
    final static private int REPORTS_MENU_ID = 1001;


    private SQLiteDatabase mDatabase;
    private BedesseeDatabase mDbHelper;
    private ContentResolver mContentResolver;

    @Override
    public boolean onCreate() {
        return true;
    }

    private void lazyCreation() {
        Timber.d("lazyCreation");

        final Context context = getContext();
        String directory = new SharedPrefsManager(context).getSugarSyncDir();
        mDbHelper = new BedesseeDatabase(context, directory);
        mContentResolver = context.getContentResolver();
    }

    /**
     * Build and return a {@link UriMatcher} that catches all {@link Uri}
     */
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(Contract.CONTENT_AUTHORITY, "product", PRODUCT);
        matcher.addURI(Contract.CONTENT_AUTHORITY, "product/*", PRODUCT_ID);

        matcher.addURI(Contract.CONTENT_AUTHORITY, "brand", BRAND);
        matcher.addURI(Contract.CONTENT_AUTHORITY, "brand/*", BRAND_ID);

        matcher.addURI(Contract.CONTENT_AUTHORITY, "category", CATEGORY);
        matcher.addURI(Contract.CONTENT_AUTHORITY, "category/*", CATEGORY_ID);

        matcher.addURI(Contract.CONTENT_AUTHORITY, "user", USER);
        matcher.addURI(Contract.CONTENT_AUTHORITY, "user/*", USER_ID);

        matcher.addURI(Contract.CONTENT_AUTHORITY, "salesmanstore", SALESMAN_STORE);
        matcher.addURI(Contract.CONTENT_AUTHORITY, "salesmanstore/*", SALESMAN_STORE_ID);

        matcher.addURI(Contract.CONTENT_AUTHORITY, "saveditem", SAVED_ITEM);
        matcher.addURI(Contract.CONTENT_AUTHORITY, "saveditem/*", SAVED_ITEM_ID);

        matcher.addURI(Contract.CONTENT_AUTHORITY, "savedorder", SAVED_ORDER);
        matcher.addURI(Contract.CONTENT_AUTHORITY, "savedorder/*", SAVED_ORDER_ID);

        matcher.addURI(Contract.CONTENT_AUTHORITY, "sidemenu", SIDE_MENU);
        matcher.addURI(Contract.CONTENT_AUTHORITY, "sidemenu/*", SIDE_MENU_ID);

        matcher.addURI(Contract.CONTENT_AUTHORITY, "custspecprice", CUST_SPEC_PRICE);
        matcher.addURI(Contract.CONTENT_AUTHORITY, "custspecprice/*", CUST_SPEC_PRICE_ID);

        matcher.addURI(Contract.CONTENT_AUTHORITY, "reportsmenu", REPORTS_MENU);
        matcher.addURI(Contract.CONTENT_AUTHORITY, "reportsmenu/*", REPORTS_MENU_ID);

        return matcher;
    }



    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        checkInit();

        mDatabase = mDbHelper.getWritableDatabase();

        final String table = getTable(uri);

        if (table != null) {
            final Cursor cursor = mDatabase.query(table, projection, selection, selectionArgs, null, null, sortOrder);
            cursor.setNotificationUri(mContentResolver, uri);
            return cursor;
        }
        return null;
    }


    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                return Contract.Product.CONTENT_TYPE;
            case PRODUCT_ID:
                return Contract.Product.CONTENT_ITEM_TYPE;

            case BRAND:
                return Contract.Brand.CONTENT_TYPE;
            case BRAND_ID:
                return Contract.Brand.CONTENT_ITEM_TYPE;

            case CATEGORY:
                return Contract.Category.CONTENT_TYPE;
            case CATEGORY_ID:
                return Contract.Category.CONTENT_ITEM_TYPE;

            case USER:
                return Contract.User.CONTENT_TYPE;
            case USER_ID:
                return Contract.User.CONTENT_ITEM_TYPE;

            case SALESMAN_STORE:
                return Contract.SalesmanStore.CONTENT_TYPE;
            case SALESMAN_STORE_ID:
                return Contract.SalesmanStore.CONTENT_ITEM_TYPE;

            case SAVED_ITEM:
                return Contract.SavedItem.CONTENT_TYPE;
            case SAVED_ITEM_ID:
                return Contract.SavedItem.CONTENT_ITEM_TYPE;

            case SAVED_ORDER:
                return Contract.SavedOrder.CONTENT_TYPE;
            case SAVED_ORDER_ID:
                return Contract.SavedOrder.CONTENT_ITEM_TYPE;

            case SIDE_MENU:
                return Contract.SideMenu.CONTENT_TYPE;
            case SIDE_MENU_ID:
                return Contract.SideMenu.CONTENT_ITEM_TYPE;

            case CUST_SPEC_PRICE:
                return Contract.CustSpecPrice.CONTENT_TYPE;
            case CUST_SPEC_PRICE_ID:
                return Contract.CustSpecPrice.CONTENT_ITEM_TYPE;

            case REPORTS_MENU:
                return Contract.ReportsMenu.CONTENT_TYPE;
            case REPORTS_MENU_ID:
                return Contract.ReportsMenu.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        checkInit();

        mDatabase = mDbHelper.getWritableDatabase();

        final String table = getTable(uri);

        final Long id = mDatabase.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        uri = id == -1 ? uri : Uri.withAppendedPath(uri, String.valueOf(id));
        mContentResolver.notifyChange(uri, null);
        return uri;
    }


    @Override
    public int delete(@NonNull Uri uri, String whereClause, String[] selectionArgs) {
        checkInit();

        mDatabase = mDbHelper.getWritableDatabase();

        final String table = getTable(uri);
        String directory = new SharedPrefsManager(getContext()).getSugarSyncDir();
        File file = new File(BedesseeDatabase.getDatabaseFile(directory));
        if (file.exists()) {
            if (table != null) {
                final int del = mDatabase.delete(table, whereClause, selectionArgs);
                mContentResolver.notifyChange(uri, null);
                return del;
            }
        }

        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        checkInit();
        mDatabase = mDbHelper.getWritableDatabase();

        final String table = getTable(uri);

        if(table != null) {
            final int ui =  mDatabase.update(table, values, selection, selectionArgs);
            mContentResolver.notifyChange(uri, null);
            return ui;
        }

        return 0;
    }


    private void checkInit(){
        if (mDatabase == null) {
            lazyCreation();
        }
    }


    private String getTable(Uri uri) {

        switch (sUriMatcher.match(uri)) {
            case PRODUCT: return BedesseeDatabase.Tables.PRODUCT;
            case BRAND: return BedesseeDatabase.Tables.BRAND;
            case CATEGORY: return BedesseeDatabase.Tables.CATEGORY;
            case USER: return BedesseeDatabase.Tables.USER;
            case SALESMAN_STORE: return BedesseeDatabase.Tables.SALESMAN_STORE;
            case SAVED_ITEM: return BedesseeDatabase.Tables.SAVED_ITEM;
            case SAVED_ORDER: return BedesseeDatabase.Tables.SAVED_ORDER;
            case SIDE_MENU: return BedesseeDatabase.Tables.SIDE_MENU;
            case CUST_SPEC_PRICE: return BedesseeDatabase.Tables.CUST_SPEC_PRICE;
            case REPORTS_MENU: return BedesseeDatabase.Tables.REPORTS_MENU;
            default: return null;
        }
    }

}
