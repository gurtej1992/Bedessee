package com.bedessee.salesca.backorder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bedessee.salesca.R;
import com.bedessee.salesca.customview.ItemType;
import com.bedessee.salesca.main.MainActivity;
import com.bedessee.salesca.product.ProductFragment;
import com.bedessee.salesca.sharedprefs.SharedPrefsManager;
import com.bedessee.salesca.shoppingcart.ShoppingCart;
import com.bedessee.salesca.shoppingcart.ShoppingCartDialog;
import com.bedessee.salesca.store.Store;
import com.bedessee.salesca.store.StoreManager;
import com.bedessee.salesca.utilities.ProductEnteredFrom;
import com.bedessee.salesca.utilities.Utilities;
import com.bedessee.salesca.utilities.ViewUtilities;
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

public class BackOrderActivity extends AppCompatActivity implements BackOrderQuantityChangedListener {
    private List<BackOrderQuantity> productsQuantities = new ArrayList<>();
    private BackOrderAdapter backOrderAdapter;

    public static File getBackOrderFile(Context context, Store store){
        String baseFilePath = new SharedPrefsManager(context).getSugarSyncDir();
        return new File(baseFilePath + "/past_bo/bo_" + store.getBaseNumber() + ".json");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtilities.Companion.setTheme(this, getWindow());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_order);
        SharedPreferences sh = getSharedPreferences("setting", Context.MODE_PRIVATE);
       String orient= sh.getString("orientation","landscape");
//        if(orient.equals("landscape")){
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        }else {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
        ViewUtilities.Companion.setActivityWindowSize(getWindow());

        final Store store = StoreManager.getCurrentStore();
        if (store == null) {
            Toast.makeText(getApplicationContext(), "Please select store", Toast.LENGTH_LONG).show();
            finish();
        } else {
            TextView title = findViewById(R.id.title_toolbar);
            findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            title.setText("Back orders of " + store.getName());

            final File file = getBackOrderFile(getApplicationContext(), store);

            if (file.exists()) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        String readFile = loadJSONFromFile(file);
                        ArrayList<OrderProduct> products = new Gson().fromJson(readFile, new TypeToken<List<OrderProduct>>() {
                        }.getType());
                        Timber.d("backorders:%s", products.size());
                        productsQuantities = new ArrayList<>();

                        for (OrderProduct order : products) {
                            int defaultQuantity = 0;
                            if (order.getDefaultQuantity() != null) {
                                defaultQuantity = Integer.parseInt(order.getDefaultQuantity().trim());
                            }
                            productsQuantities.add(new BackOrderQuantity(order, ItemType.CASE, defaultQuantity));
                        }
                        backOrderAdapter = new BackOrderAdapter(productsQuantities, getSupportFragmentManager());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                backOrderAdapter.setQuantityChangedListener(BackOrderActivity.this);
                                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                                View progress = findViewById(R.id.progress_bar);

                                recyclerView.setLayoutManager(new LinearLayoutManager(BackOrderActivity.this));
                                recyclerView.setAdapter(backOrderAdapter);
                                progress.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "No back orders found", Toast.LENGTH_LONG).show();
                finish();
            }

            TextView add_all = findViewById(R.id.add_all);
            add_all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShoppingCart shoppingCart = ShoppingCart.getCurrentShoppingCart();
                    shoppingCart.clearProducts();
                    View progress = findViewById(R.id.progress_bar);

                    progress.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(),"Adding " + productsQuantities.size() + " to the ShoppingCart", Toast.LENGTH_SHORT).show();
                    for (BackOrderQuantity order : productsQuantities) {
                        int quantity = order.getQuantity();
                        if (quantity == 0) continue;

                        Utilities.updateShoppingCart("inc",
                                "backorder",
                                v.getContext(), order.getProduct(), order.getQuantity(),
                                null, order.getType(), ProductEnteredFrom.BACK_ORDER, null);
                    }

                    progress.setVisibility(View.GONE);

                    Toast.makeText(getApplicationContext(),"Added "  + productsQuantities.size() + " products", Toast.LENGTH_SHORT).show();
                   //startActivityForResult(new Intent(BackOrderActivity.this, ShoppingCartDialog.class), ShoppingCartDialog.REQUEST_CODE);
                    finish();
                    Intent intent=new Intent(BackOrderActivity.this,MainActivity.class);
                    intent.putExtra("from","order");
                    startActivity(intent);


                }
            });
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
    public void onChanged(BackOrderQuantity backOrderQuantity) {
        for(BackOrderQuantity product:productsQuantities){
            if(backOrderQuantity.getProduct().getBrand().equals(product.getProduct().getBrand())) {
                product.setQuantity(product.getQuantity());
            }
        }
    }

    @Override
    public void onListChanged() {
        if (backOrderAdapter != null && backOrderAdapter.getItemCount() == 0) {
           finish();
        }
    }
}
