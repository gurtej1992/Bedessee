package com.bedessee.sales.backorder;

public interface BackOrderQuantityChangedListener {
       void onChanged(BackOrderQuantity pastOrderQuantity);
       void onListChanged();
}