package com.bedessee.salesca.utilities

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import com.bedessee.salesca.main.MainActivity
import com.bedessee.salesca.reportsmenu.ReportsMenu
import com.bedessee.salesca.sharedprefs.SharedPrefsManager
import com.bedessee.salesca.store.Store
import com.bedessee.salesca.store.WebViewer.Companion.show
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader


class ReportsUtilities {
    companion object {
        /**
         * Opens first report with property with the property defaultOpenAfterCustomerSelect equals to YES
         */
        fun openFirstDefaultOpenReport(context: Context, store: Store) {
            try {
                val type = object : TypeToken<List<ReportsMenu?>?>() {}.type
                val reader = BufferedReader(FileReader(SharedPrefsManager(context).sugarSyncDir + "/data/bottom_left_reports_menu.json"))
                val reports = Gson().fromJson<List<ReportsMenu>>(reader, type)
                for (i in reports.indices) {
                    if (isYES(reports[i].defaultOpenAfterCustomerSelect)) {
                        openReportMenu(context, reports[i], store)
                        break
                    }
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }

        private fun isYES(value:String): Boolean {
            return (value == "YES" || value == "Y")
        }

        private fun notSuccessfullyOpen(context: Context) {
            if (context is MainActivity) {
               // context.clearReportMenu()
            }
        }


        fun openReportMenu(context: Context, reportsMenu: ReportsMenu, store: Store) {
            val file: File = FileUtilities.getFile(
                context,
                store.baseNumber,
                reportsMenu.popupType,
                reportsMenu.deviceFolder
            )
            if (file.exists()) {
                when (reportsMenu.popupType) {
                    "TXT" -> {
                        show(context, file.absolutePath)
                    }
                    "PDF" -> {
                        val result = FileUtilities.openPDF(context, file)
                        if (!result) notSuccessfullyOpen(context)
                    }
                    "CSV" -> {
                        val result = FileUtilities.openCSV(context, file)
                        if (!result) notSuccessfullyOpen(context)
                    }
                    "HTM" -> {

                        if (isChromeInstalledAndVersionGreaterThan65(context)){
                            openHtml(context,FileUtilities.getFile(
                                context,
                                store.baseNumber,
                                reportsMenu.popupType,
                                reportsMenu.deviceFolder
                            ).absolutePath)
                        }
                        else{
                            show(context, FileUtilities.getFile(
                            context,
                            store.baseNumber,
                            reportsMenu.popupType,
                            reportsMenu.deviceFolder
                        ).absolutePath)
                        }
                       
                    }
                }
            } else {
                FileUtilities.showToastFileNotFound(context)
                notSuccessfullyOpen(context)
            }
        }

        private fun openHtml(context: Context, path: String) {
            val uri2 = Uri.fromFile(
                File(path)
            )
            val intent = Intent(ACTION_VIEW, uri2)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setClassName("com.android.chrome", "com.google.android.apps.chrome.Main")
          //  intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");

            try {
                context.startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                try {
                    intent.setPackage(null)
                    context.startActivity(intent)
                } catch (e: Exception) {
                }
            }
        }

        private fun isChromeInstalledAndVersionGreaterThan65( context: Context): Boolean {
            val pInfo: PackageInfo = try {
                context.packageManager.getPackageInfo("com.android.chrome", 0)
            } catch (e: PackageManager.NameNotFoundException) {
                //chrome is not installed on the device
                return false
            }
            if (pInfo != null) {
                //Chrome has versions like 68.0.3440.91, we need to find the major version
                //using the first dot we find in the string
                val firstDotIndex = pInfo.versionName.indexOf(".")
                //take only the number before the first dot excluding the dot itself
                val majorVersion = pInfo.versionName.substring(0, firstDotIndex)
                return majorVersion.toInt() > 65
            }
            return false
        }
    }


}