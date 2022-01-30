package com.bedessee.sales.product.category;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

import com.bedessee.sales.provider.ProviderUtils;

/**
 * TODO: Document me...
 */
public class CategoryAdapter extends SimpleCursorAdapter {


    public CategoryAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to);
    }

    /**
     * @param position
     * @see android.widget.ListAdapter#getItem(int)
     */
    @Override
    public Category2 getItem(int position) {
        final Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        return ProviderUtils.cursorToCategory(cursor);
    }


}
