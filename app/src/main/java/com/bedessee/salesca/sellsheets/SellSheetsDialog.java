package com.bedessee.salesca.sellsheets;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bedessee.salesca.R;
import com.bedessee.salesca.utilities.DepthPageTransformer;

/**
 * SellSheets Dialog.
 */
public class SellSheetsDialog extends FragmentActivity {

    public static final int RESULT_CODE = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_catalog_pages);

        // Instantiate a ViewPager and a PagerAdapter.
        ViewPager pager = findViewById(R.id.pager);
        PagerAdapter pagerAdapter = new SellSheetsPagerAdapter(this, getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setPageTransformer(true, new DepthPageTransformer());

        if (pagerAdapter.getCount() == 0) {
            Toast.makeText(this, "No sell sheets found.", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "HAVE YOU SET UP A DATA FOLDER?", Toast.LENGTH_LONG).show();
            setResult(RESULT_CODE);
            finish();
        }


        findViewById(R.id.imageView_close).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setResult(RESULT_CODE);
                finish();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(lp);
    }


}
