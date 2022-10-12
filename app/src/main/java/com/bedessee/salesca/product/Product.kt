package com.bedessee.salesca.product

import android.content.Context
import com.bedessee.salesca.product.category.Category
import com.bedessee.salesca.sharedprefs.SharedPrefsManager
import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class Product(
    @SerializedName("PROD#")
    var number: String?,
    @SerializedName("BRAND")
    var brand: String?,
    @SerializedName("DESCRIP")
    var description: String?,
    @SerializedName("UOM")
    var pieceUom: String?,
    @SerializedName("PRICE")
    var casePrice: String?,
    @SerializedName("LPRICE COLOR")
    var lPriceColor: String?,
    @SerializedName("LPRICE BCKG COLOR")
    var lPriceBackgroundColor: String?,
    @SerializedName("LEVEL1PRICE")
    var level1Price: String?,
    @SerializedName("LVL1 COLOR")
    var level1PriceColor: String?,
    @SerializedName("LVL1 BCKG COLOR")
    var level1BackgroundColor: String?,
    @SerializedName("LEVEL2PRICE")
    var level2Price: String?,
    @SerializedName("LVL2 COLOR")
    var level2PriceColor: String?,
    @SerializedName("LVL2 BCKG COLOR")
    var level2BackgroundColor: String?,
    @SerializedName("LEVEL3PRICE")
    var level3Price: String?,
    @SerializedName("LVL3 COLOR")
    var level3PriceColor: String?,
    @SerializedName("LVL3 BCKG COLOR")
    var level3BackgroundColor: String?,
    @SerializedName("STATUS CODE")
    var statusCode: String?,
    @SerializedName("M STATUS")
    var mStatus: String?,
    @SerializedName("STATUS DESCRIPTION")
    var statusDescription: String?,
    @SerializedName("CASES PER SKID")
    var casesPerSkid: String?,
    @SerializedName("CASES PER ROW")
    var casesPerRow: String?,
    @SerializedName("LAYERS PER SKID")
    var layersPerSkid: String?,
    var imagePath: String?,
    @SerializedName("UNIT PRICE")
    var piecePrice: String?,
    @SerializedName("CASE SIZE")
    var caseUom: String?,
    @SerializedName("TOTAL QTY")
    var totalQty: String?,
    @SerializedName("UPC")
    var uPC: String?,
    @SerializedName("QTY1")
    var qty1: String?,
    @SerializedName("QTY2")
    var qty2: String?,
    @SerializedName("QTY3")
    var qty3: String?,
    @SerializedName("QTY4")
    var qty4: String?,
    @SerializedName("SHOW_QTY1")
    var showQty1: String?,
    @SerializedName("SHOW_QTY2")
    var showQty2: String?,
    @SerializedName("SHOW_QTY3")
    var showQty3: String?,
    @SerializedName("SHOW_QTY4")
    var showQty4: String?,
    @SerializedName("NOTE01")
    var note01: String?,
    @SerializedName("NOTE02")
    var note02: String?,
    @SerializedName("NOTE03")
    var note03: String?,
    @SerializedName("NOTE04")
    var note04: String?,
    @SerializedName("NOTE05")
    var note05: String?,
    @SerializedName("POP UP PRICE")
    var popUpPrice: String?,
    @SerializedName("POP PRICE FLAG")
    var popUpPriceFlag: String?,
    @SerializedName("LIKE TAG")
    var likeTag: String?,
    @SerializedName("NEW STATUS SORT")
    var newTag: String?,
    @SerializedName("PRICE RANGE FROM")
    var priceRangeFrom: String?,
    @SerializedName("PRICE RANGE TO")
    var priceRangeTo: String?,
    @SerializedName("FILE CREATED ON")
    var fileCreatedOn: String?,
    @SerializedName("TOTAL QTY SOLD")
    var totalQtySold: String?,
    @SerializedName("LVL0_FROM")
    var lvl0From: String?,
    @SerializedName("LVL0_TO")
    var lvl0To: String?,
    @SerializedName("LVL0_PRICE")
    var lvl0Price: String?,
   @SerializedName("LVL1_FROM")
   var lvl1From: String?,
   @SerializedName("LVL1_TO")
   var lvl1To: String?,
    @SerializedName("LVL1_PRICE")
    var lvl1Price: String?,
    @SerializedName("LVL2_FROM")
    var lvl2From: String?,
    @SerializedName("LVL2_TO")
    var lvl2To: String?,
    @SerializedName("LVL2_PRICE")
    var lvl2Price: String?,
    @SerializedName("LVL3_FROM")
    var lvl3From: String?,
    @SerializedName("LVL3_TO")
    var lvl3To: String?,
    @SerializedName("LVL3_PRICE")
    var lvl3Price: String?,
    @SerializedName("PLUS SIGN COLOR")
    var plus_color: String?,
    @SerializedName("PLUS SIGN BCKG COLOR")
    var plus_bckg_color: String?
    ) : Serializable, Comparable<Product> {
    fun getLargeImagePath(context: Context?): String? {
        val sharedPrefs = SharedPrefsManager(context)
        val folderImagePath = sharedPrefs.linkToProdImages
        val folderLargeImagePath = sharedPrefs.linkToLargeProdImages
        return imagePath?.replace(folderImagePath, folderLargeImagePath)
    }

    val category: Category
        get() = Category.getCategoryFromChar(number!![0])

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is Product) return false
        return number == o.number
    }

    override fun hashCode(): Int {
        return number.hashCode()
    }

    /**
     * Compares this object to the specified object to determine their relative
     * order.
     *
     * @param other the object to compare to this instance.
     * @return a negative integer if this instance is less than `another`;
     * a positive integer if this instance is greater than
     * `another`; 0 if this instance has the same order as
     * `another`.
     * @throws ClassCastException if `another` cannot be converted into something
     * comparable to `this` instance.
     */
    override fun compareTo(other: Product): Int {
        return number!!.compareTo(other.number!!)
    }
}