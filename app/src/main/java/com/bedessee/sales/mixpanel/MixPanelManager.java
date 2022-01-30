package com.bedessee.sales.mixpanel;

import android.content.Context;

public class MixPanelManager {

    final static private String PROJECT_TOKEN = "9d0a6b66401baffceea0476b9a2001a6";;

    public static MixpanelAPI getInstance(final Context context) {
//        final MixpanelAPI mixpanelAPI = MixpanelAPI.getInstance(context, PROJECT_TOKEN);
//        final Salesman salesman = SalesmanManager.getCurrentSalesman(context);
//        if (salesman != null) {
//            final String name = salesman.getName();
//            final String email = salesman.getEmail();
//            if (!TextUtils.isEmpty(name)) {
//                mixpanelAPI.getPeople().identify(name);
//                mixpanelAPI.identify(name);
//                mixpanelAPI.getPeople().set("$first_name", name);
//            }
//            if (!TextUtils.isEmpty(email)) {
//                mixpanelAPI.getPeople().set("$email", email);
//            }
//        }
//        return mixpanelAPI;
        return new MixpanelAPI();
    }


    public static void trackScreenView(final Context context, final String screenDesctiprion) {
//        getInstance(context).track(screenDesctiprion);
    }

    public static void trackButtonClick(final Context context, final String buttonDescription) {
//        getInstance(context).track(buttonDescription);
    }

    public static void trackProductView(final Context context, final String productDescription) {
//        try {
//            JSONObject props = new JSONObject();
//            props.put("Product", productDescription);
//        getInstance(context).track("Product Detail Popup", props);
//        } catch (JSONException e) {
//            Log.e("MYAPP", "Unable to add properties to JSONObject", e);
//        }
    }

    public static void trackSearch(final Context context, final String pageDescription, final String query) {
//        try {
//            JSONObject props = new JSONObject();
//            props.put("Page", pageDescription);
//            props.put("Search string", query);
//        getInstance(context).track("Search", props);
//        } catch (JSONException e) {
//            Log.e("MYAPP", "Unable to add properties to JSONObject", e);
//        }
    }

    public static void selectBrand(final Context context, final String brand) {
//        try {
//            JSONObject props = new JSONObject();
//            props.put("Brand", brand);
//        getInstance(context).track("Select Brand", props);
//        } catch (JSONException e) {
//            Log.e("MYAPP", "Unable to add properties to JSONObject", e);
//        }
    }

    public static void selectCategory(final Context context, final String category) {
//        try {
//            JSONObject props = new JSONObject();
//            props.put("Category", category);
//        getInstance(context).track("Select Category", props);
//        } catch (JSONException e) {
//            Log.e("MYAPP", "Unable to add properties to JSONObject", e);
//        }
    }

    public static void trackSuccessfulLogin(final Context context, final boolean isCached) {
//        getInstance(context).track(isCached ? "Re-login from cache" : "Successful login");
    }

    public static void selectProduct(final Context context, final String s) {
//        try {
//            JSONObject props = new JSONObject();
//            props.put("Product", s);
//            getInstance(context).track("Select Product", props);
//        } catch (JSONException e) {
//            Log.e("MYAPP", "Unable to add properties to JSONObject", e);
//        }
    }

    public static class MixpanelAPI {

    }
}
