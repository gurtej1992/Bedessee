package com.bedessee.salesca.customview

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bedessee.salesca.R
import com.bedessee.salesca.sharedprefs.SharedPrefsManager
import com.bedessee.salesca.store.WebViewer
import com.bedessee.salesca.utilities.Utilities.getDifferenceDays
import com.bedessee.salesca.utilities.ViewUtilities
import kotlinx.android.synthetic.main.dialog_generic.view.*
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class GenericDialog : DialogFragment() {

    interface OnDismissListener {
        fun onDismiss()
    }

    interface OnClickListener {
        fun onClick(dialog: DialogFragment)
    }

    companion object {
        fun newInstance(title: String?, message: String, positiveListener: OnClickListener?, negativeListener: OnClickListener?) = GenericDialog().apply {
            this.title = title
            this.message = message
            this.positiveListener = positiveListener
            this.negativeListener = negativeListener
        }

        fun newInstance(title: String?, message: String, yesListener: OnClickListener?, positiveButtonText: String, noListener: OnClickListener?, negativeButtonText: String) = GenericDialog().apply {
            this.title = title
            this.message = message
            this.positiveListener = yesListener
            this.positiveButtonText = positiveButtonText
            this.negativeListener = noListener
            this.negativeButtonText = negativeButtonText
        }

        fun switchStoreInstance(context: Context, positiveListener: OnClickListener?) = GenericDialog().apply {
            this.title = context.getString(R.string.switch_store_dialog_title)
            this.message = context.getString(R.string.switch_store_dialog_message)
            this.positiveListener = positiveListener
        }

        /**
         * Displays the outstanding balance of a store. Dialog dismissing needs to be implemented manually.
         *
         * @param balance Balance of the store.
         */
        fun outstandingDialogInstance(context: Activity, balance: String, lastCollectDate: String, statementUrl: String, dismissListener: OnDismissListener?) = GenericDialog().apply {
            var collectDate = lastCollectDate
            if (collectDate.isNotEmpty()) {
                try {
                    val format = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
                    val date = format.parse(lastCollectDate)
                    val days = getDifferenceDays(date, Date())
                    collectDate = if (days == 1L) {
                        "\nLAST COLLECT DATE: $lastCollectDate\n($days DAY SINCE LAST PAYMENT)"
                    } else {
                        "\nLAST COLLECT DATE: $lastCollectDate\n($days DAYS SINCE LAST PAYMENT)"
                    }
                } catch (e: ParseException) {
                    Timber.e(e)
                }
            }

            title = context.getString(R.string.outstanding_balance)
            message = context.getString(R.string.amount_owed, balance, collectDate)
            positiveButtonText = context.getString(R.string.view_statement)
            negativeButtonText = context.getString(R.string.ok)
            icon = R.drawable.ic_alert
            positiveListener = object : OnClickListener {
                override fun onClick(dialog: DialogFragment) {
                    val path = SharedPrefsManager(context).sugarSyncDir
                    WebViewer.show(dialog, "$path/custstmt/$statementUrl")
                }
            }
            negativeListener = object : OnClickListener {
                override fun onClick(dialog: DialogFragment) {
                    dialog.dismiss()
                }
            }
            onDismissListener = dismissListener
            manageDismiss = false
        }
    }

    @DrawableRes
    private var icon: Int? = null
    private var title : String? = null
    private var message = ""
    private var positiveListener: OnClickListener? = null
    private var positiveButtonText: String? = null
    private var negativeListener: OnClickListener? = null
    private var negativeButtonText: String? = null
    private var onDismissListener: OnDismissListener? = null

    /**
     * Controls if dismiss functionality is managed by the user implementing the GenericDialog or by the GenericDialog itself.
     *
     * So, if true, dialog will be dismissed after clicking any positive or negative buttons.
     */
    private var manageDismiss: Boolean = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //in kitkat the title is shown, with this title will be hidden.
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        val view = inflater.inflate(R.layout.dialog_generic, container, false)

        isCancelable = true

        view.number_pad_title.text = title
        if (title.isNullOrEmpty()) {
            view.number_pad_title.visibility = View.GONE
        }

        view.message.text = message

        setupPositiveButton(view)
        setupNegativeButton(view)

        if (positiveListener == null && negativeListener == null) {
            view.positiveButton.apply {
                visibility = View.VISIBLE
                text = getString(R.string.ok)
                setOnClickListener { dismiss() }
            }

            view.negativeButton.visibility = View.GONE
        }

        icon?.let {
            view.icon.setImageDrawable(ContextCompat.getDrawable(context!!, it))
            view.icon.visibility = View.VISIBLE
        }


        return view
    }

    private fun setupPositiveButton(view: View) {
        positiveListener?.let {
            view.positiveButton.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    positiveListener?.onClick(this@GenericDialog)
                    if (manageDismiss) dismiss()
                }
            }
        }

        positiveButtonText?.let {
            view.positiveButton.text = positiveButtonText
        }
    }

    private fun setupNegativeButton(view: View) {
        view.negativeButton.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                negativeListener?.onClick(this@GenericDialog)
                if (manageDismiss) dismiss()
            }
        }

        negativeButtonText?.let {
            view.negativeButton.text = negativeButtonText
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.onDismiss()
    }

    override fun onStart() {
        super.onStart()
        ViewUtilities.setSmallDialogWindowSize(dialog!!.window!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(resultCode) {
            WebViewer.REQUEST_CODE -> {
                //This is done this way for maintaining the order of appearance between this dialog and the WebViewer instances
                dismiss()
            }
        }
    }
}