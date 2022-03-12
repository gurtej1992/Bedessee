package com.bedessee.salesca.utilities;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private boolean isBottomToo;

    public SpacesItemDecoration(int space, boolean isBottomToo) {
        this.space = space;
        this.isBottomToo = isBottomToo;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        if(isBottomToo){
            outRect.bottom = space;
        }


        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = space;
        } else {
            outRect.top = 0;
        }
    }
}
