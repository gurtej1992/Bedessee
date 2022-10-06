package com.bedessee.salesca.product

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.ActivityInfo
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.loader.app.LoaderManager
import com.bedessee.salesca.R
import com.bedessee.salesca.customview.QtySelector
import com.bedessee.salesca.customview.QtySelector.QtySelectorClickListener
import com.bedessee.salesca.mixpanel.MixPanelManager
import com.bedessee.salesca.product.ProductFragment.shouldRestartLoaderOnResume
import com.bedessee.salesca.provider.Contract
import com.bedessee.salesca.provider.ProviderUtils
import com.bedessee.salesca.sharedprefs.SharedPrefsManager
import com.bedessee.salesca.store.StoreManager
import com.bedessee.salesca.utilities.FieldUtilities.Companion.setupField
import com.bedessee.salesca.utilities.FileUtilities.Companion.getFile
import com.bedessee.salesca.utilities.FileUtilities.Companion.openPDF
import com.bedessee.salesca.utilities.ProductEnteredFrom
import com.bedessee.salesca.utilities.Utilities
import com.bedessee.salesca.utilities.ViewUtilities
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.dialog_product_details.view.*
import kotlinx.android.synthetic.main.dialog_product_qty.view.*
import java.io.File

class ProductDetailDialog : DialogFragment() {

    companion object {
        private const val TAG = "ProductDetailsDialog"

        fun create(product: Product) = ProductDetailDialog().apply {
            this.product = product
        }

        fun create(product: Product, btnZoomClickedRunnable: Runnable) = ProductDetailDialog().apply {
            this.product = product
            this.btnZoomClickedRunnable = btnZoomClickedRunnable
        }
    }

    private val loaderCallbacks = object : androidx.loader.app.LoaderManager.LoaderCallbacks<Cursor> {

        override fun onLoadFinished(loader: androidx.loader.content.Loader<Cursor?>, cursor: Cursor?) {
            productAdapter.changeCursor(cursor)
        }

        override fun onLoaderReset(loader: androidx.loader.content.Loader<Cursor?>) {
            productAdapter.changeCursor(null)
        }

        override fun onCreateLoader(id: Int, args: Bundle?): androidx.loader.content.Loader<Cursor?> {
            var searchString = ""
            val likeTag = product.likeTag
            if (SharedPrefsManager(context).useNewLikeLogic == "YES" && !TextUtils.isEmpty(likeTag)) {
                searchString = searchString + Contract.ProductColumns.COLUMN_LIKE_TAG + " = '" + product.likeTag + "'"
            } else {
                for (i in searchStrings.indices) {
                    searchString = searchString + Contract.ProductColumns.COLUMN_DESCRIPTION + " like '%" + searchStrings[i] + "%'" + if (i == searchStrings.size - 1) "" else " OR "
                }
            }
            return androidx.loader.content.CursorLoader(requireContext(), Contract.Product.CONTENT_URI, null, searchString, null, null)

        }
    }

    private lateinit var product: Product
    private lateinit var productAdapter: ProductAdapter
    private var btnZoomClickedRunnable: Runnable? = null
    private lateinit var searchStrings: Array<String>

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_product_details, container, false)

            MixPanelManager.trackProductView(context, "${product.brand} ${product.description}")

        val sh = requireActivity().getSharedPreferences("setting", AppCompatActivity.MODE_PRIVATE)
        val orient = sh.getString("orientation", "landscape")
        requireActivity().requestedOrientation = if (orient == "landscape") {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            view.findViewById<View>(R.id.like_label).visibility = View.GONE
            view.findViewById<View>(R.id.horizontalScrollView_similarProducts).visibility = View.GONE
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
            val productNum = product.number
            val currentStore = StoreManager.getCurrentStore()
            var custSpecPrice: CustSpecPrice? = null

            if (currentStore != null) {
                val custSpecPriceCursor = requireContext().contentResolver.query(Contract.CustSpecPrice.CONTENT_URI, null, Contract.CustSpecPriceColumns.COLUMN_CUST_NUM + " = ? AND " + Contract.CustSpecPriceColumns.COLUMN_PROD_NUM + " = ?", arrayOf(currentStore.number, productNum), null)
                    if (custSpecPriceCursor != null && custSpecPriceCursor.moveToFirst()) {
                    custSpecPrice = ProviderUtils.cursorToCustSpecPrice(custSpecPriceCursor)
                    custSpecPriceCursor.close()
                }
            }
            val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)

            if (custSpecPrice != null) {
                view.textView_special_available.visibility = View.VISIBLE
            }

            var image = product.getLargeImagePath(context)
            if (!image.isNullOrBlank()) {
                val f = File(image)

                if (!f.exists() || !f.isFile) {
                    image = product.imagePath!!
                }
            } else {
                image = product.imagePath!!
            }
            val file = File(image)

            val imageDimValue = SharedPrefsManager(context).fadePercentage.toFloat()
            view.background_dimmer.alpha = imageDimValue / 100f

            Glide.with(this).load(file).into(view.product_image);

            view.btn_share.setOnClickListener {
                Log.e("@@@@@","get value  file://${file.absolutePath}")
                MixPanelManager.trackButtonClick(context, "Button Click: Image Share")
                Utilities.shareImage(context, "file://${file.absolutePath}")

            }
        view.btn_code.setOnClickListener {

            val file = getFile(
                context!!, product.number!!, "PDF",
                "barcode"
            )

            if (file.exists()) {
                openPDF(requireContext(), file)
            }
            else{
                Toast.makeText(context,"No barcode found!",Toast.LENGTH_SHORT).show()
            }
        }

            view.btn_zoom.setOnClickListener {
                MixPanelManager.trackButtonClick(context, "Button Click: Image Zoo ")
                btnZoomClickedRunnable?.run()
                FullScreenImageActivity.launch(requireContext(), Uri.parse("file://${file.absolutePath}"))
            }

            view.textView_brand.text = "${product.brand} ${product.description}"
            view.textView_uom.text = "Unit: ${product.pieceUom}"

            val casePrice = if (custSpecPrice == null || TextUtils.isEmpty(custSpecPrice.price)) product.casePrice else custSpecPrice.price
            setupField(
                    view.textView_price_field,
                    requireContext().getString(R.string.field_string_formatter, product.caseUom),
                    requireContext().getString(R.string.quantity_string_formatter, casePrice),
                    product.lPriceColor!!,
                    product.lPriceBackgroundColor!!
            )

            if (sharedPrefs.getBoolean("pref_show_level1price", true)) {
                val level1price = if (custSpecPrice == null || TextUtils.isEmpty(custSpecPrice.level1Price)) product.level1Price else custSpecPrice.level1Price
                setupField(
                        view.textView_level1price_field,
                        if (level1price == "0.00") "N/A" else level1price!!,
                        product.level1PriceColor!!,
                        product.level1BackgroundColor!!
                )
            } else {
                view.textView_level1price_field.visibility = View.GONE
            }

            if (sharedPrefs.getBoolean("pref_show_level2price", true)) {
                val level2price = if (custSpecPrice == null || TextUtils.isEmpty(custSpecPrice.level2Price)) product.level2Price else custSpecPrice.level2Price
                setupField(
                        view.textView_level2price_field,
                        if (level2price == "0.00") "N/A" else level2price!!,
                        product.level2PriceColor!!,
                        product.level2BackgroundColor!!
                )
            } else {
                view.textView_level2price_field.visibility = View.GONE
            }

            if (sharedPrefs.getBoolean("pref_show_level3price", true)) {
                val level3price = if (custSpecPrice == null || TextUtils.isEmpty(custSpecPrice.level3Price)) product.level3Price else custSpecPrice.level3Price
                setupField(
                        view.textView_level3price_field,
                        if (level3price == "0.00") "N/A" else level3price!!,
                        product.level3PriceColor!!,
                        product.level3BackgroundColor!!
                )
            } else {
                view.findViewById<View>(R.id.textView_level3price_field).visibility = View.GONE
            }

            val unitPrice = if (custSpecPrice == null || TextUtils.isEmpty(custSpecPrice.unitPrice)) product.piecePrice else custSpecPrice.unitPrice
            setupField(
                    view.textView_unitprice_field,
                    "${product.pieceUom} $unitPrice",
                    product.lPriceColor!!,
                    product.lPriceBackgroundColor!!
            )

            view.textView_status.text = "Status: ${product.mStatus}"

            view.textView_casesperskid.text = "Cases Per Skid: ${product.casesPerSkid}"
            view.textView_casesperrow.text = "Cases Per Row: ${product.casesPerRow}"
            view.textView_layersperskid.text = "Layers Per Skid: ${product.layersPerSkid}"
            view.textView_totalqty.text = "Total Qty: ${product.totalQty}"

            setupDataInfo(view, product, custSpecPrice)

            view.textView_prodnum.text = "Product #: ${product.number}"
            view.textView_upc.text = "UPC Code: ${product.uPC}"

            val gridViewSimilarProducts = view.horizontalScrollView_similarProducts
            searchStrings = product.description!!.split(" ").toTypedArray()

            productAdapter = ProductAdapter(context, Utilities.getScreenDimensInPx(activity))
            productAdapter.setListener { product ->
                MixPanelManager.selectProduct(activity, product.brand + " " + product.description)
                create(product, Runnable { shouldRestartLoaderOnResume = false }).show(fragmentManager!!, TAG)
                dismiss()
            }

            gridViewSimilarProducts.adapter = productAdapter

            if (SharedPrefsManager(context).useNewLikeLogic == "YES" && !TextUtils.isEmpty(product.likeTag)) {
                view.like_label.text = "SIMILAR PRODUCTS (${product.likeTag})"
            }

            LoaderManager.getInstance(this).initLoader(searchStrings.contentHashCode(), null, loaderCallbacks)

            val qtySelector: QtySelector = view.findViewById(R.id.qty_selector)

            val btnAddToCart = view.findViewById<Button>(R.id.btnAddToCart)

            val qtySelectorClickListener: QtySelectorClickListener = object : QtySelectorClickListener {
                override fun onPlusButtonClick(): View.OnClickListener {
                    return View.OnClickListener {
                        qtySelector.incrementQty()
                        btnAddToCart.performClick()
                    }
                }

                override fun onMinusButtonClick(): View.OnClickListener {
                    return View.OnClickListener { qtySelector.decrementQty() }
                }
            }

            qtySelector.setProduct(product)
            qtySelector.setQtySelectorClickListener(qtySelectorClickListener)

            btnAddToCart.setOnClickListener {
                if (checkCurrentStore()) {
                    if (qtySelector.selectedQty > 0) {
                        MixPanelManager.trackButtonClick(context, "Button Click: Add to cart")
                        if (product.popUpPriceFlag.equals("Y", ignoreCase = true)) {
                            val qtyBuilder = AlertDialog.Builder(context)
                            val quantityView = LayoutInflater.from(context).inflate(R.layout.dialog_product_qty, null, false)
                            val price = view.findViewById<TextView>(R.id.edt_price)
                            price.text = product.popUpPrice
                            qtyBuilder.setView(view)
                            val dialog = qtyBuilder.create()
                            dialog.show()
                            view.findViewById<View>(R.id.closeView).setOnClickListener { dialog.hide() }
                            view.findViewById<View>(R.id.btn_done).setOnClickListener {
                                dialog.hide()
                                val newPrice = (view.findViewById<View>(R.id.edt_price) as TextView).text.toString()
                                Utilities.updateShoppingCart("inc",TAG, context, product, qtySelector.selectedQty, newPrice, qtySelector.itemType, ProductEnteredFrom.PRODUCT_LIST) { qty, itemType -> qtySelector.setQty(0) }
                            }
                            quantityView.btn_delete.setOnClickListener { price.text = null }
                            quantityView.btn0.setOnClickListener { price.text = price.text.toString() + "0" }
                            quantityView.btn1.setOnClickListener { price.text = price.text.toString() + "1" }
                            quantityView.btn2.setOnClickListener { price.text = price.text.toString() + "2" }
                            quantityView.btn3.setOnClickListener { price.text = price.text.toString() + "3" }
                            quantityView.btn4.setOnClickListener { price.text = price.text.toString() + "4" }
                            quantityView.btn5.setOnClickListener { price.text = price.text.toString() + "5" }
                            quantityView.btn6.setOnClickListener { price.text = price.text.toString() + "6" }
                            quantityView.btn7.setOnClickListener { price.text = price.text.toString() + "7" }
                            quantityView.btn8.setOnClickListener { price.text = price.text.toString() + "8" }
                            quantityView.btn9.setOnClickListener { price.text = price.text.toString() + "9" }
                            quantityView.btnComma.setOnClickListener { price.text = price.text.toString() + "." }
                        } else {
                            Utilities.updateShoppingCart("inc",TAG, context, product, qtySelector.selectedQty, null, qtySelector.itemType, ProductEnteredFrom.PRODUCT_LIST) { qty, itemType -> qtySelector.setQty(0) }
                        }
                    } else {
                        MixPanelManager.trackButtonClick(context, "Button Click: Add to cart: ZERO QTY")
                        Toast.makeText(context, "Please provide quantity before adding a product.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            view.findViewById<View>(R.id.imageView_close).setOnClickListener {
                dismiss()
            }


        return view
    }

    @SuppressLint("SetTextI18n")
    private fun setupDataInfo(view: View, product: Product, custSpecPrice: CustSpecPrice?) {
        val showQty1 = product.showQty1
        if (TextUtils.isEmpty(showQty1)) {
            view.textView_qty1_field.visibility = View.GONE
        } else {
            view.textView_qty1_field.text = "${product.showQty1} ${product.qty1}"
        }

        val showQy2 = product.showQty2
        if (TextUtils.isEmpty(showQy2)) {
            view.textView_qty2_field.visibility = View.GONE
        } else {
            view.textView_qty2_field.text = "${product.showQty2} ${product.qty2}"
        }

        val showQty3 = product.showQty3
        if (TextUtils.isEmpty(showQty3)) {
            view.textView_qty3_field.visibility = View.GONE
        } else {
            view.textView_qty3_field.text = "${product.showQty3} ${product.qty3}"
        }

        val showQty4 = product.showQty4
        if (TextUtils.isEmpty(showQty4)) {
            view.textView_qty4_field.visibility = View.GONE
        } else {
            view.textView_qty4_field.text = "${product.showQty4} ${product.qty4}"
        }

        val note01 = if (custSpecPrice == null || TextUtils.isEmpty(custSpecPrice.note01)) product.note01 else custSpecPrice.note01
        val note02 = if (custSpecPrice == null || TextUtils.isEmpty(custSpecPrice.note02)) product.note02 else custSpecPrice.note02
        val note03 = if (custSpecPrice == null || TextUtils.isEmpty(custSpecPrice.note03)) product.note03 else custSpecPrice.note03
        val note04 = if (custSpecPrice == null || TextUtils.isEmpty(custSpecPrice.note04)) product.note04 else custSpecPrice.note04
        val note05 = if (custSpecPrice == null || TextUtils.isEmpty(custSpecPrice.note05)) product.note05 else custSpecPrice.note05

        if (TextUtils.isEmpty(note01)) {
            view.textView_note01.visibility = View.GONE
        } else {
            view.textView_note01.text = note01
        }

        if (TextUtils.isEmpty(note02)) {
            view.textView_note02.visibility = View.GONE
        } else {
            view.textView_note02.text = note02
        }

        if (TextUtils.isEmpty(note03)) {
            view.textView_note03.visibility = View.GONE
        } else {
            view.textView_note03.text = note03
        }

        if (TextUtils.isEmpty(note04)) {
            view.textView_note04.visibility = View.GONE
        } else {
            view.textView_note04.text = note04
        }

        if (TextUtils.isEmpty(note05)) {
            view.textView_note05.visibility = View.GONE
        } else {
            view.textView_note05.text = note05
        }
    }

    override fun onStart() {
        super.onStart()
        ViewUtilities.setActivityWindowSize(dialog!!.window!!)
    }

    private fun checkCurrentStore() : Boolean {
        if (StoreManager.getCurrentStore() == null)  {
            Toast.makeText(context, getString(R.string.select_store), Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }
}