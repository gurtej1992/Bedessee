package com.bedessee.salesca.product

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.bedessee.salesca.R
import com.bedessee.salesca.mixpanel.MixPanelManager
import com.bedessee.salesca.utilities.Utilities
import com.bumptech.glide.Glide
import it.sephiroth.android.library.imagezoom.ImageViewTouch
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase

/**
 * Activity where user can see a full view of an image and can pinch to zoom.
 */
class FullScreenImageActivity : AppCompatActivity() {

    var imageViewTouch:ImageViewTouch?=null
    var shareImage:ImageView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Setup Window
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.fullscreenactivity)
        val sh = getSharedPreferences("setting", MODE_PRIVATE)
        val orient = sh.getString("orientation", "landscape")
//        requestedOrientation = if (orient == "landscape") {
//            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//        } else {
//            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//        }


        imageViewTouch = findViewById<View>(R.id.imageViewTouch) as ImageViewTouch
        shareImage = findViewById<View>(R.id.share) as ImageView

        //Setup View
//        val imageViewTouch = ImageViewTouch(this, null)

       // imageViewTouch.displayType = ImageViewTouchBase.DisplayType.FIT_TO_SCREEN
        val imageUri = intent.extras!![KEY_IMAGE_URI] as Uri?

        if (imageUri == null) {
            Utilities.shortToast(this, "Oops, couldn't load image!")
            finish()
        } else {
            Glide.with(this).load(imageUri).into(imageViewTouch!!);
        }
        shareImage!!.setOnClickListener {
            Log.e("@@@@@","get value  file://${KEY_IMAGE_URI}")
            MixPanelManager.trackButtonClick(this, "Button Click: Image Share")
            Utilities.shareImage(this, "file://${imageUri}")
        }

       // setContentView(imageViewTouch)
    }

    companion object {
        const val KEY_IMAGE_URI = "image_uri"
        fun launch(context: Context, uri: Uri?) {
            val intent = Intent(context, FullScreenImageActivity::class.java)
            intent.putExtra(KEY_IMAGE_URI, uri)
            context.startActivity(intent)
        }
    }
}