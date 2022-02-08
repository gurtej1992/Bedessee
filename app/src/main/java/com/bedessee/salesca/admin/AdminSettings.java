package com.bedessee.salesca.admin;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.bedessee.salesca.R;

/**
 * Activity where an admin can change app settings for salesmen.
 */
public class AdminSettings extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}
