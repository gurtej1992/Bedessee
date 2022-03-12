package com.bedessee.salesca

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bedessee.salesca.main.MainActivity
import kotlinx.android.synthetic.main.activity_launch.*
import java.lang.String
import java.util.*

class LaunchActivity : AppCompatActivity() {
    var progressBar:ProgressBar?=null
    var textView:TextView?=null
    var timer:Timer?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        // Send user to MainActivity as soon as this activity loads
//        val intent = Intent(this, MainActivity::class.java)
//        startActivity(intent)
        // remove this activity from the stack
       // finish()

        progressBar = findViewById<View>(R.id.progressBar) as ProgressBar
        progressBar!!.setProgress(0)
        textView = findViewById<View>(R.id.textView) as TextView
        textView!!.setText("")
        val landLogo = findViewById<ImageView>(R.id.landLogo);
        landLogo.animate().apply {
            duration = 2000
            rotationYBy(360f)
        }.start()
        val period: Long = 25
        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            var i:Int=1
            override fun run() {
                //this repeats every 100 ms

                if (i < 100) {
                    runOnUiThread { textView!!.text=(String.valueOf(i).toString() + "%") }
                    progressBar!!.setProgress(i)
                    i++
                } else {
                    //closing the timer
                    timer!!.cancel()
                    val intent = Intent(this@LaunchActivity, MainActivity::class.java)
                    startActivity(intent)
                    // close this activity
                    finish()
                }
            }
        }, 0, period)
    }
}