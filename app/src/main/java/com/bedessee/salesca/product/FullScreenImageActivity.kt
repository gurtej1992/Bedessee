package com.bedessee.salesca.product

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.bedessee.salesca.utilities.Utilities
import com.bumptech.glide.Glide
import it.sephiroth.android.library.imagezoom.ImageViewTouch
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase

/**
 * Activity where user can see a full view of an image and can pinch to zoom.
 */
class FullScreenImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sh = getSharedPreferences("setting", MODE_PRIVATE)
        val orient = sh.getString("orientation", "landscape")
        requestedOrientation = if (orient == "landscape") {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        //Setup Window
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)


        //Setup View
        val imageViewTouch = ImageViewTouch(this, null)
        imageViewTouch.displayType = ImageViewTouchBase.DisplayType.FIT_TO_SCREEN
        val imageUri = intent.extras!![KEY_IMAGE_URI] as Uri?

        if (imageUri == null) {
            Utilities.shortToast(this, "Oops, couldn't load image!")
            finish()
        } else {
            Glide.with(this).load(imageUri).into(imageViewTouch);
        }

        setContentView(imageViewTouch)
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