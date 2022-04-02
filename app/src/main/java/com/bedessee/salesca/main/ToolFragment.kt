package com.bedessee.salesca.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bedessee.salesca.R
import com.bedessee.salesca.admin.AdminPanel
import com.bedessee.salesca.admin.AdminSettings
import com.bedessee.salesca.customview.*
import com.bedessee.salesca.login.Login
import com.bedessee.salesca.mixpanel.MixPanelManager
import com.bedessee.salesca.reportsmenu.ReportFragment
import com.bedessee.salesca.sharedprefs.SharedPrefsManager
import com.bedessee.salesca.store.WebViewer
import com.bedessee.salesca.utilities.DividerItemDecoration
import com.bedessee.salesca.utilities.FolderClearUp
import com.bedessee.salesca.utilities.SpacesItemDecoration
import com.bedessee.salesca.utilities.Utilities
import com.tonyodev.fetch2.NetworkType
import com.tonyodev.fetch2.Priority
import com.tonyodev.fetch2.Request
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.net.URL

class ToolFragment : Fragment() {
    var toolAdapter: ToolAdapter? = null
    val TAG = "ReportList"
    var recyclerView:RecyclerView?=null
    private var instance: ReportFragment? = null
    fun getInstance(): ReportFragment? {
        if (instance == null) {
            instance = ReportFragment()
        }
        return instance
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
       val v:View= inflater.inflate(R.layout.fragment_tool, container, false)
      recyclerView = v.findViewById(R.id.recyclerView)
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.default_margin)
        if (recyclerView != null){
            recyclerView!!.addItemDecoration(SpacesItemDecoration(spacingInPixels, true))
            recyclerView!!.addItemDecoration(
                DividerItemDecoration(
                    activity
                )
            )
        }

        init(requireContext())
        return v;
    }

    private fun init(context: Context) {
        val utilsStrings = arrayOf("Util", "Calc", "Margin", "Load old", "Admin", "Wipe Data", "New?").toMutableList()
        val noLogin = SharedPrefsManager(context).noLogin
        if (noLogin != null) {
            if (noLogin.menuLabel1 != null) {
                utilsStrings.add(noLogin.menuLabel1)
            }
            if (noLogin.menuLabel2 != null) {
                utilsStrings.add(noLogin.menuLabel2)
            }
        }

        toolAdapter = object : ToolAdapter(getContext(), utilsStrings) {
            override fun onClickView(pos: Int) {
                when (pos) {
                    1 -> {
                        Utilities.launchRegularCalculator(context)
                    }
                    /* Launch markup calc */
                    2 -> context.startActivity(Intent(context, MarginCalculator::class.java))
//                    3 -> {
//                        /* Launch GMail */
//                        val intent = context.packageManager.getLaunchIntentForPackage("com.google.android.gm")
//                        context.startActivity(intent)
//                    }
                    3 -> {
                        /* Load prev update */
                        UpdateSelector().show((context as AppCompatActivity).supportFragmentManager, "Test")
                    }
                    4 -> {
                        /* Admin menu */
                        val secretPin = SharedPrefsManager(context).adminPin

                        DialogNumberPad.newInstance(object :
                                DialogNumberPad.OnNumberSelectedListener {
                            override fun onSelected(number: Double) {
                                if (number.toInt() == secretPin) {
                                    MixPanelManager.trackButtonClick(
                                            context,
                                            "Button click: Admin settings"
                                    )
                                    context.startActivity(
                                            Intent(
                                                    context,
                                                    AdminPanel::class.java
                                            )
                                    )
                               } else {
                                    Utilities.shortToast(context, "Sorry, wrong pin!")
                                }
                            }
                        }, formatNumber = false, allowDecimals = true, showHint = false)
                                .show((context as AppCompatActivity).supportFragmentManager, "Test")
                    }
//                    6 -> {
//                        val clearAction = FolderClearUp.clear_folder_json
//                        showClearDialog(clearAction)
//                    }
//                    7 -> {
//                        val clearAction = FolderClearUp.general_cleanup
//                        showClearDialog(clearAction)
//                    }
                    5 -> GenericDialog.newInstance(
                            context.getString(R.string.wipe_data_message),
                            "",
                            object : GenericDialog.OnClickListener {
                                override fun onClick(dialog: DialogFragment) {
                                    wipeData()
                                }
                            },
                            null
                    ).show((context as AppCompatActivity).supportFragmentManager, UtilitiesSpinner.TAG)
//                    9 -> {
//                        MixPanelManager.trackButtonClick(getContext(), "Button click: Top menu: SIGN OUT")
//                        signOut(true)
//                    }
                    6 -> showWhatsNew()
                    7 -> {
                        noLogin?.let {
                            FolderClearUp.clearFoldersByName(context, it.fileToProcessBefore)
                            fetchRequest(context, it.link1)
                        }
                    }
                    8 -> {
                        noLogin?.let {
                            FolderClearUp.clearFoldersByName(context, it.fileToProcessBefore2)
                            fetchRequest(context, it.link2)
                        }
                    }
                }

            }

        }
        recyclerView !!.layoutManager = LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false)
        recyclerView!!.adapter = toolAdapter
    }

    private fun showClearDialog(clearAction: String){
        GenericDialog.newInstance(
                context?.getString(R.string.clear_folders_message),
                "it will run the following clean up: $clearAction",
                object : GenericDialog.OnClickListener {
                    override fun onClick(dialog: DialogFragment) {
                        val result = FolderClearUp.clearFoldersByName(context!!, clearAction)
                        if (result) {
                            Utilities.shortToast(context, "The folders have been clear out")
                        }
                        dialog.dismiss()
                    }
                },
                null
        ).show((context as AppCompatActivity).supportFragmentManager, UtilitiesSpinner.TAG)
    }

    private fun fetchRequest(context: Context, url: String) {
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

        DownloadProgressDialog.newInstance(request)
                .show((context as AppCompatActivity).supportFragmentManager, UtilitiesSpinner.TAG)
    }


    private fun showWhatsNew() {
        val context = context

        MixPanelManager.trackButtonClick(context, "Button click: Top menu: WHAT'S NEW")
        val sharedPrefs = SharedPrefsManager(context)
        val file = File(sharedPrefs.sugarSyncDir, "/App Version/version.html")
        WebViewer.show(context!!, file.absolutePath)
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
            context?.startActivity(Intent(context, Login::class.java))
        }
        (context as Activity).finish()
    }

    companion object {
        lateinit var TAG: String

    }


}