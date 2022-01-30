package com.bedessee.sales.customview

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.bedessee.sales.R
import com.bedessee.sales.admin.AdminSettings
import com.bedessee.sales.login.Login
import com.bedessee.sales.mixpanel.MixPanelManager
import com.bedessee.sales.sharedprefs.SharedPrefsManager
import com.bedessee.sales.store.WebViewer
import com.bedessee.sales.utilities.FolderClearUp
import com.bedessee.sales.utilities.FolderClearUp.clear_folder_json
import com.bedessee.sales.utilities.FolderClearUp.general_cleanup
import com.bedessee.sales.utilities.Utilities
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.DownloadBlock
import com.tonyodev.fetch2core.Func
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipFile
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.util.*


/**
 * Utilities Spinner used in MainActivity.
 */
@SuppressLint("AppCompatCustomView")
class UtilitiesSpinner : Spinner {

    companion object {
        const val TAG = "UtilitiesSpinner"
    }

    constructor(context: Context) : super(context) {
        init(context)
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
    }

    /**
     * Initialize the utilities spinner.
     *
     * @param context Context
     */
    private fun init(context: Context) {
        val utilsStrings = arrayOf("Util", "Calc", "Margin", "GMail", "Load old", "Admin", "Clear1", "Clear2", "Wipe Data", "Sign Out", "New?").toMutableList()
        val noLogin = SharedPrefsManager(context).noLogin
        if (noLogin != null) {
            if (noLogin.menuLabel1 != null) {
                utilsStrings.add(noLogin.menuLabel1)
            }
            if (noLogin.menuLabel2 != null) {
                utilsStrings.add(noLogin.menuLabel2)
            }
        }
        val utilsAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, utilsStrings)
        adapter = utilsAdapter
        onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position) {
                    1 -> {
                        Utilities.launchRegularCalculator(context)
                    }
                    /* Launch markup calc */
                    2 -> context.startActivity(Intent(context, MarginCalculator::class.java))
                    3 -> {
                        /* Launch GMail */
                        val intent = context.packageManager.getLaunchIntentForPackage("com.google.android.gm")
                        context.startActivity(intent)
                    }
                    4 -> {
                        /* Load prev update */
                        UpdateSelector().show((context as AppCompatActivity).supportFragmentManager, "Test")
                    }
                    5 -> {
                        /* Admin menu */
                        val secretPin = SharedPrefsManager(context).adminPin

                        DialogNumberPad.newInstance(object : DialogNumberPad.OnNumberSelectedListener {
                            override fun onSelected(number: Double) {
                                if (number.toInt() == secretPin) {
                                    MixPanelManager.trackButtonClick(context, "Button click: Admin settings")
                                    context.startActivity(Intent(context, AdminSettings::class.java))
                                } else {
                                    Utilities.shortToast(context, "Sorry, wrong pin!")
                                }
                            }
                        }, formatNumber = false, allowDecimals = true, showHint = false).show((context as AppCompatActivity).supportFragmentManager, "Test")
                    }
                    6 -> {
                        val clearAction = clear_folder_json
                        showClearDialog(clearAction)
                    }
                    7 -> {
                        val clearAction = general_cleanup
                        showClearDialog(clearAction)
                    }
                    8 -> GenericDialog.newInstance(context.getString(R.string.wipe_data_message), "", object : GenericDialog.OnClickListener {
                        override fun onClick(dialog: DialogFragment) {
                            wipeData()
                        }
                    }, null).show((context as AppCompatActivity).supportFragmentManager, TAG)
                    9 -> {
                        MixPanelManager.trackButtonClick(getContext(), "Button click: Top menu: SIGN OUT")
                        signOut(true)
                    }
                    10 -> showWhatsNew()
                    11 -> {
                        noLogin?.let {
                            FolderClearUp.clearFoldersByName(context, it.fileToProcessBefore)
                            fetchRequest(context, it.link1)
                        }
                    }
                    12 -> {
                        noLogin?.let {
                            FolderClearUp.clearFoldersByName(context, it.fileToProcessBefore2)
                            fetchRequest(context, it.link2)
                        }
                    }
                }
                //reset spinner back to position 0
                parent.setSelection(0)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun showClearDialog(clearAction : String){
        GenericDialog.newInstance(context.getString(R.string.clear_folders_message), "it will run the following clean up: $clearAction", object : GenericDialog.OnClickListener {
            override fun onClick(dialog: DialogFragment) {
                val result = FolderClearUp.clearFoldersByName(context, clearAction)
                if (result) {
                    Utilities.shortToast(context, "The folders have been clear out")
                }
                dialog.dismiss()
            }
        }, null).show((context as AppCompatActivity).supportFragmentManager, TAG)
    }

    private fun fetchRequest(context: Context, url:String) {
        val safeUrl = if (!url.contains("http")) {
            "https://$url"
        } else {
            url
        }

        val fileName = (FilenameUtils.getName(URL(safeUrl).path))
        val sugarPath = SharedPrefsManager(context).sugarSyncDir
        val request = Request(safeUrl, "$sugarPath/$fileName")
        request.networkType = NetworkType.ALL
        request.priority = Priority.HIGH
        request.addHeader("Referer", "https://www.bedessee.com/")

        DownloadProgressDialog.newInstance(request).show((context as AppCompatActivity).supportFragmentManager, TAG)
    }


    private fun showWhatsNew() {
        val context = context

        MixPanelManager.trackButtonClick(context, "Button click: Top menu: WHAT'S NEW")
        val sharedPrefs = SharedPrefsManager(context)
        val file = File(sharedPrefs.sugarSyncDir, "/App Version/version.html")
        WebViewer.show(context, file.absolutePath)
    }

    private fun wipeData() {
        val context = context

//        val dir = File(SharedPrefsManager(context).sugarSyncDir)
//        deleteChildrenInDirectory(dir)
        val sharedPrefs = SharedPrefsManager(context)
        sharedPrefs.sugarSyncDir = null
        signOut(false)
    }

    private fun deleteChildrenInDirectory(dir: File) {
        if (dir.isDirectory) {
            val children = dir.list()
            for (aChildren in children) {
                val file = File(dir, aChildren)
                if (file.isDirectory) {
                    deleteChildrenInDirectory(file)
                } else {
                    file.delete()
                }
            }
        }
    }

    private fun signOut(launchLoginScreen: Boolean) {
        val context = context
        SharedPrefsManager(context).removeLoggedInUser()
        if (launchLoginScreen) {
            context.startActivity(Intent(context, Login::class.java))
        }
        (context as Activity).finish()
    }
}
