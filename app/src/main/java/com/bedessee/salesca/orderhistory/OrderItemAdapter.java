package com.bedessee.salesca.orderhistory;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.bedessee.salesca.R;
import com.bedessee.salesca.customview.GenericDialog;
import com.bedessee.salesca.provider.Contract;
import com.bedessee.salesca.provider.ProviderUtils;
import com.bedessee.salesca.salesmanstore.SalesmanStore;
import com.bedessee.salesca.shoppingcart.ShoppingCart;
import com.bedessee.salesca.store.Store;
import com.bedessee.salesca.store.StoreManager;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;

import timber.log.Timber;

public class OrderItemAdapter extends BaseAdapter {
    public static final String TAG = "OrderItemAdapter";
    final private Context mContext;
    private ArrayList<SavedOrder> mSavedOrders;
    private Dialog parentDialog;

    OrderItemAdapter(Context context, ArrayList<SavedOrder> savedOrders, Dialog parentDialog) {
        mContext = context;
        mSavedOrders = savedOrders;
        Collections.sort(mSavedOrders, null);
        this.parentDialog = parentDialog;
    }

    @Override
    public int getCount() {
        return mSavedOrders.size();
    }


    @Override
    public SavedOrder getItem(int position) {
        return mSavedOrders.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final SavedOrder order = getItem(position);

        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_line_item, parent, false);

        if (order.getStartTime() != null) {
            ((TextView) convertView.findViewById(R.id.date)).setText(DateFormat.getDateTimeInstance().format(order.getStartTime()));
        }

        final String storeName = order.getId().split("_")[0];

        ((TextView) convertView.findViewById(R.id.customer)).setText(mContext.getString(R.string.order_item_title, storeName, order.getNumProducts()));

        convertView.findViewById(R.id.btn_delete_order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GenericDialog.Companion.newInstance(
                        "Order Deletion",
                        "Are you sure you want to delete this order\nfrom the order history archive?",
                        new GenericDialog.OnClickListener() {
                            @Override
                            public void onClick(@NotNull DialogFragment dialog) {
                                mContext.getContentResolver().delete(Contract.SavedOrder.CONTENT_URI, Contract.SavedOrderColumns.COLUMN_ID + " = ?", new String[]{order.getId()});
                                mSavedOrders.remove(order);
                                notifyDataSetChanged();

                                parentDialog.dismiss();
                            }
                        },"OK", null, "NO")
                .show(((AppCompatActivity) mContext).getSupportFragmentManager(), "df");
            }
        });

        convertView.findViewById(R.id.btn_load_order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.d("context is instance of AppCompatActivity: " + (mContext instanceof AppCompatActivity));
                Timber.d(mContext.toString());
                GenericDialog.Companion.switchStoreInstance(mContext, new GenericDialog.OnClickListener() {
                    @Override
                    public void onClick(@NotNull DialogFragment dialog) {
                        final ShoppingCart shoppingCart = ShoppingCart.getSavedOrder(mContext, order.getId());
                        ShoppingCart.setCurrentShoppingCart(shoppingCart);

                        final Cursor cursor = mContext.getContentResolver().query(Contract.SalesmanStore.CONTENT_URI, null, Contract.SalesmanStoreColumns.COLUMN_CUST_NAME + " = ?", new String[]{storeName}, null);

                        if (cursor != null && cursor.moveToFirst()) {
                            final SalesmanStore salesmanStore = ProviderUtils.cursorToSalesmanStore(cursor);
                            final Store store = salesmanStore.getStore();
                            StoreManager.setCurrentStore(mContext, store);
                            GenericDialog.Companion.outstandingDialogInstance(
                                    ((Activity) mContext), store.getOutstandingBalanceDue(),
                                    store.getLastCollectDate(),
                                    store.getStatementUrl(),
                                    null
                            ).show(((AppCompatActivity) mContext).getSupportFragmentManager(), TAG);
                            cursor.close();
                        }

                        parentDialog.dismiss();
                    }
                }).show(((AppCompatActivity) mContext).getSupportFragmentManager(), TAG);
            }
        });

        return convertView;
    }
}
