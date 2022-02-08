package com.bedessee.salesca.customview

import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bedessee.salesca.R
import com.bedessee.salesca.utilities.ViewUtilities
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.dialog_number_pad.view.*
import kotlinx.android.synthetic.main.dialog_number_pad.view.number_pad_title
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class DialogNumberPad : DialogFragment() {
    data class DefaultNumberPad(val kind: ItemType, val value: String)

    companion object {
        const val DEFAULT_INITIAL_VALUE = "0"

        /**
         * Creates a new instance of DialogNumberPad with value formatting and buttons case and piece visible.
         */
        fun newInstance(listenerItem: OnItemSelectedListener, defaultNumberPad: DefaultNumberPad? = null, initialValue: String = DEFAULT_INITIAL_VALUE) = DialogNumberPad().apply {
            itemListener = listenerItem
            kind = Kind.CASE_PIECE
            formatNumber = true
            this.defaultNumberPad = defaultNumberPad
            this.initialValue = initialValue
        }

        /**
         * Creates a new instance of DialogNumberPad with buttons case and piece hidden.
         *
         * @param formatNumber Tells if the number in the dialog must be formatted with comma for thousand groups.
         * @param allowDecimals Tells if want to accept numbers with decimals
         * @param showHint Tells if want to show text hint in input
         */
        fun newInstance(listener: OnNumberSelectedListener, formatNumber: Boolean, allowDecimals: Boolean, showHint: Boolean, initialValue: String = DEFAULT_INITIAL_VALUE) = DialogNumberPad().apply {
            numberListener = listener
            kind = Kind.NUMBER
            this.formatNumber = formatNumber
            showPoint = allowDecimals
            this.showHint = showHint
            this.initialValue = initialValue
        }

        /**
         * Creates a new instance of DialogNumberPad with buttons case and piece hidden.
         */
        fun newInstance(title:String? = null, listener: OnStringSelectedListener, initialValue: String = DEFAULT_INITIAL_VALUE) = DialogNumberPad().apply {
            stringListener = listener
            kind = Kind.STRING
            formatNumber = false
            showPoint = false
            showHint = false
            this.initialValue = initialValue
            this.customTitle = title
        }
    }

    interface OnNumberSelectedListener {
        fun onSelected(number: Double)
    }

    interface OnItemSelectedListener {
        fun onSelected(itemType: ItemType, qty: Int)
    }

    interface OnStringSelectedListener {
        fun onSelected(value: String)
    }

    //Views
    private lateinit var btnPoint: MaterialButton
    private lateinit var btn0: MaterialButton
    private lateinit var btnCase: Button
    private lateinit var btnPiece: Button
    private lateinit var quantity: EditText
    private lateinit var qtySelected: TextView

    //Control variables
    private lateinit var initialValue: String
    private lateinit var selectedItemType: ItemType
    private lateinit var kind: Kind
    private var formatNumber = false
    private var showPoint = false
    private var showHint = true
    private var defaultNumberPad: DefaultNumberPad? = null
    private var customTitle: String? = null

    //Listeners
    private lateinit var numberListener: OnNumberSelectedListener
    private lateinit var itemListener: OnItemSelectedListener
    private lateinit var stringListener: OnStringSelectedListener

    //Formatting control
    private val format = DecimalFormat()
    private lateinit var decimalSeparator: String

    /**
     * Represents DialogNumberPad functionality modes:
     *
     * - CASE_PIECE: Layout shows buttons case and piece, treat value as number and number formatting.
     * - NUMBER: Layout hides buttons case and piece, treat value as a number and variable number formatting.
     * - STRING: Layout hides buttons case and piece, treat values as a string and no number formatting.
     */
    enum class Kind {
        CASE_PIECE, NUMBER, STRING
    }

    private val quantityTextWatcher = object : TextWatcher {
        private var isFirstTime : Boolean = true

        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            quantity.removeTextChangedListener(this)
            val number = s.toString()

            if (number.isNotEmpty()) {
                val lastAddedChar = number[number.length - 1]
                if (isFirstTime && before != count) {
                   quantity.setText(lastAddedChar.toString())
                   isFirstTime = false
                } else {
                    //Check if last added value is not a point or a comma and number is not 0
                    if (lastAddedChar.compareTo('0') != 0 && lastAddedChar.toString().compareTo(decimalSeparator) != 0) {
                        quantity.setText(format.format(format.parse(number)).toString())
                    }
                }
            }

            quantity.addTextChangedListener(this)
            updateQtySelectedText()
        }
    }

    private val btnClickListener = View.OnClickListener { view ->
        val currentQty = quantity.text.toString()
        val btnText = (view as MaterialButton).text.toString()

        quantity.setText(getString(R.string.quantity, currentQty, btnText))
    }

    private val btnDeleteClickListener = View.OnClickListener {
        val currentQty = quantity.text.toString()

        if (quantity.text.isNotEmpty()) {
            if (quantity.text.length == 1) {
                if (kind == Kind.STRING) {
                    quantity.setText("")
                } else {
                    quantity.setText(getString(R.string.quantity_initial_value))
                }
            } else {
                quantity.setText(currentQty.substring(0, currentQty.length - 1))
            }
        }
    }

    private val btnDeleteLongClickListener = View.OnLongClickListener {
        if (kind == Kind.STRING) {
            quantity.setText("")
        } else {
            quantity.setText(getString(R.string.quantity_initial_value))
        }

        true
    }

    private val btnPointClickListener = View.OnClickListener {
        if (showPoint) {
            //Prevents adding more than one decimal separator
            if (!quantity.text.toString().contains(("\\" + decimalSeparator).toRegex())) {
                val currentQty = quantity.text.toString()

                quantity.setText(getString(R.string.quantity, currentQty, "."))
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //in kitkat the title is shown, with this title will be hidden.
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        val view = inflater.inflate(R.layout.dialog_number_pad, container, false)

        btnPoint = view.btn_point
        btn0 = view.btn_0
        btnCase = view.btn_case
        btnPiece = view.btn_piece
        quantity = view.quantity
        qtySelected = view.number_pad_title

        //Add listeners to each buttons
        btn0.setOnClickListener(btnClickListener)
        view.btn_1.setOnClickListener(btnClickListener)
        view.btn_2.setOnClickListener(btnClickListener)
        view.btn_3.setOnClickListener(btnClickListener)
        view.btn_4.setOnClickListener(btnClickListener)
        view.btn_5.setOnClickListener(btnClickListener)
        view.btn_6.setOnClickListener(btnClickListener)
        view.btn_7.setOnClickListener(btnClickListener)
        view.btn_8.setOnClickListener(btnClickListener)
        view.btn_9.setOnClickListener(btnClickListener)
        btnPoint.setOnClickListener(btnPointClickListener)
        view.btn_delete.setOnClickListener(btnDeleteClickListener)
        view.btn_delete.setOnLongClickListener(btnDeleteLongClickListener)

        setLocaleFormatting()

        //Changes layout depending kind
        when (kind) {
            Kind.CASE_PIECE -> {
                defaultNumberPad?.let {
                    when (it.kind) {
                        ItemType.CASE -> {
                            changeSelectedItemType(btnCase)

                        }
                        ItemType.PIECE -> {
                            changeSelectedItemType(btnPiece)
                        }
                        else -> {
                        }
                    }
                    quantity.setText(it.value)

                } ?: run {
                    //Sets default selected item type as Case
                    changeSelectedItemType(btnCase)
                }

                btnCase.setOnClickListener {
                    changeSelectedItemType(it)
                }
                btnPiece.setOnClickListener {
                    changeSelectedItemType(it)
                }

                btnCase.visibility = View.VISIBLE
                btnPiece.visibility = View.VISIBLE

                quantity.addTextChangedListener(quantityTextWatcher)
            }
            Kind.NUMBER -> {
                btnCase.visibility = View.GONE
                btnPiece.visibility = View.GONE

                if (showPoint) {
                    showPoint()
                } else {
                    hidePoint()
                }

                quantity.addTextChangedListener(quantityTextWatcher)
            }
            Kind.STRING -> {
                quantity.setText("")

                btnCase.visibility = View.GONE
                btnPiece.visibility = View.GONE

                if (showPoint) {
                    showPoint()
                } else {
                    hidePoint()
                }
            }
        }

        if (!showHint) {
            quantity.setText("")
            quantity.hint = ""
        }

        view.btn_close.setOnClickListener {
            dismiss()
        }

        view.btn_done.setOnClickListener {
            val result = getQuantity()

            when (kind) {
                Kind.CASE_PIECE -> {
                    itemListener.onSelected(selectedItemType, result.toInt())
                }
                Kind.NUMBER -> {
                    numberListener.onSelected(result.toDouble())
                }
                Kind.STRING -> {
                    stringListener.onSelected(result)
                }
            }

            dismiss()
        }

        setInitialValue(initialValue)

        customTitle?.let {
            view.number_pad_title.text = it
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        ViewUtilities.setDialogWindowSize(dialog!!.window!!)
    }

    private fun setInitialValue(initialValue: String) {
        quantity.setText(initialValue)
    }

    /**
     * Gets locale number formatting symbols and applies it to layout.
     */
    private fun setLocaleFormatting() {
        //Get symbols for grouping a decimal from locale
        val symbols = DecimalFormatSymbols.getInstance(Locale.getDefault())
        format.decimalFormatSymbols = symbols

        //Deactivate grouping when formatNumber flag is not set
        if (!formatNumber) {
            format.isGroupingUsed = false
        }

        //Update decimal separator symbol button with locale
        decimalSeparator = symbols.decimalSeparator.toString()
        btnPoint.text = decimalSeparator
    }

    /**
     * Updates selectedItemType, sets received view as selected and
     * hides/show point in consequence. When id is btn_case, shows point button,
     * when id is btn_piece, hides point button.
     *
     * @param view AppCompactButton where its id is btn_case or btn_piece.
     */
    private fun changeSelectedItemType(view: View) {
        view.isSelected = true

        when (view.id) {
            R.id.btn_case -> {
                showPoint = true
                showPoint()

                //Selects Case as selectedItemType
                selectedItemType = ItemType.CASE
                btnPiece.isSelected = false

                //Update typeface
                btnCase.typeface = Typeface.DEFAULT_BOLD
                btnPiece.typeface = Typeface.DEFAULT
            }
            R.id.btn_piece -> {
                showPoint = false
                hidePoint()

                //Eliminates decimal part
                val pointIndex = quantity.text.toString().indexOf(".")
                if (pointIndex != -1) {
                    val currentQty = quantity.text.toString()
                    quantity.setText(currentQty.subSequence(0, pointIndex))
                }

                //Sets Piece as selectedItemType
                selectedItemType = ItemType.PIECE
                btnCase.isSelected = false

                //Update typeface
                btnPiece.typeface = Typeface.DEFAULT_BOLD
                btnCase.typeface = Typeface.DEFAULT
            }
        }

        updateQtySelectedText()
    }

    private fun getQuantity(): String {
        return quantity.text.toString().replace(",".toRegex(), "")
    }

    private fun updateQtySelectedText() {
        if (kind == Kind.CASE_PIECE) {
            val qty = getQuantity()
            if (qty.toIntOrNull() != null) {
                when (selectedItemType) {
                    ItemType.CASE -> {
                        qtySelected.text = resources.getQuantityString(R.plurals.case_qty_selected, qty.toInt(), qty)
                    }
                    ItemType.PIECE -> {
                        qtySelected.text = resources.getQuantityString(R.plurals.piece_qty_selected, qty.toInt(), qty)
                    }
                    ItemType.NONE -> {

                    }
                }
            }
        }

    }

    private fun showPoint() {
        btnPoint.visibility = View.VISIBLE
    }

    private fun hidePoint() {
        btnPoint.visibility = View.INVISIBLE
    }
}