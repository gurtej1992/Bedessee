package com.bedessee.sales.utilities

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.bedessee.sales.sharedprefs.SharedPrefsManager
import java.io.File

class FileUtilities {
    companion object {
        private fun getFormat(type: String): String? {
            if (type == "PDF") return "pdf"
            if (type == "CSV") return "csv"
            if (type == "HTM") return "htm"
            return if (type == "TXT") "txt" else null
        }

        private fun getRelativePath(baseStoreNumber: String, type: String, directory: String): String? {
            return directory + "/" + baseStoreNumber + "." + getFormat(type)
        }

        fun getFile(context: Context, baseStoreNumber: String, type: String, directory: String): File {
            val baseFilePath = SharedPrefsManager(context).sugarSyncDir
            return File(baseFilePath + "/" + getRelativePath(baseStoreNumber, type, directory))
        }

        fun openPDF(context: Context, file: File): Boolean {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.fromFile(file), "application/pdf")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            return try {
                context.startActivity(intent)
                true
            } catch (exception: ActivityNotFoundException) {
                Toast.makeText(context, "PDF file can not be open, please install PDF reader and try again", Toast.LENGTH_SHORT).show()
                false
            }
        }

        fun openCSV(context: Context, file: File): Boolean {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.fromFile(file), "text/csv")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            return try {
                context.startActivity(intent)
                true
            } catch (exception: ActivityNotFoundException) {
                Toast.makeText(context, "CSV file can not be open, please install CSV reader and try again", Toast.LENGTH_SHORT).show()
                false
            }
        }

        fun showToastFileNotFound(context: Context) {
            Utilities.shortToast(context, "Document not found")
        }
    }
}