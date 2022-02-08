package com.bedessee.salesca.utilities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.bedessee.salesca.R;

/**
 * TODO: Document me...
 */
public class ShareImage {

    public ShareImage(final Context context, String filePath) {

        @SuppressLint("InflateParams")
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_share_image, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Share image...")
                .setCancelable(true)
                .setView(view);

        final AlertDialog alert = builder.create();

        alert.show();
    }

}
