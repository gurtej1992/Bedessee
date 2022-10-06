package com.bedessee.salesca.product;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bedessee.salesca.R;
import com.bedessee.salesca.mixpanel.MixPanelManager;
import com.bedessee.salesca.product.brand.Brand;
import com.bedessee.salesca.product.category.Category2;
import com.bedessee.salesca.product.status.Status;
import com.bedessee.salesca.provider.Contract;
import com.bedessee.salesca.utilities.Utilities;

import java.lang.ref.WeakReference;

/**
 * Fragment that holds a list of products. If no brand or category is passed into the constructor,
 * the fragment will show all products by default.
 */
public class ProductFragment extends Fragment implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    final public static String TAG = "ProductList";

    private WeakReference<EditText> mEditSearchReference;
    private ProductsFilter mFilter;
    private Brand mBrand;
    private Category2 mCategory;
    private Status mStatus;
    private String mStatusString;
    CheckBox upcBox;
    String currentHint = "",searchtype="";
    public static boolean shouldRestartLoaderOnResume = true;
    private ProductDummyAdapter mAdapter;

    private static ProductFragment instance;

    public enum ProductsFilter {
        ALL, BRAND, CATEGORY, STATUS
    }


    public ProductFragment() {
        mFilter = ProductsFilter.ALL;
    }


    public static ProductFragment getInstance() {
        if (instance == null) {
            instance = new ProductFragment();
        } else {
            instance = new ProductFragment();
        }
        return instance;
    }


    public void setFilter(Brand brand) {
        mBrand = brand;
        mFilter = ProductsFilter.BRAND;
    }


    public void setFilter(Category2 category) {
        mCategory = category;
        mFilter = ProductsFilter.CATEGORY;
    }

    public void setFilter(String statusString) {
        mStatusString = statusString;
        mFilter = ProductsFilter.STATUS;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_product_list, container, false);

        final RecyclerView gridView = rootView.findViewById(R.id.gridView);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("setting", Context.MODE_PRIVATE);
        String orient= sharedPreferences.getString("orientation","landscape");
        if(orient.equals("landscape")){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        int[] dimens = Utilities.getScreenDimensInPx(getActivity());
        mAdapter = new ProductDummyAdapter(getContext(),null, dimens);
        mAdapter.setListener(new ProductDummyAdapter.Listener() {
            @Override
            public void onClick(Product product) {
                MixPanelManager.selectProduct(getActivity(), product.getBrand() + " " + product.getDescription());
                final ProductDetailDialog productDetailDialog = ProductDetailDialog.Companion.create(product, new Runnable() {
                    @Override
                    public void run() {
                        shouldRestartLoaderOnResume = false;
                    }
                });

                productDetailDialog.show(requireFragmentManager(), TAG);

            }
        });
        upcBox = rootView.findViewById(R.id.checkBoxUPC);
        mEditSearchReference = new WeakReference<>(rootView.findViewById(R.id.editText_search));
        mEditSearchReference.get().clearFocus();
        Utilities.hideSoftKeyboard(requireActivity());

        rootView.findViewById(R.id.btnClearSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = mEditSearchReference.get();
                if (editText != null) {
                    editText.setText(null);
                    Utilities.hideSoftKeyboard(requireActivity());
                }
            }
        });
        upcBox.setOnClickListener(v->{
            if(!upcBox.isChecked()){
                mEditSearchReference.get().setHint(currentHint);
                mEditSearchReference.get().setInputType(InputType.TYPE_CLASS_TEXT);
                mEditSearchReference.get().setSelection(mEditSearchReference.get().getText().length());
            }
            else{
                mEditSearchReference.get().setHint("Enter UPC");
                mEditSearchReference.get().setInputType(InputType.TYPE_CLASS_NUMBER);
                mEditSearchReference.get().setSelection(mEditSearchReference.get().getText().length());
            }

        });




//        mAdapter.setListener(new ProductAdapter.Listener() {
//            @Override
//            public void onClick(Product product) {
//                MixPanelManager.selectProduct(getActivity(), product.getBrand() + " " + product.getDescription());
//                final ProductDetailDialog productDetailDialog = ProductDetailDialog.Companion.create(product, new Runnable() {
//                    @Override
//                    public void run() {
//                        shouldRestartLoaderOnResume = false;
//                    }
//                });
//
//                productDetailDialog.show(requireFragmentManager(), TAG);
//            }
//        });



        //GET

        SharedPreferences sh = getActivity().getSharedPreferences("setting", Context.MODE_PRIVATE);
        int spanCount = sh.getInt("spanCount",5);
        gridView.setLayoutManager(new GridLayoutManager(getContext(),spanCount));
        gridView.setAdapter(mAdapter);

        final int loaderId;
        switch (mFilter) {
            case BRAND:
                if(searchtype.equals("All")){
                    loaderId = mFilter.hashCode();
            }else {
                    loaderId = mBrand.hashCode();
                }
                break;
            case CATEGORY:
                if(searchtype.equals("All")){
                    loaderId = mFilter.hashCode();
                }else {
                    loaderId = mCategory.hashCode();
                }

                break;
            case STATUS:
                if (mStatus != null) {
                    if(searchtype.equals("All")){
                        loaderId = mFilter.hashCode();
                    }else {
                        loaderId = mStatus.hashCode();
                    }

                } else {
                    if(searchtype.equals("All")){
                        loaderId = mFilter.hashCode();
                    }else {
                        loaderId = mStatusString.hashCode();
                    }
                }
                break;
            default:
                loaderId = mFilter.hashCode();
                break;
        }
        requireActivity().getLoaderManager().initLoader(loaderId, null, this);


        ProductScrollListener gridScrollListener = new ProductScrollListener(mAdapter);
        //gridView.setOnScrollListener(gridScrollListener);

        mEditSearchReference.get().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
              //  mAdapter.mCursorAdapter.getFilter().filter(s);

                MixPanelManager.trackSearch(getActivity(), "Products screen", s.toString());

                final String filter = getFilterWhereClause();
                String whereClause;
                if(upcBox.isChecked()){
                    whereClause = Contract.ProductColumns.COLUMN_UPC + " LIKE '%" + s + "%'";
                }
                else{
                     whereClause = Contract.ProductColumns.COLUMN_BRAND + " LIKE '%" + s + "%'" + " OR " +
                            Contract.ProductColumns.COLUMN_NUMBER + " LIKE '%" + s + "%'" + " OR " +
                            Contract.ProductColumns.COLUMN_DESCRIPTION + " LIKE '%" + s + "%'";
                }
                //
                String filterWhere = filter + (!filter.equals("") ? " AND " : "") + "(" + whereClause + ")";
                Log.e("LOLLLOL",filterWhere);
                mAdapter.mCursorAdapter.getFilter().filter(filterWhere);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEditSearchReference.get().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    v.clearFocus();
                    Utilities.hideSoftKeyboard(requireActivity());
                    return true;
                }
                return false;
            }
        });

        View sview = getActivity().findViewById(R.id.search);
        View searchBar = rootView.findViewById(R.id.searchBar);
        sview.setOnClickListener(v -> {


                        if(searchBar.getVisibility() == View.VISIBLE){
                searchBar.animate()
                        .translationY(0)
                        .alpha(0.0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                searchBar.setVisibility(View.GONE);
                            }
                        });
            }
            else{
                searchBar.setVisibility(View.VISIBLE);
                searchBar.setAlpha(0.0f);
                searchBar.animate()
                        .alpha(1.0f)
                        .setListener(null);
                    searchtype="All";
            }
        } );

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (shouldRestartLoaderOnResume) {
            final int loaderId = getLoaderId();
            requireActivity().getLoaderManager().restartLoader(loaderId, null, this);
        }

        shouldRestartLoaderOnResume = true;
    }


    private int getLoaderId() {
        switch (mFilter) {
            case BRAND:
                if(searchtype.equals("All")){
                    return mFilter.hashCode();
                }else {
                    return mBrand.hashCode();
                }

            case CATEGORY:
                if(searchtype.equals("All")){
                    return mFilter.hashCode();
                }else {
                    return mCategory.hashCode();
                }

            case STATUS:
                if (mStatus != null) {
                    if(searchtype.equals("All")){
                        return mFilter.hashCode();
                    }else {
                        return mStatus.hashCode();
                    }
                } else {
                    if(searchtype.equals("All")){
                        return mFilter.hashCode();
                    }else {
                        return mStatusString.hashCode();
                    }
                }
            default:
                return mFilter.hashCode();
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


    private void appendSearchbarHint(String string) {
        mEditSearchReference.get().setHint(mEditSearchReference.get().getHint() + " >> " + string);
        currentHint = mEditSearchReference.get().getHint().toString();
    }


    private String getFilterWhereClause() {
        switch (mFilter) {
            case BRAND:
                if(searchtype.equals("All")){
                    return "";
                }else {
                    return Contract.ProductColumns.COLUMN_BRAND + " = '" + mBrand.getName().toUpperCase() + "'";
                }


            case CATEGORY:
                if(searchtype.equals("All")){
                    return "";
                }else {
                    return Contract.ProductColumns.COLUMN_NUMBER + " like '" + mCategory.getChar() + "%'";
                }
            case STATUS:
                if(searchtype.equals("All")){
                    return "";
                }else {
                    return Contract.ProductColumns.COLUMN_M_STATUS + " like '%" + (mStatus != null ? mStatus.name() : mStatusString) + "%'";
                }
                default:
                return "";
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (mFilter) {
            case BRAND:
                mEditSearchReference.get().setHint("Filter");
                appendSearchbarHint("Brand");
                appendSearchbarHint(mBrand.getName());
                return new CursorLoader(getActivity(), Contract.Product.CONTENT_URI, null, Contract.ProductColumns.COLUMN_BRAND + " = '" + mBrand.getName().toUpperCase() + "'", null, Contract.ProductColumns.COLUMN_NUMBER + " ASC");

            case CATEGORY:
                mEditSearchReference.get().setHint("Filter");
                appendSearchbarHint("Category");
                appendSearchbarHint(mCategory.getDescription());
                return new CursorLoader(getActivity(), Contract.Product.CONTENT_URI, null, Contract.ProductColumns.COLUMN_NUMBER + " like '" + mCategory.getChar() + "%'", null, Contract.ProductColumns.COLUMN_NUMBER + " ASC");

            case STATUS:
                mEditSearchReference.get().setHint("Filter");
                appendSearchbarHint("Status");
                appendSearchbarHint(mStatus != null ? mStatus.getDescription() : mStatusString);
                return new CursorLoader(getActivity(), Contract.Product.CONTENT_URI, null, Contract.ProductColumns.COLUMN_M_STATUS + " like '%" + (mStatus != null ? mStatus.name() : mStatusString) + "%'", null, (mStatusString != null && mStatusString.equals("NP") ? Contract.ProductColumns.COLUMN_NEW_TAG : Contract.ProductColumns.COLUMN_NUMBER) + " ASC");

            default:
                mEditSearchReference.get().setHint("Filter");
                appendSearchbarHint("All products");
                return new CursorLoader(getActivity(), Contract.Product.CONTENT_URI, null, null, null, Contract.ProductColumns.COLUMN_NUMBER + " ASC");
        }

    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null) {
            mAdapter.mCursorAdapter.changeCursor(cursor);
            mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mAdapter.mCursorAdapter.changeCursor(null);
    }


}
