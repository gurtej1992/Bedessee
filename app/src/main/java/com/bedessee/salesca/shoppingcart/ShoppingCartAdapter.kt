package com.bedessee.salesca.shoppingcart

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.bedessee.salesca.R
import com.bedessee.salesca.customview.DialogNumberPad
import com.bedessee.salesca.customview.DialogNumberPad.Companion.newInstance
import com.bedessee.salesca.customview.DialogNumberPad.DefaultNumberPad
import com.bedessee.salesca.customview.GenericDialog
import com.bedessee.salesca.customview.GenericDialog.Companion.newInstance
import com.bedessee.salesca.customview.ItemType
import com.bedessee.salesca.provider.Contract
import com.bedessee.salesca.provider.ProviderUtils
import com.bedessee.salesca.store.StoreManager
import com.bedessee.salesca.utilities.Utilities


class ShoppingCartAdapter(
         val mContext: Context,
         val mShoppingCartProducts: List<ShoppingCartProduct>
) : RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder>() {

    private val TAG = "ShoppingCartAdapter"


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
                lateinit var radioPiece: RadioButton
        lateinit var radioCase: RadioButton
        lateinit var brand: TextView
        lateinit var description: TextView
        lateinit var edtQty: EditText
        lateinit var removeItem: ImageButton
        lateinit var totalCase:EditText
        lateinit var Price:TextView

        init {
            // Define click listener for the ViewHolder's View.
            radioPiece = view.findViewById(R.id.radioPiece)
            radioCase = view.findViewById(R.id.radioCase)
           edtQty = view.findViewById(R.id.edtQty)
           removeItem = view.findViewById(R.id.btnRemoveItem)
            brand = view.findViewById(R.id.textView_brand)
           description = view.findViewById(R.id.textView_description)
            totalCase=view.findViewById(R.id.textView_totalCase)
           Price=view.findViewById(R.id.textView_totalFull)
        }
    }

    private fun getQuantity(shoppingCartProduct: ShoppingCartProduct?): String {
        return shoppingCartProduct!!.quantity.toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.shopping_cart_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
       return mShoppingCartProducts.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val shoppingCartProduct = mShoppingCartProducts[position]
        val product = shoppingCartProduct!!.product
        holder.radioPiece.isChecked = shoppingCartProduct.itemType === ItemType.PIECE
        holder.radioCase.isChecked = shoppingCartProduct.itemType === ItemType.CASE
        holder.brand.text = product.brand
        val price = shoppingCartProduct.enteredPrice
        val hidePrice = TextUtils.isEmpty(price) || price.equals("null", ignoreCase = true)
        holder.description.text = "${product.description} ~ ${product.caseUom}"
        // holder.description.text = "${product.description} ~ ${product.caseUom} ~ ${product.number}" + if (!hidePrice) "" else " price: ${shoppingCartProduct.enteredPrice}"
        holder.edtQty.setText(getQuantity(shoppingCartProduct))
        val shared = mContext.getSharedPreferences("includeprice", AppCompatActivity.MODE_PRIVATE)
        val value= shared.getBoolean("show",true)

        val f:Float
        if(holder.radioCase.isChecked){

            if(value){

            if((getQuantity(shoppingCartProduct).toInt()> product.lvl0From!!.toInt()&&getQuantity(shoppingCartProduct).toInt()<= product.lvl0To!!.toInt())||product.lvl0To.equals("999")){
                f = java.lang.Float.valueOf(product.lvl0Price.toString())
                holder.totalCase.setText("$" + String.format("%.2f",f))
                holder.Price.setText("$" + String.format("%.2f",(getQuantity(shoppingCartProduct).toDouble() * product.lvl0Price!!.toDouble())))
            }else if((getQuantity(shoppingCartProduct).toInt()>= product.lvl1From!!.toInt()&&getQuantity(shoppingCartProduct).toInt()<= product.lvl1To!!.toInt())||product.lvl1To.equals("999"))

            {
                f = java.lang.Float.valueOf(product.lvl1Price.toString())
                holder.totalCase.setText("$" + String.format("%.2f",f))
                holder.Price.setText("$" + String.format("%.2f",(getQuantity(shoppingCartProduct).toDouble() * product.lvl1Price!!.toDouble())))
            }
            else if((getQuantity(shoppingCartProduct).toInt()>= product.lvl2From!!.toInt()&&getQuantity(shoppingCartProduct).toInt()<= product.lvl2To!!.toInt())||product.lvl2To.equals("999")){
                f = java.lang.Float.valueOf(product.lvl2Price.toString())
                holder.totalCase.setText("$" + String.format("%.2f",f))
                holder.Price.setText("$" + String.format("%.2f",(getQuantity(shoppingCartProduct).toDouble() * product.lvl2Price!!.toDouble())))
            }
            else{
                f = java.lang.Float.valueOf(product.lvl3Price.toString())
                holder.totalCase.setText("$" + String.format("%.2f",f))
                holder.Price.setText("$" + String.format("%.2f",(getQuantity(shoppingCartProduct).toDouble() * product.lvl3Price!!.toDouble())))

            }

        }else {

                            f = java.lang.Float.valueOf(product.casePrice.toString())
            holder.totalCase.setText("$" + String.format("%.2f",f))
            holder.Price.setText("$" + String.format("%.2f",(getQuantity(shoppingCartProduct).toDouble() * product.casePrice!!.toDouble())))

            }
        }
        else{
            val parts: List<String> = product.piecePrice.toString().split(" ")
            if(parts.size==2){
                val part1 = parts.first() // 004

                val part2 = parts[1]
                f = java.lang.Float.valueOf(part2)
                holder.totalCase.setText("$" + String.format("%.2f",f))
                holder.Price.setText("$" + String.format("%.2f",(getQuantity(shoppingCartProduct).toDouble() * part2!!.toDouble())))

            }else{
                val part1 = parts.first() // 004

                val part2 = parts[1]
                f = java.lang.Float.valueOf(product.piecePrice.toString())
                holder.totalCase.setText("$" + String.format("%.2f",f))
                holder.Price.setText("$" + String.format("%.2f",(getQuantity(shoppingCartProduct).toDouble() * part2!!.toDouble())))

            }

        }



        holder.removeItem.setOnClickListener {
            val products = ShoppingCart.getCurrentShoppingCart().products
            val index = products.indexOf(shoppingCartProduct)
            val selectedProduct = products[index].product
            val orderId = ShoppingCart.getCurrentOrderId(mContext)
            newInstance("", mContext.getString(R.string.delete_item_shopping_cart_message, selectedProduct.brand, selectedProduct.description), object : GenericDialog.OnClickListener {
                override fun onClick(dialog: DialogFragment) {
                    mContext.contentResolver.delete(Contract.SavedItem.CONTENT_URI, Contract.SavedItemColumns.COLUMN_ORDER_ID + " = ?" + " AND " + Contract.SavedItemColumns.COLUMN_PRODUCT_NUMBER + " = ?", arrayOf(orderId, product.number))
                    val cursor: Cursor? = mContext.getContentResolver().query(
                        Contract.SavedOrder.CONTENT_URI,
                        null,
                        Contract.SavedOrderColumns.COLUMN_ID + " = ?",
                        arrayOf(ShoppingCart.getCurrentOrderId(mContext)),
                        null
                    )
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            val order = ProviderUtils.cursorToSavedOrder(cursor)

                            //comment
                            if (order != null) {
                                val contentValues = ContentValues(1)
                                contentValues.put(
                                    Contract.SavedOrderColumns.COLUMN_NUM_PRODUCTS,
                                    order.numProducts - shoppingCartProduct!!.quantity
                                )
                                contentValues.put(
                                    Contract.SavedOrderColumns.COLUMN_STORE,
                                    StoreManager.getCurrentStore().baseNumber
                                )
                                mContext.getContentResolver().update(
                                    Contract.SavedOrder.CONTENT_URI,
                                    contentValues,
                                    Contract.SavedOrderColumns.COLUMN_ID + " = ?",
                                    arrayOf(ShoppingCart.getCurrentOrderId(mContext))
                                )
                            }
                        }
                    }

                    ShoppingCart.getCurrentShoppingCart().products.remove(shoppingCartProduct)
                    notifyDataSetChanged()
                    ShoppingCart.getCurrentShoppingCart().productChanged();
                }
            }, null).show((mContext as AppCompatActivity).supportFragmentManager, TAG)
        }
        holder.edtQty.setOnClickListener {

            AlertDialog.Builder(mContext).setTitle("Do you want to add quantity or replace?")
                .setPositiveButton(
                    "Add"
                ) { dialog, which ->
                    newInstance(object : DialogNumberPad.OnItemSelectedListener {
                        override fun onSelected(itemType: ItemType, qty: Int) {
                            val products = ShoppingCart.getCurrentShoppingCart().products
                            val index = products.indexOf(shoppingCartProduct)
                            Utilities.updateShoppingCart("inc",TAG, mContext, products[index].product, qty, null, itemType, null) { newQty, newItemType ->
                                products[index].quantity = newQty
                                products[index].itemType = newItemType
                                notifyDataSetChanged()
                                ShoppingCart.getCurrentShoppingCart().productChanged();
                            }
                            holder.edtQty.setText(getQuantity(shoppingCartProduct))
                            val f:Float
                            if(holder.radioCase.isChecked){

                                if(value){

                                    if((getQuantity(shoppingCartProduct).toInt()> product.lvl0From!!.toInt()&&getQuantity(shoppingCartProduct).toInt()<= product.lvl0To!!.toInt())||product.lvl0To.equals("999")){
                                        holder.Price.setText("$"+ String.format("%.2f",qty* product.lvl0Price!!.toDouble()))

                                    }else if((getQuantity(shoppingCartProduct).toInt()> product.lvl1From!!.toInt()&&getQuantity(shoppingCartProduct).toInt()<= product.lvl1To!!.toInt())||product.lvl1To.equals("999")){
                                        holder.Price.setText("$"+ String.format("%.2f",qty* product.lvl1Price!!.toDouble()))
                                    }
                                    else if((getQuantity(shoppingCartProduct).toInt()> product.lvl2From!!.toInt()&&getQuantity(shoppingCartProduct).toInt()<= product.lvl2To!!.toInt())||product.lvl2To.equals("999")){
                                        holder.Price.setText("$"+ String.format("%.2f",qty* product.lvl2Price!!.toDouble()))
                                    }else{
                                        holder.Price.setText("$"+ String.format("%.2f",qty* product.lvl3Price!!.toDouble()))
                                    }
                                }
                                else {
                                    holder.Price.setText("$"+ String.format("%.2f",qty* product.casePrice!!.toDouble()))
                                }


                            }else {
                                val parts: List<String> = product.piecePrice.toString().split(" ")
                                if(parts.size==2){
                                    val part1 = parts.first() // 004

                                    val part2 = parts[1]
                                    f = java.lang.Float.valueOf(part2)
                                    holder.totalCase.setText("$" + String.format("%.2f",f))
                                    holder.Price.setText("$" + String.format("%.2f", qty * part2!!.toDouble()))

                                }else{
                                    val part1 = parts.first() // 004

                                    val part2 = parts[1]
                                    f = java.lang.Float.valueOf(product.piecePrice.toString())
                                    holder.totalCase.setText("$" + String.format("%.2f",f))
                                    holder.Price.setText("$" + String.format("%.2f", qty * part2!!.toDouble()))

                                }
                            }

                        }
                    }, DefaultNumberPad(shoppingCartProduct.itemType, getQuantity(shoppingCartProduct)), getQuantity(shoppingCartProduct)).show((mContext as AppCompatActivity).supportFragmentManager, TAG)

                    dialog.dismiss()

                }
                .setNegativeButton(
                    "Replace"
                ) { dialog, which -> // Do nothing
                    newInstance(object : DialogNumberPad.OnItemSelectedListener {
                        override fun onSelected(itemType: ItemType, qty: Int) {
                            val products = ShoppingCart.getCurrentShoppingCart().products
                            val index = products.indexOf(shoppingCartProduct)
                            Utilities.updateShoppingCart("update",TAG, mContext, products[index].product, qty, null, itemType, null) { newQty, newItemType ->
                                products[index].quantity = qty
                                products[index].itemType = newItemType
                                notifyDataSetChanged()
                                ShoppingCart.getCurrentShoppingCart().productChanged();
                            }
                            holder.edtQty.setText(""+qty)
                            val f:Float
                            if(holder.radioCase.isChecked){

                                if(value){

                                    if((getQuantity(shoppingCartProduct).toInt()> product.lvl0From!!.toInt()&&getQuantity(shoppingCartProduct).toInt()<= product.lvl0To!!.toInt())||product.lvl0To.equals("999")){
                                        holder.Price.setText("$"+ String.format("%.2f",qty* product.lvl0Price!!.toDouble()))

                                    }else if((getQuantity(shoppingCartProduct).toInt()> product.lvl1From!!.toInt()&&getQuantity(shoppingCartProduct).toInt()<= product.lvl1To!!.toInt())||product.lvl1To.equals("999")){
                                        holder.Price.setText("$"+ String.format("%.2f",qty* product.lvl1Price!!.toDouble()))
                                    }
                                    else if((getQuantity(shoppingCartProduct).toInt()> product.lvl2From!!.toInt()&&getQuantity(shoppingCartProduct).toInt()<= product.lvl2To!!.toInt())||product.lvl2To.equals("999")){
                                        holder.Price.setText("$"+ String.format("%.2f",qty* product.lvl2Price!!.toDouble()))
                                    }else{
                                        holder.Price.setText("$"+ String.format("%.2f",qty* product.lvl3Price!!.toDouble()))
                                    }
                                }
                                else {
                                    holder.Price.setText("$"+ String.format("%.2f",qty* product.casePrice!!.toDouble()))
                                }


                            }else {
                                val parts: List<String> = product.piecePrice.toString().split(" ")
                                if(parts.size==2){
                                    val part1 = parts.first() // 004

                                    val part2 = parts[1]
                                    f = java.lang.Float.valueOf(part2)
                                    holder.totalCase.setText("$" + String.format("%.2f",f))
                                    holder.Price.setText("$" + String.format("%.2f", qty * part2!!.toDouble()))

                                }else{
                                    val part1 = parts.first() // 004

                                    val part2 = parts[1]
                                    f = java.lang.Float.valueOf(product.piecePrice.toString())
                                    holder.totalCase.setText("$" + String.format("%.2f",f))
                                    holder.Price.setText("$" + String.format("%.2f", qty * part2!!.toDouble()))

                                }
                            }

                        }
                    }, DefaultNumberPad(shoppingCartProduct.itemType, getQuantity(shoppingCartProduct)), getQuantity(shoppingCartProduct)).show((mContext as AppCompatActivity).supportFragmentManager, TAG)

                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }
}



