package com.bedessee.salesca.product.brand;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bedessee.salesca.R;
import com.bedessee.salesca.provider.Contract;
import com.bedessee.salesca.provider.ProviderUtils;
import com.bedessee.salesca.sharedprefs.SharedPrefsManager;
import com.bedessee.salesca.utilities.BitmapWorkerTask;
import com.bedessee.salesca.utilities.Utilities;

/**
 * Brands Adapter.
 */
public class BrandAdapter extends CursorAdapter implements Filterable {

    private Context mContext;
    final String mSugarSyncDir;

    public BrandAdapter(final Context context) {
        super(context, null, false);
        mContext = context;
        final SharedPrefsManager sharedPrefs = new SharedPrefsManager(context);
        mSugarSyncDir = sharedPrefs.getSugarSyncDir();
    }

    @Override
    public Brand getItem(int position) {
        final Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        return ProviderUtils.cursorToBrand(cursor);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        FilterQueryProvider filter = getFilterQueryProvider();
        if (filter != null) {
            return filter.runQuery(constraint);
        }

        return mContext.getContentResolver().query(Contract.Brand.CONTENT_URI, null, Contract.BrandColumns.COLUMN_BRAND_NAME + " LIKE '%" + constraint + "%'", null, Contract.BrandColumns.COLUMN_BRAND_NAME + " ASC");
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        final View view = LayoutInflater.from(mContext).inflate(R.layout.brand_list_item, parent, false);

        BrandViewHolder holder = new BrandViewHolder();

        holder.logo = (ImageView) view.findViewById(R.id.brand_logo);
        holder.name = (TextView) view.findViewById(R.id.brand_name);

        view.setTag(holder);

        return view;
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final BrandViewHolder holder = (BrandViewHolder) view.getTag();

        final Brand brand = ProviderUtils.cursorToBrand(cursor);

        String filePath = mSugarSyncDir + "/brands/" + brand.getLogoName();

        int[] dimens = Utilities.getScreenDimensInPx(null);

        BitmapWorkerTask task = new BitmapWorkerTask(mContext, holder.logo, new int[]{dimens[0] / 6, dimens[1] / 3});
        task.execute(filePath);

        holder.name.setText(brand.getName());
    }


    private class BrandViewHolder {
        public ImageView logo;
        public TextView name;
    }

}