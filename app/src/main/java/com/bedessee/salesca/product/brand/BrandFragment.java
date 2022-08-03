package com.bedessee.salesca.product.brand;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bedessee.salesca.R;
import com.bedessee.salesca.main.MainActivity;
import com.bedessee.salesca.mixpanel.MixPanelManager;
import com.bedessee.salesca.product.ProductFragment;
import com.bedessee.salesca.provider.Contract;
import com.bedessee.salesca.utilities.Utilities;

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
        SharedPreferences sh = getActivity().getSharedPreferences("setting", Context.MODE_PRIVATE);
        String orient= sh.getString("orientation","landscape");
        if(orient.equals("landscape")){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        getActivity().getLoaderManager().initLoader(LOADER_ID, null, this);

        mBrandAdapter = new BrandAdapter(getActivity());

        GridView gridView = rootView.findViewById(R.id.gridView);
        int spanCount = sh.getInt("spanCount",5);
        gridView.setNumColumns(spanCount);
        gridView.setAdapter(mBrandAdapter);
        gridView.setOnItemClickListener(this);


        final EditText editText = rootView.findViewById(R.id.editText_search);
        Utilities.hideSoftKeyboard(requireActivity());

        rootView.findViewById(R.id.btnClearSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText(null);
                Utilities.hideSoftKeyboard(requireActivity());
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


//    @Override
//    public void onConfigurationChanged(@NonNull Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//
//        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//
//        }
//    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Utilities.hideSoftKeyboard(requireActivity());
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
