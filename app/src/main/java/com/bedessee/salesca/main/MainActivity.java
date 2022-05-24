package com.bedessee.salesca.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bedessee.salesca.R;
import com.bedessee.salesca.backorder.BackOrderActivity;
import com.bedessee.salesca.customview.DialogNumberPad;
import com.bedessee.salesca.customview.GenericDialog;
import com.bedessee.salesca.login.Login;
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
import com.bedessee.salesca.reportsmenu.ReportFragment;
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
import com.bedessee.salesca.utilities.SpacesItemDecoration;
import com.bedessee.salesca.utilities.Utilities;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import timber.log.Timber;

import static com.bedessee.salesca.backorder.BackOrderActivity.getBackOrderFile;


public class MainActivity extends AppCompatActivity {
    DrawerLayout drawer;
    NavigationView navigationView;
    RecyclerView lst_menu_items;
    public MainDrawerAdapter mainMenuAdapter;
    private FragmentManager mFragmentManager;
    private Store mCurrentStore;
    private TextView toolbarTitle;
    private TextView toolbarSubtitle;
    private Boolean isFirstTime = true;
    private boolean mShowBalanceDialog;
    private Salesman mSalesman;
    private static boolean reportsOpen;
    private static boolean reportsSpinnerInit;
    ImageView drawericon,search,storeicon,menuicon,home_icon,cart_icon,report_icon,tool_icon;
    TextView home_txt,cart_txt,report_txt,tool_txt;
    LinearLayout home,cart,report,tools;
    String from="",orient;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        SharedPreferences sh = getSharedPreferences("setting", Context.MODE_PRIVATE);
         orient= sh.getString("orientation","landscape");
         if(orient.equals("landscape")){
             setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
         }else {
             setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
         }
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        lst_menu_items = findViewById(R.id.lst_menu_items);
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarSubtitle = findViewById(R.id.toolbar_subtitle);
        drawericon = findViewById(R.id.drawer);
        search = findViewById(R.id.search);
        storeicon = findViewById(R.id.select_store);
        menuicon = findViewById(R.id.popup_menu);
        home_icon = findViewById(R.id.home_icon);
        cart_icon = findViewById(R.id.cart_icon);
        report_icon = findViewById(R.id.report_icon);
        tool_icon = findViewById(R.id.tool_icon);
        home_txt = findViewById(R.id.home_txt);
        cart_txt = findViewById(R.id.cart_txt);
        report_txt = findViewById(R.id.report_txt);
        tool_txt = findViewById(R.id.tool_txt);
        home = findViewById(R.id.home);
        cart = findViewById(R.id.cart);
        report = findViewById(R.id.report);
        tools = findViewById(R.id.tools);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        );
        Intent i=getIntent();
        from=i.getStringExtra("from");

        drawericon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);

            }
        });

        if(from.equalsIgnoreCase("order")){
            home_icon.setImageResource(R.drawable.ic_homelight);
            cart_icon.setImageResource(R.drawable.ic_cart);
            report_icon.setImageResource(R.drawable.ic_documentlight);
            tool_icon.setImageResource(R.drawable.ic_toolslight);
            home_txt.setTextColor(getResources().getColor(R.color.divider));
            cart_txt.setTextColor(getResources().getColor(R.color.white));
            report_txt.setTextColor(getResources().getColor(R.color.divider));
            tool_txt.setTextColor(getResources().getColor(R.color.divider));
            if (StoreManager.isStoreSelected()) {
                MixPanelManager.trackButtonClick(MainActivity.this, "Button click: Top menu: Shopping Cart");
                //startActivityForResult(new Intent(MainActivity.this, ShoppingCartDialog.class), ShoppingCartDialog.REQUEST_CODE);
                switchFragment(new ShoppingCartDialog(), ShoppingCartDialog.TAG);

            } else {
                MixPanelManager.trackButtonClick(MainActivity.this, "Button click: Top menu: Select Store -NO STORE SELECTED");
                Toast.makeText(MainActivity.this, "Please select store to continue.", Toast.LENGTH_SHORT).show();
            }
        }

        home.setOnClickListener(v -> {
            home_icon.setImageResource(R.drawable.ic_home);
            cart_icon.setImageResource(R.drawable.ic_cartight);
            report_icon.setImageResource(R.drawable.ic_documentlight);
            tool_icon.setImageResource(R.drawable.ic_toolslight);
            home_txt.setTextColor(getResources().getColor(R.color.white));
            cart_txt.setTextColor(getResources().getColor(R.color.divider));
            report_txt.setTextColor(getResources().getColor(R.color.divider));
            tool_txt.setTextColor(getResources().getColor(R.color.divider));
            final ProductFragment productFragment = ProductFragment.getInstance();
            productFragment.setFilter("HM");
            switchFragment(productFragment, ProductFragment.TAG);
        });


        report.setOnClickListener(v -> {
            home_icon.setImageResource(R.drawable.ic_homelight);
            cart_icon.setImageResource(R.drawable.ic_cartight);
            report_icon.setImageResource(R.drawable.ic_document);
            tool_icon.setImageResource(R.drawable.ic_toolslight);
            home_txt.setTextColor(getResources().getColor(R.color.divider));
            cart_txt.setTextColor(getResources().getColor(R.color.divider));
            report_txt.setTextColor(getResources().getColor(R.color.white));
            tool_txt.setTextColor(getResources().getColor(R.color.divider));
            switchFragment(ReportFragment.getInstance(), ReportFragment.TAG);
        });



        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home_icon.setImageResource(R.drawable.ic_homelight);
                cart_icon.setImageResource(R.drawable.ic_cart);
                report_icon.setImageResource(R.drawable.ic_documentlight);
                tool_icon.setImageResource(R.drawable.ic_toolslight);
                home_txt.setTextColor(getResources().getColor(R.color.divider));
                cart_txt.setTextColor(getResources().getColor(R.color.white));
                report_txt.setTextColor(getResources().getColor(R.color.divider));
                tool_txt.setTextColor(getResources().getColor(R.color.divider));
                if (StoreManager.isStoreSelected()) {
                    MixPanelManager.trackButtonClick(MainActivity.this, "Button click: Top menu: Shopping Cart");
                    //startActivityForResult(new Intent(MainActivity.this, ShoppingCartDialog.class), ShoppingCartDialog.REQUEST_CODE);
                    switchFragment(new ShoppingCartDialog(), ShoppingCartDialog.TAG);

                } else {
                    MixPanelManager.trackButtonClick(MainActivity.this, "Button click: Top menu: Select Store -NO STORE SELECTED");
                    Toast.makeText(MainActivity.this, "Please select store to continue.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        tools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home_icon.setImageResource(R.drawable.ic_homelight);
                cart_icon.setImageResource(R.drawable.ic_cartight);
                report_icon.setImageResource(R.drawable.ic_documentlight);
                tool_icon.setImageResource(R.drawable.ic_tools);
                home_txt.setTextColor(getResources().getColor(R.color.divider));
                cart_txt.setTextColor(getResources().getColor(R.color.divider));
                report_txt.setTextColor(getResources().getColor(R.color.divider));
                tool_txt.setTextColor(getResources().getColor(R.color.white));
                switchFragment(new ToolFragment(), ToolFragment.TAG);
            }
        });
        storeicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShowBalanceDialog = true;
                MixPanelManager.trackButtonClick(MainActivity.this, "Button click: Top menu: Select Store");
                StoreSelector.open(MainActivity.this);
            }
        });
        menuicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, v);
                popup.getMenuInflater().inflate(R.menu.menu, popup.getMenu());
                popup.getMenu().findItem(R.id.version).setTitle("V " + Utilities.getVersionString(MainActivity.this));
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        final SharedPrefsManager sharedPrefsManager = new SharedPrefsManager(MainActivity.this);
                        switch (item.getItemId()) {
                            case R.id.daily_update:
                                MixPanelManager.trackButtonClick(MainActivity.this, "Button click: Top menu: DAILY UPDATE");
                                startActivityForResult(UpdateActivity.newIntent(MainActivity.this), UpdateActivity.REQUEST_CODE);
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
                                System.exit(0);
                                return true;

                            case R.id.version:
                                MixPanelManager.trackButtonClick(MainActivity.this, "Button click: Top menu: APP VERSION");
                                final SharedPrefsManager sharedPref = new SharedPrefsManager(MainActivity.this);
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
                                switchFragment(new ShoppingCartDialog(), ShoppingCartDialog.TAG);

                               // startActivityForResult(new Intent(MainActivity.this, ShoppingCartDialog.class), ShoppingCartDialog.REQUEST_CODE);
                                return true;

                           // case R.id.force_crash:
//                                new AlertDialog.Builder(MainActivity.this).setTitle("Are you sure want to crash the app?")
//                                        .setMessage("This is for Testing, Force crash will crash the app.")
//                                        .setPositiveButton("YES",
//                                                new DialogInterface.OnClickListener() {
//                                                    public void onClick(DialogInterface dialog, int which) {
//                                                        throw new RuntimeException("Test Crash"); // Force a crash
//                                                        // Perform Action & Dismiss dialog
//                                                        // dialog.dismiss();
//                                                    }
//                                                })
//                                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                // Do nothing
//                                                dialog.dismiss();
//                                            }
//                                        })
//                                        .create()
//                                        .show();
//                                return true;
                            case R.id.clear:
                                String directory = new SharedPrefsManager(getApplicationContext()).getSugarSyncDir();
                                File file = new File(BedesseeDatabase.getDatabaseFile(directory));
                                if(file.exists()){
                                    getApplicationContext().deleteDatabase(file.getAbsolutePath());
                                    signOut(true);
                                }
                                return true;
                            default:

                                return false;
                        }
                    }
                });

                popup.show();//showing popup menu
            }
        });


        String directory = new SharedPrefsManager(getApplicationContext()).getSugarSyncDir();
        if(directory != null) {
            File file = new File(BedesseeDatabase.getDatabaseFile(directory));
            if (!file.exists()) {
                MixPanelManager.trackButtonClick(this, "Button click: Top menu: DAILY UPDATE");
                startActivityForResult(UpdateActivity.newIntent(this), UpdateActivity.REQUEST_CODE);
            }
        }
        else{
            signOut(true);
        }


    }

    private void openBackOrderActivity() {
        final Intent backOrderIntent = new Intent(MainActivity.this, BackOrderActivity.class);
        startActivity(backOrderIntent);
    }



    private boolean initSideMenu() {
        Timber.d("initSideMenu");
        final ListView listViewFilters = findViewById(R.id.listView_filters);
        final List<SideMenu> sideMenus = new ArrayList<>();
        sideMenus.add(new SideMenu("NO", "#000000", "0", "PRODUCTS", "0", ""));
        sideMenus.add(new SideMenu("NO", "#000000", "0", "BRANDS", "0", ""));
        sideMenus.add(new SideMenu("NO", "#000000", "0", "CATEGORIES", "0", ""));

       // sideMenus.add(new SideMenu("NO", "#000000", "0", "UPC", "0", ""));
        sideMenus.add(new SideMenu("NO", "#000000", "0", "ORDER HISTORY", "0", ""));

        final Cursor cursorSideMenu = getContentResolver().query(Contract.SideMenu.CONTENT_URI, null, null, null, Contract.SideMenuColumns.COLUMN_SORT + " ASC");
        if (cursorSideMenu != null) {
            while (cursorSideMenu.moveToNext()) {
                sideMenus.add(ProviderUtils.cursorToSideMenu(cursorSideMenu));
            }
            cursorSideMenu.close();
        }



        Timber.d("initializing the menu adapter with #%s", sideMenus);

        mainMenuAdapter = new MainDrawerAdapter(this, sideMenus) {
            @Override
            protected void onClickView(int pos) {
                selectItem(pos);
            }
        };
        lst_menu_items.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        lst_menu_items.setAdapter(mainMenuAdapter);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.default_margin);
        lst_menu_items.addItemDecoration(new SpacesItemDecoration(spacingInPixels,false));

        for (SideMenu sideMenu : sideMenus) {
            if ("YES".equals(sideMenu.getOpenByDefault())) {
                selectItem(sideMenus.indexOf(sideMenu));
                return true;
            }
        }
        return false;
    }

    private void selectItem(final int position) {
        switch (position) {
            case 0:
                MixPanelManager.trackButtonClick(this, "Button click: Left Menu: Products");
                switchFragment(ProductFragment.getInstance(), ProductFragment.TAG);
                drawer.closeDrawer(GravityCompat.START);
                break;
            case 1:
                MixPanelManager.trackButtonClick(this, "Button click: Left Menu: Brands");
                switchFragment(BrandFragment.getInstance(), BrandFragment.TAG);
                drawer.closeDrawer(GravityCompat.START);
                break;
            case 2:
                MixPanelManager.trackButtonClick(this, "Button click: Left Menu: Categories");
                switchFragment(CategoryFragment.getInstance(), CategoryFragment.TAG);
                drawer.closeDrawer(GravityCompat.START);
                break;
//            case 3:
//                upcClicked();
//                drawer.closeDrawer(GravityCompat.START);
//                break;
            case 3:
                MixPanelManager.trackButtonClick(MainActivity.this, "Button click: Top menu: ORDER HISTORY");
                switchFragment(OrderHistoryDialog.getInstance(), OrderHistoryDialog.TAG);

                drawer.closeDrawer(GravityCompat.START);
                break;
            default:
                if(from.equalsIgnoreCase("order")){
                    home_icon.setImageResource(R.drawable.ic_homelight);
                    cart_icon.setImageResource(R.drawable.ic_cart);
                    report_icon.setImageResource(R.drawable.ic_documentlight);
                    tool_icon.setImageResource(R.drawable.ic_toolslight);
                    home_txt.setTextColor(getResources().getColor(R.color.divider));
                    cart_txt.setTextColor(getResources().getColor(R.color.white));
                    report_txt.setTextColor(getResources().getColor(R.color.divider));
                    tool_txt.setTextColor(getResources().getColor(R.color.divider));
                    if (StoreManager.isStoreSelected()) {
                        MixPanelManager.trackButtonClick(MainActivity.this, "Button click: Top menu: Shopping Cart");
                        //startActivityForResult(new Intent(MainActivity.this, ShoppingCartDialog.class), ShoppingCartDialog.REQUEST_CODE);
                        switchFragment(new ShoppingCartDialog(), ShoppingCartDialog.TAG);

                    } else {
                        MixPanelManager.trackButtonClick(MainActivity.this, "Button click: Top menu: Select Store -NO STORE SELECTED");
                        Toast.makeText(MainActivity.this, "Please select store to continue.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    home_icon.setImageResource(R.drawable.ic_home);
                    cart_icon.setImageResource(R.drawable.ic_cartight);
                    report_icon.setImageResource(R.drawable.ic_documentlight);
                    tool_icon.setImageResource(R.drawable.ic_toolslight);
                    home_txt.setTextColor(getResources().getColor(R.color.white));
                    cart_txt.setTextColor(getResources().getColor(R.color.divider));
                    report_txt.setTextColor(getResources().getColor(R.color.divider));
                    tool_txt.setTextColor(getResources().getColor(R.color.divider));
                    final String status = mainMenuAdapter.mMenuItems.get(position).getStatusCode();
                    final ProductFragment productFragment = ProductFragment.getInstance();
                    productFragment.setFilter(status);
                    switchFragment(productFragment, ProductFragment.TAG);
                }
                drawer.closeDrawer(GravityCompat.START);
                break;

        }
    }
    public void switchFragment(final androidx.fragment.app.Fragment fragment, final String tag) {
        mFragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment, tag)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
    }

    private void upcClicked() {
        MixPanelManager.trackButtonClick(this, "Button click: Top menu: UPC");
        DialogNumberPad.Companion.newInstance("UPC Search", new DialogNumberPad.OnStringSelectedListener() {
            @Override
            public void onSelected(@NotNull String value) {
                if (value.length() > 2) {
                    loadUpcDialog(value);
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter minimum 3 digits", Toast.LENGTH_SHORT).show();
                }
            }
        }, "").show(getSupportFragmentManager(), TAG);
    }

    private void displayStoreInActionBar() {

        Salesman loggedInUser = null;

        final SharedPrefsManager sharedPrefs = new SharedPrefsManager(this);

        final Cursor userCursor = getContentResolver().query(Contract.User.CONTENT_URI, null, Contract.UserColumns.COLUMN_NAME + " = ?", new String[]{sharedPrefs.getLoggedInUser()}, null);
        if (userCursor != null && userCursor.moveToFirst()) {
            loggedInUser = ProviderUtils.CursorToSalesman(userCursor);
            userCursor.close();
        }


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
//                        if (mMenu != null) {
//                            ((Button) mMenu.findItem(R.id.select_store).getActionView()).setText("Select Store");
//                        }
                    }
                }
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
                                // RESTRICTING TO OPEN PDF
                                // openBackOrderActivity();
                            }

                            if (store.isOpenDefaultReport()) {
                                //    ReportsUtilities.Companion.openFirstDefaultOpenReport(MainActivity.this, store);
                            }
                        }
                    }).show(getSupportFragmentManager(), TAG);
            mShowBalanceDialog = false;
//            ((Button) mMenu.findItem(R.id.select_store).getActionView()).setText("Change Store");
        } else if (store.isOpenDefaultReport()) {
            ReportsUtilities.Companion.openFirstDefaultOpenReport(this, store);
        }
    }


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

    public void signOut(Boolean launchLoginScreen){
        final SharedPrefsManager sharedPrefs = new SharedPrefsManager(this);
        sharedPrefs.removeLoggedInUser();
        if(launchLoginScreen){
            Intent intent=new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
        }
        finish();

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        String directory = new SharedPrefsManager(getApplicationContext()).getSugarSyncDir();
        if(directory != null){
            File file = new File(BedesseeDatabase.getDatabaseFile(directory));
            if(file.exists()) {
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
        }

    }

    private void prepareData() {
        String directory = new SharedPrefsManager(getApplicationContext()).getSugarSyncDir();
        if(directory != null) {
            File file = new File(BedesseeDatabase.getDatabaseFile(directory));
            if (file.exists()) {
                boolean init = initSideMenu();
                setCurrentSalesman();
                Utilities.forceShowOverflowIcon(this);

                findViewById(R.id.sell_sheet_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MixPanelManager.trackButtonClick(MainActivity.this
                                , "Button click: Sell sheets");
                        startActivityForResult(new Intent(MainActivity.this, SellSheetsDialog.class), SellSheetsDialog.RESULT_CODE);
                    }
                });

                if (!init) {
                    switchFragment(new CategoryFragment(), CategoryFragment.TAG);
                }
            }
        }
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
    @Override
    protected void onPostResume() {
        super.onPostResume();
        final Store store = StoreManager.getCurrentStore();
        String directory = new SharedPrefsManager(getApplicationContext()).getSugarSyncDir();
        if(directory != null) {
            File file = new File(BedesseeDatabase.getDatabaseFile(directory));
            if (file.exists()) {
                if (store == null || mCurrentStore == null) {
                    loadSalesMan();
                }
                if (store != null && mCurrentStore != null &&
                        !mCurrentStore.getBaseNumber().equals(store.getBaseNumber())) {
                    loadSalesMan();
                }
                Timber.d("MainActivity onPostResume");
            }
        }
    }

    private void loadSalesMan(){
        mSalesman = SalesmanManager.getCurrentSalesman(this);
        displayStoreInActionBar();

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
            case ShoppingCartDialog.REQUEST_CODE:{

            }

            case OrderHistoryDialog.REQUEST_CODE: {
                if (resultCode == OrderHistoryDialog.RESULT_CODE_LOAD) {
                    mShowBalanceDialog = true;
                    Store store = StoreManager.getCurrentStore();
                    if (mCurrentStore!=null && store != null && store.getBaseNumber() != null && !(store.getBaseNumber().equals(mCurrentStore.getBaseNumber()))) {
                        displayStoreInActionBar();
                    }
                }
            }
            break;

            case WebViewer.REQUEST_CODE: {
//                ((Spinner) findViewById(R.id.spinner_reports)).setSelection(0);
//                reportsOpen = false;
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
    public void onBackPressed() {
        /* Check is back stack > 1 b/c first transaction just loads the landing page. If the
         * back stack is NOT > 1 (i.e. we're at the landing page), the app should never close
         * by the user clicking the back button, as per client's request.*/
        if (mFragmentManager.getBackStackEntryCount() > 1) {
            mFragmentManager.popBackStack();
        }
    }



}