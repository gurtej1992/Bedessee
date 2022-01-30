package com.bedessee.sales.order

import com.bedessee.sales.backorder.OrderProduct
import com.bedessee.sales.customview.ItemType

class PastOrderQuantity(
        val product: OrderProduct,
        var type: ItemType,
        var quantity: Int
)
