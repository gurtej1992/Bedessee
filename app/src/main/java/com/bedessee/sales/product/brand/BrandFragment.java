package com.bedessee.sales.product.brand;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import androidx.fragment.app.Fragment;

import com.bedessee.sales.R;
import com.bedessee.sales.main.MainActivity;
import com.bedessee.sales.mixpanel.MixPanelManager;
import com.bedessee.sales.product.ProductFragment;
import com.bedessee.sales.provider.Contract;

/**
 * Fragment that shows the list of BRANDS. From here, the user can click on a brand to go back
 * to the ProductsFragment but showing only the products from the selected brand.
 */
public class BrandFragment extends Fragment implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    public final static String TAG = "BrandsList";
    private BrandAdapter mBrandAdapter;
    final static int LOADER_ID = 11;

    private static BrandFragment instance;
    private View rootView;

    public static BrandFragment getInstance() {
        if (instance == null) {
            instance = new BrandFragment();
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_brand_list, container, false);

        MixPanelManager.trackScreenView(getActivity(), "Brands screen");

        getActivity().getLoaderManager().initLoader(LOADER_ID, null, this);

        mBrandAdapter = new BrandAdapter(getActivity());

        GridView gridView = rootView.findViewById(R.id.gridView);

        gridView.setAdapter(mBrandAdapter);
        gridView.setOnItemClickListener(this);


        final EditText editText = rootView.findViewById(R.id.editText_search);

        rootView.findViewById(R.id.btnClearSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText(null);
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBrandAdapter.getFilter().filter(s);
                MixPanelManager.trackSearch(getActivity(), "Brands", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return rootView;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ProductFragment productFragment = new ProductFragment();
        final Brand brand = mBrandAdapter.getItem(position);
        MixPanelManager.selectBrand(getActivity(), brand.getName());
        productFragment.setFilter(brand);
        ((MainActivity) getActivity()).switchFragment(productFragment, ProductFragment.TAG);
    }


    @Override
    public void onResume() {
        super.onResume();
        final LoaderManager loaderManager = getActivity().getLoaderManager();
        loaderManager.restartLoader(LOADER_ID, null, this);
        ((EditText) rootView.findViewById(R.id.editText_search)).setText(null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), Contract.Brand.CONTENT_URI, null, null, null, Contract.BrandColumns.COLUMN_BRAND_NAME + " ASC");
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            mBrandAdapter.changeCursor(cursor);
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBrandAdapter.changeCursor(null);
    }
}
