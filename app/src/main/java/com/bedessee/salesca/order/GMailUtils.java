package com.bedessee.salesca.order;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.bedessee.salesca.BuildConfig;
import com.bedessee.salesca.customview.ItemType;
import com.bedessee.salesca.orderhistory.SavedItem;
import com.bedessee.salesca.product.Product;
import com.bedessee.salesca.salesman.SalesmanManager;
import com.bedessee.salesca.sharedprefs.SharedPrefsManager;
import com.bedessee.salesca.shoppingcart.ShoppingCart;
import com.bedessee.salesca.shoppingcart.ShoppingCartProduct;
import com.bedessee.salesca.store.Store;
import com.bedessee.salesca.store.StoreCustomAttributes;
import com.bedessee.salesca.store.StoreManager;
import com.bedessee.salesca.utilities.ProductEnteredFrom;
import com.bedessee.salesca.utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import timber.log.Timber;

/**
 * This class contains one single utility function to sendShoppingCart an email via the Gmail app.
 */
public class GMailUtils {

    final private static String GMAIL_PACKAGE_NAME = "com.google.android.gm";

    private static Activity sActivity;
    private static ShoppingCart sShoppingCart;
    static Double latitude=0.0,logitude=0.0;

    public static void sendShoppingCart(Activity activity, ShoppingCart shoppingCart,Double lat,Double lng) {

        sActivity = activity;
        sShoppingCart = shoppingCart;
      latitude=lat;
      logitude=lng;
        final Intent intent = getGmailIntent(activity);

        final SharedPrefsManager prefsManager = new SharedPrefsManager(activity);

        //some samples on adding more then one email address
        String[] aEmailList = null;
        if (prefsManager.getOrderEmailRecips() != null && !prefsManager.getOrderEmailRecips().isEmpty()) {
            aEmailList = prefsManager.getOrderEmailRecips().split(",");
        }
//        final String aEmailList[] = {"sales@bedessee.com", "noreply@bedessee.com", "invor@bedessee.com"};
        final String[] emailCC = {SalesmanManager.getCurrentSalesman(sActivity).getEmail()};
        String emailPrefix = prefsManager.getCurrentEmailPrefix();
        String subjectLine;

        if (TextUtils.isEmpty(emailPrefix)){
            subjectLine = "APP~" + DateFormat.format("yyyy-MM-dd hh:mmaa", new Date()).toString() + "~" + StoreManager.getCurrentStore().getName().split("-")[0] + "~" + StoreManager.getCurrentStore().getAddress().split("\\,")[0] + ".";
        } else {
            subjectLine = "APP~" + DateFormat.format("yyyy-MM-dd hh:mmaa", new Date()).toString() + "~" + emailPrefix + "~" + StoreManager.getCurrentStore().getName().split("-")[0] + "~" + StoreManager.getCurrentStore().getAddress().split("\\,")[0] + ".";
        }

        Log.d("SalesMan", " object: " + emailPrefix);

        makeJson(activity,sShoppingCart,DateFormat.format("yyyy-MM-dd hh:mmaa", new Date()).toString(),StoreManager.getCurrentStore().getName().split("-")[0]);
        //all the extras that will be passed to the email app
        if (aEmailList != null && aEmailList.length > 0) {
            intent.putExtra(android.content.Intent.EXTRA_EMAIL, aEmailList);
        }
        intent.putExtra(android.content.Intent.EXTRA_CC, emailCC);
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT,subjectLine);
        intent.putExtra(android.content.Intent.EXTRA_TEXT, getBody());

        //start the app
        activity.startActivity(intent);
    }


    public static void sendAttachment(final Activity activity, final String subject, final String absAttachmentPath) {

        final Intent intent = getGmailIntent(activity);
        Timber.d("%s.provider", BuildConfig.APPLICATION_ID);
        Timber.d(BuildConfig.FLAVOR);

        Uri fileUri = FileProvider.getUriForFile(activity.getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider.File", new File(absAttachmentPath));

//        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, new SharedPrefsManager(activity).getStatementEmailSubject());

        //start the app
        activity.startActivity(intent);
    }



    private static Intent getGmailIntent(Activity activity) {
        //set the main intent to ACTION_SEND for looking for applications that share information
        final Intent intent = new Intent(Intent.ACTION_SEND, null);

        //filter out apps that are able to sendShoppingCart plain text
        intent.setType("*/*");

        //get a list of apps that meet your criteria above
        final List<ResolveInfo> pkgAppsList = activity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY | PackageManager.GET_RESOLVED_FILTER);

        //Cycle through list of apps in list and select the one that matches GMail's package name
        for (ResolveInfo resolveInfo : pkgAppsList) {
            final String packageName = resolveInfo.activityInfo.packageName;
            String className;
            if (packageName.equals(GMAIL_PACKAGE_NAME)) {
                className = resolveInfo.activityInfo.name;
                intent.setClassName(packageName, className);
            }
        }
        return intent;
    }


    /**
     * Returns the body of the order email. The following is the template that it follows...
     *
     * prod_num, brand, description, UOM, original_price, level1, level2, level3,: qty PC <—PC optional if pieces selected…also, if PC selected, change UOM format from 3X200ML to just 200ML (remove all after X)
     *
     * salesrep: CURR_SALESMAN_NAME
     *
     * name: CLIENT_NAME
     *
     * company: CUST_ACCT_NUM, CUST_ACCT_NAME, CUST_ACCT_ADDRESS
     *
     * comment1: “comment…”
     *
     * upload: DATE_OF_SALE TIME_OF_SALE
     *
     * duration: DURATION_OF_SALE (always in minutes only...no hours or seconds)
     *
     *
     * @return Body of email.
     */
    private static String getBody() {

        ArrayList<ShoppingCartProduct> shoppingCartProducts = sShoppingCart.getProducts();

        ArrayList<ShoppingCartProduct> sortedList = new ArrayList<>(new HashSet<>(shoppingCartProducts));
        Collections.sort(sortedList);


        String bodyProducts1 = "\n\n" + "# OF ITEMS : " + sShoppingCart.getTotalItems();
        String bodyProducts2 = "";

        for (ShoppingCartProduct shoppingCartProduct : sortedList) {

            final String price = shoppingCartProduct.getEnteredPrice();

            final Product product = shoppingCartProduct.getProduct();
            final boolean isPiece = shoppingCartProduct.getItemType() == ItemType.PIECE;

            bodyProducts1 = bodyProducts1 + "\n\n" +
                    "~" + product.getNumber() + " : " + shoppingCartProduct.getQuantity() + (isPiece ? " PC" : "") + (TextUtils.isEmpty(price) || price.equalsIgnoreCase("null") ? "" : " [price: " + price + "]");


            bodyProducts2 = bodyProducts2 + "\n\n" +
                    product.getNumber() + ", " +
                    product.getBrand() + ", " +
                    product.getDescription() + ", " +
                    (isPiece ? product.getPieceUom() : product.getCaseUom()) + "," +
                    (isPiece ? product.getPiecePrice() : product.getCasePrice()) + ", " +
                    product.getLevel1Price() + ", " +
                    product.getLevel2Price() + ", " +
                    product.getLevel3Price() + ",: " +
                    shoppingCartProduct.getQuantity() + " " +
                    (isPiece ? "PC" : "") +
                    (TextUtils.isEmpty(price) || (price.equals("0")) || price.equalsIgnoreCase("null") ? "" : " [price: " + price + "]");
        }

        String bodyProducts = bodyProducts1 + "\n\n" + bodyProducts2;

        bodyProducts += "\n\n" + "# OF ITEMS : " + sShoppingCart.getTotalItems();

        final Store store = StoreManager.getCurrentStore();

        if (store == null) {
            return "ERROR IN STORE INFORMATION. PLEASE MAKE SURE A STORE IS SELECTED.";
        }

        final String date = Utilities.emptyIfNull(DateFormat.format("yyyy-MM-dd hh:mm aa", new Date()).toString());
        final String comment = Utilities.emptyIfNull(sShoppingCart.getComment());

        String output;
        if (!store.isNewStoreAdded()) {

            final String currentSalesmanName = Utilities.emptyIfNull(SalesmanManager.getCurrentSalesman(sActivity).getName());
            final String contact = Utilities.emptyIfNull(sShoppingCart.getContact());
            final String storeNumber = Utilities.emptyIfNull(store.getNumber());
            final String storeName = Utilities.emptyIfNull(store.getName());
            final String storeAddress = Utilities.emptyIfNull(store.getAddress());

            output = bodyProducts +
                    "\n\n" +
                    "salesrep: " + currentSalesmanName +
                    "\n\n" +
                    "name:" + contact +
                    "\n\n" +
                    "company: " + storeNumber + ", " + storeName + ", " + storeAddress;
        } else {
            StoreCustomAttributes attributes = store.getCustomAttributes();
            output = bodyProducts +
                    "\n\n" +
                    "salesrep: " + Utilities.emptyIfNull(SalesmanManager.getCurrentSalesman(sActivity).getName()) +
                    "\n\n" +
                    "name:" + Utilities.emptyIfNull(sShoppingCart.getContact()) +
                    "\n\n" +

                    "==================== NEW CUSTOMER ====================" +
                    "\n\n" +


                    "new customer contact name: " + Utilities.emptyIfNull(attributes.getContactName()) +
                    "\n\n" +
                    "new customer company name: " + Utilities.emptyIfNull(attributes.getCompanyName()) +
                    "\n\n" +
                    "new customer address: " + Utilities.emptyIfNull(attributes.getAddress()) +
                    "\n\n" +
                    "new customer city: " + Utilities.emptyIfNull(attributes.getCity()) +
                    "\n\n" +
                    "new customer province: " + Utilities.emptyIfNull(attributes.getProvince()) +
                    "\n\n" +
                    "new customer country: " + Utilities.emptyIfNull(attributes.getCountry()) +
                    "\n\n" +
                    "new customer postal code: " + Utilities.emptyIfNull(attributes.getPostalCode()) +
                    "\n\n" +
                    "new customer telephone: " + Utilities.emptyIfNull(attributes.getTelephone()) +
                    "\n\n" +
                    "new customer email: " + Utilities.emptyIfNull(attributes.getEmail()) +
                    "\n\n" +

                    "=============== END NEW CUSTOMER INFO ===============" +
                    "\n\n";
        }

        return output +
                "\n\n" +
                "comment1: " + comment +
                "\n\n" + "# OF ITEMS : " + sShoppingCart.getTotalItems() +
                "\n\n" +
                "upload: " + shoppingCartProducts.get(0).getProduct().getFileCreatedOn() +
                "\n\n" +
                "APP VERSION: " + Utilities.getVersionString(sActivity) +
                "\n\n" +
                "Location Coordinates:  " + latitude +" , "+ logitude;
    }

    private static int getTotalProductsFromList(ProductEnteredFrom countFrom) {
        ArrayList<ProductEnteredFrom> fromArray = sShoppingCart.getFrom();
        int count = 0;
        for     (ProductEnteredFrom from : fromArray) {
            if(from == countFrom) {
                count++;
            }
        }
        return count;
    }
    private static void writeToFile(String data,String path) {
        Log.e("@#@#","get store name"+StoreManager.getCurrentStore());
        try {
            OutputStream outputStreamWriter = new FileOutputStream(new File(path + "/CompletedOrder/cos_" + StoreManager.getCurrentStore().getName() + ".txt"),true);
            outputStreamWriter.write(data.getBytes(StandardCharsets.UTF_8));
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("@#@#","exception here"+e.getLocalizedMessage());
        }
    }

    public static void makeJson(Activity activity, ShoppingCart sShoppingCart, String s, String s1){
        String baseFilePath = new SharedPrefsManager(activity).getSugarSyncDir();
        String parentDirectory = new File(baseFilePath).getPath();
        File f1 = new File(parentDirectory , "CompletedOrder");
        if (!f1.exists()) {
            f1.mkdirs();
        }
        File file = new File(parentDirectory + "/CompletedOrder/cos_" + StoreManager.getCurrentStore().getName() + ".txt");
        String json = null;
        json = makeJsonObject(sShoppingCart,s,s1);

        writeToFile(json, parentDirectory);
    }
    private static String makeJsonObject(ShoppingCart shoppingCart,String datetime,String Storename){

        String output;
        output=
                        "\n\n" +
                        "Date&Time: " + datetime +
                        "\n\n" +
                                "Total Items: " + shoppingCart.getTotalItems() +
                                "\n\n" +
                                "Store Name: " + Storename +
                                "\n\n" ;

        String bodyproducts ="";
        ArrayList<ShoppingCartProduct> shoppingCartProducts = shoppingCart.getProducts();

        ArrayList<ShoppingCartProduct> sortedList = new ArrayList<>(new HashSet<>(shoppingCartProducts));
        Collections.sort(sortedList);
        for (ShoppingCartProduct shoppingCartProduct : sortedList) {
            final Product product = shoppingCartProduct.getProduct();
            bodyproducts= bodyproducts+
                    "\n\n" +
                    "Product Number: " + product.getNumber() +
                    "\n\n" +
                    "Product Brand: " + product.getBrand() +
                    "\n\n" +
                    "Product Quantity: " + shoppingCartProduct.getQuantity() +
                            "\n\n" +
                            "========================" ;



        }

        return output +
                "\n\n" +
                "========== Products ========" + bodyproducts;
    }
}
