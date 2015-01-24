package com.kramerweb.softballstats;

import java.text.ParseException;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;
 


import com.google.analytics.tracking.android.EasyTracker;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
 
public class Login extends Activity {
    Button btnLogin;
    Button btnLinkToRegister;
    EditText inputEmail;
    EditText inputPassword;
    TextView loginErrorMsg;
 
    // JSON Response node names
    private static String KEY_SUCCESS = "success";
    private static String KEY_UID = "usr_uid";
    private static String KEY_NAME = "usr_name";
    private static String KEY_EMAIL = "usr_email";
    private static String KEY_CREATED_DT = "usr_created_dt";
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
 
        // Importing all assets like buttons, text fields
        inputEmail = (EditText) findViewById(R.id.loginEmail);
        inputPassword = (EditText) findViewById(R.id.loginPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        loginErrorMsg = (TextView) findViewById(R.id.login_error);
 
        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
				String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                
                doLogin task = new doLogin();
                task.execute(new String[] { email, password });
            }
            
            class doLogin extends AsyncTask<String, Void, String> {
				protected String doInBackground(String... loginInfo) {
                    UserFunctions userFunction = new UserFunctions();
                    String email = loginInfo[0];
                    String password = loginInfo[1];
                    String lsResponse = "";
                    
                    JSONObject json = userFunction.loginUser(email, password);
     
                    if(json != null) {
	                    // check for login response
	                    try {
	                        if (json.getString(KEY_SUCCESS) != null) {
	//                            loginErrorMsg.setText("");
	                            String res = json.getString(KEY_SUCCESS); 
	                            if(Integer.parseInt(res) == 1){
	                                // user successfully logged in
	                                // Store user details in SQLite Database
	                                //StatsDbHelper db = new StatsDbHelper(getApplicationContext());
	                                
	                                JSONObject json_user = json.getJSONObject("user");
	                                
	                                userFunction.logoutUser(getApplicationContext());
	                                userFunction.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json.getString(KEY_UID), json_user.getString(KEY_CREATED_DT), getApplicationContext());
	                            	
	                                //sync data with online database
	                                try {
										userFunction.syncData(email, getApplicationContext());
									} catch (ParseException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
	                                 
	                                
	                            }else{
	                                // Error in login
	                            	lsResponse = "Incorrect username/password";
	                                return lsResponse;
	                            }
	                        }
	                    } catch (JSONException e) {
	                        e.printStackTrace();
	                    }
                    } else {
                    	// Error in login
                    	lsResponse = "Unable to reach server";
                        return lsResponse;
                    }
                    
                    return lsResponse;
				}

				@Override
			    protected void onPostExecute(String result) {
					if (result.length() > 0) {
						loginErrorMsg.setText(result);
					} else {
						// Launch Dashboard Screen
                        Intent dashboard = new Intent(getApplicationContext(), Dashboard.class);
                         
                        // Close all views before launching Dashboard
                        dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(dashboard);
                         
                        // Close Login Screen
                        finish();
					}
			    }
            }
        });
 
        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Register.class);
                startActivity(i);
                finish();
            }
        });
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
