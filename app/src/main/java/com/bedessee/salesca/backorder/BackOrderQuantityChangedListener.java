package com.bedessee.salesca.backorder;

public interface BackOrderQuantityChangedListener {
       void onChanged(BackOrderQuantity pastOrderQuantity);
       void onListChanged();
}