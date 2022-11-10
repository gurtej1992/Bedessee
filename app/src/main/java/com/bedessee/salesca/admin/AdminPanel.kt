package com.bedessee.salesca.admin

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Environment
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.bedessee.salesca.R
import com.bedessee.salesca.customview.GenericDialog
import com.bedessee.salesca.customview.UtilitiesSpinner
import com.bedessee.salesca.login.Login
import com.bedessee.salesca.mixpanel.MixPanelManager
import com.bedessee.salesca.sharedprefs.SharedPrefsManager
import com.bedessee.salesca.update.UpdateActivity
import com.bedessee.salesca.utilities.FolderClearUp
import com.bedessee.salesca.utilities.Utilities
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FilenameFilter
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset


class AdminPanel : AppCompatActivity() {
    var price: TextView? = null
    var clear1: TextView? = null
    var clear2: TextView? = null
    var signout: TextView? = null
    var force: TextView? = null
    var num:TextView?=null
    var show_file:TextView?=null
    var set_ornt:TextView?=null
    var select_file:TextView?=null
    var include_file:TextView?=null
    var newlayout:TextView?=null
    var updatedList= arrayOf<String>()
    var fileList= arrayOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        price = findViewById<View>(R.id.price) as TextView
        clear1 = findViewById<View>(R.id.clear1) as TextView
        clear2 = findViewById<View>(R.id.clear2) as TextView
        signout = findViewById<View>(R.id.sign_out) as TextView
        force = findViewById<View>(R.id.force_crash) as TextView
        num = findViewById<View>(R.id.Num_Columns) as TextView
        set_ornt = findViewById<View>(R.id.set_ornt) as TextView
        select_file = findViewById<View>(R.id.select_file) as TextView
        show_file = findViewById<View>(R.id.show_file) as TextView
        include_file = findViewById<View>(R.id.include_price) as TextView
        newlayout = findViewById<View>(R.id.new_layout) as TextView


        //Creating a File object for directory
        val sharedPrefs = SharedPrefsManager(this)
        var mSugarSyncDir = sharedPrefs.sugarSyncDir


        try {
            val directoryPath = File(mSugarSyncDir + "/data/prod_files_can_select.json")
            val stream = FileInputStream(directoryPath)
            var jsonStr: String? = null
            try {
                val fc: FileChannel = stream.getChannel()
                val bb: MappedByteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size())
                jsonStr = Charset.defaultCharset().decode(bb).toString()

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                stream.close()
            }

            val jsonObj = JSONArray(jsonStr)

            // looping through All nodes
            for (i in 0 until jsonObj.length()) {
                val c = jsonObj.getJSONObject(i)
                val menu_name = c.getString("MENU TO DISPLAY")
                val fileselect = c.getString("FILE NAME TO SELECT")
                updatedList+=menu_name
                fileList+=fileselect

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val landLogo = findViewById<ImageView>(R.id.landLogo);
        landLogo.animate().apply {
            duration =2000
            rotationYBy(360f)

        }.start()

        price!!.setOnClickListener {

            this@AdminPanel.startActivity(
                Intent(
                    this@AdminPanel,
                    AdminSettings::class.java
                )
            )
        }
        clear1!!.setOnClickListener {
            val clearAction = FolderClearUp.clear_folder_json
            showClearDialog(clearAction)
        }
        clear2!!.setOnClickListener {
            val clearAction = FolderClearUp.clear_folder_json
            showClearDialog(clearAction)
        }
        signout!!.setOnClickListener {
            MixPanelManager.trackButtonClick(this, "Button click: Top menu: SIGN OUT")
            signOut(true)
        }
        num!!.setOnClickListener {
            val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Set Columns")

// Set up the input
            val input = EditText(this)
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setHint("Enter Number of Column")
            input.inputType = InputType.TYPE_CLASS_NUMBER
            builder.setView(input)

// Set up the buttons
            builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                // Here you get get input text from the Edittext
                if(input.text.toString().isNotBlank()) {
                    var m_Text: Int = input.text.toString().toInt()

                    val sharedPreferences: SharedPreferences =
                        getSharedPreferences("setting", Context.MODE_PRIVATE)
                    val edit = sharedPreferences.edit()
                    edit.putInt("spanCount", m_Text)
                    edit.apply()
                }
            })
            builder.setNegativeButton(
                "Cancel",
                DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

            builder.show()
        }

        show_file!!.setOnClickListener {
            AlertDialog.Builder(this).setTitle("Do you want to show file name?")
                .setMessage("This will show the selected file on main screen.")
                .setPositiveButton(
                    "YES"
                ) { dialog, which ->
                    val sharedPreferences: SharedPreferences =
                        getSharedPreferences("selectedfile", Context.MODE_PRIVATE)
                    val edit = sharedPreferences.edit()
                    edit.putBoolean("show",true)
                    edit.apply()
                    dialog.dismiss()

                }
                .setNegativeButton(
                    "NO"
                ) { dialog, which -> // Do nothing
                    val sharedPreferences: SharedPreferences =
                        getSharedPreferences("selectedfile", Context.MODE_PRIVATE)
                    val edit = sharedPreferences.edit()
                    edit.putBoolean("show",false)
                    edit.apply()
                    dialog.dismiss()
                }
                .create()
                .show()
        }

        newlayout!!.setOnClickListener {
            AlertDialog.Builder(this).setTitle("Do you want to show new layout?")
//                .setMessage("This will show the selected file on main screen.")
                .setPositiveButton(
                    "YES"
                ) { dialog, which ->
                    val sharedPreferences: SharedPreferences =
                        getSharedPreferences("shownewlyout", Context.MODE_PRIVATE)
                    val edit = sharedPreferences.edit()
                    edit.putBoolean("show",true)
                    edit.apply()
                    dialog.dismiss()

                }
                .setNegativeButton(
                    "NO"
                ) { dialog, which -> // Do nothing
                    val sharedPreferences: SharedPreferences =
                        getSharedPreferences("shownewlyout", Context.MODE_PRIVATE)
                    val edit = sharedPreferences.edit()
                    edit.putBoolean("show",false)
                    edit.apply()
                    dialog.dismiss()
                }
                .create()
                .show()
        }

        include_file!!.setOnClickListener {
            AlertDialog.Builder(this).setTitle("Do you want to include level prices?")
                .setPositiveButton(
                    "YES"
                ) { dialog, which ->
                    val sharedPreferences: SharedPreferences =
                        getSharedPreferences("includeprice", Context.MODE_PRIVATE)
                    val edit = sharedPreferences.edit()
                    edit.putBoolean("show",true)
                    edit.apply()
                    dialog.dismiss()

                }
                .setNegativeButton(
                    "NO"
                ) { dialog, which -> // Do nothing
                    val sharedPreferences: SharedPreferences =
                        getSharedPreferences("includeprice", Context.MODE_PRIVATE)
                    val edit = sharedPreferences.edit()
                    edit.putBoolean("show",false)
                    edit.apply()
                    dialog.dismiss()
                }
                .create()
                .show()
        }

//        show_html!!.setOnClickListener {
//            AlertDialog.Builder(this).setTitle("Show Html File")
//                .setPositiveButton(
//                    "Chrome"
//                ) { dialog, which ->
//                    // Perform Action & Dismiss dialog
//                    var orn_text: String = "chrome"
//                    val sharedPreferences: SharedPreferences =
//                        getSharedPreferences("htmlsetting", Context.MODE_PRIVATE)
//                    val edit = sharedPreferences.edit()
//                    edit.putString("option", orn_text)
//                    edit.apply()
//                    dialog.dismiss();
//                }
//                .setNegativeButton(
//                    "Html Viewer"
//                ) { dialog, which ->
//                    var orn_text: String = "html"
//                    val sharedPreferences: SharedPreferences =
//                        getSharedPreferences("htmlsetting", Context.MODE_PRIVATE)
//                    val edit = sharedPreferences.edit()
//                    edit.putString("option", orn_text)
//                    edit.apply()
//                    dialog.dismiss()
//                }
//                .create()
//                .show()
//        }

        set_ornt!!.setOnClickListener {
            AlertDialog.Builder(this).setTitle("Change Orientation")
                .setPositiveButton(
                    "Landscape"
                ) { dialog, which ->
                    // Perform Action & Dismiss dialog
                    var orn_text: String = "landscape"
                    val sharedPreferences: SharedPreferences =
                        getSharedPreferences("setting", Context.MODE_PRIVATE)
                    val edit = sharedPreferences.edit()
                    edit.putString("orientation", orn_text)
                    edit.apply()
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    dialog.dismiss();
                }
                .setNegativeButton(
                    "Portrait"
                ) { dialog, which ->
                    var orn_text: String = "portrait"
                    val sharedPreferences: SharedPreferences =
                        getSharedPreferences("setting", Context.MODE_PRIVATE)
                    val edit = sharedPreferences.edit()
                    edit.putString("orientation", orn_text)
                    edit.apply()
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    dialog.dismiss()
                }
                .create()
                .show()
        }
        force!!.setOnClickListener {
            AlertDialog.Builder(this).setTitle("Are you sure want to crash the app?")
                .setMessage("This is for Testing, Force crash will crash the app.")
                .setPositiveButton(
                    "YES"
                ) { dialog, which ->
                    throw RuntimeException("Test Crash") // Force a crash
                    // Perform Action & Dismiss dialog
                    // dialog.dismiss();
                }
                .setNegativeButton(
                    "NO"
                ) { dialog, which -> // Do nothing
                    dialog.dismiss()
                }
                .create()
                .show()
        }

        select_file!!.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            with(builder)
            {
                val sharedPreferences: SharedPreferences =
                    getSharedPreferences("selectedfile", Context.MODE_PRIVATE)
                val edit = sharedPreferences.edit()
                setTitle("List of Files")

                setItems(updatedList) { dialog, which ->
                    edit.putString("filename", fileList[which])
                    edit.putString("Showfilename",updatedList[which])
                    edit.putBoolean("update",true)
                    edit.apply()
                    dialog.dismiss();
                    startActivity(UpdateActivity.newIntent(this@AdminPanel))
                    finish()
                }
                setNegativeButton("Cancel")
                { dialog, which ->
                    // Perform Action & Dismiss dialog
                     dialog.dismiss();
                }.create()
                show()
            }
        }
    }
    private fun showClearDialog(clearAction: String){
        GenericDialog.newInstance(
            this?.getString(R.string.clear_folders_message),
            "it will run the following clean up: $clearAction",
            object : GenericDialog.OnClickListener {
                override fun onClick(dialog: DialogFragment) {
                    val result = FolderClearUp.clearFoldersByName(this@AdminPanel!!, clearAction)
                    if (result) {
                        Utilities.shortToast(this@AdminPanel, "The folders have been clear out")
                    }
                    dialog.dismiss()
                }
            },
            null
        ).show((this as AppCompatActivity).supportFragmentManager, UtilitiesSpinner.TAG)




    }

    //readfile
    open fun ReadFile() {

    }

    private fun signOut(launchLoginScreen: Boolean) {
        SharedPrefsManager(this).removeLoggedInUser()
        if (launchLoginScreen) {
            this?.startActivity(Intent(this, Login::class.java))
        }
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


}