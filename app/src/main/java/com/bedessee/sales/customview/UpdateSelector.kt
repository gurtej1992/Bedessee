package com.bedessee.sales.customview

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bedessee.sales.R
import com.bedessee.sales.update.UpdateActivity
import com.bedessee.sales.utilities.ViewUtilities
import kotlinx.android.synthetic.main.update_selector.view.*

class UpdateSelector : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.update_selector, container, false)

        view.positiveButton.setOnClickListener {
            var updateDir = ""

            val radioButtonID = view.options.checkedRadioButtonId
            val radioButton = view.options.findViewById<View>(radioButtonID)

            when (view.options.indexOfChild(radioButton)) {
                0 -> updateDir = "01-MON"
                1 -> updateDir = "02-TUE"
                2 -> updateDir = "03-WED"
                3 -> updateDir = "04-THUR"
                4 -> updateDir = "05-FRI"
                5 -> updateDir = "R-D"
            }

            val updateIntent = Intent(context, UpdateActivity::class.java)
            updateIntent.putExtra(UpdateActivity.KEY_UPDATE_DIR, updateDir)
            context!!.startActivity(updateIntent)

            dismiss()
        }

        view.negativeButton.setOnClickListener {
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        ViewUtilities.setSmallDialogWindowSize(dialog!!.window!!)

    }
}