package com.bedessee.salesca.pastorder;

import com.bedessee.salesca.order.PastOrderQuantity;

public interface QuantityChangedListener {
       void onChanged(PastOrderQuantity pastOrderQuantity);
}