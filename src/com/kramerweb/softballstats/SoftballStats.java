package com.kramerweb.softballstats;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
 
import com.google.analytics.tracking.android.EasyTracker;

public class SoftballStats extends Activity {
    UserFunctions userFunctions;
    Button btnLogout;
    
// // Constants
//    // The authority for the sync adapter's content provider
//    public static final String AUTHORITY = "com.kramerweb.softballstats";
//    // An account type, in the form of a domain name
//    public static final String ACCOUNT_TYPE = "example.com";
//    // The account name
//    public static final String ACCOUNT = "dummyaccount";
//    // Instance fields
//    Account mAccount;
    
    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
//    public static Account CreateSyncAccount(Context context) {
//        // Create the account type and default account
//        Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
//        // Get an instance of the Android account manager
//        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
//        /*
//         * Add the account and account type, no password or user data
//         * If successful, return the Account object, otherwise report an error.
//         */
//        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
//            /*
//             * If you don't set android:syncable="true" in
//             * in your <provider> element in the manifest,
//             * then call context.setIsSyncable(account, AUTHORITY, 1)
//             * here.
//             */
//        } else {
//            /*
//             * The account exists or some other error occurred. Log this, report it,
//             * or handle it internally.
//             */
//        }
//        return newAccount;
//    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         
//     // Create the dummy account
//        mAccount = CreateSyncAccount(this);
        
        /**
         * Dashboard Screen for the application
         * */       
        // Check login status in database
        userFunctions = new UserFunctions();
        if(userFunctions.isUserLoggedIn(getApplicationContext())){
       // user already logged in show dashboard
            /*setContentView(R.layout.dashboard);
            btnLogout = (Button) findViewById(R.id.btnLogout);
             
            btnLogout.setOnClickListener(new View.OnClickListener() {
                 
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    userFunctions.logoutUser(getApplicationContext());
                    Intent login = new Intent(getApplicationContext(), Login.class);
                    login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(login);
                    // Closing dashboard screen
                    finish();
                }
            });*/
        	
        	Intent login = new Intent(getApplicationContext(), Dashboard.class);
            startActivity(login);
            // Closing login screen
            finish();
             
        }else{
            // user is not logged in show login screen
            Intent login = new Intent(getApplicationContext(), Login.class);
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(login);
            // Closing dashboard screen
            finish();
        }        
    }
    
    @Override
    public void onStart() {
      super.onStart();
      
      EasyTracker.getInstance().activityStart(this);  // Add this method.
    }

    @Override
    public void onStop() {
      super.onStop();
      
      EasyTracker.getInstance().activityStop(this);  // Add this method.
    }
}