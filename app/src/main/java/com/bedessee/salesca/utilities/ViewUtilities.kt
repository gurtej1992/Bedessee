package com.bedessee.salesca.utilities

import android.graphics.Point
import android.util.DisplayMetrics
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import com.bedessee.salesca.R
import kotlin.math.sqrt

class ViewUtilities {
    companion object {
        private fun getDiagonalInches(display: Display): Double {
            val metrics = DisplayMetrics()
            display.getMetrics(metrics)
            val yInches = metrics.heightPixels / metrics.ydpi
            val xInches = metrics.widthPixels / metrics.xdpi
            return sqrt((xInches * xInches + yInches * yInches).toDouble())
        }

        fun setDialogWindowSize(window: Window, wrapHeight: Boolean = false) {
            val size = Point()
            val display = window.windowManager.defaultDisplay
            display.getSize(size)

            val height = if (wrapHeight) {
                ViewGroup.LayoutParams.WRAP_CONTENT
            } else {
                (size.y * 0.90).toInt()
            }

            if (getDiagonalInches(display) >= 6.5) {
                // 6.5inch device or bigger
                window.setLayout((size.x * 0.45).toInt(), height)
            } else {
                // smaller device
                window.setLayout((size.x * 0.9).toInt(), height)
            }

            window.setGravity(Gravity.CENTER)
        }

        fun setSmallDialogWindowSize(window: Window) {
            val size = Point()
            val display = window.windowManager.defaultDisplay
            display.getSize(size)
            window.setLayout((size.x * 0.65).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
            window.setGravity(Gravity.CENTER)
        }

        fun setTheme(activity: AppCompatActivity, window: Window) {
            val size = Point()
            val display = window.windowManager.defaultDisplay
            display.getSize(size)

            if (getDiagonalInches(display) >= 6.5) {
                // 6.5inch device or bigger
                activity.setTheme(R.style.MaterialDialogAppTheme)
            } else {
                activity.setTheme(R.style.MaterialAppTheme)
            }
        }

        fun setActivityWindowSize(window: Window) {
            val size = Point()
            val display = window.windowManager.defaultDisplay
            display.getSize(size)

            if (getDiagonalInches(display) >= 6.5) {
                // 6.5inch device or bigger
                window.setLayout((size.x * 0.95).toInt(), (size.y * 0.95).toInt())
            } else {
                // smaller device
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }



            window.setGravity(Gravity.CENTER)
        }
    }
}