package com.bedessee.salesca.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bedessee.salesca.BuildConfig;
import com.bedessee.salesca.sharedprefs.SharedPrefsManager;

import java.io.File;

/**
 * TODO: Document me...
 */
public class BedesseeDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bedessee.db";

    private static final int CUR_DATABASE_VERSION = 43;

    public static String getDatabaseFile(String directory) {
        String parentDirectory = new File(directory).getParent();
        return parentDirectory
                + File.separator + "sls_app_db"
                + File.separator + BuildConfig.FLAVOR + DATABASE_NAME;
    }

    public static boolean isDatabaseCreated(Context context) {
        String directory = new SharedPrefsManager(context).getSugarSyncDir();
        File file = new File(getDatabaseFile(directory));
        return file.exists();
    }

    public BedesseeDatabase(Context context, String directory) {
        super(context, getDatabaseFile(directory), null, CUR_DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + Tables.PRODUCT + " (" +
            Contract.ProductColumns.COLUMN_ID + " integer primary key, " +
            Contract.ProductColumns.COLUMN_NUMBER + " text, " +
            Contract.ProductColumns.COLUMN_BRAND + " text, " +
            Contract.ProductColumns.COLUMN_DESCRIPTION + " text, " +
            Contract.ProductColumns.COLUMN_UOM + " text, " +
            Contract.ProductColumns.COLUMN_PRICE + " text, " +
            Contract.ProductColumns.COLUMN_PRICE_COLOR + " text, " +
            Contract.ProductColumns.COLUMN_PRICE_BACKGROUND + " text, " +
            Contract.ProductColumns.COLUMN_LEVEL_1_PRICE + " text, " +
            Contract.ProductColumns.COLUMN_LEVEL_1_COLOR + " text, " +
            Contract.ProductColumns.COLUMN_LEVEL_1_BACKGROUND + " text, " +
            Contract.ProductColumns.COLUMN_LEVEL_2_PRICE + " text, " +
            Contract.ProductColumns.COLUMN_LEVEL_2_COLOR + " text, " +
            Contract.ProductColumns.COLUMN_LEVEL_2_BACKGROUND + " text, " +
            Contract.ProductColumns.COLUMN_LEVEL_3_PRICE + " text, " +
            Contract.ProductColumns.COLUMN_LEVEL_3_COLOR + " text, " +
            Contract.ProductColumns.COLUMN_LEVEL_3_BACKGROUND + " text, " +
            Contract.ProductColumns.COLUMN_STATUS_CODE + " text, " +
            Contract.ProductColumns.COLUMN_M_STATUS + " text, " +
            Contract.ProductColumns.COLUMN_STATUS_DESCRIPTION + " text, " +
            Contract.ProductColumns.COLUMN_CASES_PER_SKID + " text, " +
            Contract.ProductColumns.COLUMN_CASES_PER_ROW + " text, " +
            Contract.ProductColumns.COLUMN_LAYERS_PER_SKID + " text, " +
            Contract.ProductColumns.COLUMN_IMAGE_PATH + " text, " +
            Contract.ProductColumns.COLUMN_UNIT_PRICE + " text, " +
            Contract.ProductColumns.COLUMN_CASE_UOM + " text, " +
            Contract.ProductColumns.COLUMN_TOTAL_QTY + " text, " +
            Contract.ProductColumns.COLUMN_QTY1 + " text, " +
            Contract.ProductColumns.COLUMN_QTY2 + " text, " +
            Contract.ProductColumns.COLUMN_QTY3 + " text, " +
            Contract.ProductColumns.COLUMN_QTY4 + " text, " +
            Contract.ProductColumns.COLUMN_SHOW_QTY1 + " text, " +
            Contract.ProductColumns.COLUMN_SHOW_QTY2 + " text, " +
            Contract.ProductColumns.COLUMN_SHOW_QTY3 + " text, " +
            Contract.ProductColumns.COLUMN_SHOW_QTY4 + " text, " +
            Contract.ProductColumns.COLUMN_NOTE01 + " text, " +
            Contract.ProductColumns.COLUMN_NOTE02 + " text, " +
            Contract.ProductColumns.COLUMN_NOTE03 + " text, " +
            Contract.ProductColumns.COLUMN_NOTE04 + " text, " +
            Contract.ProductColumns.COLUMN_NOTE05 + " text, " +
            Contract.ProductColumns.COLUMN_POPUPPRICE + " text, " +
            Contract.ProductColumns.COLUMN_POPUPPRICEFLAG + " text, " +
            Contract.ProductColumns.COLUMN_LIKE_TAG + " text, " +
            Contract.ProductColumns.COLUMN_NEW_TAG + " text, " +
            Contract.ProductColumns.COLUMN_FILE_CREATED_ON + " text, " +
            Contract.ProductColumns.COLUMN_PRICE_FROM + " text, " +
            Contract.ProductColumns.COLUMN_PRICE_TO + " text, " +
            Contract.ProductColumns.COLUMN_UPC + " text, " +
            Contract.ProductColumns.COLUMN_TOTAL_QTY_SOLD + " text, " +
                        Contract.ProductColumns.COLUMN_LVL0FROM + " text, " +
                        Contract.ProductColumns.COLUMN_LVL0To + " text, " +
                Contract.ProductColumns.COLUMN_LVL0PRICE + " text, " +
                Contract.ProductColumns.COLUMN_LVL1FROM + " text, " +
                Contract.ProductColumns.COLUMN_LVL1To + " text, " +
                Contract.ProductColumns.COLUMN_LVL1PRICE + " text, " +
                Contract.ProductColumns.COLUMN_LVL2FROM + " text, " +
                Contract.ProductColumns.COLUMN_LVL2To + " text, " +
                Contract.ProductColumns.COLUMN_LVL2PRICE + " text, " +
                Contract.ProductColumns.COLUMN_LVL3FROM + " text, " +
                Contract.ProductColumns.COLUMN_LVL3To + " text, " +
                Contract.ProductColumns.COLUMN_LVL3PRICE + " text, "+
                        Contract.ProductColumns.COLUMN_PLUS_SIGN_COLOR + " text, " +
                        Contract.ProductColumns.COLUMN_PLUS_SIGN_BCKG_COLOR + " text,"+
                Contract.ProductColumns.COLUMN_PROD_LINE1_LEFTA_COLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE1_LEFTA_BCKCOLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE1A + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE1_LEFTB_COLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE1_LEFTB_BCKCOLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE1B + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE2_LEFTA_COLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE2_LEFTA_BCKCOLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE2A + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE2_LEFTB_COLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE2_LEFTB_BCKCOLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE2B + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE3_LEFTA_COLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE3_LEFTA_BCKCOLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE3A + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE3_LEFTB_COLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE3_LEFTB_BCKCOLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE3B + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE4_LEFTA_COLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE4_LEFTA_BCKCOLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE4A + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE4_LEFTB_COLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE4_LEFTB_BCKCOLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE4B + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE5_LEFTA_COLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE5_LEFTA_BCKCOLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE5A + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE5_LEFTB_COLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE5_LEFTB_BCKCOLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE5B + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE1_RGHTA_COLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE1_RGHTA_BCKCOLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE1R + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE2_RGHTA_COLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE2_RGHTA_BCKCOLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE2R + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE3_RGHTA_COLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE3_RGHTA_BCKCOLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE3R + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE4_RGHTA_COLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE4_RGHTA_BCKCOLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE4R + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE5_RGHTA_COLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE5_RGHTA_BCKCOLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE5R + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE6_RGHTA_COLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE6_RGHTA_BCKCOLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE6R + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE7_RGHTA_COLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE7_RGHTA_BCKCOLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE7R + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE8_RGHTA_COLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE8_RGHTA_BCKCOLOR + " text," +
                Contract.ProductColumns.COLUMN_PROD_LINE8R + " text," +
                Contract.ProductColumns.COLUMN_PRODUCT_TILE_SCREEN_LINE1_COLOR + " text," +
                Contract.ProductColumns.COLUMN_PRODUCT_TILE_SCREEN_LINE1_BCKGRD + " text," +
                Contract.ProductColumns.COLUMN_PRODUCT_TILE_SCREEN_LINE1_SHOW + " text," +
                Contract.ProductColumns.COLUMN_PRODUCT_TILE_SCREEN_LINE2_COLOR + " text," +
                Contract.ProductColumns.COLUMN_PRODUCT_TILE_SCREEN_LINE2_BCKGRD + " text," +
                Contract.ProductColumns.COLUMN_PRODUCT_TILE_SCREEN_LINE2_SHOW + " text)"


        ) ;


        db.execSQL("CREATE TABLE " + Tables.BRAND + "(" +
            Contract.BrandColumns.COLUMN_ID + " integer primary key, " +
            Contract.BrandColumns.COLUMN_BRAND_NAME + " text, " +
            Contract.BrandColumns.COLUMN_BRAND_NUM_PRODUCTS + " text, " +
            Contract.BrandColumns.COLUMN_FILE_CREATED_ON + " text, " +
            Contract.BrandColumns.COLUMN_LOGO_NAME + " text)");

        db.execSQL("CREATE TABLE " + Tables.SIDE_MENU + "(" +
            Contract.SideMenuColumns.COLUMN_ID + " integer primary key autoincrement, " +
            Contract.SideMenuColumns.COLUMN_CODE + " text, " +
            Contract.SideMenuColumns.COLUMN_OPEN_BY_DEFAULT + " text, " +
            Contract.SideMenuColumns.COLUMN_TITLE + " text, " +
            Contract.SideMenuColumns.COLUMN_SORT + " text, " +
            Contract.SideMenuColumns.COLUMN_COLOUR + " text, " +
            Contract.SideMenuColumns.COLUMN_FILE_CREATED_ON + " text, " +
            Contract.SideMenuColumns.COLUMN_COUNT + " text)");

        db.execSQL("CREATE TABLE " + Tables.CATEGORY + "(" +
            Contract.CategoryColumns.COLUMN_ID + " integer primary key, " +
            Contract.CategoryColumns.COLUMN_ACTIVE + " text, " +
            Contract.CategoryColumns.COLUMN_CHAR + " text, " +
            Contract.CategoryColumns.COLUMN_FILE_CREATED_ON + " text, " +
            Contract.CategoryColumns.COLUMN_DESCRIPTION + " text)");

        db.execSQL("CREATE TABLE " + Tables.USER + "(" +
            Contract.UserColumns.COLUMN_ID + " integer primary key, " +
            Contract.UserColumns.COLUMN_NAME + " text, " +
            Contract.UserColumns.COLUMN_IS_ADMIN + " text, " +
            Contract.UserColumns.COLUMN_EMAIL_ORDER_PREFIX + " text, " +
            Contract.UserColumns.COLUMN_EMAIL + " text)");

        db.execSQL("CREATE TABLE " + Tables.SALESMAN_STORE + "(" +
            Contract.SalesmanStoreColumns.COLUMN_ID + " integer primary key, " +
            Contract.SalesmanStoreColumns.COLUMN_SALESPERSON + " text, " +
            Contract.SalesmanStoreColumns.COLUMN_SALES_EMAIL + " text, " +
            Contract.SalesmanStoreColumns.COLUMN_IS_ADMIN + " text, " +
            Contract.SalesmanStoreColumns.COLUMN_CUST_NAME + " text, " +
            Contract.SalesmanStoreColumns.COLUMN_CUST_NUM + " text, " +
            Contract.SalesmanStoreColumns.COLUMN_CUST_ADDR + " text, " +
            Contract.SalesmanStoreColumns.COLUMN_LAST_COLLECT_DAYS_OLD + " text, " +
            Contract.SalesmanStoreColumns.COLUMN_LAST_COLLECT_DATE + " text, " +
            Contract.SalesmanStoreColumns.COLUMN_LAST_COLLECT_INVOICE + " text, " +
            Contract.SalesmanStoreColumns.COLUMN_LAST_COLLECT_AMOUNT + " text, " +
            Contract.SalesmanStoreColumns.COLUMN_OUTSTANDING_BAL_DUE + " text, " +
            Contract.SalesmanStoreColumns.COLUMN_SHOW_POPUP + " text, " +
            Contract.SalesmanStoreColumns.COLUMN_CREATED_ON + " text, " +
            Contract.SalesmanStoreColumns.COLUMN_NEW_STORE + " text, " +
            Contract.SalesmanStoreColumns.COLUMN_CUSTOM_ATTRIBUTES + " text, " +
            Contract.SalesmanStoreColumns.COLUMN_SHOW_ACCOUNT_STATUS + " text, " +
            Contract.SalesmanStoreColumns.COLUMN_SHOW_DEFAULT_REPORT + " text, " +
            Contract.SalesmanStoreColumns.COLUMN_STATEMENT_URL + " text)");

        db.execSQL("CREATE TABLE " + Tables.SAVED_ITEM + "(" +
            Contract.SavedItemColumns.COLUMN_ID + " integer primary key, " +
            Contract.SavedItemColumns.COLUMN_ORDER_ID + " text, " +
            Contract.SavedItemColumns.COLUMN_PRODUCT_NUMBER + " text, " +
            Contract.SavedItemColumns.COLUMN_PRODUCT_QUANTITY + " text, " +
            Contract.SavedItemColumns.COLUMN_PRODUCT_PRICE + " text, " +
            Contract.SavedItemColumns.COLUMN_PRODUCT_QUANTITY_TYPE + " text)");

        db.execSQL("CREATE TABLE " + Tables.SAVED_ORDER + "(" +
            Contract.SavedOrderColumns.COLUMN_ID + " text, " +
            Contract.SavedOrderColumns.COLUMN_STORE + " text, " +
            Contract.SavedOrderColumns.COLUMN_START_TIME + " text, " +
            Contract.SavedOrderColumns.COLUMN_END_TIME + " text, " +
            Contract.SavedOrderColumns.COLUMN_NUM_PRODUCTS + " integer, " +
            Contract.SavedOrderColumns.COLUMN_IS_CLOSED + " text, " +
                Contract.SavedOrderColumns.COLUMN_COMMENT + " text, " +
                Contract.SavedOrderColumns.COLUMN_CONTACT + " text)");

        db.execSQL("CREATE TABLE " + Tables.REPORTS_MENU + "(" +
                Contract.ReportsMenuColumns.COLUMN_ID + " integer primary key, " +
                Contract.ReportsMenuColumns.COLUMN_SIDE_MENU_DISPLAY + " text, " +
                Contract.ReportsMenuColumns.COLUMN_DEVICE_FOLDER + " text, " +
                Contract.ReportsMenuColumns.COLUMN_POP_UP_TYPE + " text, " +
                Contract.ReportsMenuColumns.COLUMN_FILE_NAME + " text, " +
                Contract.ReportsMenuColumns.COLUMN_FILE_CREATRED + " text," +
                Contract.ReportsMenuColumns.COLUMN_DEFAULT_OPEN_AFTER_CUSTOMER_SELECT + " text)");

        db.execSQL("CREATE TABLE " + Tables.CUST_SPEC_PRICE + "(" +
            Contract.CustSpecPriceColumns.COLUMN_ID + " text, " +
            Contract.CustSpecPriceColumns.COLUMN_CUST_NUM + " text, " +
            Contract.CustSpecPriceColumns.COLUMN_PROD_NUM + " text, " +
            Contract.CustSpecPriceColumns.COLUMN_PRICE + " text, " +
            Contract.CustSpecPriceColumns.COLUMN_UNIT_PRICE + " text, " +
            Contract.CustSpecPriceColumns.COLUMN_1 + " text, " +
            Contract.CustSpecPriceColumns.COLUMN_LEVEL1PRICE + " text, " +
            Contract.CustSpecPriceColumns.COLUMN_2 + " text, " +
            Contract.CustSpecPriceColumns.COLUMN_LEVEL2PRICE + " text, " +
            Contract.CustSpecPriceColumns.COLUMN_3 + " text, " +
            Contract.CustSpecPriceColumns.COLUMN_LEVEL3PRICE + " text, " +
            Contract.CustSpecPriceColumns.COLUMN_NOTE_01 + " text, " +
            Contract.CustSpecPriceColumns.COLUMN_NOTE_02 + " text, " +
            Contract.CustSpecPriceColumns.COLUMN_NOTE_03 + " text, " +
            Contract.CustSpecPriceColumns.COLUMN_NOTE_04 + " text, " +
            Contract.CustSpecPriceColumns.COLUMN_NOTE_05 + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.PRODUCT);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.BRAND);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.SIDE_MENU);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.USER);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.SALESMAN_STORE);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.SAVED_ITEM);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.SAVED_ORDER);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.CUST_SPEC_PRICE);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.REPORTS_MENU);
        onCreate(db);
    }

    public static class Tables {
        public static String PRODUCT = "product";
        public static String BRAND = "brand";
        public static String CATEGORY = "category";
        public static String USER = "user";
        public static String SALESMAN_STORE = "salesmanstore";
        public static String SAVED_ITEM = "saveditem";
        public static String SAVED_ORDER = "savedorder";
        public static String SIDE_MENU = "sidemenu";
        public static String CUST_SPEC_PRICE = "custspecprice";
        public static String REPORTS_MENU = "reportsmenu";
    }
}
