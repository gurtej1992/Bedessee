package com.bedessee.sales.utilities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.bedessee.sales.R;
import com.bedessee.sales.customview.GenericDialog;
import com.bedessee.sales.customview.ItemType;
import com.bedessee.sales.orderhistory.SavedItem;
import com.bedessee.sales.orderhistory.SavedOrder;
import com.bedessee.sales.product.Product;
import com.bedessee.sales.provider.Contract;
import com.bedessee.sales.provider.ProviderUtils;
import com.bedessee.sales.salesman.SalesmanManager;
import com.bedessee.sales.sharedprefs.SharedPrefsManager;
import com.bedessee.sales.shoppingcart.ShoppingCart;
import com.bedessee.sales.shoppingcart.ShoppingCartProduct;
import com.bedessee.sales.store.StoreManager;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * Class containing public static utility functions that are used throughout the app.
 */
public class Utilities {

    private static int[] screenDimens;

    /**
     * Checks if an internet connection (cellular or wifi) exists
     *
     * @param context Context
     * @return True if internet exists, false otherwise
     */
    public static boolean isInternetPresent(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    @Nullable
    public static Integer parseSaveColor(@Nullable String color) {
        if (color != null && !color.isEmpty()) {
            try {
                return Color.parseColor(color);
            } catch (Exception e) {
                Timber.e(e);
            }
        }
        return null;
    }

    /**
     * Update the shopping cart from anywhere in the app. From here, the user can either
     * add a new product or update the quantity of a product already in the shopping cart.
     *
     * @param product     Product to add/update
     * @param selectedQty Quantity of product
     * @param itemType    {@link ItemType} of item
     */
    public static void updateShoppingCart(String TAG, final Context context, final Product product, final int selectedQty, final String price, final ItemType itemType, final ProductEnteredFrom from, final OnProductUpdatedListener onProductUpdatedListener) {

        boolean updatedQty = false;

        final String orderId = ShoppingCart.getCurrentOrderId(context);
        final ShoppingCartProduct productToSave = new ShoppingCartProduct(product, selectedQty, itemType);
        productToSave.setEnteredPrice(price);
        final SavedItem savedItem = new SavedItem(orderId, productToSave);
        final ContentValues values = ProviderUtils.savedItemToContentValues(savedItem);

        for (final ShoppingCartProduct shoppingCartProduct : ShoppingCart.getCurrentShoppingCart().getProducts()) {
            if (shoppingCartProduct.getProduct().getNumber().equals(product.getNumber())) {

                updatedQty = true;

                shoppingCartProduct.setEnteredPrice(price);

                /* User added same item type */
                if (shoppingCartProduct.getItemType().equals(itemType)) {
                    GenericDialog.OnClickListener replaceClickListener = null;
                    if (selectedQty != shoppingCartProduct.getQuantity()) {
                        replaceClickListener = new GenericDialog.OnClickListener() {
                            @Override
                            public void onClick(@NotNull DialogFragment dialog) {
                                final ShoppingCartProduct productToSave2 = new ShoppingCartProduct(product, shoppingCartProduct.getQuantity() + selectedQty, itemType);
                                productToSave2.setEnteredPrice(price);
                                final SavedItem savedItem2 = new SavedItem(orderId, productToSave2);
                                final ContentValues values = ProviderUtils.savedItemToContentValues(savedItem2);

                                shoppingCartProduct.setQuantity(selectedQty);
                                shoppingCartProduct.setItemType(itemType);
                                shoppingCartProduct.setEnteredPrice(price);

                                context.getContentResolver().update(Contract.SavedItem.CONTENT_URI, values, Contract.SavedItemColumns.COLUMN_ORDER_ID + " = ?" + " AND " + Contract.SavedItemColumns.COLUMN_PRODUCT_NUMBER + " = ?", new String[]{orderId, product.getNumber()});
                                Toast.makeText(context, "Added " + selectedQty + " " + product.getBrand() + " " + product.getDescription() + " to your shopping cart.", Toast.LENGTH_SHORT).show();
                                if (onProductUpdatedListener != null) {
                                    onProductUpdatedListener.onUpdated(shoppingCartProduct.getQuantity(), itemType);
                                }
                            }
                        };
                    }

                    incrementProductInShoppingCart(TAG, context, shoppingCartProduct, selectedQty,
                            //Increment click listener
                            new GenericDialog.OnClickListener() {
                                @Override
                                public void onClick(@NotNull DialogFragment dialog) {
                                    final ShoppingCartProduct productToSave2 = new ShoppingCartProduct(product, shoppingCartProduct.getQuantity() + selectedQty, itemType);
                                    productToSave2.setEnteredPrice(price);
                                    final SavedItem savedItem2 = new SavedItem(orderId, productToSave2);
                                    final ContentValues values = ProviderUtils.savedItemToContentValues(savedItem2);

                                    shoppingCartProduct.setQuantity(shoppingCartProduct.getQuantity() + selectedQty);
                                    shoppingCartProduct.setItemType(itemType);
                                    shoppingCartProduct.setEnteredPrice(price);

                                    context.getContentResolver().update(Contract.SavedItem.CONTENT_URI, values, Contract.SavedItemColumns.COLUMN_ORDER_ID + " = ?" + " AND " + Contract.SavedItemColumns.COLUMN_PRODUCT_NUMBER + " = ?", new String[]{orderId, product.getNumber()});
                                    Toast.makeText(context, "Added " + selectedQty + " " + product.getBrand() + " " + product.getDescription() + " to your shopping cart.", Toast.LENGTH_SHORT).show();
                                    if (onProductUpdatedListener != null) {
                                        onProductUpdatedListener.onUpdated(shoppingCartProduct.getQuantity(), itemType);
                                    }
                                }
                            }, replaceClickListener
                    );
                }

                /* User added different item type */
                else {
                    shoppingCartProduct.setEnteredPrice(price);
                    switchProductTypeInCart(TAG, context, shoppingCartProduct, selectedQty, itemType, new GenericDialog.OnClickListener() {
                        @Override
                        public void onClick(@NotNull DialogFragment dialog) {
                            shoppingCartProduct.setQuantity(selectedQty);
                            shoppingCartProduct.setItemType(itemType);
                            context.getContentResolver().update(Contract.SavedItem.CONTENT_URI, values, Contract.SavedItemColumns.COLUMN_ORDER_ID + " = ?" + " AND " + Contract.SavedItemColumns.COLUMN_PRODUCT_NUMBER + " = ?", new String[]{orderId, product.getNumber()});
                            Toast.makeText(context, "Added " + selectedQty + " " + product.getBrand() + " " + product.getDescription() + " to your shopping cart.", Toast.LENGTH_SHORT).show();
                            if (onProductUpdatedListener != null) {
                                onProductUpdatedListener.onUpdated(selectedQty, itemType);
                            }
                        }
                    });
                }
            }
        }

        if (!updatedQty) {
            final ShoppingCartProduct shoppingCartProduct = new ShoppingCartProduct(product, selectedQty, itemType);
            shoppingCartProduct.setEnteredPrice(price);
            ShoppingCart.getCurrentShoppingCart().addProduct(shoppingCartProduct, from);
            Toast.makeText(context, "Added " + selectedQty + " " + product.getBrand() + " " + product.getDescription() + " to your shopping cart.", Toast.LENGTH_SHORT).show();

            final Cursor cursor = context.getContentResolver().query(Contract.SavedOrder.CONTENT_URI, null, Contract.SavedOrderColumns.COLUMN_ID + " = ?", new String[]{savedItem.getOrderId()}, null);
            if (cursor.moveToFirst()) {
                final SavedOrder order = ProviderUtils.cursorToSavedOrder(cursor);

                if (order != null) {
                    final ContentValues contentValues = new ContentValues(1);
                    contentValues.put(Contract.SavedOrderColumns.COLUMN_NUM_PRODUCTS, order.getNumProducts() + 1);
                    contentValues.put(Contract.SavedOrderColumns.COLUMN_STORE, StoreManager.getCurrentStore().getBaseNumber());
                    context.getContentResolver().update(Contract.SavedOrder.CONTENT_URI, contentValues, Contract.SavedOrderColumns.COLUMN_ID + " = ?", new String[]{orderId});
                }
            }

            context.getContentResolver().insert(Contract.SavedItem.CONTENT_URI, values);
        }

    }

    public static FragmentManager getFragmentManager(Context context) {
        String append = "";
        if (context instanceof AppCompatActivity) {
            return ((AppCompatActivity) context).getSupportFragmentManager();
        } else if (context instanceof ContextThemeWrapper) {
            Context contextTheme = ((ContextThemeWrapper) context).getBaseContext();
            append = contextTheme.toString();
            if (contextTheme instanceof AppCompatActivity) {
                return ((AppCompatActivity) contextTheme).getSupportFragmentManager();
            }
        }
        throw new IllegalStateException("not able to cast context to get supportFragmentManager " + context.toString() + " " + append);
    }

    private static void switchProductTypeInCart(String TAG, Context context, ShoppingCartProduct shoppingCartProduct, int selectedQty, ItemType itemType, GenericDialog.OnClickListener clickListener) {
        final int currQty = shoppingCartProduct.getQuantity();
        final String currType = shoppingCartProduct.getItemType().name();

        GenericDialog.Companion.newInstance(
                context.getString(R.string.this_product_already_exist),
                "You currently have " + currQty + " " + currType + "S of this product in your shopping cart.\n\tAre you sure you want to REMOVE THESE " + currQty + " " + currType + "S AND REPLACE THEM WITH " + selectedQty + " " + itemType.name() + "S?",
                clickListener,
                null
        ).show(getFragmentManager(context), TAG);
    }

    private static void incrementProductInShoppingCart(String TAG, Context context, ShoppingCartProduct shoppingCartProduct, int newQty, GenericDialog.OnClickListener clickListener, GenericDialog.OnClickListener replaceClickListener) {

        final int currQty = shoppingCartProduct.getQuantity();
        final String itemTypeName = shoppingCartProduct.getItemType().name();

        String replaceButtonText = context.getString(R.string.cancel);
        if (replaceClickListener != null) {
            replaceButtonText = "REPLACE QTY\nNEW TOTAL: " + newQty;
        }

        GenericDialog.Companion.newInstance(
                context.getString(R.string.this_product_already_exist),
                "You currently have " + currQty + " " + itemTypeName + "S of this product in your shopping cart.\n\tWould you like to add " + newQty + " more " + itemTypeName + "S for a total of " + (newQty + currQty) + "?\n\tOr would you like to replace the " + currQty + " you have for a new total of " + newQty + "?",
                clickListener,
                "ADD QTY\nNEW TOTAL: " + (currQty + newQty),
                replaceClickListener,
                replaceButtonText
        ).show(getFragmentManager(context), TAG);
    }

    /**
     * Decodes a bitmap to the required size efficiently.
     *
     * @param file      Absolute file path
     * @param reqWidth  Width
     * @param reqHeight Height
     * @return Decoded Bitmap
     */
    public static Bitmap decodeSampledBitmapFromFile(String file, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;
        return BitmapFactory.decodeFile(file, options);
    }

    /**
     * Calculates inSample size for image loading.
     *
     * @param options   {@link BitmapFactory.Options}
     * @param reqWidth  int
     * @param reqHeight int
     * @return Sample size
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Gets the width and height of the screen. If null is passed, this method returns the previously
     * requested parameters.
     *
     * @param activity {@link Activity}
     * @return int [0] = width, int[1] = height
     */
    public static int[] getScreenDimensInPx(Activity activity) {
        if (activity != null) {
            Display display = activity.getWindowManager().getDefaultDisplay();

            Point size = new Point();
            display.getSize(size);

            int width = size.x;
            int height = size.y;

            screenDimens = new int[]{width, height};
        }

        return screenDimens;
    }

    /**
     * Performs a Toast with {@linkplain Toast#setDuration(int)} set to {@linkplain Toast#LENGTH_LONG}
     *
     * @param context {@linkplain Context}
     * @param message the message
     */
    public static void longToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Performs a Toast with {@linkplain Toast#setDuration(int)} set to {@linkplain Toast#LENGTH_SHORT}
     *
     * @param context {@linkplain Context}
     * @param message the message
     */
    public static void shortToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Forces the overflow icon to show regardless of device configuration.
     */
    public static void forceShowOverflowIcon(Context context) {
        try {
            ViewConfiguration config = ViewConfiguration.get(context);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Removes the soft keyboard from the screen.
     *
     * @param activity Activity displaying the soft keyboard.
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Searches for any app containing the word "calculator" in the package name and runs it.
     * Stolen (but modified) from SO. Works great!
     */
    public static void launchRegularCalculator(Context context) {
        final ArrayList<HashMap<String, Object>> items = new ArrayList<>();
        final PackageManager pm = context.getPackageManager();
        final List<PackageInfo> packs = pm.getInstalledPackages(0);
        for (PackageInfo pi : packs) {
            if (pi.packageName.toLowerCase().contains("calculator")) {
                final HashMap<String, Object> map = new HashMap<>();
                map.put("appName", pi.applicationInfo.loadLabel(pm));
                map.put("packageName", pi.packageName);
                items.add(map);
            }
        }

        boolean calculatorFound = false;
        if (items.size() > 0) {
            // it looks for custom bedessee calculator
            String packageFound = null;
            for (int i = 0; i < items.size(); i++) {
                String packageName = (String) items.get(i).get("packageName");
                if (packageName != null && packageName.contains("bedessee")) {
                    packageFound = packageName;
                }
            }

            if (packageFound == null) {
                packageFound = (String) items.get(0).get("packageName");
            }

            if (packageFound != null) {
                final Intent i = pm.getLaunchIntentForPackage(packageFound);
                if (i != null) {
                    context.startActivity(i);
                    calculatorFound = true;
                }
            }
        }

        if (!calculatorFound) {
            Toast.makeText(context, "No calculator app found!", Toast.LENGTH_LONG).show();
        }
    }

    public static void shareImage(final Context context, final String filePath) {
        final Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse(filePath));
        context.startActivity(Intent.createChooser(share, "Share Image"));
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile(String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    public static String emptyIfNull(final String string) {
        return TextUtils.isEmpty(string) ? "" : string;
    }

    public static String getVersionString(final Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public static String getSavedOrderId(final Context context, final String storeName, final Date date) {
        final String dateKey = DateFormat.getDateTimeInstance().format(date);
        final String salesmanEmailKey = SalesmanManager.getCurrentSalesman(context).getEmail().substring(0, 4);

        return storeName + "_" + salesmanEmailKey + dateKey;
    }

    /**
     * Checks if a date (long) is 7 days old or not
     *
     * @param when
     * @return
     */
    public static boolean is7DaysOld(long when) {
        final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
        return (int) ((new Date().getTime() - when) / DAY_IN_MILLIS) > 7;
    }

    public static void installAp(final Activity activity) {

        final SharedPrefsManager sharedPrefsManager = new SharedPrefsManager(activity);
        final String appPath = sharedPrefsManager.getSugarSyncDir() + sharedPrefsManager.getApkPath();
        final Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.setDataAndType(Uri.fromFile(new File(appPath)), "application/vnd.android.package-archive");
        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


//        final Uri packageURI = Uri.parse("package:" + activity.getApplication().getPackageName());
//        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);

        activity.startActivity(installIntent);
//        activity.startActivity(uninstallIntent);
    }

    public static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public interface OnProductUpdatedListener {
        void onUpdated(int qty, ItemType itemType);
    }

}
