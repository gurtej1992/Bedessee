package com.bedessee.sales.shoppingcart

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.bedessee.sales.R
import com.bedessee.sales.customview.DialogNumberPad
import com.bedessee.sales.customview.DialogNumberPad.Companion.newInstance
import com.bedessee.sales.customview.DialogNumberPad.DefaultNumberPad
import com.bedessee.sales.customview.GenericDialog
import com.bedessee.sales.customview.GenericDialog.Companion.newInstance
import com.bedessee.sales.customview.ItemType
import com.bedessee.sales.provider.Contract
import com.bedessee.sales.utilities.Utilities

class ShoppingCartAdapter(
        private val mContext: Context,
        resource: Int, textViewResourceId: Int,
        private val mShoppingCartProducts: List<ShoppingCartProduct>
) : ArrayAdapter<Any?>(mContext, resource, textViewResourceId) {
    private val TAG = "ShoppingCartAdapter"

    override fun getItem(position: Int): ShoppingCartProduct? {
        return mShoppingCartProducts[position]
    }

    override fun getCount(): Int {
        return mShoppingCartProducts.size
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var convertView = view

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.shopping_cart_list_item, parent, false)
            val viewHolder = ViewHolder()
            viewHolder.radioPiece = convertView.findViewById(R.id.radioPiece)
            viewHolder.radioCase = convertView.findViewById(R.id.radioCase)
            viewHolder.edtQty = convertView.findViewById(R.id.edtQty)
            viewHolder.removeItem = convertView.findViewById(R.id.btnRemoveItem)
            viewHolder.brand = convertView.findViewById(R.id.textView_brand)
            viewHolder.description = convertView.findViewById(R.id.textView_description)
            convertView.tag = viewHolder
        }

        val holder = convertView!!.tag as ViewHolder

        val shoppingCartProduct = getItem(position)
        val product = shoppingCartProduct!!.product
        holder.radioPiece.isChecked = shoppingCartProduct.itemType === ItemType.PIECE
        holder.radioCase.isChecked = shoppingCartProduct.itemType === ItemType.CASE
        holder.brand.text = product.brand
        val price = shoppingCartProduct.enteredPrice
        val hidePrice = TextUtils.isEmpty(price) || price.equals("null", ignoreCase = true)
        holder.description.text = "${product.description} ~ ${product.caseUom} ~ ${product.number}" + if (!hidePrice) "" else " price: ${shoppingCartProduct.enteredPrice}"
        holder.edtQty.setText(getQuantity(shoppingCartProduct))
        holder.removeItem.setOnClickListener {
            val products = ShoppingCart.getCurrentShoppingCart().products
            val index = products.indexOf(shoppingCartProduct)
            val selectedProduct = products[index].product
            val orderId = ShoppingCart.getCurrentOrderId(mContext)
            newInstance("", mContext.getString(R.string.delete_item_shopping_cart_message, selectedProduct.brand, selectedProduct.description), object : GenericDialog.OnClickListener {
                override fun onClick(dialog: DialogFragment) {
                    mContext.contentResolver.delete(Contract.SavedItem.CONTENT_URI, Contract.SavedItemColumns.COLUMN_ORDER_ID + " = ?" + " AND " + Contract.SavedItemColumns.COLUMN_PRODUCT_NUMBER + " = ?", arrayOf(orderId, product.number))
                    ShoppingCart.getCurrentShoppingCart().products.remove(shoppingCartProduct)
                    notifyDataSetChanged()
                    ShoppingCart.getCurrentShoppingCart().productChanged();
                }
            }, null).show((mContext as AppCompatActivity).supportFragmentManager, TAG)
        }
        holder.edtQty.setOnClickListener {
            newInstance(object : DialogNumberPad.OnItemSelectedListener {
                override fun onSelected(itemType: ItemType, qty: Int) {
                    val products = ShoppingCart.getCurrentShoppingCart().products
                    val index = products.indexOf(shoppingCartProduct)
                    Utilities.updateShoppingCart(TAG, mContext, products[index].product, qty, null, itemType, null) { newQty, newItemType ->
                        products[index].quantity = newQty
                        products[index].itemType = newItemType
                        notifyDataSetChanged()
                        ShoppingCart.getCurrentShoppingCart().productChanged();
                    }
                }
            }, DefaultNumberPad(shoppingCartProduct.itemType, getQuantity(shoppingCartProduct)), getQuantity(shoppingCartProduct)).show((mContext as AppCompatActivity).supportFragmentManager, TAG)
        }
        return convertView
    }

    private fun getQuantity(shoppingCartProduct: ShoppingCartProduct?): String {
        return shoppingCartProduct!!.quantity.toString()
    }

    /**
     * Class for ViewHolder pattern.
     */
    private inner class ViewHolder {
        lateinit var radioPiece: RadioButton
        lateinit var radioCase: RadioButton
        lateinit var brand: TextView
        lateinit var description: TextView
        lateinit var edtQty: EditText
        lateinit var removeItem: ImageButton
    }
}