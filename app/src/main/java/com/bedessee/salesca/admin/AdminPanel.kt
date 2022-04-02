package com.bedessee.salesca.admin

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.bedessee.salesca.R
import com.bedessee.salesca.customview.GenericDialog
import com.bedessee.salesca.customview.UtilitiesSpinner
import com.bedessee.salesca.login.Login
import com.bedessee.salesca.mixpanel.MixPanelManager
import com.bedessee.salesca.sharedprefs.SharedPrefsManager
import com.bedessee.salesca.utilities.FolderClearUp
import com.bedessee.salesca.utilities.Utilities

class AdminPanel : AppCompatActivity() {
    var price: TextView? = null
    var clear1: TextView? = null
    var clear2: TextView? = null
    var signout: TextView? = null
    var force: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)
        price = findViewById<View>(R.id.price) as TextView
        clear1 = findViewById<View>(R.id.clear1) as TextView
        clear2 = findViewById<View>(R.id.clear2) as TextView
        signout = findViewById<View>(R.id.sign_out) as TextView
        force = findViewById<View>(R.id.force_crash) as TextView
       price!!.setOnClickListener {

                       this@AdminPanel.startActivity(
                           Intent(
                               this@AdminPanel,
                               AdminSettings::class.java
                           )
                       )
       }
        clear1!!.setOnClickListener {
            val clearAction = FolderClearUp.clear_folder_json
                        showClearDialog(clearAction)
        }
        clear2!!.setOnClickListener {
            val clearAction = FolderClearUp.clear_folder_json
            showClearDialog(clearAction)
        }
        signout!!.setOnClickListener {
            MixPanelManager.trackButtonClick(this, "Button click: Top menu: SIGN OUT")
                       signOut(true)
        }
    }
    private fun showClearDialog(clearAction: String){
        GenericDialog.newInstance(
            this?.getString(R.string.clear_folders_message),
            "it will run the following clean up: $clearAction",
            object : GenericDialog.OnClickListener {
                override fun onClick(dialog: DialogFragment) {
                    val result = FolderClearUp.clearFoldersByName(this@AdminPanel!!, clearAction)
                    if (result) {
                        Utilities.shortToast(this@AdminPanel, "The folders have been clear out")
                    }
                    dialog.dismiss()
                }
            },
            null
        ).show((this as AppCompatActivity).supportFragmentManager, UtilitiesSpinner.TAG)

        force!!.setOnClickListener {
            AlertDialog.Builder(this).setTitle("Are you sure want to crash the app?")
                .setMessage("This is for Testing, Force crash will crash the app.")
                .setPositiveButton(
                    "YES"
                ) { dialog, which ->
                    throw RuntimeException("Test Crash") // Force a crash
                    // Perform Action & Dismiss dialog
                    // dialog.dismiss();
                }
                .setNegativeButton(
                    "NO"
                ) { dialog, which -> // Do nothing
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    private fun signOut(launchLoginScreen: Boolean) {
        SharedPrefsManager(this).removeLoggedInUser()
        if (launchLoginScreen) {
            this?.startActivity(Intent(this, Login::class.java))
        }
        finish()
    }
}