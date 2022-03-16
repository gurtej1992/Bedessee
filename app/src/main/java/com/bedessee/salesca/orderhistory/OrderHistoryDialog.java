package com.bedessee.salesca.orderhistory;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bedessee.salesca.R;
import com.bedessee.salesca.provider.Contract;
import com.bedessee.salesca.provider.ProviderUtils;
import com.bedessee.salesca.reportsmenu.ReportFragment;
import com.bedessee.salesca.store.StoreManager;
import com.bedessee.salesca.utilities.ViewUtilities;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity that shows a list of the user's saved orders
 */
interface OrderLoaderListener {
    void onLoaded(List<SavedOrder> orders);
}

public class OrderHistoryDialog extends Fragment {

    final public static int REQUEST_CODE = 3803;
    final public static int RESULT_CODE_LOAD = 2;
    private List<SavedOrder> orders = new ArrayList<>();
    private SwitchMaterial switchMaterial;
    private RecyclerView listView;
    private ProgressBar progressBar;
    public final static String TAG = "OrderList";
    private static OrderHistoryDialog instance;
    public static OrderHistoryDialog getInstance() {
        if (instance == null) {
            instance = new OrderHistoryDialog();
        }
        return instance;
    }

    class OrderListener implements OrderLoaderListener {
        @Override
        public void onLoaded(List<SavedOrder> orderList) {
            orders = orderList;

            if (orders.isEmpty()) {
                Toast.makeText(requireContext(), "There is no order history available", Toast.LENGTH_SHORT).show();
//                dismiss();
            } else {
                showAll();

                if (orders.size() == 1) {
                    switchMaterial.setVisibility(View.GONE);
                } else {
                    String number = StoreManager.getCurrentStore().getBaseNumber();
                    boolean currentStoreOrder = false;
                    boolean otherStore = false;

                    // let's check if it contains an order of current store
                    if (number != null) {
                        for(SavedOrder order: orders) {
                            if (order.getStore().equals(number)) {
                               currentStoreOrder = true;
                            } else {
                               otherStore = true;
                            }
                        }
                    }

                    if (currentStoreOrder && otherStore) {
                        switchMaterial.setVisibility(View.VISIBLE);
                    } else {
                        switchMaterial.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    private void showAll(){
        updateOrders(requireContext(), orders);
    }

    private void filterStore() {
        String store = StoreManager.getCurrentStore().getBaseNumber();
        ArrayList<SavedOrder> filtered = new ArrayList<>();
        for(SavedOrder order: orders) {
            if(order.getStore().equals(store)) {
                filtered.add(order);
            }
        }
        updateOrders(requireContext(), filtered);
    }

    private void updateOrders(Context context, List<SavedOrder> orders){
        listView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        listView.setAdapter(new OrderItemAdapter(context, new ArrayList<>(orders)));
        listView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_orderhistory, container, false);
        new LoadOrders(requireActivity().getContentResolver(), new OrderListener()).execute();

        switchMaterial = view.findViewById(R.id.switch_stores);
        if (StoreManager.getCurrentStore() == null) {
            switchMaterial.setVisibility(View.GONE);
        }
        listView = view.findViewById(R.id.list);
        progressBar = view.findViewById(R.id.progress_bar);
        switchMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showAll();
                } else {
                    filterStore();
                }
            }
        });

        view.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //dismiss();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
       // ViewUtilities.Companion.setActivityWindowSize(getDialog().getWindow());
    }

    private static class LoadOrders extends AsyncTask<Void, Void, List<SavedOrder>> {
        private OrderLoaderListener listener;
        private ContentResolver contentResolver;

        LoadOrders(ContentResolver contentResolver, OrderLoaderListener listener) {
            this.contentResolver = contentResolver;
            this.listener = listener;
        }

        @Override
        protected List<SavedOrder> doInBackground(Void... params) {

            final Cursor cursor = contentResolver.query(Contract.SavedOrder.CONTENT_URI, null, null, null, null);
            final ArrayList<SavedOrder> orders = new ArrayList<>();

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    final SavedOrder order = ProviderUtils.cursorToSavedOrder(cursor);
                    if (order != null) {
                        if (order.getNumProducts() > 0) {
                            orders.add(order);
                        }
                    }
                }
                cursor.close();
            }

            return orders;
        }


        @Override
        protected void onPostExecute(List<SavedOrder> orders) {
            super.onPostExecute(orders);
            listener.onLoaded(orders);
        }
    }
}
