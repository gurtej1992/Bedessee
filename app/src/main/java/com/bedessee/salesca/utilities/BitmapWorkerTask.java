package com.bedessee.salesca.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.bedessee.salesca.R;

import java.lang.ref.WeakReference;

public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private int[] mDimens;
    private static Bitmap sDefaultBitmap;

    public BitmapWorkerTask(Context context, ImageView imageView, int [] dimens) {
        imageViewReference = new WeakReference<>(imageView);
        mDimens = dimens;
        if (sDefaultBitmap == null) {
            sDefaultBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.product_default_image);
        }
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String data = params[0];
        return Utilities.decodeSampledBitmapFromFile(data, mDimens[0], mDimens[1]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        } else if (imageViewReference != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(sDefaultBitmap);
            }
        }
    }
}