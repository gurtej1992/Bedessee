package com.bedessee.salesca.backorder

import com.bedessee.salesca.customview.ItemType

class BackOrderQuantity(
    val product: OrderProduct,
    var type: ItemType,
    var quantity: Int
)
