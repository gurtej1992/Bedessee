//package com.bedessee.sales20.login;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentSender;
//import android.database.Cursor;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.RadioButton;
//import android.widget.Toast;
//
//import com.bedessee.sales20.R;
//import com.bedessee.sales20.main.MainActivity;
//import com.bedessee.sales20.mixpanel.MixPanelManager;
//import com.bedessee.sales20.provider.Contract;
//import com.bedessee.sales20.provider.ProviderUtils;
//import com.bedessee.sales20.salesman.Salesman;
//import com.bedessee.sales20.salesman.SalesmanManager;
//import com.bedessee.sales20.sharedprefs.SharedPrefsManager;
//import com.bedessee.sales20.update.json.UpdateUsers;
//import com.bedessee.sales20.utilities.Utilities;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.Scope;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Helper class to manage Google+ stuff.
// */
//public class GoogleApiHelper {
//
//    /* Client used to interact with Google APIs. */
//    private static GoogleApiClient sGoogleApiClient;
//
//    private static Context sContext;
//
//    /* Request code used to invoke sign in user interactions. */
//    public static final int RC_SIGN_IN = 0;
//
//    /* A flag indicating that a PendingIntent is in progress and prevents
//     * us from starting further intents.
//     */
//    public static boolean sIntentInProgress;
//
//    /* Track whether the sign-in button has been clicked so that we know to resolve
//    * all issues preventing sign-in without waiting.
//    */
//    public static boolean sSignInClicked;
//
//    /* Store the connection result from onConnectionFailed callbacks so that we can
//     * resolve them when the user clicks sign-in.
//     */
//    private static ConnectionResult mConnectionResult;
//
//    public static View.OnClickListener loginClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            if (view.getId() == R.id.sign_in_button && !sGoogleApiClient.isConnecting()) {
//                sSignInClicked = true;
//                sGoogleApiClient.connect();
//            }
//        }
//    };
//
//    private static Activity sActivity;
//
//    private static Dialog dialog;
//
//    private static GoogleApiClient.ConnectionCallbacks mConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
//
//        @Override
//        public void onConnected(Bundle bundle) {
//
//            final UpdateUsers.OnUpdateUsersCompleteListener lastResortCompleteListener = new UpdateUsers.OnUpdateUsersCompleteListener() {
//                @Override
//                public void onComplete() {
//                    onUpdateUsersComplete();
//                }
//
//                @Override
//                public void onError() {
//                    if (dialog != null && dialog.isShowing()) {
//                        dialog.dismiss();
//                    }
//                    Utilities.longToast(sActivity, "Couldn't log in. Please contact Henry.");
//                    sActivity.finish();
//                }
//            };
//
//            final UpdateUsers.OnUpdateUsersCompleteListener monFriCompleteListener = new UpdateUsers.OnUpdateUsersCompleteListener() {
//                @Override
//                public void onComplete() {
//                    onUpdateUsersComplete();
//                }
//
//                @Override
//                public void onError() {
//                    if (dialog != null && dialog.isShowing()) {
//                        dialog.dismiss();
//                    }
//                    onUpdateUsersError(lastResortCompleteListener);
//                }
//            };
//
//            final UpdateUsers.OnUpdateUsersCompleteListener completeListener = new UpdateUsers.OnUpdateUsersCompleteListener() {
//                @Override
//                public void onComplete() {
//                    onUpdateUsersComplete();
//                }
//
//                @Override
//                public void onError() {
//                    onUpdateUsersError(monFriCompleteListener);
//                }
//            };
//
//            final UpdateUsers updateUsers = new UpdateUsers(sActivity);
//            updateUsers.setOnUpdateUsersCompleteListener(completeListener);
//            updateUsers.execute("");
//        }
//
//        @Override
//        public void onConnectionSuspended(int i) {
//            sGoogleApiClient.connect();
//        }
//    };
//
//    private static void onUpdateUsersError(final UpdateUsers.OnUpdateUsersCompleteListener completeListener) {
//
//        dialog = new Dialog(sActivity);
//
//        dialog.setContentView(R.layout.dialog_login_old_data);
//        dialog.setTitle("WARNING!!!");
//        dialog.findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final boolean isMonday = ((RadioButton) dialog.findViewById(R.id.slct_monday)).isChecked();
//                final boolean isTuesday = ((RadioButton) dialog.findViewById(R.id.slct_tuesday)).isChecked();
//                final boolean isWednesday = ((RadioButton) dialog.findViewById(R.id.slct_wednesday)).isChecked();
//                final boolean isThursday = ((RadioButton) dialog.findViewById(R.id.slct_thursday)).isChecked();
//                final boolean isFriday = ((RadioButton) dialog.findViewById(R.id.slct_friday)).isChecked();
//                final boolean isRnD = ((RadioButton) dialog.findViewById(R.id.slct_rnd)).isChecked();
//
//                if (completeListener != null) {
//                    if (isMonday) {
//                        final UpdateUsers updateUsers1 = new UpdateUsers(sActivity);
//                        updateUsers1.setOnUpdateUsersCompleteListener(completeListener);
//                        updateUsers1.execute("01-MON");
//                    }
//                    else if (isTuesday) {
//                        final UpdateUsers updateUsers1 = new UpdateUsers(sActivity);
//                        updateUsers1.setOnUpdateUsersCompleteListener(completeListener);
//                        updateUsers1.execute("02-TUE");
//                    }
//                    else if (isWednesday) {
//                        final UpdateUsers updateUsers1 = new UpdateUsers(sActivity);
//                        updateUsers1.setOnUpdateUsersCompleteListener(completeListener);
//                        updateUsers1.execute("03-WED");
//                    }
//                    else if (isThursday) {
//                        final UpdateUsers updateUsers1 = new UpdateUsers(sActivity);
//                        updateUsers1.setOnUpdateUsersCompleteListener(completeListener);
//                        updateUsers1.execute("04-THUR");
//                    }
//                    else if (isFriday) {
//                        final UpdateUsers updateUsers1 = new UpdateUsers(sActivity);
//                        updateUsers1.setOnUpdateUsersCompleteListener(completeListener);
//                        updateUsers1.execute("05-FRI");
//                    }
//                    else if (isFriday) {
//                        final UpdateUsers updateUsers1 = new UpdateUsers(sActivity);
//                        updateUsers1.setOnUpdateUsersCompleteListener(completeListener);
//                        updateUsers1.execute("05-FRI");
//                    }
//                    else if (isRnD) {
//                        final UpdateUsers updateUsers1 = new UpdateUsers(sActivity);
//                        updateUsers1.setOnUpdateUsersCompleteListener(completeListener);
//                        updateUsers1.execute("R&D");
//                    }
//                }
//
//            }
//        });
//        dialog.show();
//    }
//
//    private static void onUpdateUsersComplete() {
//        sSignInClicked = false;
//
//        if (sGoogleApiClient.isConnected()) {
//            String loggedInEmail = Plus.AccountApi.getAccountName(sGoogleApiClient);
//
//            final List<Salesman> users = new ArrayList<>();
//
//            final Cursor usersCursor = sActivity.getContentResolver().query(Contract.User.CONTENT_URI, null, null, null, null);
//
//            while (usersCursor.moveToNext()) {
//                users.add(ProviderUtils.CursorToSalesman(usersCursor));
//            }
//
//
//            Salesman loggedInUser = null;
//
//            for (Salesman user : users) {
//                if (user.getEmail().equals(loggedInEmail)) {
//                    loggedInUser = user;
//                }
//            }
//
//            if (loggedInUser != null) {
//                Toast.makeText(sContext, loggedInUser.getName() + " is connected!", Toast.LENGTH_LONG).show();
//                SharedPrefsManager sharedPrefsManager = new SharedPrefsManager(sContext);
//                sharedPrefsManager.saveLoggedInUser(loggedInUser);
//                SalesmanManager.setCurrentSalesman(sActivity, loggedInUser);
//
//                setIsAdmin(loggedInEmail);
//
//                MixPanelManager.trackSuccessfulLogin(sActivity, false);
//
//                sActivity.startActivity(new Intent(sActivity, MainActivity.class));
//                sActivity.finish();
//
//            } else {
//                Utilities.longToast(sContext, "Sorry, you are not a recognized user!");
//            }
//        }
//    }
//
//
//    private static GoogleApiClient.OnConnectionFailedListener mOnConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
//        @Override
//        public void onConnectionFailed(ConnectionResult result) {
//            if (!sIntentInProgress && result.hasResolution()) {
//                try {
//                    sIntentInProgress = true;
//                    if(sActivity != null) {
//                        sActivity.startIntentSenderForResult(result.getResolution().getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
//                    }
//                } catch (IntentSender.SendIntentException e) {
//                    // The intent was canceled before it was sent.  Return to the default
//                    // state and attempt to connect to get an updated ConnectionResult.
//                    sIntentInProgress = false;
//                    sGoogleApiClient.connect();
//                }
//            } else if (!sIntentInProgress) {
//                // Store the ConnectionResult so that we can use it later when the user clicks
//                // 'sign-in'.
//                mConnectionResult = result;
//
//                if (sSignInClicked) {
//                    // The user has already clicked 'sign-in' so we attempt to resolve all
//                    // errors until the user is signed in, or they cancel.
//                    resolveSignInErrors();
//                }
//            }
//        }
//    };
//
//    public static void init(Context context, Activity activity) {
//
//        sContext = context;
//
//        sActivity = activity;
//
//        sGoogleApiClient = new GoogleApiClient.Builder(context)
//                .addConnectionCallbacks(mConnectionCallbacks)
//                .addOnConnectionFailedListener(mOnConnectionFailedListener)
//                .addApi(Plus.API, new Plus.PlusOptions.Builder().build())
//                .addScope(Plus.SCOPE_PLUS_PROFILE)
//                .addScope(new Scope("https://www.googleapis.com/auth/plus.login"))
//                .build();
//    }
//
//
//    public static View.OnClickListener getOnClickListener() {
//        return loginClickListener;
//    }
//
//
//
//    public static GoogleApiClient getGoogleApiClient() {
//        return  sGoogleApiClient;
//    }
//
//
//    /* A helper method to resolve the current ConnectionResult error. */
//    private static void resolveSignInErrors() {
//        if (mConnectionResult.hasResolution()) {
//            try {
//                sIntentInProgress = true;
//                if(sActivity != null) {
//                    sActivity.startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
//                }
//            } catch (IntentSender.SendIntentException e) {
//                // The intent was canceled before it was sent.  Return to the default
//                // state and attempt to connect to get an updated ConnectionResult.
//                sIntentInProgress = false;
//                sGoogleApiClient.connect();
//            }
//        }
//    }
//
//
//
//
//    private static void setIsAdmin(String email) {
//
//        String[] adminEmails = { "bedessee@gmail.com", "stvmaraj@gmail.com" };
//
//        for (String adminEmail : adminEmails) {
//            if(email.equals(adminEmail)) {
//                SharedPrefsManager sharedPrefsManager = new SharedPrefsManager(sContext);
//                sharedPrefsManager.setIsAdmin(true);
//            }
//        }
//    }
//
//}