package com.bedessee.salesca.sellsheets;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bedessee.salesca.R;
import com.bedessee.salesca.product.FullScreenImageActivity;
import com.bedessee.salesca.product.category.Category;
import com.bedessee.salesca.utilities.Utilities;

import java.util.List;

/**
 * ViewPager fragment for SellSheets dialog.
 */
public class SellSheetsFragment extends Fragment implements View.OnClickListener{

    private int mPosition;
    private List<String> mDrawableList;


    public SellSheetsFragment() {

    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public void setDrawableList(List<String> drawableList) {
        mDrawableList = drawableList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.sell_sheets_item, container, false);

//        SharedPreferences sh = getActivity().getSharedPreferences("setting", Context.MODE_PRIVATE);
//        String orient= sh.getString("orientation","landscape");
//        if(orient.equals("landscape")){
//            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        }else {
//            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }

        ImageView imageView = view.findViewById(R.id.product_image);

        int [] dimens = Utilities.getScreenDimensInPx(getActivity());

        if (mDrawableList != null && !mDrawableList.isEmpty()) {
            Bitmap bitmap = Utilities.decodeSampledBitmapFromFile(mDrawableList.get(mPosition), dimens[0] / 3, dimens[1]);

            imageView.setImageBitmap(bitmap);

            view.findViewById(R.id.btn_zoom).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FullScreenImageActivity.Companion.launch(getActivity(), Uri.parse("file://" + mDrawableList.get(mPosition)));
                }
            });

            return view;
        } else {
            final Context context = container.getContext();
            Utilities.shortToast(context, "Error loading image");
            getFragmentManager().popBackStack();
            return new View(context);
        }
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();

        switch (mPosition) {
            case 0:
                intent.putExtra("Category", Category.ACHAR_CONDIMENTS);
                break;
            case 1:
                intent.putExtra("Category", Category.CANNED_FOODS_MILK_PRODUCTS);
                break;
            case 2:
                intent.putExtra("Category", Category.FROZEN_FISH_FROZEN_VEGETABLES);
                break;
            case 3:
                intent.putExtra("Category", Category.HEALTHY_CARE_MEDICINES);
                break;
            case 4:
                intent.putExtra("Category", Category.KITCHEN_WARE_HARDWARE);
                break;
            case 5:
                intent.putExtra("Category", Category.NOODLES_BAKING_PRODUCTS);
                break;
            case 6:
                intent.putExtra("Category", Category.QUICK_FROZEN_PRODUCTS);
                break;
            case 7:
                intent.putExtra("Category", Category.RELIGIOUS_BRASS_WARES);
                break;
            case 8:
                intent.putExtra("Category", Category.SPICES_CURRY_POWDERS);
                break;
            case 9:
                intent.putExtra("Category", Category.VEGETABLES_OILS_GHEE_PRODUCTS);
                break;
            default:
                break;
        }

        getActivity().setResult(SellSheetsDialog.RESULT_CODE, intent);
        getActivity().finish();
    }

}
