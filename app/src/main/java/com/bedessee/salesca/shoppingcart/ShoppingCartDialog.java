package com.bedessee.salesca.shoppingcart;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bedessee.salesca.R;
import com.bedessee.salesca.order.GMailUtils;
import com.bedessee.salesca.sharedprefs.SharedPrefsManager;
import com.bedessee.salesca.store.Store;
import com.bedessee.salesca.store.StoreManager;
import com.bedessee.salesca.store.WebViewer;
import com.bedessee.salesca.utilities.FileUtilities;
import com.bedessee.salesca.utilities.Utilities;
import com.bedessee.salesca.utilities.ViewUtilities;

import java.io.File;

/**
 * Dialog for shopping cart.
 */
public class ShoppingCartDialog extends AppCompatActivity implements View.OnClickListener {

    final public static int REQUEST_CODE = 200;
    final public static int RESULT_CODE_CONTINUED = 199;
    final public static int RESULT_CODE_CHECKED_OUT = 198;

    final public static String KEY_SHOPPING_CART = "shopping_cart_key";

    ListView shoppingCartListView;

    private ShoppingCart mShoppingCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtilities.Companion.setTheme(this, getWindow());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_shopping_cart);
        ViewUtilities.Companion.setActivityWindowSize(getWindow());

        final Bundle extras = getIntent().getExtras();

        if (extras != null && extras.containsKey(KEY_SHOPPING_CART)) {
            mShoppingCart = (ShoppingCart) extras.getSerializable(KEY_SHOPPING_CART);
        } else {
            mShoppingCart = ShoppingCart.getCurrentShoppingCart();
        }

        shoppingCartListView = findViewById(R.id.listView_shoppingCart);
        ShoppingCartAdapter shoppingCartAdapter = new ShoppingCartAdapter(this, R.layout.shopping_cart_list_item, R.id.textView_brand, mShoppingCart.getProducts());
        shoppingCartListView.setAdapter(shoppingCartAdapter);

        ((EditText) findViewById(R.id.edtComment)).setText(mShoppingCart.getComment());
        ((EditText) findViewById(R.id.edtContact)).setText(mShoppingCart.getContact());

        String storeName = StoreManager.getCurrentStore().getName();
        if (storeName != null) {
            ((TextView) findViewById(R.id.store_name)).setText(storeName);
        }

        findViewById(R.id.btnViewStatement).setOnClickListener(this);
        findViewById(R.id.btnSave).setOnClickListener(this);
        findViewById(R.id.btnCheckout).setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);

        updateTotal();
        updatePrice();

        mShoppingCart.setOnShoppingCartChanged(new ShoppingCart.OnShoppingCartChanges() {
            @Override
            public void onChanged() {
                updateTotal();
                updatePrice();
            }
        });
    }

    private void updateTotal() {
        ((TextView) findViewById(R.id.totalItems)).setText("Total items: " + mShoppingCart.getTotalItems());
    }

    private void updatePrice(){

        double sum=0;
        for(int i=0;i<mShoppingCart.getProducts().size();i++){
             sum=sum+(double)mShoppingCart.getProducts().get(i).getQuantity()*Double.parseDouble(mShoppingCart.getProducts().get(i).getProduct().getCasePrice());
        }
        double total_price = Math.round(sum * 100.0) / 100.0;
        ((TextView) findViewById(R.id.order_total_price)).setText("$ " + total_price);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnViewStatement:
                Store store = StoreManager.getCurrentStore();
                 File file = FileUtilities.Companion.getFile(getApplicationContext(),store.getBaseNumber(), "PDF",
                        "r545");

                if (file.exists()) {
                    FileUtilities.Companion.openPDF(getApplicationContext(), file);
                }
//                String path = new SharedPrefsManager(ShoppingCartDialog.this).getSugarSyncDir();
//                Store store = StoreManager.getCurrentStore();
//                WebViewer.Companion.show(ShoppingCartDialog.this, path + "/custstmt/" + store.getStatementUrl());
                break;

            case R.id.btn_close:
                setResult(RESULT_CODE_CONTINUED);
                finish();
                break;

            case R.id.btnSave:
                saveCommentAndContact();
                setResult(RESULT_CODE_CONTINUED);
                finish();
                break;

            case R.id.btnCheckout:

                if (mShoppingCart.isEmpty()) {
                    Utilities.shortToast(ShoppingCartDialog.this, "Please add a product before checking out");
                } else {
                    saveCommentAndContact();
                    ShoppingCart shoppingCart = mShoppingCart;
                    shoppingCart.stopTimer();
                    setResult(RESULT_CODE_CHECKED_OUT);
                    GMailUtils.sendShoppingCart(this, shoppingCart);

                    mShoppingCart.clearProducts();
                    mShoppingCart.clearComment();
                    mShoppingCart.clearContact();

                    StoreManager.clearCurrentStore();
                    StoreManager.setCurrentStore(this,null);

                    finish();
                }
                break;
            default:
                break;
        }
    }

    /**
     * Saves the comment and the contact that the user typed in.
     */
    private void saveCommentAndContact() {
        mShoppingCart.setComment((((EditText) findViewById(R.id.edtComment)).getText().toString()));
        mShoppingCart.setContact((((EditText) findViewById(R.id.edtContact)).getText().toString()));
    }
}