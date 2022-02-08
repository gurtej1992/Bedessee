package com.bedessee.salesca.order

import com.bedessee.salesca.backorder.OrderProduct
import com.bedessee.salesca.customview.ItemType

class PastOrderQuantity(
    val product: OrderProduct,
    var type: ItemType,
    var quantity: Int
)
