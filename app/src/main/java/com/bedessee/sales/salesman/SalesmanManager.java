package com.bedessee.sales.salesman;

import android.content.Context;

import com.bedessee.sales.sharedprefs.SharedPrefsManager;


public class SalesmanManager {

    private static Salesman sCurrentSalesman = new Salesman("BALA", "bala_rabhouse@yahoo.com");

    public static Salesman getCurrentSalesman(Context context) {
        SharedPrefsManager sharedPrefsManager = new SharedPrefsManager(context);
        //Below is for default purposes
        if (sharedPrefsManager.getCurrentSalesman() == null) {
            setCurrentSalesman(context, sCurrentSalesman);
        }
        return sCurrentSalesman = sharedPrefsManager.getCurrentSalesman();
    }

    public static void setCurrentSalesman(Context context, Salesman currentSalesman) {
        sCurrentSalesman = currentSalesman;
        SharedPrefsManager sharedPrefsManager = new SharedPrefsManager(context);
        sharedPrefsManager.setCurrentSalesman(sCurrentSalesman);
    }
}
