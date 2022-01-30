package com.bedessee.sales.shoppingcart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.SystemClock;

import com.bedessee.sales.orderhistory.SavedItem;
import com.bedessee.sales.provider.Contract;
import com.bedessee.sales.provider.ProviderUtils;
import com.bedessee.sales.sharedprefs.SharedPrefsManager;
import com.bedessee.sales.utilities.ProductEnteredFrom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Shopping Cart Manager.
 */
public class ShoppingCart implements Serializable {

    private static ShoppingCart sCurrentShoppingCart;
    private OnShoppingCartChanges listener;
    private String mComment;
    private String mContact;

    private long mMillisStart;
    private long mMillisFirstProduct;
    private String mSaleDuration;
    private String mPreOrderDuration;

    final private ArrayList<ShoppingCartProduct> mProducts = new ArrayList<>();
    final private ArrayList<ProductEnteredFrom> mFrom = new ArrayList<>();

    private static String sCurrentOrderId;

    public static ShoppingCart getCurrentShoppingCart() {
        if (sCurrentShoppingCart == null) {
            sCurrentShoppingCart = new ShoppingCart();
        }
        return sCurrentShoppingCart;
    }

    public static void setCurrentOrderId(final Context context, final String currentOrderId) {
        sCurrentOrderId = currentOrderId;

        SharedPrefsManager sharedPrefsManager = new SharedPrefsManager(context);
        sharedPrefsManager.setCurrentOrderId(currentOrderId);
    }

    public static String getCurrentOrderId(final Context context) {
        if(sCurrentOrderId == null) {
            SharedPrefsManager sharedPrefsManager = new SharedPrefsManager(context);
            sCurrentOrderId = sharedPrefsManager.getCurrentOrderId();
        }
        return sCurrentOrderId;
    }

    public void addProduct(ShoppingCartProduct product, ProductEnteredFrom from) {
        mProducts.add(product);
        mFrom.add(from);
        Collections.sort(mProducts);
        startFirstProductTimer();
        productChanged();
    }

    public ArrayList<ShoppingCartProduct> getProducts() {
        return mProducts;
    }

    public Integer getTotalItems() {
        return getProducts().size();
    }

    public void productChanged(){
        if (listener != null) {
            listener.onChanged();
        }
    }

    public void clearProducts() {
        mProducts.clear();
        productChanged();
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public void clearComment() {
        mComment = null;
    }
    public void clearContact() {
        mContact = null;
    }

    public String getContact() {
        return mContact;
    }

    public void setContact(String contact) {
        mContact = contact;
    }

    private void startFirstProductTimer() {
        if (mMillisFirstProduct == 0) {
            mMillisFirstProduct = SystemClock.uptimeMillis();
        }
    }

    public void startTimer() {
        mMillisFirstProduct = 0;
        mMillisStart = SystemClock.uptimeMillis();
    }

    public void stopTimer(){
        mPreOrderDuration = formatDelta(mMillisFirstProduct);
        mSaleDuration = formatDelta(mMillisStart);
    }

    @SuppressLint("DefaultLocale")
    private String formatDelta(Long start) {
        long delta = SystemClock.uptimeMillis() - start;
        return delta + " seconds";
    }

    public String getSaleDuration() {
        return mSaleDuration;
    }

    public String getPreSaleDuration() {
        return mPreOrderDuration;
    }

    public static ShoppingCart getSavedOrder(final Context context, final String savedOrderId) {

        final List<SavedItem> savedItems = new ArrayList<>();

        final Cursor cursor = context.getContentResolver().query(Contract.SavedItem.CONTENT_URI, null, Contract.SavedItemColumns.COLUMN_ORDER_ID + " = ?", new String[]{savedOrderId}, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                final SavedItem savedItem = ProviderUtils.cursorToSavedItem(context, cursor);
                savedItems.add(savedItem);
            }
            cursor.close();
        }

        final ShoppingCart shoppingCart = new ShoppingCart();

        if (!savedItems.isEmpty()) {
            for (final SavedItem savedItem : savedItems) {
                if (savedItem != null) {
                    shoppingCart.addProduct(savedItem.getShoppingCartProduct(), ProductEnteredFrom.PAST_ORDER);
                }
            }
        }

        return shoppingCart;
    }


    public static void setCurrentShoppingCart(final ShoppingCart shoppingCart) {
        sCurrentShoppingCart = shoppingCart;
    }

    public void clear() {
        clearComment();
        clearContact();
        clearProducts();
    }

    public boolean isEmpty() {
        return mProducts.isEmpty();
    }

    public ArrayList<ProductEnteredFrom> getFrom(){
        return mFrom;
    }

    public interface OnShoppingCartChanges {
        void onChanged();
    }

    public void setOnShoppingCartChanged(OnShoppingCartChanges changed) {
        listener = changed;
    }
}
