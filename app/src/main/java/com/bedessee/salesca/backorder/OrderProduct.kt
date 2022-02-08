package com.bedessee.salesca.backorder

import com.bedessee.salesca.product.Product
import com.google.gson.annotations.SerializedName

class OrderProduct(number: String?, brand: String?, description: String?, pieceUom: String?, casePrice: String?, lPriceColor: String?, lPriceBackgroundColor: String?, level1Price: String?, level1PriceColor: String?, level1BackgroundColor: String?, level2Price: String?, level2PriceColor: String?, level2BackgroundColor: String?, level3Price: String?, level3PriceColor: String?, level3BackgroundColor: String?, statusCode: String?, mStatus: String?, statusDescription: String?, casesPerSkid: String?, casesPerRow: String?, layersPerSkid: String?, imagePath: String?, piecePrice: String?, caseUom: String?, totalQty: String?, uPC: String?, qty1: String?, qty2: String?, qty3: String?, qty4: String?, showQty1: String?, showQty2: String?, showQty3: String?, showQty4: String?, note01: String?, note02: String?, note03: String?, note04: String?, note05: String?, popUpPrice: String?, popUpPriceFlag: String?, likeTag: String?, newTag: String?, priceRangeFrom: String?, priceRangeTo: String?, fileCreatedOn: String?, totalQtySold: String?) : Product(number, brand, description, pieceUom, casePrice, lPriceColor, lPriceBackgroundColor, level1Price, level1PriceColor, level1BackgroundColor, level2Price, level2PriceColor, level2BackgroundColor, level3Price, level3PriceColor, level3BackgroundColor, statusCode, mStatus, statusDescription, casesPerSkid, casesPerRow, layersPerSkid, imagePath, piecePrice, caseUom, totalQty, uPC, qty1, qty2, qty3, qty4, showQty1, showQty2, showQty3, showQty4, note01, note02, note03, note04, note05, popUpPrice, popUpPriceFlag, likeTag, newTag, priceRangeFrom, priceRangeTo, fileCreatedOn, totalQtySold) {
    @SerializedName("DEFAULT QTY")
    var defaultQuantity: String? = null
        get() = field ?: "0"

    @SerializedName("OTHER DISPLAY INFO")
    var otherInfo: String? = null

    @SerializedName("OTHER DISPLAY LINE2")
    var otherInfo2: String? = null
}