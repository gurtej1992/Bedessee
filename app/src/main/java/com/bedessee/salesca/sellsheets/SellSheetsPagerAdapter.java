package com.bedessee.salesca.sellsheets;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.bedessee.salesca.sharedprefs.SharedPrefsManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SellSheetsPagerAdapter extends FragmentStatePagerAdapter {

    private File directory;
    private int numPages;

    public SellSheetsPagerAdapter(final Context context, final FragmentManager fragmentManager) {
        super(fragmentManager);

        final String mSugarSyncDir = new SharedPrefsManager(context).getSugarSyncDir();

        directory = new File(mSugarSyncDir + "/sale-sheets");

        if(directory.list() != null) {
            for (String s : directory.list()) {
                if(!s.equals("null")) {
                    numPages++;
                }
            }
        }



    }

    @Override
    public Fragment getItem(int position) {
        List<String> listOfImagePaths = new ArrayList<>();



        if (numPages > 0) {

            for (String filePath : directory.list()) {

                //check if file was created from null String
                if(!filePath.equals("null")) {
                    File f = new File(filePath);
                    listOfImagePaths.add(directory.getAbsolutePath() + "/" + f.getAbsolutePath());
                }

            }

        }

        Collections.sort(listOfImagePaths);

        SellSheetsFragment fragment = new SellSheetsFragment();
        fragment.setPosition(position);
        fragment.setDrawableList(listOfImagePaths);

        return fragment;
    }

    @Override
    public int getCount() {
        return numPages;
    }
}

