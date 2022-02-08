package com.bedessee.salesca.product;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

/**
 * Custom view that accepts a {@link android.widget.BaseAdapter} and sets the items in a horizontal
 * LinearLayout to be later placed in a HorizontalScrollView.
 */
public class HorizontalList extends LinearLayout {

    public HorizontalList(Context context) {
        super(context);
    }

    public HorizontalList(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setListOfViews(List<View> views) {
        setOrientation(HORIZONTAL);

        setBackgroundColor(Color.parseColor("#C0C0C0"));

        for (View view : views) {
            addView(view);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
            layoutParams.setMargins(3, 6, 3, 6);
            view.setLayoutParams(layoutParams);
        }
    }

}
