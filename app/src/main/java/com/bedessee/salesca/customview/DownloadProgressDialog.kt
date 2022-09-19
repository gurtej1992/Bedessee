package com.bedessee.salesca.customview

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.bedessee.salesca.BedesseeApp
import com.bedessee.salesca.R
import com.bedessee.salesca.mixpanel.MixPanelManager
import com.bedessee.salesca.sharedprefs.SharedPrefsManager
import com.bedessee.salesca.update.UpdateActivity
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.DownloadBlock
import ir.mahdi.mzip.zip.ZipArchive
import kotlinx.android.synthetic.main.dialog_progress.*
import org.apache.commons.io.FilenameUtils
import timber.log.Timber
import java.io.File
import java.io.IOException


class DownloadProgressDialog : DialogFragment() {
    private lateinit var request: Request
    val mainLooper = Looper.getMainLooper()


    companion object {
        fun newInstance(request:Request) = DownloadProgressDialog().apply {
            this.request = request
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //in kitkat the title is shown, with this title will be hidden.
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        val view = inflater.inflate(R.layout.dialog_progress, container, false)

        isCancelable = false

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener(view.context)
        getFetchManager(view.context).enqueue(request)
        negativeButton.visibility = View.VISIBLE
        negativeButton.text = "Cancel"
        negativeButton.setOnClickListener {
            dismiss()
        }

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        clearUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearUp()
    }

    private fun clearUp() {
        getFetchManager(requireView()!!.context).cancel(request.id)
        val file = File(request.file)
        if (file.exists()) {
            file.delete()
        }
    }

    private fun getFetchManager(context: Context) : Fetch {
        return (context.applicationContext as BedesseeApp).fetchManager
    }

    private fun setupListener(context:Context) {
        getFetchManager(context).addListener(object:FetchListener {
            override fun onAdded(download: Download) {
                progressTitle?.let {
                    it.text = "The Download has added"
                }
            }

            override fun onCancelled(download: Download) {
                progressTitle?.let {
                    it.text = "The Download has been cancelled"
                }
            }

            override fun onCompleted(download: Download) {
                progressTitle?.let {
                    it.text = "The Download has been completed."
                }

                positiveButton?.let { posButton ->
                    posButton.text = "Unzip"
                    posButton.visibility = View.VISIBLE
                   posButton.setOnClickListener {

                           unzip(context, download.file)

                       progressBarView.visibility = View.GONE
                        progressText.visibility = View.GONE
                       posButton.visibility = View.GONE
                        negativeButton.visibility = View.GONE

                    }
                }
            }

            override fun onDeleted(download: Download) {

            }

            override fun onDownloadBlockUpdated(download: Download, downloadBlock: DownloadBlock, totalBlocks: Int) {

            }

            override fun onError(download: Download, error: Error, throwable: Throwable?) {
                progressTitle?.let {
                    it.text = "Error occurred while downloading.URL is not valid.Please check the URL."
                }
            }

            override fun onPaused(download: Download) {
                progressTitle?.let {
                    it.text = "The download has been paused"
                }
            }

            override fun onProgress(download: Download, etaInMilliSeconds: Long, downloadedBytesPerSecond: Long) {
                if (progressBarView!= null) {
                    progressBarView.progress = download.progress
                    progressText.text = "${download.progress} %"
                }
            }

            override fun onQueued(download: Download, waitingOnNetwork: Boolean) {

            }

            override fun onRemoved(download: Download) {

            }

            override fun onResumed(download: Download) {
                progressTitle?.let {
                    it.text = "The download has been resumed"
                }
            }

            override fun onStarted(download: Download, downloadBlocks: List<DownloadBlock>, totalBlocks: Int) {
                progressTitle?.let {
                    it.text = "Downloading..."
                }
            }

            override fun onWaitingNetwork(download: Download) {
                progressTitle?.let {
                    it.text = "The download is waiting to get a valid network"
                }
            }

        })
    }

    private fun unzip(context:Context, archiveFile: String) {
        val sugarPath = SharedPrefsManager(context).sugarSyncDir
        val unzippedFile = FilenameUtils.getName(archiveFile)
        val alertDialogBuilder = AlertDialog.Builder(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialogBuilder.setTitle("Unzipping File")
                .setMessage("The file $unzippedFile is Unzipping Please wait\"")
                .setView(R.layout.alertdialog_progress)
                .setCancelable(false)
        }
        val mAlertDialog = alertDialogBuilder.create()
        mAlertDialog.show()

        try {
            Thread(Runnable {

                ZipArchive.unzip(archiveFile, sugarPath, "")

                Handler(mainLooper).post {

                mAlertDialog.dismiss()

                    progressTitle?.let { it ->
            it.text = "The file $unzippedFile was unzipped completely"
            positiveButton.post {
                positiveButton.visibility = View.VISIBLE
                positiveButton.text = "Cancel"
                negativeButton.text = "Daily update"
                negativeButton.visibility = View.VISIBLE
                dialog?.setCancelable(true)
                positiveButton.setOnClickListener {
                    dismiss()
                }
                message.visibility= View.VISIBLE
                message.text="Now, It is recommended to run a daily update"

                negativeButton.setOnClickListener { view ->
                    MixPanelManager.trackButtonClick(context, "Button click: Top menu: DAILY UPDATE")
                    startActivityForResult(UpdateActivity.newIntent(context), UpdateActivity.REQUEST_CODE)
                    dismiss()
                }
            }
        }
                }
            }).start()




        } catch(ex:Exception) {
            when(ex) {
                is NullPointerException,
                is IOException -> {
                    Toast.makeText(context, "Error unzipping archive file: $archiveFile $ex", Toast.LENGTH_SHORT).show()

                    Timber.d("Error unzipping archive file: $archiveFile $ex")
                }
                else -> throw ex
            }

            return
        }

//        val unzippedFile = FilenameUtils.getName(archiveFile)
//        progressTitle?.let { it ->
//            it.text = "The file $unzippedFile was unzipped completely"
//            positiveButton.post {
//                positiveButton.visibility = View.VISIBLE
//                positiveButton.text = "Cancel"
//                negativeButton.text = "Daily update"
//                negativeButton.visibility = View.VISIBLE
//                dialog?.setCancelable(true)
//                positiveButton.setOnClickListener {
//                    dismiss()
//                }
//                message.visibility= View.VISIBLE
//                message.text="Now, It is recommended to run a daily update"
//
//                negativeButton.setOnClickListener { view ->
//                    MixPanelManager.trackButtonClick(context, "Button click: Top menu: DAILY UPDATE")
//                    startActivityForResult(UpdateActivity.newIntent(context), UpdateActivity.REQUEST_CODE)
//                    dismiss()
//                }
//            }
//        }
    }
}