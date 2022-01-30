package com.bedessee.sales.utilities

import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.widget.TextView

class FieldUtilities {
    companion object {
        /**
         * Utility function for setting up the data and colors for a field
         */
        fun setupField(textView: TextView, fieldText: String, valueText: String, strValueColor: String?, strValueBackgroundColor: String?) {
            val spannableStringField = SpannableString(fieldText)
            val spannableStringValue = SpannableString(valueText)
            val valueColor = Utilities.parseSaveColor(strValueColor)
            if (valueColor != null) {
                spannableStringValue.setSpan(ForegroundColorSpan(valueColor), 0, spannableStringValue.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            val priceBackgroundColor = Utilities.parseSaveColor(strValueBackgroundColor)
            if (priceBackgroundColor != null) {
                spannableStringValue.setSpan(BackgroundColorSpan(priceBackgroundColor), 0, spannableStringValue.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            textView.text = spannableStringField
            textView.append(spannableStringValue)
        }

        /**
         * Utility function for setting up the data and colors for a field
         */
        fun setupField(textView: TextView, valueText: String, strValueColor: String, strValueBackgroundColor: String) {
            val spannableStringValue = SpannableString(valueText)
            val valueColor = Utilities.parseSaveColor(strValueColor)
            if (valueColor != null) {
                spannableStringValue.setSpan(ForegroundColorSpan(valueColor), 0, spannableStringValue.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            val priceBackgroundColor = Utilities.parseSaveColor(strValueBackgroundColor)
            if (priceBackgroundColor != null) {
                spannableStringValue.setSpan(BackgroundColorSpan(priceBackgroundColor), 0, spannableStringValue.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            textView.append(spannableStringValue)
        }
    }
}