package com.bedessee.salesca.product;

import android.widget.AbsListView;

/**
 * OnScrollListener that notifies the ProductAdapter when flinging is occurring.
 */
public class ProductScrollListener implements AbsListView.OnScrollListener {

    private ProductDummyAdapter mAdapter;

    public ProductScrollListener(ProductDummyAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case SCROLL_STATE_IDLE:
                mAdapter.isBusy = false;
                mAdapter.notifyDataSetChanged();
                break;
            case SCROLL_STATE_TOUCH_SCROLL:
                mAdapter.isBusy = false;
                mAdapter.notifyDataSetChanged();
                break;
            case SCROLL_STATE_FLING:
                mAdapter.isBusy = true;
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
