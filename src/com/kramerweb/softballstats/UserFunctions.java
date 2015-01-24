	package com.kramerweb.softballstats;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
 




import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 




import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
 
public class UserFunctions {
     
    private JSONParser jsonParser;
	private StatsDbHelper mDbHelper;
     
    // Testing in localhost using wamp or xampp 
    // use http://10.0.2.2/ to connect to your localhost ie http://localhost/
    private static String LOGIN_URL = "http://www.kramerweb.us/stats-api/DEV/";
    private static String REGISTER_URL = "http://www.kramerweb.us/stats-api/DEV/";
     
    private static String LOGIN_TAG = "login";
    private static String REGISTER_TAG = "register";
    private static String TEAM_SYNCUP_TAG = "teamsyncup";
    private static String TEAM_SYNCDOWN_TAG = "teamsyncdown";
    private static String LEAGUE_SYNCUP_TAG = "leaguesyncup";
    private static String LEAGUE_SYNCDOWN_TAG = "leaguesyncdown";
    private static String TOURNAMENT_SYNCUP_TAG = "tournamentsyncup";
    private static String TOURNAMENT_SYNCDOWN_TAG = "tournamentsyncdown";
    private static String GAME_SYNCUP_TAG = "gamesyncup";
    private static String GAME_SYNCDOWN_TAG = "gamesyncdown";
    
    private static String KEY_SUCCESS = "success";
    
    //team table
//    private static String KEY_TM_SERVER_ID;
//    private static String KEY_TM_NAME;
//    private static String KEY_TM_YEAR;
//    private static String KEY_TM_USR_UNIQUE_ID;
//    private static String KEY_TM_SYNC_DT = "tm_sync_dt";
//    private static String KEY_TM_DELETE_FLG;
//    private static String KEY_TM_APP_ID = "tm_app_id";
    
     
    // constructor
    public UserFunctions(){
        jsonParser = new JSONParser();
    }
    
    public void addUser(String lsName, String lsEmail, String lsUID, String lsCreatedDt, Context context) {
    	mDbHelper = new StatsDbHelper(context);
        mDbHelper.open();
         
        // Clear all previous data in database
        mDbHelper.addUser(lsName, lsEmail, lsUID, lsCreatedDt);
        
    }
    
    //check for user being logged in
	public boolean isUserLoggedIn(Context context){
		mDbHelper = new StatsDbHelper(context);
		mDbHelper.open();
		int count = mDbHelper.getRowCount();
		if(count > 0){
			// user logged in
			mDbHelper.close();
	        return true;
		}
		mDbHelper.close();
	    return false;
	}
     
    //make login request
    public JSONObject loginUser(String email, String password){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", LOGIN_TAG));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonParser.getJSONFromUrl(LOGIN_URL, params);
        // return json
        // Log.e("JSON", json.toString());
        return json;
    }
    
	//logout user
	public boolean logoutUser(Context context){
//		mDbHelper = new StatsDbHelper(context);
//		mDbHelper.open();
//		mDbHelper.resetTables();
//		mDbHelper.close();
		return true;
	}
    
    public JSONObject registerUser(String name, String email, String password){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", REGISTER_TAG));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
         
        // getting JSON Object
        JSONObject json = jsonParser.getJSONFromUrl(REGISTER_URL, params);
        // return json
        return json;
    }
     
    //function for sycnhronizing data between sqlite db and my server (cloud) database
    public void syncData(String liUID, Context context) throws ParseException {
    	JSONObject json;
    	Cursor teamsCursor;
    	Cursor leaguesCursor;
    	Cursor tournamentsCursor;
    	Cursor gamesCursor;
    	List<NameValuePair> teamParams = new ArrayList<NameValuePair>();
    	List<NameValuePair> leaguesParams = new ArrayList<NameValuePair>();
    	List<NameValuePair> tournamentsParams = new ArrayList<NameValuePair>();
    	List<NameValuePair> gamesParams = new ArrayList<NameValuePair>();
    	String lsSyncDate;
    	final long CURRENT_UTC = System.currentTimeMillis();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
    	
    	String modifiedDate;
    	String tnDate;
    	String gmDate;
    	Long appId;
    	
        Date resultdate = new Date(CURRENT_UTC);
        lsSyncDate = sdf.format(resultdate);
    	
        mDbHelper = new StatsDbHelper(context);
        mDbHelper.open();
        
        /**************************************** sync teams ****************************************/
        //look for records on server to update to app
        teamParams.add(new BasicNameValuePair("tag", TEAM_SYNCDOWN_TAG));
        teamParams.add(new BasicNameValuePair("uid", liUID));
    	// getting JSON Object
        json = jsonParser.getJSONFromUrl(REGISTER_URL, teamParams);
        
        // check for team update response
        try {
            if (json.getString(KEY_SUCCESS) != null) {
                //registerErrorMsg.setText(""); ------Setup later to show in logs
                String res = json.getString(KEY_SUCCESS); 
                if(Integer.parseInt(res) == 1){
                    JSONArray getTeamsArray = json.getJSONArray("team");
                    for(int a =0 ; a < getTeamsArray.length(); a++)
                    {
                        JSONObject getJSonObj = getTeamsArray.getJSONObject(a);
                        mDbHelper.syncTeam(getJSonObj.getString(StatsDbHelper.KEY_TM_SERVER_ID), 
                        					getJSonObj.getString(StatsDbHelper.KEY_TM_NAME), 
                        					getJSonObj.getString(StatsDbHelper.KEY_TM_YEAR), 
                        					getJSonObj.getString(StatsDbHelper.KEY_TM_MOD_DT), 
                        					getJSonObj.getString(StatsDbHelper.KEY_TM_DELETE), 
                        					getJSonObj.getString(StatsDbHelper.KEY_TM_APP_ID));                        
                    }
                    
                }else{
                    // Error in registration
                    //registerErrorMsg.setText("Error occured in registration");  ------Setup later to show in logs
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    	//loop through records on app to sync to server
    	teamsCursor = mDbHelper.fetchTeams(0);
    	if(teamsCursor.moveToFirst()){
	     	//loop through cursor and insert values into adaptor
	        do {
		    	teamParams.add(new BasicNameValuePair("tag", TEAM_SYNCUP_TAG));
		    	teamParams.add(new BasicNameValuePair("uid", liUID));
		    	teamParams.add(new BasicNameValuePair("id", teamsCursor.getString(teamsCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_ID))));
		    	teamParams.add(new BasicNameValuePair("tm_year", teamsCursor.getString(teamsCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_TM_YEAR))));
		    	teamParams.add(new BasicNameValuePair("tm_name", teamsCursor.getString(teamsCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_TM_NAME))));
	        	modifiedDate = sdf.format(new Date(teamsCursor.getLong(teamsCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_TM_MOD_DT))));
		    	teamParams.add(new BasicNameValuePair("tm_modified_dt", modifiedDate));
		    	teamParams.add(new BasicNameValuePair("tm_delete", teamsCursor.getString(teamsCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_TM_DELETE))));
		    	teamParams.add(new BasicNameValuePair("tm_server_id", teamsCursor.getString(teamsCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_TM_SERVER_ID))));
		    	// getting JSON Object
		        json = jsonParser.getJSONFromUrl(REGISTER_URL, teamParams);
		        
		        // check for team update response
                try {
                    if (json.getString(KEY_SUCCESS) != null) {
                        //registerErrorMsg.setText(""); ------Setup later to show in logs
                        String res = json.getString(KEY_SUCCESS); 
                        if(Integer.parseInt(res) == 1){
                            JSONObject json_team = json.getJSONObject("team");
                             
                            // sync back to database
                            mDbHelper.syncTeam(json_team.getString(StatsDbHelper.KEY_TM_SERVER_ID), 
                            					json_team.getString(StatsDbHelper.KEY_TM_NAME), 
                            					json_team.getString(StatsDbHelper.KEY_TM_YEAR), 
                            					json_team.getString(StatsDbHelper.KEY_TM_MOD_DT), 
                            					json_team.getString(StatsDbHelper.KEY_TM_DELETE), 
                            					json_team.getString(StatsDbHelper.KEY_TM_APP_ID));                        
                            
                        }else{
                            // Error in registration
                            //registerErrorMsg.setText("Error occurred in registration");  ------Setup later to show in logs
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
	        } while(teamsCursor.moveToNext());
    	}
    	/**************************************** end sync teams ****************************************/
    	
    	/**************************************** sync leagues ****************************************/
    	//look for records on server to update to app
    	leaguesParams.add(new BasicNameValuePair("tag", LEAGUE_SYNCDOWN_TAG));
    	leaguesParams.add(new BasicNameValuePair("uid", liUID));
    	// getting JSON Object
        json = jsonParser.getJSONFromUrl(REGISTER_URL, leaguesParams);
        
        // check for league update response
        try {
            if (json.getString(KEY_SUCCESS) != null) {
                //registerErrorMsg.setText(""); ------Setup later to show in logs
                String res = json.getString(KEY_SUCCESS); 
                if(Integer.parseInt(res) == 1){
                    //JSONObject json_league = json.getJSONObject("league");
                    
                    JSONArray getLeaguesArray = json.getJSONArray("league");
                    for(int a =0 ; a < getLeaguesArray.length(); a++)
                    {
                        JSONObject getJSonObj = getLeaguesArray.getJSONObject(a);
                        //String Name = getJSonObj.getString("name");// sync back to database
                        mDbHelper.syncLeague(getJSonObj.getString(StatsDbHelper.KEY_LG_TM_ID), 
                        						getJSonObj.getString(StatsDbHelper.KEY_LG_NAME), 
                        						getJSonObj.getString(StatsDbHelper.KEY_LG_DESCRIPTION), 
                        						getJSonObj.getString(StatsDbHelper.KEY_LG_MOD_DT), 
                        						getJSonObj.getString(StatsDbHelper.KEY_LG_DELETE), 
                        						getJSonObj.getString(StatsDbHelper.KEY_LG_SERVER_ID), 
                        						getJSonObj.getString(StatsDbHelper.KEY_LG_APP_ID));                        
                    }
                    
                }else{
                    // Error in registration
                    //registerErrorMsg.setText("Error occured in registration");  ------Setup later to show in logs
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
    	//loop through records on app to sync to server
    	leaguesCursor = mDbHelper.fetchLeagues((long) 0);
    	if(leaguesCursor.moveToFirst()){
	     	//loop through cursor and insert values into adaptor
	        do {
		    	leaguesParams.add(new BasicNameValuePair("tag", LEAGUE_SYNCUP_TAG));
		    	leaguesParams.add(new BasicNameValuePair("uid", liUID));
		    	leaguesParams.add(new BasicNameValuePair("id", leaguesCursor.getString(leaguesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_ID))));
		    	leaguesParams.add(new BasicNameValuePair("lg_name", leaguesCursor.getString(leaguesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_LG_NAME))));
		    	leaguesParams.add(new BasicNameValuePair("lg_description", leaguesCursor.getString(leaguesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_LG_DESCRIPTION))));
		    	leaguesParams.add(new BasicNameValuePair("lg_tm_id", leaguesCursor.getString(leaguesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_LG_TM_ID))));
	        	modifiedDate = sdf.format(new Date(leaguesCursor.getLong(leaguesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_LG_MOD_DT))));
		    	leaguesParams.add(new BasicNameValuePair("lg_modified_dt", modifiedDate));
		    	leaguesParams.add(new BasicNameValuePair("lg_delete", leaguesCursor.getString(leaguesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_LG_DELETE))));
		    	leaguesParams.add(new BasicNameValuePair("lg_server_id", leaguesCursor.getString(leaguesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_LG_SERVER_ID))));
		    	// getting JSON Object
		        json = jsonParser.getJSONFromUrl(REGISTER_URL, leaguesParams);
		        
		        // check for league update response
                try {
                    if (json.getString(KEY_SUCCESS) != null) {
                        //registerErrorMsg.setText(""); ------Setup later to show in logs
                        String res = json.getString(KEY_SUCCESS); 
                        if(Integer.parseInt(res) == 1){
                            JSONObject json_league = json.getJSONObject("league");
                             
                            // sync back to database
                            mDbHelper.syncLeague(json_league.getString(StatsDbHelper.KEY_LG_TM_ID), 
                            						json_league.getString(StatsDbHelper.KEY_LG_NAME), 
                            						json_league.getString(StatsDbHelper.KEY_LG_DESCRIPTION), 
                            						json_league.getString(StatsDbHelper.KEY_LG_MOD_DT), 
                            						json_league.getString(StatsDbHelper.KEY_LG_DELETE), 
                            						json_league.getString(StatsDbHelper.KEY_LG_SERVER_ID), 
                            						json_league.getString(StatsDbHelper.KEY_LG_APP_ID));                        
                        }else{
                            // Error in registration
                            //registerErrorMsg.setText("Error occured in registration");  ------Setup later to show in logs
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
	        } while(leaguesCursor.moveToNext());
    	}
    	/**************************************** end sync leagues ****************************************/
    	
    	/**************************************** sync tournaments ****************************************/
    	//look for records on server to update to app
    	tournamentsParams.add(new BasicNameValuePair("tag", TOURNAMENT_SYNCDOWN_TAG));
    	tournamentsParams.add(new BasicNameValuePair("uid", liUID));
    	// getting JSON Object
        json = jsonParser.getJSONFromUrl(REGISTER_URL, tournamentsParams);
        
        // check for tournament update response
        try {
            if (json.getString(KEY_SUCCESS) != null) {
                //registerErrorMsg.setText(""); ------Setup later to show in logs
                String res = json.getString(KEY_SUCCESS); 
                if(Integer.parseInt(res) == 1){
                    //JSONObject json_league = json.getJSONObject("league");
                    
                    JSONArray getLeaguesArray = json.getJSONArray("tournament");
                    for(int a =0 ; a < getLeaguesArray.length(); a++)
                    {
                        JSONObject getJSonObj = getLeaguesArray.getJSONObject(a);
                        //String Name = getJSonObj.getString("name");// sync back to database
                        mDbHelper.syncTournament(getJSonObj.getString(StatsDbHelper.KEY_TN_TM_ID), 
                        							getJSonObj.getString(StatsDbHelper.KEY_TN_NAME), 
                        							getJSonObj.getString(StatsDbHelper.KEY_TN_DATE), 
                        							getJSonObj.getString(StatsDbHelper.KEY_TN_MOD_DT), 
                        							getJSonObj.getString(StatsDbHelper.KEY_TN_DELETE), 
                        							getJSonObj.getString(StatsDbHelper.KEY_TN_SERVER_ID), 
                        							getJSonObj.getString(StatsDbHelper.KEY_TN_APP_ID));                        
                    }
                    
                }else{
                    // Error in registration
                    //registerErrorMsg.setText("Error occured in registration");  ------Setup later to show in logs
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
    	//loop through records on app to sync to server
    	tournamentsCursor = mDbHelper.fetchTournaments((long) 0);
    	if(tournamentsCursor.moveToFirst()){
	     	//loop through cursor and insert values into adaptor
	        do {
		    	tournamentsParams.add(new BasicNameValuePair("tag", TOURNAMENT_SYNCUP_TAG));
		    	tournamentsParams.add(new BasicNameValuePair("uid", liUID));
		    	tournamentsParams.add(new BasicNameValuePair("id", tournamentsCursor.getString(tournamentsCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_ID))));
		    	tournamentsParams.add(new BasicNameValuePair("tn_name", tournamentsCursor.getString(tournamentsCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_TN_NAME))));
		    	tnDate = sdf.format(dateFormat.parse(tournamentsCursor.getString(tournamentsCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_TN_DATE))));
		    	//tnDate = tournamentsCursor.getString(tournamentsCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_TN_DATE));
		    	tournamentsParams.add(new BasicNameValuePair("tn_date", tnDate));
		    	tournamentsParams.add(new BasicNameValuePair("tn_tm_id", tournamentsCursor.getString(tournamentsCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_TN_TM_ID))));
	        	modifiedDate = sdf.format(new Date(tournamentsCursor.getLong(tournamentsCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_TN_MOD_DT))));
	        	tournamentsParams.add(new BasicNameValuePair("tn_modified_dt", modifiedDate));
	        	tournamentsParams.add(new BasicNameValuePair("tn_delete", tournamentsCursor.getString(tournamentsCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_TN_DELETE))));
	        	tournamentsParams.add(new BasicNameValuePair("tn_server_id", tournamentsCursor.getString(tournamentsCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_TN_SERVER_ID))));
		    	// getting JSON Object
		        json = jsonParser.getJSONFromUrl(REGISTER_URL, tournamentsParams);
		        Log.w("DEBUG", "tn_name=" + tournamentsCursor.getString(tournamentsCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_TN_NAME)) + ";");
		        // check for league update response
                try {
                    if (json.getString(KEY_SUCCESS) != null) {
                        //registerErrorMsg.setText(""); ------Setup later to show in logs
                        String res = json.getString(KEY_SUCCESS); 
                        if(Integer.parseInt(res) == 1){
                            JSONObject json_tournament = json.getJSONObject("tournament");
                             
                            // sync back to database
                            mDbHelper.syncTournament(json_tournament.getString(StatsDbHelper.KEY_TN_TM_ID), 
                            							json_tournament.getString(StatsDbHelper.KEY_TN_NAME), 
                            							json_tournament.getString(StatsDbHelper.KEY_TN_DATE), 
                            							json_tournament.getString(StatsDbHelper.KEY_TN_MOD_DT), 
                            							json_tournament.getString(StatsDbHelper.KEY_TN_DELETE), 
                            							json_tournament.getString(StatsDbHelper.KEY_TN_SERVER_ID), 
                            							json_tournament.getString(StatsDbHelper.KEY_TN_APP_ID));                        
                        }else{
                            // Error in registration
                            //registerErrorMsg.setText("Error occured in registration");  ------Setup later to show in logs
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
	        } while(tournamentsCursor.moveToNext());
    	}
    	/**************************************** end sync tournaments ****************************************/
    	
    	/**************************************** sync games ****************************************/
    	//look for records on server to update to app
    	gamesParams.add(new BasicNameValuePair("tag", GAME_SYNCDOWN_TAG));
    	gamesParams.add(new BasicNameValuePair("uid", liUID));
    	// getting JSON Object
        json = jsonParser.getJSONFromUrl(REGISTER_URL, gamesParams);
        
        // check for tournament update response
        try {
            if (json.getString(KEY_SUCCESS) != null) {
                //registerErrorMsg.setText(""); ------Setup later to show in logs
                String res = json.getString(KEY_SUCCESS); 
                if(Integer.parseInt(res) == 1){
                    //JSONObject json_league = json.getJSONObject("league");
                    
                    JSONArray getGamesArray = json.getJSONArray("game");
                    for(int a =0 ; a < getGamesArray.length(); a++)
                    {
                        JSONObject getJSonObj = getGamesArray.getJSONObject(a);
                        //String Name = getJSonObj.getString("name");// sync back to database
                        mDbHelper.syncGame(getJSonObj.getString(StatsDbHelper.KEY_GM_DATE), 
                        												getJSonObj.getString(StatsDbHelper.KEY_GM_AB), 
                        												getJSonObj.getString(StatsDbHelper.KEY_GM_1B), 
                        												getJSonObj.getString(StatsDbHelper.KEY_GM_2B),  
                        												getJSonObj.getString(StatsDbHelper.KEY_GM_3B), 
                        												getJSonObj.getString(StatsDbHelper.KEY_GM_HR), 
                        												getJSonObj.getString(StatsDbHelper.KEY_GM_R), 
                        												getJSonObj.getString(StatsDbHelper.KEY_GM_RBI), 
                        												getJSonObj.getString(StatsDbHelper.KEY_GM_BB), 
                        												getJSonObj.getString(StatsDbHelper.KEY_GM_SO), 
                        												getJSonObj.getString(StatsDbHelper.KEY_GM_SAC), 
                        												getJSonObj.getString(StatsDbHelper.KEY_GM_ROE), 
                        												getJSonObj.getString(StatsDbHelper.KEY_GM_FC),
                        												getJSonObj.getString(StatsDbHelper.KEY_GM_MOD_DT), 
                        												getJSonObj.getString(StatsDbHelper.KEY_GM_DELETE), 
                        												getJSonObj.getString(StatsDbHelper.KEY_GM_SERVER_ID), 
                        												getJSonObj.getString(StatsDbHelper.KEY_GM_APP_ID));                        
                    }
                }else{
                    // Error in registration
                    //registerErrorMsg.setText("Error occured in registration");  ------Setup later to show in logs
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
    	//loop through records on app to sync to server
    	gamesCursor = mDbHelper.fetchGame((long) 0);
    	if(gamesCursor.moveToFirst()){
	     	//loop through cursor and insert values into adaptor
	        do {
		    	gamesParams.add(new BasicNameValuePair("tag", GAME_SYNCUP_TAG));
		    	gamesParams.add(new BasicNameValuePair("uid", liUID));
		    	gamesParams.add(new BasicNameValuePair("id", gamesCursor.getString(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_ID))));
		    	gmDate = sdf.format(dateFormat.parse(gamesCursor.getString(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_DATE))));
		    	gamesParams.add(new BasicNameValuePair("gm_date", gmDate));
		    	gamesParams.add(new BasicNameValuePair("gm_ab", gamesCursor.getString(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_AB))));
		    	gamesParams.add(new BasicNameValuePair("gm_1b", gamesCursor.getString(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_1B))));
		    	gamesParams.add(new BasicNameValuePair("gm_2b", gamesCursor.getString(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_2B))));
		    	gamesParams.add(new BasicNameValuePair("gm_3b", gamesCursor.getString(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_3B))));
		    	gamesParams.add(new BasicNameValuePair("gm_hr", gamesCursor.getString(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_HR))));
		    	gamesParams.add(new BasicNameValuePair("gm_r", gamesCursor.getString(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_R))));
		    	gamesParams.add(new BasicNameValuePair("gm_rbi", gamesCursor.getString(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_RBI))));
		    	gamesParams.add(new BasicNameValuePair("gm_bb", gamesCursor.getString(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_BB))));
		    	gamesParams.add(new BasicNameValuePair("gm_so", gamesCursor.getString(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_SO))));
		    	gamesParams.add(new BasicNameValuePair("gm_sac", gamesCursor.getString(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_SAC))));
		    	gamesParams.add(new BasicNameValuePair("gm_roe", gamesCursor.getString(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_ROE))));
		    	gamesParams.add(new BasicNameValuePair("gm_fc", gamesCursor.getString(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_FC))));
	        	modifiedDate = sdf.format(new Date(gamesCursor.getLong(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_MOD_DT))));
	        	gamesParams.add(new BasicNameValuePair("gm_modified_dt", modifiedDate));
	        	gamesParams.add(new BasicNameValuePair("gm_delete", gamesCursor.getString(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_DELETE))));
	        	gamesParams.add(new BasicNameValuePair("gm_server_id", gamesCursor.getString(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_SERVER_ID))));
		    	// getting JSON Object
		        json = jsonParser.getJSONFromUrl(REGISTER_URL, gamesParams);
		        //Log.w("DEBUG", "tn_name=" + gamesCursor.getString(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_TN_NAME)) + ";");
		        // check for league update response
                try {
                    if (json.getString(KEY_SUCCESS) != null) {
                        //registerErrorMsg.setText(""); ------Setup later to show in logs
                        String res = json.getString(KEY_SUCCESS); 
                        if(Integer.parseInt(res) == 1){
                            JSONObject json_game = json.getJSONObject("game");
                             
                            // sync back to database
                            mDbHelper.syncGame(json_game.getString(StatsDbHelper.KEY_GM_DATE), 
                            							json_game.getString(StatsDbHelper.KEY_GM_AB), 
                            							json_game.getString(StatsDbHelper.KEY_GM_1B),
                            							json_game.getString(StatsDbHelper.KEY_GM_2B),
                            							json_game.getString(StatsDbHelper.KEY_GM_3B),
                            							json_game.getString(StatsDbHelper.KEY_GM_HR),
                            							json_game.getString(StatsDbHelper.KEY_GM_R),
                            							json_game.getString(StatsDbHelper.KEY_GM_RBI),
                            							json_game.getString(StatsDbHelper.KEY_GM_BB),
                            							json_game.getString(StatsDbHelper.KEY_GM_SO),
                            							json_game.getString(StatsDbHelper.KEY_GM_SAC),
                            							json_game.getString(StatsDbHelper.KEY_GM_ROE),
                            							json_game.getString(StatsDbHelper.KEY_GM_FC),
                            							json_game.getString(StatsDbHelper.KEY_GM_MOD_DT), 
                            							json_game.getString(StatsDbHelper.KEY_GM_DELETE), 
                            							json_game.getString(StatsDbHelper.KEY_GM_SERVER_ID), 
                            							json_game.getString(StatsDbHelper.KEY_GM_APP_ID));                        
                        }else{
                            // Error in registration
                            //registerErrorMsg.setText("Error occured in registration");  ------Setup later to show in logs
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
	        } while(gamesCursor.moveToNext());
    	}
    	/**************************************** end sync games ****************************************/
    	
    	//sync games
    	
    	
    	mDbHelper.close();
    }
}
