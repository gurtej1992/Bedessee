package com.bedessee.sales.pastorder;

import com.bedessee.sales.order.PastOrderQuantity;

public interface QuantityChangedListener {
       void onChanged(PastOrderQuantity pastOrderQuantity);
}