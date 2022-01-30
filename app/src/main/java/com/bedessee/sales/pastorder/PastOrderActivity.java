package com.bedessee.sales.pastorder;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bedessee.sales.R;
import com.bedessee.sales.backorder.OrderProduct;
import com.bedessee.sales.customview.ItemType;
import com.bedessee.sales.order.PastOrderQuantity;
import com.bedessee.sales.product.Product;
import com.bedessee.sales.sharedprefs.SharedPrefsManager;
import com.bedessee.sales.store.Store;
import com.bedessee.sales.store.StoreManager;
import com.bedessee.sales.utilities.ViewUtilities;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class PastOrderActivity extends AppCompatActivity implements QuantityChangedListener {
    private List<PastOrderQuantity> orders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtilities.Companion.setTheme(this, getWindow());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_order);
        ViewUtilities.Companion.setActivityWindowSize(getWindow());
        findViewById(R.id.add_all).setVisibility(View.GONE);

        final Store store = StoreManager.getCurrentStore();
        if (store == null) {
            Toast.makeText(getApplicationContext(), "Please select store", Toast.LENGTH_LONG).show();
            finish();
        } else {
            TextView title_past_orders = findViewById(R.id.title_toolbar);
            findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            title_past_orders.setText("Past orders of " + store.getName());
            String baseFilePath = new SharedPrefsManager(getApplicationContext()).getSugarSyncDir();
            final File file = new File(baseFilePath + "/past/ps_" + store.getBaseNumber() + ".json");
            Timber.d("file path:%s", file.getAbsolutePath());

            if (file.exists()) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        String readFile = loadJSONFromFile(file);
                        final List<OrderProduct> pastProducts = new Gson().fromJson(readFile, new TypeToken<List<OrderProduct>>() {
                        }.getType());
                        Timber.d("pastorders:%s", pastProducts.size());
                        List<PastOrderQuantity> quantities = new ArrayList<>();
                        for (OrderProduct order : pastProducts) {
                            quantities.add(new PastOrderQuantity(order, ItemType.NONE, 0));
                        }
                        //let's remove the last item
                        quantities.remove(quantities.size() - 1);
                        final PastOrderAdapter adapter = new PastOrderAdapter(quantities, getSupportFragmentManager());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.setQuantityChangedListener(PastOrderActivity.this);
                                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                                View progress = findViewById(R.id.progress_bar);

                                recyclerView.setLayoutManager(new LinearLayoutManager(PastOrderActivity.this));
                                recyclerView.setAdapter(adapter);
                                progress.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "No past orders found", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    public String loadJSONFromFile(File fileName) {
        String json;
        try {
            InputStream is = new FileInputStream(fileName);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, StandardCharsets.UTF_8);


        } catch (IOException ex) {
            Timber.e(ex);
            return null;
        }
        return json;

    }

    @Override
    public void onChanged(PastOrderQuantity pastOrderQuantity) {
        //TODO: check for existing and update.
        orders.add(pastOrderQuantity);
    }
}
