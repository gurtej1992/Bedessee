package com.bedessee.salesca.login

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bedessee.salesca.R
import com.bedessee.salesca.main.MainActivity
import com.bedessee.salesca.mixpanel.MixPanelManager
import com.bedessee.salesca.provider.Contract
import com.bedessee.salesca.provider.ProviderUtils
import com.bedessee.salesca.salesman.Salesman
import com.bedessee.salesca.salesman.SalesmanManager
import com.bedessee.salesca.sharedprefs.SharedPrefsManager
import com.bedessee.salesca.update.json.UpdateUsers
import com.bedessee.salesca.utilities.Utilities
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.nononsenseapps.filepicker.FilePickerActivity
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.nio.charset.Charset
import java.util.*

/**
 * Login screen.
 */
class Login : Activity() {

    private val RC_SIGN_IN: Int = 101
    private val MY_PERMISSIONS_REQUEST_READ_CONTACTS: Int = 202

//    private var googleApiClient: GoogleApiClient? = null
    private var mLoginButton: SignInButton? = null
    private var mProgressBar: ProgressBar? = null

    private var dialog: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        MixPanelManager.trackScreenView(this, "Login screen")

        val sharedPrefs = SharedPrefsManager(this)

        if (sharedPrefs.sugarSyncDir == null) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.GET_ACCOUNTS,
                                    Manifest.permission.ACCESS_NETWORK_STATE,
                                    Manifest.permission.BLUETOOTH),
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS)

            } else {
                /*------------------------------------------------------------*/
               //comment on 27 sep 2023
            launchFilePicker()

                /*------------------------------------------------------------*/
            }
        } else {


            if (doesNoLoginFileExist()) {
                getDefaultLogin()
//                loginWithStubUser()

            } else {

                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build()

                val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

                mLoginButton = findViewById<View>(R.id.sign_in_button) as SignInButton
                mProgressBar = findViewById<View>(R.id.progress_bar) as ProgressBar

                mLoginButton!!.setOnClickListener {
                    if (Utilities.isInternetPresent(this@Login)) {
                        mLoginButton!!.visibility = View.GONE
                        mProgressBar!!.visibility = View.VISIBLE
                        val signInIntent = mGoogleSignInClient.signInIntent
                        startActivityForResult(signInIntent, RC_SIGN_IN)
                    } else {
                        Utilities.longToast(this@Login, "No internet connection found!")
                    }
                }
            }
        }
    }

    private fun launchFilePicker() {
        val intent = Intent(this@Login, FilePickerActivity::class.java)
        intent.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)
        intent.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().path)
        intent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false)
        intent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR)
        intent.putExtra(FilePickerActivity.EXTRA_TITLE, "PLEASE SELECT A DATA FOLDER...")
        startActivityForResult(intent, FILE_CODE)
    }


    override fun onResume() {
        super.onResume()

        val sharedPrefsManager = SharedPrefsManager(this)

        val loggedInUserEmail = sharedPrefsManager.loggedInUser

        if (loggedInUserEmail.isNotEmpty()) {
            mLoginButton?.visibility = View.GONE
            mProgressBar?.visibility = View.VISIBLE

            MixPanelManager.trackSuccessfulLogin(this, true)

            val intent = Intent(this@Login, MainActivity::class.java)
            intent.putExtra("from", "home")
            startActivity(intent)
            finish()
        } else if(sharedPrefsManager.sugarSyncDir != null) {
            Toast.makeText(applicationContext, "The folder is not valid", Toast.LENGTH_SHORT).show()

            /*------------------------------------------------------------*/
           //Comment on 27 sep 2023
        launchFilePicker()

            /*------------------------------------------------------------*/
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    /*------------------------------------------------------------*/
                 //Comment on 27 sep 2023
                launchFilePicker()

                    /*------------------------------------------------------------*/
                } else {
                    Toast.makeText(this, "YOU MUST ACCEPT STORAGE PERMISSION TO CONTINUE", Toast.LENGTH_LONG).show()
                }
                return
            }

        // Add other 'when' lines to check for other
        // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }


    override fun onActivityResult(requestCode: Int, responseCode: Int, intent: Intent?) {
        //        if (requestCode == GoogleApiHelper.RC_SIGN_IN) {
        //            GoogleApiHelper.sIntentInProgress = false;
        //            if (responseCode != RESULT_OK) {
        //                GoogleApiHelper.sSignInClicked = false;
        //            }
        //
        //            if (!googleApiClient.isConnecting()) {
        //                googleApiClient.connect();
        //            }
        //        }
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            handleSignInResult(task)
        }

        if (requestCode == FILE_CODE && responseCode == Activity.RESULT_OK) {
            val uri = intent?.data
            val path = uri!!.path
            val sharedPrefs = SharedPrefsManager(this)
            Timber.d("setting sugar directory")
            sharedPrefs.sugarSyncDir = path

            onCreate(null)

        } else if (requestCode == FILE_CODE && responseCode == RESULT_CANCELED) {
            finish()
        }

    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
//            Toast.makeText(this, "Logged in with email ${account.email}", Toast.LENGTH_LONG).show()


            val lastResortCompleteListener = object : UpdateUsers.OnUpdateUsersCompleteListener {
                override fun onComplete() {
                    onUpdateUsersComplete(account!!)
                }

                override fun onError() {
                    if (dialog != null && dialog!!.isShowing) {
                        dialog!!.dismiss()
                    }
                    Utilities.longToast(this@Login, "Couldn't log in. Please contact Henry.")
                    finish()
                }
            }

            val monFriCompleteListener = object : UpdateUsers.OnUpdateUsersCompleteListener {
                override fun onComplete() {
                    onUpdateUsersComplete(account!!)
                }

                override fun onError() {
                    if (dialog != null && dialog!!.isShowing) {
                        dialog!!.dismiss()
                    }
                    onUpdateUsersError(lastResortCompleteListener)
                }
            }

            val completeListener = object : UpdateUsers.OnUpdateUsersCompleteListener {
                override fun onComplete() {
                    onUpdateUsersComplete(account!!)
                }

                override fun onError() {
                    onUpdateUsersError(monFriCompleteListener)
                }
            }

            val updateUsers = UpdateUsers(this)
            updateUsers.setOnUpdateUsersCompleteListener(completeListener)
            updateUsers.execute("")

        } catch (e: ApiException) {

            Toast.makeText(this, "Encountered error ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }

    }


    private fun onUpdateUsersComplete(account: GoogleSignInAccount) {
//        sSignInClicked = false

//        if (googleApiClient!!.isConnected) {
            val loggedInEmail = account.email

            val users = ArrayList<Salesman>()

            val usersCursor = contentResolver.query(Contract.User.CONTENT_URI, null, null, null, null)

            while (usersCursor!!.moveToNext()) {
                users.add(ProviderUtils.CursorToSalesman(usersCursor))
            }


            var loggedInUser: Salesman? = null

            for (user in users) {
                if (user.email.equals(loggedInEmail)) {
                    loggedInUser = user
                }
            }

            if (loggedInUser != null) {
                Toast.makeText(this, loggedInUser.name + " is connected!", Toast.LENGTH_LONG).show()
                val sharedPrefsManager = SharedPrefsManager(this)
                sharedPrefsManager.saveLoggedInUser(loggedInUser)
                SalesmanManager.setCurrentSalesman(this, loggedInUser)

                setIsAdmin(loggedInEmail)

                MixPanelManager.trackSuccessfulLogin(this, false)

                val intent = Intent(this@Login, MainActivity::class.java)
                intent.putExtra("from", "home")
                startActivity(intent)
                finish()

            } else {
                Utilities.longToast(this, "Sorry, you are not a recognized user!")
            }
//        }
    }


    private fun onUpdateUsersError(completeListener: UpdateUsers.OnUpdateUsersCompleteListener?) {

        dialog = Dialog(this)

        dialog?.setContentView(R.layout.dialog_login_old_data)
        dialog?.setTitle("WARNING!!!")
        dialog?.findViewById<Button>(R.id.btn_done)?.setOnClickListener {
            val isMonday = (dialog?.findViewById(R.id.slct_monday) as RadioButton).isChecked
            val isTuesday = (dialog?.findViewById(R.id.slct_tuesday) as RadioButton).isChecked
            val isWednesday = (dialog?.findViewById(R.id.slct_wednesday) as RadioButton).isChecked
            val isThursday = (dialog?.findViewById(R.id.slct_thursday) as RadioButton).isChecked
            val isFriday = (dialog?.findViewById(R.id.slct_friday) as RadioButton).isChecked
            val isRnD = (dialog?.findViewById(R.id.slct_rnd) as RadioButton).isChecked

            if (completeListener != null) {
                when {
                    isMonday -> {
                        val updateUsers1 = UpdateUsers(this)
                        updateUsers1.setOnUpdateUsersCompleteListener(completeListener)
                        updateUsers1.execute("01-MON")
                    }
                    isTuesday -> {
                        val updateUsers1 = UpdateUsers(this)
                        updateUsers1.setOnUpdateUsersCompleteListener(completeListener)
                        updateUsers1.execute("02-TUE")
                    }
                    isWednesday -> {
                        val updateUsers1 = UpdateUsers(this)
                        updateUsers1.setOnUpdateUsersCompleteListener(completeListener)
                        updateUsers1.execute("03-WED")
                    }
                    isThursday -> {
                        val updateUsers1 = UpdateUsers(this)
                        updateUsers1.setOnUpdateUsersCompleteListener(completeListener)
                        updateUsers1.execute("04-THUR")
                    }
                    isFriday -> {
                        val updateUsers1 = UpdateUsers(this)
                        updateUsers1.setOnUpdateUsersCompleteListener(completeListener)
                        updateUsers1.execute("05-FRI")
                    }
                    isFriday -> {
                        val updateUsers1 = UpdateUsers(this)
                        updateUsers1.setOnUpdateUsersCompleteListener(completeListener)
                        updateUsers1.execute("05-FRI")
                    }
                    isRnD -> {
                        val updateUsers1 = UpdateUsers(this)
                        updateUsers1.setOnUpdateUsersCompleteListener(completeListener)
                        updateUsers1.execute("R&D")
                    }
                }
            }
        }
        dialog?.show()
    }

    override fun onBackPressed() {
        finish()
    }

    companion object {

        private val FILE_CODE = 23
    }

    private fun setIsAdmin(email: String?) {

        val adminEmails = arrayOf("bedessee@gmail.com", "stvmaraj@gmail.com")

        for (adminEmail in adminEmails) {
            if (email == adminEmail) {
                val sharedPrefsManager = SharedPrefsManager(this)
                sharedPrefsManager.isAdmin = true
            }
        }
    }


    private fun doesNoLoginFileExist(): Boolean {
        return try {
            val sharedPrefs = SharedPrefsManager(this)
            val path = sharedPrefs.sugarSyncDir + "/data/Nologin.json"
            return File(path).exists()
        }
        catch (ex: Exception) {
            false
        }
    }


    private fun getDefaultLogin() {

            val dirpath: String

            val sharedPrefs = SharedPrefsManager(this)

            val sugarSyncPath = sharedPrefs.sugarSyncDir

            dirpath = "$sugarSyncPath/data/nologin.json"

            val file = File(dirpath)

            val ois = FileInputStream(file)
            val inputStream = BufferedInputStream(ois)

            val size = inputStream.available()
            val buffer = ByteArray(size)

            inputStream.read(buffer)

            inputStream.close()
            ois.close()

            val result = String(buffer, Charset.forName("UTF-8"))

            val jsonObject = JSONObject(result)

            val noLogin = Gson().fromJson(jsonObject.toString(), NoLogin::class.java)


        if (TextUtils.isEmpty(noLogin.name) || TextUtils.isEmpty(noLogin.email)) {
            loginWithStubUser(noLogin)
        } else {
            loginWithStubUser(noLogin, Salesman(noLogin.name, noLogin.email))
        }
    }


    private fun loginWithStubUser(noLogin: NoLogin?, loggedInUser: Salesman = Salesman("PRODUCT APP", "bil.android.app@gmail.com")) {

//        val loggedInUser = Salesman("PRODUCT APP", "bil.android.app@gmail.com")

// Cosmetic changes remove toast, https://trello.com/c/y9vfEt3E/15-do-cosmetic-changes-as-per-rayman
//        Toast.makeText(this, "Logged in with default account (PRODUCT APP - bil.android.app@gmail.com)", Toast.LENGTH_LONG).show()

        val sharedPrefsManager = SharedPrefsManager(this)
        sharedPrefsManager.saveLoggedInUser(loggedInUser)
        SalesmanManager.setCurrentSalesman(this, loggedInUser)

        noLogin?.let {
            sharedPrefsManager.saveNoLogin(noLogin)
        }

        setIsAdmin(loggedInUser.email)

        MixPanelManager.trackSuccessfulLogin(this, false)
        val intent = Intent(this@Login, MainActivity::class.java)
        intent.putExtra("from", "home")
        startActivity(intent)

        finish()

    }

}
