package com.bedessee.sales.backorder

import com.bedessee.sales.customview.ItemType

class BackOrderQuantity(
        val product: OrderProduct,
        var type: ItemType,
        var quantity: Int
)
