package com.bedessee.salesca.customview

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bedessee.salesca.R
import com.bedessee.salesca.modal.SalesPerson


class UserListDialog(context: Context, private val userList: List<SalesPerson>,onItemClick: (SalesPerson) -> Unit) {

    private val dialog: AlertDialog

    init {
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.custom_salespersondialog, null)

        val recyclerView: RecyclerView = dialogView.findViewById(R.id.recyclerview)
        val adapter = UserListAsapter(userList,onItemClick)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        val textView = TextView(context)
        textView.text = "SalesPerson List"
        textView.setPadding(20, 30, 20, 30)
        textView.textSize = 20f
        textView.setBackgroundColor(R.color.colorAccentDark)
        textView.setTextColor(Color.WHITE)
        // Create the dialog and set its content view
        dialog = AlertDialog.Builder(context)
            .setCustomTitle(textView)
            .setView(dialogView)
            .create()
    }

    fun show() {
        dialog.show()
    }
}