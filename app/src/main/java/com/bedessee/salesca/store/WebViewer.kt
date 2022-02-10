package com.bedessee.salesca.store

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bedessee.salesca.R
import com.bedessee.salesca.order.GMailUtils
import com.bedessee.salesca.utilities.Utilities
import com.bedessee.salesca.utilities.ViewUtilities
import java.io.File

/**
 * Activity containing WebView to show Store's statement.
 */
class WebViewer : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_webview)
        ViewUtilities.setActivityWindowSize(window)

        val ua = "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0"

        val webView = findViewById<WebView>(R.id.webview)
        webView.settings.builtInZoomControls = true
        webView.settings.setSupportZoom(true)
        webView.webViewClient = WebViewClient()

        //This will zoom out the WebView
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true

        val filePath = intent.extras!!.getString(KEY_URL)
        if (!File(filePath).exists()) {
            Utilities.shortToast(this@WebViewer, "Document not found")
            setResult(RESULT_CODE)
            finish()
        } else {
            findViewById<View>(R.id.btn_email_statement).setOnClickListener {
                GMailUtils.sendAttachment(this@WebViewer, "STATEMENT", filePath)
            }

            val client = object : WebViewClient() {
                override fun onPageFinished(v: WebView, url: String) {
                    findViewById<View>(R.id.progressBar).visibility = View.GONE
                    webView.zoomIn()
                }

                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    url?.let{
                        openBrowser(url)
                    }
                    return true
                }

                @SuppressLint("NewApi")
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest): Boolean {
                    openBrowser(request.url.buildUpon().toString())
                    return true
                }
            }
            webView.webViewClient = client


            findViewById<View>(R.id.btn_close).setOnClickListener {
                setResult(RESULT_CODE)
                finish()
            }

            webView.loadUrl("file://" + filePath!!)
        }
    }

    private fun openBrowser(url:String){
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    companion object {
        fun show(activity: Activity, absolutePath: String) {
            val intent = Intent(activity, WebViewer::class.java)
            intent.putExtra(KEY_URL, absolutePath)
            activity.startActivityForResult(intent, REQUEST_CODE)
        }

        fun show(fragment: Fragment, absolutePath: String) {
            val intent = Intent(fragment.context, WebViewer::class.java)
            intent.putExtra(KEY_URL, absolutePath)
            fragment.startActivityForResult(intent, REQUEST_CODE)
        }

        fun show(context: Context, absolutePath: String) {
            if (context is Activity) {
               Log.e("@#@",absolutePath)
                show(context, absolutePath)
            }
            if (context is Fragment) {
                @Suppress("CAST_NEVER_SUCCEEDS")
                (show(
        context as Fragment,
        absolutePath
    ))
            }

        }

        const val RESULT_CODE = 88
        const val REQUEST_CODE = 88
        const val KEY_URL = "url"
    }
}
