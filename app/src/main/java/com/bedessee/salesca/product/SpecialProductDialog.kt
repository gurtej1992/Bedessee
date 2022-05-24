package com.bedessee.salesca.product

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.bedessee.salesca.R
import com.bedessee.salesca.customview.QtySelector
import com.bedessee.salesca.customview.QtySelector.QtySelectorClickListener
import com.bedessee.salesca.mixpanel.MixPanelManager
import com.bedessee.salesca.product.FullScreenImageActivity.Companion.launch
import com.bedessee.salesca.utilities.BitmapWorkerTask
import com.bedessee.salesca.utilities.ProductEnteredFrom
import com.bedessee.salesca.utilities.Utilities
import com.bedessee.salesca.utilities.ViewUtilities
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.dialog_product_qty.view.*
import kotlinx.android.synthetic.main.dialog_special_product_details.view.*
import kotlinx.android.synthetic.main.widget_product_detail.view.*
import java.io.File
import java.util.*

/**
 * Dialog to display a series of special products in a HorizontalList
 */
class SpecialProductDialog : DialogFragment() {
    lateinit var products : List<Product>
    var searchNumber : String? = null

    companion object {
        const val TAG = "SpecialProductDialog"

        fun create(products: List<Product>, searchNumber: String?) = SpecialProductDialog().apply {
            this.products = products
            this.searchNumber = searchNumber
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val sh = requireActivity().getSharedPreferences("setting", AppCompatActivity.MODE_PRIVATE)
        val orient = sh.getString("orientation", "landscape")
        requireActivity().requestedOrientation = if (orient == "landscape") {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        val context = inflater.context

        val dimens = Utilities.getScreenDimensInPx(null)
        val width = dimens[0] / 6
        val height = dimens[1] / 6

        val mainView = inflater.inflate(R.layout.dialog_special_product_details, container, false)
        mainView.number_pad_title.text = context.getString(R.string.upc_dialog_title, products.size, searchNumber)
        mainView.btn_close.setOnClickListener {
            dismiss()
        }

        val horizontalList: HorizontalList = mainView.findViewById(R.id.horizontal_list)
        val listOfProducts = ArrayList<View>(products.size)
        for (product in products) {
            val productView = LayoutInflater.from(context).inflate(R.layout.widget_product_detail, null, false)
            val file = File(product.getLargeImagePath(context)!!)
            val imageView = productView.findViewById<ImageView>(R.id.product_image)
            val task = BitmapWorkerTask(context, imageView, intArrayOf(width, height))
            task.execute(file.absolutePath)
            Glide.with(this).load(file.absolutePath).into(imageView);

            productView.btn_share.setOnClickListener { Utilities.shareImage(context, "file://" + file.absolutePath) }
            productView.btn_zoom.setOnClickListener { launch(context, Uri.parse("file://" + file.absolutePath)) }
            productView.textView_brand_text.text = "${product.brand} ${product.description}"
            productView.textView_price_field.text = "${product.caseUom}: $${product.casePrice}"
            val level1price = product.level1Price
            productView.textView_level1price_field.text = "Level 1 Price: " + if (level1price == "0.00") "N/A" else "$$level1price"
            val level2price = product.level2Price
            productView.textView_level2price_field.text = "Level 2 Price: " + if (level2price == "0.00") "N/A" else "$$level2price"
            val level3price = product.level3Price
            productView.textView_level3price_field.text = "Level 3 Price: " + if (level3price == "0.00") "N/A" else "$$level3price"
            productView.textView_unitprice_field.text = "${product.pieceUom}: $${product.piecePrice}"
            productView.textView_status.text = "Status: ${product.statusDescription}"
            productView.textView_casesperskid.text = "Cases Per Skid: ${product.casesPerSkid}"
            productView.textView_casesperrow.text = "Cases Per Row: ${product.casesPerRow}"
            productView.textView_layersperskid.text = "Layers Per Skid: ${product.layersPerSkid}"
            productView.textView_totalqty.text = "Total Qty: ${product.totalQty}"
            productView.textView_prodnum.text = "Product #: ${product.number}"
            productView.textView_upc.text = "UPC Code: ${product.uPC}"
            val qtySelector: QtySelector = productView.findViewById(R.id.qty_selector)
            val btnAddToCart = productView.findViewById<Button>(R.id.btnAddToCart)
            val showQty1 = product.showQty1
            if (TextUtils.isEmpty(showQty1)) {
                productView.findViewById<View>(R.id.textView_qty1_field).visibility = View.GONE
            } else {
                (productView.findViewById<View>(R.id.textView_qty1_field) as TextView).text = "${product.showQty1}: ${product.qty1}"
            }
            val showQy2 = product.showQty2
            if (TextUtils.isEmpty(showQy2)) {
                productView.findViewById<View>(R.id.textView_qty2_field).visibility = View.GONE
            } else {
                (productView.findViewById<View>(R.id.textView_qty2_field) as TextView).text = "${product.showQty2}: ${product.qty2}"
            }
            val showQty3 = product.showQty3
            if (TextUtils.isEmpty(showQty3)) {
                productView.findViewById<View>(R.id.textView_qty3_field).visibility = View.GONE
            } else {
                (productView.findViewById<View>(R.id.textView_qty3_field) as TextView).text = "${product.showQty3}: ${product.qty3}"
            }
            val showQty4 = product.showQty4
            if (TextUtils.isEmpty(showQty4)) {
                productView.findViewById<View>(R.id.textView_qty4_field).visibility = View.GONE
            } else {
                (productView.findViewById<View>(R.id.textView_qty4_field) as TextView).text = "${product.showQty4}: ${product.qty4}"
            }
            val note01 = product.note01
            val note02 = product.note02
            val note03 = product.note03
            val note04 = product.note04
            val note05 = product.note05
            if (TextUtils.isEmpty(note01)) {
                productView.textView_note01.visibility = View.GONE
            } else {
                productView.textView_note01.text = note01
            }
            if (TextUtils.isEmpty(note02)) {
                productView.textView_note02.visibility = View.GONE
            } else {
                productView.textView_note02.text = note02
            }
            if (TextUtils.isEmpty(note03)) {
                productView.textView_note03.visibility = View.GONE
            } else {
                productView.textView_note03.text = note03
            }
            if (TextUtils.isEmpty(note04)) {
                productView.textView_note04.visibility = View.GONE
            } else {
                productView.textView_note04.text = note04
            }
            if (TextUtils.isEmpty(note05)) {
                productView.textView_note05.visibility = View.GONE
            } else {
                productView.textView_note05.text = note05
            }
            val qtySelectorClickListener: QtySelectorClickListener = object : QtySelectorClickListener {
                override fun onPlusButtonClick(): View.OnClickListener {
                    return View.OnClickListener { qtySelector.incrementQty() }
                }

                override fun onMinusButtonClick(): View.OnClickListener {
                    return View.OnClickListener { qtySelector.decrementQty() }
                }
            }
            qtySelector.setQtySelectorClickListener(qtySelectorClickListener)
            qtySelector.setProduct(product)
            btnAddToCart.setOnClickListener {
                MixPanelManager.trackButtonClick(context, "Button Click: Add to cart")
                if ("Y".equals(product.popUpPriceFlag, ignoreCase = true)) {
                    val qtyBuilder = AlertDialog.Builder(context)
                    val view = LayoutInflater.from(context).inflate(R.layout.dialog_product_qty, null, false)
                    view.edt_price.text = product.popUpPrice
                    qtyBuilder.setView(view)
                    val dialog = qtyBuilder.create()
                    dialog.show()
                    view.btn_done.setOnClickListener {
                        dialog.hide()
                        val newPrice = view.edt_price.text.toString()
                        if (qtySelector.selectedQty > 0) {
                            Utilities.updateShoppingCart(TAG, context, product, qtySelector.selectedQty, newPrice, qtySelector.itemType, ProductEnteredFrom.PRODUCT_LIST) { qty, itemType ->
                                qtySelector.setQty(0)
                                qtySelector.invalidate()
                            }
                        } else {
                            Toast.makeText(context, "Please provide quantity before adding a product.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    if (qtySelector.selectedQty > 0) {
                        Utilities.updateShoppingCart(TAG, context, product, qtySelector.selectedQty, null, qtySelector.itemType, ProductEnteredFrom.PRODUCT_LIST) { qty, itemType ->
                            qtySelector.setQty(0)
                            qtySelector.invalidate()
                        }
                    } else {
                        Toast.makeText(context, "Please provide quantity before adding a product.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            listOfProducts.add(productView)
        }
        horizontalList.setListOfViews(listOfProducts)
        return mainView
    }

    override fun onStart() {
        super.onStart()
        ViewUtilities.setActivityWindowSize(dialog!!.window!!)
    }
}