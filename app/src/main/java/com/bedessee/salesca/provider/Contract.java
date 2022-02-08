package com.bedessee.salesca.provider;

import android.net.Uri;
import android.provider.BaseColumns;

import com.bedessee.salesca.BuildConfig;

/**
 * Contract class for interacting with {@link Provider}.
 */
public class Contract {


    public static class ProductColumns {
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_NUMBER = "number";
        public static final String COLUMN_BRAND = "brand";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_UOM = "uom";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_PRICE_COLOR = "price_color";
        public static final String COLUMN_PRICE_BACKGROUND = "price_background_color";
        public static final String COLUMN_LEVEL_1_PRICE = "level_1_price";
        public static final String COLUMN_LEVEL_1_COLOR = "level_1_price_color";
        public static final String COLUMN_LEVEL_1_BACKGROUND = "level_1_price_background_color";
        public static final String COLUMN_LEVEL_2_PRICE = "level_2_price";
        public static final String COLUMN_LEVEL_2_COLOR = "level_2_price_color";
        public static final String COLUMN_LEVEL_2_BACKGROUND = "level_2_price_background_color";
        public static final String COLUMN_LEVEL_3_PRICE = "level_3_price";
        public static final String COLUMN_LEVEL_3_COLOR = "level_3_price_color";
        public static final String COLUMN_LEVEL_3_BACKGROUND = "level_3_price_background_color";
        public static final String COLUMN_STATUS_CODE = "status_code";
        public static final String COLUMN_M_STATUS = "m_status";
        public static final String COLUMN_STATUS_DESCRIPTION = "status_description";
        public static final String COLUMN_CASES_PER_SKID = "cases_per_skid";
        public static final String COLUMN_CASES_PER_ROW = "cases_per_row";
        public static final String COLUMN_LAYERS_PER_SKID = "layers_per_skid";
        public static final String COLUMN_IMAGE_PATH = "image_path";
        public static final String COLUMN_UNIT_PRICE = "unit_price";
        public static final String COLUMN_CASE_UOM = "case_uom";
        public static final String COLUMN_TOTAL_QTY = "total_qty";
        public static final String COLUMN_UPC = "upc";
        public static final String COLUMN_QTY1 = "qty1";
        public static final String COLUMN_QTY2 = "qty2";
        public static final String COLUMN_QTY3 = "qty3";
        public static final String COLUMN_QTY4 = "qty4";
        public static final String COLUMN_SHOW_QTY1 = "show_qty1";
        public static final String COLUMN_SHOW_QTY2 = "show_qty2";
        public static final String COLUMN_SHOW_QTY3 = "show_qty3";
        public static final String COLUMN_SHOW_QTY4 = "show_qty4";
        public static final String COLUMN_NOTE01 = "note01";
        public static final String COLUMN_NOTE02 = "note02";
        public static final String COLUMN_NOTE03 = "note03";
        public static final String COLUMN_NOTE04 = "note04";
        public static final String COLUMN_NOTE05 = "note05";
        public static final String COLUMN_POPUPPRICE = "popupprice";
        public static final String COLUMN_POPUPPRICEFLAG = "popuppriceflag";
        public static final String COLUMN_LIKE_TAG = "liketag";
        public static final String COLUMN_NEW_TAG = "newtag";
        public static final String COLUMN_FILE_CREATED_ON = "createdon";
        public static final String COLUMN_PRICE_FROM = "pricefrom";
        public static final String COLUMN_PRICE_TO = "priceto";
        public static final String COLUMN_TOTAL_QTY_SOLD = "total_qty_sold";
    }

    public static class SideMenuColumns {
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_CODE = "code";
        public static final String COLUMN_OPEN_BY_DEFAULT = "open_by_default";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SORT = "sort";
        public static final String COLUMN_COLOUR = "colour";
        public static final String COLUMN_COUNT = "count";
        public static final String COLUMN_FILE_CREATED_ON = "file_created_on";
    }

    public static class BrandColumns {
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_BRAND_NAME = "name";
        public static final String COLUMN_BRAND_NUM_PRODUCTS = "num_products";
        public static final String COLUMN_LOGO_NAME = "logo_name";
        public static final String COLUMN_FILE_CREATED_ON = "file_created_on";
    }

    public static class CategoryColumns {
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_ACTIVE = "car_is_active";
        public static final String COLUMN_CHAR = "cat_char";
        public static final String COLUMN_DESCRIPTION = "cat_description";
        public static final String COLUMN_FILE_CREATED_ON = "file_created_on";
    }

    public static class UserColumns {
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_IS_ADMIN = "is_admin";
        public static final String COLUMN_EMAIL_ORDER_PREFIX =  "email_order_prefix";
    }

    public static class SalesmanStoreColumns {
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_SALESPERSON = "salesperson";
        public static final String COLUMN_SALES_EMAIL = "sales_email";
        public static final String COLUMN_IS_ADMIN = "is_admin";
        public static final String COLUMN_CUST_NAME = "cst_name";
        public static final String COLUMN_CUST_NUM = "cust_num";
        public static final String COLUMN_CUST_ADDR = "cust_addr";
        public static final String COLUMN_LAST_COLLECT_DAYS_OLD = "last_collect_days_old";
        public static final String COLUMN_LAST_COLLECT_DATE = "last_collect_date";
        public static final String COLUMN_LAST_COLLECT_INVOICE = "last_collect_invoice";
        public static final String COLUMN_LAST_COLLECT_AMOUNT = "last_collect_amount";
        public static final String COLUMN_OUTSTANDING_BAL_DUE = "outstanding_bal_due";
        public static final String COLUMN_STATEMENT_URL = "statement_url";
        public static final String COLUMN_SHOW_POPUP = "show_popup";
        public static final String COLUMN_CREATED_ON = "created_on";
        public static final String COLUMN_NEW_STORE = "is_newStore";
        public static final String COLUMN_SHOW_ACCOUNT_STATUS = "show_account_status";
        public static final String COLUMN_SHOW_DEFAULT_REPORT = "show_default_report";
        public static final String COLUMN_CUSTOM_ATTRIBUTES = "custom_attributes";
    }


    public static class SavedItemColumns {
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_ORDER_ID = "order_id";
        public static final String COLUMN_PRODUCT_NUMBER = "prod_number";
        public static final String COLUMN_PRODUCT_QUANTITY = "prod_qty";
        public static final String COLUMN_PRODUCT_QUANTITY_TYPE = "prod_qty_type";
        public static final String COLUMN_PRODUCT_PRICE = "prod_price";
    }


    public static class SavedOrderColumns {
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_STORE = "store";
        public static final String COLUMN_START_TIME = "start_time";
        public static final String COLUMN_END_TIME = "end_time";
        public static final String COLUMN_IS_CLOSED = "is_closed";
        public static final String COLUMN_NUM_PRODUCTS = "num_prods";
    }

    public static class ReportsMenuColumns {
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_SIDE_MENU_DISPLAY = "side_menu_display";
        public static final String COLUMN_DEVICE_FOLDER = "device_folder";
        public static final String COLUMN_POP_UP_TYPE = "pop_up_type";
        public static final String COLUMN_FILE_NAME = "file_name";
        public static final String COLUMN_DEFAULT_OPEN_AFTER_CUSTOMER_SELECT = "default_open_after_customer_select";
        public static final String COLUMN_FILE_CREATRED = "file_created";
    }

    public static class CustSpecPriceColumns {
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_CUST_NUM = "cust_num";
        public static final String COLUMN_PROD_NUM = "prod_num";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_UNIT_PRICE = "unit_price";
        public static final String COLUMN_1 = "column_1";
        public static final String COLUMN_LEVEL1PRICE = "column_1_price";
        public static final String COLUMN_2 = "column_2";
        public static final String COLUMN_LEVEL2PRICE = "column_2_price";
        public static final String COLUMN_3 = "column_3";
        public static final String COLUMN_LEVEL3PRICE = "column_3_price";
        public static final String COLUMN_NOTE_01 = "note_01";
        public static final String COLUMN_NOTE_02 = "note_02";
        public static final String COLUMN_NOTE_03 = "note_03";
        public static final String COLUMN_NOTE_04 = "note_04";
        public static final String COLUMN_NOTE_05 = "note_05";
    }



    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String CONTENT_TYPE_PREFIX = "vnd.android.cursor.dir/vnd.com.thebedesseegroup.sales.provider.";
    private static final String CONTENT_TYPE_ITEM_PREFIX = "vnd.android.cursor.item/vnd.com.thebedesseegroup.sales.provider.";

    private static final String PATH_PRODUCT = "product";
    private static final String PATH_BRAND = "brand";
    private static final String PATH_CATEGORY = "category";
    private static final String PATH_USER = "user";
    private static final String PATH_SALESMAN_STORE = "salesmanstore";
    private static final String PATH_SAVED_ITEM = "saveditem";
    private static final String PATH_SAVED_ORDER = "savedorder";
    private static final String PATH_SIDE_MENU = "sidemenu";
    private static final String PATH_CUST_SPEC_PRICE = "custspecprice";
    private static final String PATH_REPORTS_MENU = "reportsmenu";


    public static class Product implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRODUCT).build();
        public static final String CONTENT_TYPE = CONTENT_TYPE_PREFIX + PATH_PRODUCT;
        public static final String CONTENT_ITEM_TYPE = CONTENT_TYPE_ITEM_PREFIX + PATH_PRODUCT;
    }


    public static class SideMenu implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SIDE_MENU).build();
        public static final String CONTENT_TYPE = CONTENT_TYPE_PREFIX + PATH_SIDE_MENU;
        public static final String CONTENT_ITEM_TYPE = CONTENT_TYPE_ITEM_PREFIX + PATH_SIDE_MENU;
    }


    public static class Brand implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_BRAND).build();
        public static final String CONTENT_TYPE = CONTENT_TYPE_PREFIX + PATH_BRAND;
        public static final String CONTENT_ITEM_TYPE = CONTENT_TYPE_ITEM_PREFIX + PATH_BRAND;
    }


    public static class Category implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();
        public static final String CONTENT_TYPE = CONTENT_TYPE_PREFIX + PATH_CATEGORY;
        public static final String CONTENT_ITEM_TYPE = CONTENT_TYPE_ITEM_PREFIX + PATH_CATEGORY;
    }

    public static class User implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER).build();
        public static final String CONTENT_TYPE = CONTENT_TYPE_PREFIX + PATH_USER;
        public static final String CONTENT_ITEM_TYPE = CONTENT_TYPE_ITEM_PREFIX + PATH_USER;
    }

    public static class SalesmanStore implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SALESMAN_STORE).build();
        public static final String CONTENT_TYPE = CONTENT_TYPE_PREFIX + PATH_SALESMAN_STORE;
        public static final String CONTENT_ITEM_TYPE = CONTENT_TYPE_ITEM_PREFIX + PATH_SALESMAN_STORE;
    }

    public static class SavedItem implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SAVED_ITEM).build();
        public static final String CONTENT_TYPE = CONTENT_TYPE_PREFIX + PATH_SAVED_ITEM;
        public static final String CONTENT_ITEM_TYPE = CONTENT_TYPE_ITEM_PREFIX + PATH_SAVED_ITEM;
    }

    public static class SavedOrder implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SAVED_ORDER).build();
        public static final String CONTENT_TYPE = CONTENT_TYPE_PREFIX + PATH_SAVED_ORDER;
        public static final String CONTENT_ITEM_TYPE = CONTENT_TYPE_ITEM_PREFIX + PATH_SAVED_ORDER;
    }

    public static class CustSpecPrice implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CUST_SPEC_PRICE).build();
        public static final String CONTENT_TYPE = CONTENT_TYPE_PREFIX + PATH_CUST_SPEC_PRICE;
        public static final String CONTENT_ITEM_TYPE = CONTENT_TYPE_ITEM_PREFIX + PATH_CUST_SPEC_PRICE;
    }

    public static class ReportsMenu implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REPORTS_MENU).build();
        public static final String CONTENT_TYPE = CONTENT_TYPE_PREFIX + PATH_REPORTS_MENU;
        public static final String CONTENT_ITEM_TYPE = CONTENT_TYPE_ITEM_PREFIX + PATH_REPORTS_MENU;
    }

}
