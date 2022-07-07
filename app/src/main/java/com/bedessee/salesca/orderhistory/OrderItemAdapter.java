package com.bedessee.salesca.orderhistory;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bedessee.salesca.R;
import com.bedessee.salesca.customview.GenericDialog;
import com.bedessee.salesca.main.MainActivity;
import com.bedessee.salesca.provider.Contract;
import com.bedessee.salesca.provider.ProviderUtils;
import com.bedessee.salesca.salesmanstore.SalesmanStore;
import com.bedessee.salesca.sharedprefs.SharedPrefsManager;
import com.bedessee.salesca.shoppingcart.ShoppingCart;
import com.bedessee.salesca.shoppingcart.ShoppingCartProduct;
import com.bedessee.salesca.store.Store;
import com.bedessee.salesca.store.StoreManager;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;

import timber.log.Timber;

public abstract class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {
    public static final String TAG = "OrderItemAdapter";
    final private Context mContext;
    private ArrayList<SavedOrder> mSavedOrders;
    //private Dialog parentDialog;

    OrderItemAdapter(Context context, ArrayList<SavedOrder> savedOrders) {
        mContext = context;
        mSavedOrders = savedOrders;
        Collections.sort(mSavedOrders, null);
        //this.parentDialog = parentDialog;
    }


    @NonNull
    @Override
    public OrderItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_line_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemAdapter.ViewHolder holder, int position) {
        final SavedOrder order = mSavedOrders.get(position);
        String baseFilePath = new SharedPrefsManager(mContext).getSugarSyncDir();
        String parentDirectory = new File(baseFilePath).getParent();
        if (order.getStartTime() != null) {
           holder.date.setText(DateFormat.getDateTimeInstance().format(order.getStartTime()));
        }

        final String storeName = order.getId().split("_")[0];
     //   Log.e("@#@","get store"+storeName.split("-")[1]);

        holder.customer.setText(mContext.getString(R.string.order_item_title, storeName, order.getNumProducts()));

       holder.btn_delete_order.setOnClickListener(new View.OnClickListener() {
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
                                File file = new File(parentDirectory + "/orderhistory/os_" + storeName+ ".json");
                                file.delete();
                                mContext.getContentResolver().delete(Contract.SavedItem.CONTENT_URI, Contract.SavedItemColumns.COLUMN_ORDER_ID + " = ?", new String[]{order.getId()});
                                Cursor cursor = mContext.getContentResolver().query(Contract.SavedOrder.CONTENT_URI, null, Contract.SavedOrderColumns.COLUMN_ID + " = ?", new String[]{ShoppingCart.getCurrentOrderId(mContext)}, null, null);
                                if (cursor.moveToFirst()) {
                              cursor.close();
                                }else {
//                                    final Date date = new Date();
//                                    final String savedOrderId = Utilities.getSavedOrderId(mContext, StoreManager.getCurrentStore().getName(), date);
//                                    final SavedOrder savedOrder = new SavedOrder(ShoppingCart.getCurrentOrderId(mContext), StoreManager.getCurrentStore().getBaseNumber(), date, null, false, 0);
//                                    final ContentValues values = ProviderUtils.savedOrderToContentValues(savedOrder);
//
//                                    mContext.getContentResolver().insert(Contract.SavedOrder.CONTENT_URI, values);
//                                    ShoppingCart.setCurrentOrderId(mContext, ShoppingCart.getCurrentOrderId(mContext));
                                    final ContentValues contentValues = new ContentValues(1);
                                    contentValues.put(Contract.SavedOrderColumns.COLUMN_NUM_PRODUCTS,0);
                                    contentValues.put(Contract.SavedOrderColumns.COLUMN_STORE, StoreManager.getCurrentStore().getBaseNumber());
                                    mContext.getContentResolver().update(Contract.SavedOrder.CONTENT_URI, contentValues, Contract.SavedOrderColumns.COLUMN_ID + " = ?", new String[]{ShoppingCart.getCurrentOrderId(mContext)});
                                    Log.e("!!!!", "get current id" + ShoppingCart.getCurrentOrderId(mContext));
                                }
                                updateData();

                                    // parentDialog.dismiss()
                            }
                        },"OK", null, "NO")
                        .show(((AppCompatActivity) mContext).getSupportFragmentManager(), "df");

            }

        });

        holder.btn_load_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //crate file for order_history

                //end of create new file
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
//                            final Date date = new Date();
//                            final String savedOrderId = Utilities.getSavedOrderId(mContext, storeName, date);

                            ShoppingCart.setCurrentOrderId(mContext, order.getId());
                            if (mContext instanceof MainActivity) {
                                ((MainActivity)mContext).toolbarSubtitle.setText(storeName);
                            }
                            GenericDialog.Companion.outstandingDialogInstance(
                                    ((Activity) mContext), store.getOutstandingBalanceDue(),
                                    store.getLastCollectDate(),
                                    store.getStatementUrl(),
                                    null
                            ).show(((AppCompatActivity) mContext).getSupportFragmentManager(), TAG);
                            cursor.close();
                        }

                        // parentDialog.dismiss();
                    }
                }).show(((AppCompatActivity) mContext).getSupportFragmentManager(), TAG);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mSavedOrders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date,customer;
        Button btn_delete_order,btn_load_order;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date =(TextView)itemView.findViewById(R.id.date);
            customer =(TextView)itemView.findViewById(R.id.customer);
            btn_delete_order =(Button) itemView.findViewById(R.id.btn_delete_order);
            btn_load_order =(Button) itemView.findViewById(R.id.btn_load_order);

        }
    }
    public abstract void updateData();
}
