package com.bedessee.salesca.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.bedessee.salesca.R;
import com.bedessee.salesca.backorder.BackOrderActivity;
import com.bedessee.salesca.customview.DialogNumberPad;
import com.bedessee.salesca.customview.GenericDialog;
import com.bedessee.salesca.mixpanel.MixPanelManager;
import com.bedessee.salesca.orderhistory.OrderHistoryDialog;
import com.bedessee.salesca.orderhistory.SavedOrder;
import com.bedessee.salesca.pastorder.PastOrderActivity;
import com.bedessee.salesca.product.Product;
import com.bedessee.salesca.product.ProductFragment;
import com.bedessee.salesca.product.SpecialProductDialog;
import com.bedessee.salesca.product.brand.BrandFragment;
import com.bedessee.salesca.product.category.CategoryFragment;
import com.bedessee.salesca.provider.BedesseeDatabase;
import com.bedessee.salesca.provider.Contract;
import com.bedessee.salesca.provider.ProviderUtils;
import com.bedessee.salesca.reportsmenu.ReportsMenu;
import com.bedessee.salesca.reportsmenu.ReportsMenuAdapter;
import com.bedessee.salesca.salesman.Salesman;
import com.bedessee.salesca.salesman.SalesmanManager;
import com.bedessee.salesca.salesmanstore.SalesmanStore;
import com.bedessee.salesca.sellsheets.SellSheetsDialog;
import com.bedessee.salesca.sharedprefs.SharedPrefsManager;
import com.bedessee.salesca.shoppingcart.ShoppingCart;
import com.bedessee.salesca.shoppingcart.ShoppingCartDialog;
import com.bedessee.salesca.store.NewStoreDialog;
import com.bedessee.salesca.store.Store;
import com.bedessee.salesca.store.StoreManager;
import com.bedessee.salesca.store.StoreSelector;
import com.bedessee.salesca.store.WebViewer;
import com.bedessee.salesca.update.UpdateActivity;
import com.bedessee.salesca.utilities.ReportsUtilities;
import com.bedessee.salesca.utilities.Utilities;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import timber.log.Timber;

import static com.bedessee.salesca.backorder.BackOrderActivity.getBackOrderFile;

/**
 * Main Activity for application.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public MainMenuAdapter mainMenuAdapter;
    public Menu mMenu;
    private FragmentManager mFragmentManager;
    private static boolean reportsSpinnerInit;
    private static boolean reportsOpen;
    private boolean mShowBalanceDialog;
    private Salesman mSalesman;
    private Spinner reportsSpinner;
    private TextView toolbarTitle;
    private TextView toolbarSubtitle;
    private Store mCurrentStore;
    private Boolean isFirstTime = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarSubtitle = findViewById(R.id.toolbar_subtitle);

        // This wil mimic the old ActionBar app icon.
        try {
            String packageName = getApplicationContext().getPackageName();
            Drawable icon = getPackageManager().getApplicationIcon(packageName);
            ((ImageView) findViewById(R.id.iconToolbar)).setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e);
        }

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mFragmentManager = getSupportFragmentManager();

        deleteExpiredSavedOrders();
        checkForExistingSavedOrder();
        Utilities.getScreenDimensInPx(this);
        prepareData();

        // Clear the store if the app is not updated
        SharedPrefsManager sharedPrefs = new SharedPrefsManager(getApplicationContext());
        if (!sharedPrefs.isDailyUpdated()) {
            Timber.d("clearing the store because isDailyUpdated needed");
            StoreManager.clearCurrentStore();
            mCurrentStore = null;
        }
    }

    private void prepareData() {
        boolean init = initSideMenu();
        initReportsMenu();
        setCurrentSalesman();
        Utilities.forceShowOverflowIcon(this);

        findViewById(R.id.btn_sell_sheets).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixPanelManager.trackButtonClick(MainActivity.this, "Button click: Sell sheets");
                startActivityForResult(new Intent(MainActivity.this, SellSheetsDialog.class), SellSheetsDialog.RESULT_CODE);
            }
        });

        if (!init) {
            switchFragment(new CategoryFragment(), CategoryFragment.TAG);
        }
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        final Store store = StoreManager.getCurrentStore();
        if (store == null || mCurrentStore == null) {
            loadSalesMan();
        } if (store != null && mCurrentStore != null &&
                !mCurrentStore.getBaseNumber().equals(store.getBaseNumber())) {
            loadSalesMan();
        }
        Timber.d("MainActivity onPostResume");
    }

    private void loadSalesMan(){
        mSalesman = SalesmanManager.getCurrentSalesman(this);
        displayStoreInActionBar();
        clearReportMenu();
    }

    @Override
    public void onBackPressed() {
        /* Check is back stack > 1 b/c first transaction just loads the landing page. If the
         * back stack is NOT > 1 (i.e. we're at the landing page), the app should never close
         * by the user clicking the back button, as per client's request.*/
        if (mFragmentManager.getBackStackEntryCount() > 1) {
            mFragmentManager.popBackStack();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {

            case ShoppingCartDialog.RESULT_CODE_CONTINUED: {
                //Do nothing...
            }
            break;

            case ShoppingCartDialog.RESULT_CODE_CHECKED_OUT: {
                ShoppingCart.setCurrentOrderId(MainActivity.this, null);
                NewStoreDialog.getInstance().clearAll();
            }
            break;

            case SellSheetsDialog.RESULT_CODE:
                break;
        }

        switch (requestCode) {
            case UpdateActivity.REQUEST_CODE: {
                // unselect store after update
                StoreManager.clearCurrentStore();
                prepareData();
                //this will prevent the reports to show up
                isFirstTime = true;
            }
            break;

            case OrderHistoryDialog.REQUEST_CODE: {
                if (resultCode == OrderHistoryDialog.RESULT_CODE_LOAD) {
                    mShowBalanceDialog = true;
                }
            }
            break;

            case WebViewer.REQUEST_CODE: {
                ((Spinner) findViewById(R.id.spinner_reports)).setSelection(0);
                reportsOpen = false;
            }
            break;

            case StoreSelector.REQUEST_CODE: {
                if (resultCode == StoreSelector.RESULT_CODE_JUST_LOOKING) {
                    mShowBalanceDialog = false;
                } else {
                    if (resultCode == Activity.RESULT_OK) {
                        final ProductFragment productFragment = ProductFragment.getInstance();
                        productFragment.setFilter("NP");
                        switchFragment(productFragment, ProductFragment.TAG);
                    }
                }
            }
            break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        mMenu = menu;

        /* Init actionbar buttons */
        View.OnClickListener actionBarButtonsClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.select_store:
                        mShowBalanceDialog = true;
                        MixPanelManager.trackButtonClick(MainActivity.this, "Button click: Top menu: Select Store");
                        StoreSelector.open(MainActivity.this);
                    break;
                    case R.id.shopping_cart:
                        if (StoreManager.isStoreSelected()) {
                            MixPanelManager.trackButtonClick(MainActivity.this, "Button click: Top menu: Shopping Cart");
                            startActivityForResult(new Intent(MainActivity.this, ShoppingCartDialog.class), ShoppingCartDialog.REQUEST_CODE);
                        } else {
                            MixPanelManager.trackButtonClick(MainActivity.this, "Button click: Top menu: Select Store -NO STORE SELECTED");
                            Toast.makeText(MainActivity.this, "Please select store to continue.", Toast.LENGTH_SHORT).show();
                        }
                    break;
                }
            }
        };

        /* Store select launcher */
        final Button selectStoreButton = (Button) mMenu.findItem(R.id.select_store).getActionView();
        selectStoreButton.setText("Select Store");
        selectStoreButton.setOnClickListener(actionBarButtonsClickListener);

        /* Shopping cart launcher */
        final Button shoppingCartButton = (Button) mMenu.findItem(R.id.shopping_cart).getActionView();
        shoppingCartButton.setText("Shopping Cart");
        shoppingCartButton.setOnClickListener(actionBarButtonsClickListener);

        /* Set version # */
        mMenu.findItem(R.id.version).setTitle("V " + Utilities.getVersionString(MainActivity.this));

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final SharedPrefsManager sharedPrefsManager = new SharedPrefsManager(MainActivity.this);

        switch (item.getItemId()) {

            case R.id.daily_update:
                MixPanelManager.trackButtonClick(MainActivity.this, "Button click: Top menu: DAILY UPDATE");
                startActivityForResult(UpdateActivity.newIntent(this), UpdateActivity.REQUEST_CODE);
                return true;

            case R.id.past_order:
                final Intent pastSalesIntent = new Intent(MainActivity.this, PastOrderActivity.class);
                startActivity(pastSalesIntent);
                return true;
            case R.id.back_order:
                openBackOrderActivity();
                return true;

            case R.id.exit:
                MixPanelManager.trackButtonClick(MainActivity.this, "Button click: Top menu: EXIT APP");
                finish();
                return true;

            case R.id.version:
                MixPanelManager.trackButtonClick(MainActivity.this, "Button click: Top menu: APP VERSION");
                final SharedPrefsManager sharedPref = new SharedPrefsManager(this);
                final boolean isNewMatchLogic = sharedPref.getUseNewLikeLogic().equals("YES");
                final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setMessage("Data folder:\n\n" + sharedPrefsManager.getSugarSyncDir() + "\n\n" + "LAST DAILY UPDATE: " + sharedPref.getLastDailyUpdate()
                        + "\n\n" + "DEVICE DATE: " + sharedPref.getDeviceDate()
                        + "\n\n" + "FORCE DAILY UPDATE: " + sharedPref.getForceDailyUpdate() + "\n\n" + "(LIKE MATCH " + (isNewMatchLogic ? "ON" : "OFF") + ")");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "CLOSE",
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                return true;

            case R.id.shopping_cart:
                    startActivityForResult(new Intent(MainActivity.this, ShoppingCartDialog.class), ShoppingCartDialog.REQUEST_CODE);
                return true;

            case R.id.force_crash:
                new AlertDialog.Builder(this).setTitle("Are you sure want to crash the app?")
                        .setMessage("This is for Testing, Force crash will crash the app.")
                        .setPositiveButton("YES",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        throw new RuntimeException("Test Crash"); // Force a crash
                                        // Perform Action & Dismiss dialog
                                       // dialog.dismiss();
                                    }
                                })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();


            default:
                Toast.makeText(getApplicationContext(),"This feature is still under-development!",Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
        }
    }

    private void openBackOrderActivity() {
        final Intent backOrderIntent = new Intent(MainActivity.this, BackOrderActivity.class);
        startActivity(backOrderIntent);
    }

    private void setCurrentSalesman() {
        final SharedPrefsManager sharedPrefsManager = new SharedPrefsManager(this);
        final String salesmanName = sharedPrefsManager.getLoggedInUser();

        if (salesmanName != null) {
            final Cursor cursor = getContentResolver().query(Contract.SalesmanStore.CONTENT_URI, null, Contract.SalesmanStoreColumns.COLUMN_SALESPERSON + " = ?", new String[]{salesmanName}, null);
            if (cursor != null && cursor.moveToFirst()) {
                final SalesmanStore salesmanStore = ProviderUtils.cursorToSalesmanStore(cursor);
                mSalesman = salesmanStore.getSalesman();
                SalesmanManager.setCurrentSalesman(this, mSalesman);
            }
        }
    }


    private boolean initSideMenu() {
        Timber.d("initSideMenu");
        final ListView listViewFilters = findViewById(R.id.listView_filters);
        final ArrayList<SideMenu> sideMenus = new ArrayList<>();
        sideMenus.add(new SideMenu("NO", "#000000", "0", "PRODUCTS", "0", ""));
        sideMenus.add(new SideMenu("NO", "#000000", "0", "BRANDS", "0", ""));
        sideMenus.add(new SideMenu("NO", "#000000", "0", "CATEGORIES", "0", ""));

        sideMenus.add(new SideMenu("NO", "#000000", "0", "UPC", "0", ""));
        sideMenus.add(new SideMenu("NO", "#000000", "0", "ORDER HISTORY", "0", ""));

        final Cursor cursorSideMenu = getContentResolver().query(Contract.SideMenu.CONTENT_URI, null, null, null, Contract.SideMenuColumns.COLUMN_SORT + " ASC");
        if (cursorSideMenu != null) {
            while (cursorSideMenu.moveToNext()) {
                sideMenus.add(ProviderUtils.cursorToSideMenu(cursorSideMenu));
            }
            cursorSideMenu.close();
        }

        Timber.d("initializing the menu adapter with #%s", sideMenus);

        mainMenuAdapter = new MainMenuAdapter(this, android.R.layout.simple_spinner_dropdown_item, sideMenus);
        listViewFilters.setAdapter(mainMenuAdapter);
        listViewFilters.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        for (SideMenu sideMenu : sideMenus) {
            if ("YES".equals(sideMenu.getOpenByDefault())) {
                selectItem(sideMenus.indexOf(sideMenu));
                return true;
            }
        }
        return false;
    }

    private void initReportsMenu() {
        final List<ReportsMenu> reportsMenus = new ArrayList<>();
        final List<String> reportsMenuTitles = new ArrayList<>();
        final Cursor reportsCursor = getContentResolver().query(Contract.ReportsMenu.CONTENT_URI, null, null, null, null);

        reportsMenus.add(null);
        reportsMenuTitles.add("Select Report");

        SpinnerAdapter spinnerAdapter = null;
        if (reportsCursor != null && reportsCursor.moveToFirst()) {
            for (int i = 0; i < reportsCursor.getCount(); i++) {
                final ReportsMenu sideMenu = ProviderUtils.cursorToReportsMenu(reportsCursor);
                reportsMenus.add(sideMenu);
                reportsCursor.moveToNext();
            }
            // WTF, first item is null
            reportsMenus.remove(0);
            // This will sort by natural order, the menus should come with number as first char.
            Collections.sort(reportsMenus, new Comparator<ReportsMenu>() {
                @Override
                public int compare(ReportsMenu o1, ReportsMenu o2) {
                    int o1Number = Character.getNumericValue(o1.getSideMenuDisplay().charAt(0));
                    int o2Number = Character.getNumericValue(o2.getSideMenuDisplay().charAt(0));
                    return o1Number - o2Number;
                }
            });
            List<String> menuList = new ArrayList<>();

            for (ReportsMenu menu : reportsMenus) {
                menuList.add(menu.getSideMenuDisplay());
            }
            reportsMenuTitles.addAll(menuList);
            spinnerAdapter = new ReportsMenuAdapter(this, reportsMenuTitles);
        }

        if (spinnerAdapter != null) {
            reportsSpinner = findViewById(R.id.spinner_reports);
            reportsSpinner.setAdapter(spinnerAdapter);
            reportsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (reportsSpinnerInit && !reportsOpen && position > 0) {
                        final Store store = StoreManager.getCurrentStore();
                        if (store != null && store.getStatementUrl() != null) {
                            reportsOpen = true;
                            ReportsMenu reportsMenu = reportsMenus.get(position - 1);
                            ReportsUtilities.Companion.openReportMenu(MainActivity.this, reportsMenu, store);
                            reportsOpen = false;
                        } else {
                            Utilities.shortToast(MainActivity.this, "Please select a store first");
                            clearReportMenu();
                        }
                    } else {
                        reportsSpinnerInit = true;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
    }

    public void clearReportMenu() {
        reportsOpen = false;
        if (reportsSpinner != null) {
            reportsSpinner.setSelection(0);
        }
    }

    private void deleteExpiredSavedOrders() {
        if (BedesseeDatabase.isDatabaseCreated(getApplicationContext())) {
            final Cursor cursor = getContentResolver().query(Contract.SavedOrder.CONTENT_URI, null, null, null, null);
            final ArrayList<SavedOrder> savedOrders = new ArrayList<>();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    final SavedOrder savedOrder = ProviderUtils.cursorToSavedOrder(cursor);
                    savedOrders.add(savedOrder);
                }
                cursor.close();
            }

            for (SavedOrder savedOrder : savedOrders) {
                if (Utilities.is7DaysOld(savedOrder.getStartTime().getTime())) {
                    getContentResolver().delete(Contract.SavedOrder.CONTENT_URI, Contract.SavedOrderColumns.COLUMN_ID + " = ?", new String[]{savedOrder.getId()});
                }
            }
        }
    }


    private void checkForExistingSavedOrder() {
        final String savedOrderId = ShoppingCart.getCurrentOrderId(MainActivity.this);

        if (!TextUtils.isEmpty(savedOrderId)) {

            final Cursor cursor = getContentResolver().query(Contract.SavedOrder.CONTENT_URI, null, Contract.SavedOrderColumns.COLUMN_ID + " = ?", new String[]{savedOrderId}, null);

            int numProducts = 0;

            if (cursor != null && cursor.moveToFirst()) {
                final SavedOrder savedOrder = ProviderUtils.cursorToSavedOrder(cursor);
                if (savedOrder != null) {
                    numProducts = savedOrder.getNumProducts();
                }
                cursor.close();
            }

            final String storeName = savedOrderId.split("_")[0];

            final ShoppingCart shoppingCart = ShoppingCart.getSavedOrder(MainActivity.this, savedOrderId);

            if (!shoppingCart.getProducts().isEmpty()) {
                GenericDialog.Companion.newInstance(getString(R.string.in_progress_detected, storeName, numProducts), getString(R.string.resume_order_message), new GenericDialog.OnClickListener() {
                    @Override
                    public void onClick(@NotNull DialogFragment dialog) {
                        /* Load saved order */
                        MixPanelManager.trackButtonClick(MainActivity.this, "Button click: Resume saved order? YES");

                        final Cursor cursor = getContentResolver().query(Contract.SalesmanStore.CONTENT_URI, null, Contract.SalesmanStoreColumns.COLUMN_CUST_NAME + " = ?", new String[]{storeName}, null);

                        if (cursor != null && cursor.moveToFirst()) {
                            final SalesmanStore salesmanStore = ProviderUtils.cursorToSalesmanStore(cursor);

                            final Store store = salesmanStore.getStore();

                            StoreManager.setCurrentStore(getApplicationContext(), store);

                            GenericDialog.Companion.outstandingDialogInstance(
                                    MainActivity.this, store.getOutstandingBalanceDue(),
                                    store.getLastCollectDate(),
                                    store.getStatementUrl(),
                                    null
                            ).show(getSupportFragmentManager(), TAG);

                            displayStoreInActionBar();
                            cursor.close();
                        }

                        ShoppingCart.setCurrentShoppingCart(shoppingCart);
                    }
                }, new GenericDialog.OnClickListener() {
                    @Override
                    public void onClick(@NotNull DialogFragment dialog) {
                        MixPanelManager.trackButtonClick(MainActivity.this, "Button click: Resume saved order? NO");
                        ShoppingCart.setCurrentOrderId(MainActivity.this, null);
                    }
                }).show(getSupportFragmentManager(), TAG);
            }
        }
    }


    /**
     * Loads the upc dialog with the specified upc code (#number)
     *
     * @param number upc code
     */
    private void loadUpcDialog(final String number) {
        final int loaderNumber = (int) Long.parseLong(number.replaceFirst("^0+(?!$)", ""));
        LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {

            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                Timber.e("onCreateLoader");
                return new CursorLoader(MainActivity.this, Contract.Product.CONTENT_URI, null, Contract.ProductColumns.COLUMN_UPC + " LIKE '%" + number + "%'", null, null);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                final List<Product> products = new ArrayList<>();
                while (data.moveToNext()) {

                    Product product = ProviderUtils.cursorToProduct(data);

                    if (number.length() < 7) {
                        final int subStringIndex = product.getUPC().length() - 7;
                        if (product.getUPC().substring(subStringIndex).contains(number)) {
                            products.add(product);
                        }
                    } else {
                        if (number.contains(product.getUPC())) {
                            products.add(product);
                        }
                    }
                }

                if (products.size() == 0) {
                    GenericDialog.Companion.newInstance(
                            getString(R.string.no_results_found_for_upc_search, number),
                            "",
                            null,
                            null).show(getSupportFragmentManager(), TAG);
                } else {
                    SpecialProductDialog.Companion.create(products, number).show(getSupportFragmentManager(), "TAG");
                }

                data.close();
                loader.deliverCancellation();
                getLoaderManager().destroyLoader(loaderNumber);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                Timber.e("onLoaderReset");
            }
        };
        getLoaderManager().initLoader(loaderNumber, null, loaderCallbacks);
    }


    private void displayStoreInActionBar() {

        Salesman loggedInUser = null;

        final SharedPrefsManager sharedPrefs = new SharedPrefsManager(this);

        final Cursor userCursor = getContentResolver().query(Contract.User.CONTENT_URI, null, Contract.UserColumns.COLUMN_NAME + " = ?", new String[]{sharedPrefs.getLoggedInUser()}, null);
        if (userCursor != null && userCursor.moveToFirst()) {
            loggedInUser = ProviderUtils.CursorToSalesman(userCursor);
            userCursor.close();
        }

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (loggedInUser != null) {
                toolbarTitle.setText(loggedInUser.getName());

                final boolean isStoreSelected = StoreManager.isStoreSelected();
                final Store store = StoreManager.getCurrentStore();
                mCurrentStore = store;
                if (isStoreSelected || store != null) {
                    proceedToDisplayStore(store);
                } else {
                    final Store savedStore = StoreManager.getLastStore(getApplicationContext());
                    if (savedStore != null) {
                        StoreManager.setCurrentStore(getApplicationContext(), savedStore);
                        mCurrentStore = savedStore;
                        proceedToDisplayStore(savedStore);
                    } else {
                        toolbarSubtitle.setText("NO STORE SELECTED");
                        if (mMenu != null) {
                            ((Button) mMenu.findItem(R.id.select_store).getActionView()).setText("Select Store");
                        }
                    }
                }
            }
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private void proceedToDisplayStore(Store store){
        toolbarSubtitle.setText(store.getName());
        if (isFirstTime) {
            isFirstTime = false;
            return;
        }

        // on new stores the info on store is empty

        if (store.isOpenAccountStatusPopUp()
                && mShowBalanceDialog
                && store.getLastCollectDate() != null) {

            GenericDialog.Companion.outstandingDialogInstance(
                    this, store.getOutstandingBalanceDue(),
                    store.getLastCollectDate(),
                    store.getStatementUrl(),
                    new GenericDialog.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            File file = getBackOrderFile(getApplicationContext(), store);

                            if (file.exists()) {
                                openBackOrderActivity();
                            }

                            if (store.isOpenDefaultReport()) {
                                ReportsUtilities.Companion.openFirstDefaultOpenReport(MainActivity.this, store);
                            }
                        }
                    }).show(getSupportFragmentManager(), TAG);
            mShowBalanceDialog = false;
            ((Button) mMenu.findItem(R.id.select_store).getActionView()).setText("Change Store");
        } else if (store.isOpenDefaultReport()) {
            ReportsUtilities.Companion.openFirstDefaultOpenReport(MainActivity.this, store);
        }
    }
    /**
     * Swaps fragments in the main content view. Temporarily switches all fragments to
     * FragmentBrandsList.
     *
     * @param position Position of list item.
     */
    private void selectItem(final int position) {
        switch (position) {
            case 0:
                MixPanelManager.trackButtonClick(MainActivity.this, "Button click: Left Menu: Products");
                switchFragment(ProductFragment.getInstance(), ProductFragment.TAG);
            break;
            case 1:
                MixPanelManager.trackButtonClick(MainActivity.this, "Button click: Left Menu: Brands");
                switchFragment(BrandFragment.getInstance(), BrandFragment.TAG);
            break;
            case 2:
                MixPanelManager.trackButtonClick(MainActivity.this, "Button click: Left Menu: Categories");
                switchFragment(CategoryFragment.getInstance(), CategoryFragment.TAG);
            break;
            case 3:
                upcClicked();
            break;
            case 4:
                MixPanelManager.trackButtonClick(MainActivity.this, "Button click: Top menu: ORDER HISTORY");
                DialogFragment dialog = new OrderHistoryDialog();
                FragmentManager fragmentManager = getSupportFragmentManager();
                dialog.show(fragmentManager, "sd");
                fragmentManager.executePendingTransactions();
                dialog.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Store store = StoreManager.getCurrentStore();
                        if (mCurrentStore!=null && store != null && store.getBaseNumber() != null && !(store.getBaseNumber().equals(mCurrentStore.getBaseNumber()))) {
                            displayStoreInActionBar();
                        }
                    }
                });
            default:
                final String status = mainMenuAdapter.getItem(position).getStatusCode();
                final ProductFragment productFragment = ProductFragment.getInstance();
                productFragment.setFilter(status);
                switchFragment(productFragment, ProductFragment.TAG);
            break;
        }
    }

    /**
     * Helper method to swap fragments into main content frame.
     *
     * @param fragment Fragment to switch to.
     * @param tag      Tag of fragment.
     */
    public void switchFragment(final androidx.fragment.app.Fragment fragment, final String tag) {
        mFragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment, tag)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
    }

    private void upcClicked() {
        MixPanelManager.trackButtonClick(MainActivity.this, "Button click: Top menu: UPC");
        DialogNumberPad.Companion.newInstance("UPC Search", new DialogNumberPad.OnStringSelectedListener() {
            @Override
            public void onSelected(@NotNull String value) {
                if (value.length() > 2) {
                    loadUpcDialog(value);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter minimum 3 digits", Toast.LENGTH_SHORT).show();
                }
            }
        }, "").show(getSupportFragmentManager(), TAG);
    }
}