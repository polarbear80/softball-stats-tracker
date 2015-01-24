package com.kramerweb.softballstats;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ParseException;
import android.util.Log;

public class StatsDbHelper {
	
	//TEAM TABLE
	public static final String KEY_TM_YEAR = "tm_year";
    public static final String KEY_TM_NAME = "tm_name";
    public static final String KEY_TM_MOD_DT = "tm_modified_dt";
    public static final String KEY_TM_DELETE = "tm_delete";
    public static final String KEY_TM_SERVER_ID = "tm_server_id";
    public static final String KEY_TM_APP_ID = "tm_app_id";
    //TOURNAMENT TABLE
    public static final String KEY_TN_DATE = "tn_date";
    public static final String KEY_TN_TM_ID = "tn_tm_id";
    public static final String KEY_TN_NAME = "tn_name";
    public static final String KEY_TN_MOD_DT = "tn_modified_dt";
    public static final String KEY_TN_DELETE = "tn_delete";
    public static final String KEY_TN_SERVER_ID = "tn_server_id";
    public static final String KEY_TN_APP_ID = "tn_app_id";
    //LEAGUE TABLE
    public static final String KEY_LG_NAME = "lg_name";
    public static final String KEY_LG_DESCRIPTION = "lg_description";
    public static final String KEY_LG_TM_ID = "lg_tm_id";
    public static final String KEY_LG_MOD_DT = "lg_modified_dt";
    public static final String KEY_LG_DELETE = "lg_delete";
    public static final String KEY_LG_SERVER_ID = "lg_server_id";
    public static final String KEY_LG_APP_ID = "lg_app_id";
    //TOURNAMENT GAME LINK TABLE
    public static final String KEY_TG_TN_ID = "tg_tn_id";
    public static final String KEY_TG_GM_ID = "tg_gm_id";
    public static final String KEY_TG_MOD_DT = "tg_modified_dt";
    public static final String KEY_TG_SERVER_ID = "tg_server_id";
    public static final String KEY_TG_APP_ID = "tg_app_id";
    //LEAGUE GAME LINK TABLE
    public static final String KEY_LGG_LG_ID = "lgg_lg_id";
    public static final String KEY_LGG_GM_ID = "lgg_gm_id";
    public static final String KEY_LGG_MOD_DT = "lgg_modified_dt";
    public static final String KEY_LGG_SERVER_ID = "lgg_server_id";
    public static final String KEY_LGG_APP_ID = "lgg_app_id";
    //GAME TABLE
    public static final String KEY_GM_DATE = "gm_date";
    public static final String KEY_GM_AB = "gm_ab";
    public static final String KEY_GM_1B = "gm_1b";
    public static final String KEY_GM_2B = "gm_2b";
    public static final String KEY_GM_3B = "gm_3b";
    public static final String KEY_GM_HR = "gm_hr";
    public static final String KEY_GM_R = "gm_r";
    public static final String KEY_GM_RBI = "gm_rbi";
    public static final String KEY_GM_BB = "gm_bb";
    public static final String KEY_GM_SO = "gm_so";
    public static final String KEY_GM_SAC = "gm_sac";
    public static final String KEY_GM_ROE = "gm_roe";
    public static final String KEY_GM_FC = "gm_fc";
    public static final String KEY_GM_MOD_DT = "gm_modified_dt";
    public static final String KEY_GM_DELETE = "gm_delete";
    public static final String KEY_GM_SERVER_ID = "gm_server_id";
    public static final String KEY_GM_APP_ID = "gm_app_id"; 
    //LOGIN TABLE
    public static final String KEY_LOGIN_NAME = "l_name";
    public static final String KEY_LOGIN_EMAIL = "l_email";
    public static final String KEY_LOGIN_UID = "l_uid";
    public static final String KEY_LOGIN_CREATED_DT = "l_created_dt";

    public static final String KEY_ID = "_id";
    
    public static final int GAMES_COUNT_TOTAL_LEAGUE = 1;
    public static final int GAMES_COUNT_LEAGUE = 2;
    
    private static DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    private static long CURRENT_UTC = System.currentTimeMillis();;

    /**************************** Database creation sql statements *********************************/
    private static final String DATABASE_TEAM_CREATE =
        "create table team (_id integer primary key autoincrement, "
        + "tm_year integer not null, tm_name text not null, tm_modified_dt real not null, tm_delete int not null, tm_server_id int null, tm_app_id text);";
    private static final String DATABASE_TOURNAMENTS_CREATE =
            "create table tournaments (_id integer primary key autoincrement, "
            + "tn_date text not null, tn_tm_id integer not null, tn_name text not null, tn_modified_dt real not null, tn_delete int not null, tn_server_id int null, tn_app_id text);";
    private static final String DATABASE_LEAGUES_CREATE =
            "create table leagues (_id integer primary key autoincrement, "
            + "lg_tm_id integer not null, lg_name text not null, lg_description text null, lg_modified_dt real not null, lg_delete int not null, lg_server_id int null, lg_app_id text);";
    private static final String DATABASE_TOURNAMENT_GAMES_CREATE =
            "create table tournament_games (_id integer primary key autoincrement, "
            + "tg_tn_id integer not null, tg_gm_id integer not null, tg_modified_dt real not null, tg_server_id int null, tg_app_id text);";
    private static final String DATABASE_LEAGUE_GAMES_CREATE =
            "create table league_games (_id integer primary key autoincrement, "
            + "lgg_lg_id integer not null, lgg_gm_id integer not null, lgg_modified_dt real not null, lgg_server_id int null, lgg_app_id text);";
    private static final String DATABASE_GAMES_CREATE =
            "create table games (_id integer primary key autoincrement, "
            + "gm_date text not null, gm_ab integer not null, "
            + "gm_1b integer not null, gm_2b integer not null, gm_3b integer not null, "
            + "gm_hr integer not null, gm_r integer not null, gm_rbi integer not null, "
            + "gm_bb integer not null, gm_so integer not null, gm_sac integer not null, "
            + "gm_roe integer not null, gm_fc integer not null, gm_modified_dt real not null, gm_delete int not null, gm_server_id int null, gm_app_id text);";
    private static final String DATABASE_LOGIN_CREATE = 
    		"create table login (_id integer primary key autoincrement, "
            + "l_name text, "
            + "l_email text unique, "
            + "l_uid text, "
            + "l_created_dt text)";
    /***********************************************************************************************/
    /****************************** Database selection statements **********************************/
    private final String LEAGUE_GAMES_QUERY = "SELECT lgg_gm_id, gm_date, gm_ab, gm_1b, gm_2b, gm_3b, gm_hr, "
    		+ "gm_r, gm_rbi, gm_bb, gm_so, gm_sac, gm_roe, gm_fc, games._id "
    		+ "FROM league_games "
    		+ "INNER JOIN games ON (lgg_gm_id = games._id) "
    		+ "WHERE (gm_delete IS NULL OR gm_delete = 0) AND lgg_lg_id=? ORDER BY DATE(gm_date) DESC";
    private final String LEAGUE_GAMES_DELETE_QUERY = "SELECT lgg_gm_id, gm_date, gm_ab, gm_1b, gm_2b, gm_3b, gm_hr, "
    		+ "gm_r, gm_rbi, gm_bb, gm_so, gm_sac, gm_roe, gm_fc, games._id "
    		+ "FROM league_games "
    		+ "INNER JOIN games ON (lgg_gm_id = games._id) "
    		+ "WHERE gm_delete = 1 AND lgg_lg_id=? ORDER BY DATE(gm_date) DESC";
    private final String TOURNAMENT_GAMES_QUERY = "SELECT tg_gm_id, gm_date, gm_ab, gm_1b, gm_2b, gm_3b, gm_hr, "
    		+ "gm_r, gm_rbi, gm_bb, gm_so, gm_sac, gm_roe, gm_fc, games._id "
    		+ "FROM tournament_games "
    		+ "INNER JOIN games ON (tg_gm_id = games._id) "
    		+ "WHERE (gm_delete IS NULL OR gm_delete = 0) AND tg_tn_id=? ORDER BY DATE(gm_date) DESC";
    private final String TOURNAMENT_GAMES_DELETE_QUERY = "SELECT tg_gm_id, gm_date, gm_ab, gm_1b, gm_2b, gm_3b, gm_hr, "
    		+ "gm_r, gm_rbi, gm_bb, gm_so, gm_sac, gm_roe, gm_fc, games._id "
    		+ "FROM tournament_games "
    		+ "INNER JOIN games ON (tg_gm_id = games._id) "
    		+ "WHERE gm_delete = 1 AND tg_tn_id=? ORDER BY DATE(gm_date) DESC";
    private final String TEAM_GAMES_QUERY = "SELECT gm_ab, gm_1b, gm_2b, gm_3b, gm_hr,  "
    		+ "gm_r, gm_rbi, gm_bb, gm_so, gm_sac, gm_roe, gm_fc, games._id "
    		+ "FROM games "
    		+ "INNER JOIN league_games ON (lgg_gm_id = games._id) " 
			+ "INNER JOIN leagues ON (lgg_lg_id = leagues._id) "
    		+ "WHERE (gm_delete IS NULL OR gm_delete = 0) AND lg_tm_id=? "
			+ "UNION "
			+ "SELECT gm_ab, gm_1b, gm_2b, gm_3b, gm_hr, " 
    		+ "gm_r, gm_rbi, gm_bb, gm_so, gm_sac, gm_roe, gm_fc, games._id " 
    		+ "FROM games "
    		+ "INNER JOIN tournament_games ON (tg_gm_id = games._id) " 
			+ "INNER JOIN tournaments ON (tg_tn_id = tournaments._id) "
    		+ "WHERE (gm_delete IS NULL OR gm_delete = 0) AND tn_tm_id=?";
    private final String TEAM_GAMES_DELETE_QUERY = "SELECT gm_ab, gm_1b, gm_2b, gm_3b, gm_hr,  "
    		+ "gm_r, gm_rbi, gm_bb, gm_so, gm_sac, gm_roe, gm_fc, games._id "
    		+ "FROM games "
    		+ "INNER JOIN league_games ON (lgg_gm_id = games._id) " 
			+ "INNER JOIN leagues ON (lgg_lg_id = leagues._id) "
    		+ "WHERE gm_delete = 1 AND lg_tm_id=? "
			+ "UNION "
			+ "SELECT gm_ab, gm_1b, gm_2b, gm_3b, gm_hr, " 
    		+ "gm_r, gm_rbi, gm_bb, gm_so, gm_sac, gm_roe, gm_fc, games._id " 
    		+ "FROM games "
    		+ "INNER JOIN tournament_games ON (tg_gm_id = games._id) " 
			+ "INNER JOIN tournaments ON (tg_tn_id = tournaments._id) "
    		+ "WHERE gm_delete = 1 AND tn_tm_id=?";
    private final String LEAGUE_GAMES_COUNT = "SELECT COUNT(*) FROM league_games INNER JOIN games ON (lgg_gm_id = games._id) "
    		+ "WHERE (gm_delete IS NULL OR gm_delete = 0) AND lgg_lg_id=?"; 
    private final String LEAGUES_QUERY = "SELECT _id, lg_name || ' (' || (SELECT COUNT(lgg_lg_id) FROM league_games "
    		+ "WHERE lgg_lg_id = leagues._id) || ')' as lg_name FROM leagues WHERE (lg_delete IS NULL OR lg_delete = 0) AND lg_tm_id=?";
    private final String TOURNAMENTS_QUERY = "SELECT _id, tn_name || ' (' || (SELECT COUNT(tg_tn_id) FROM tournament_games "
    		+ "WHERE tg_tn_id = tournaments._id) || ')' as tn_name FROM tournaments WHERE (tn_delete IS NULL OR tn_delete = 0) AND tn_tm_id=?";
    private final String TOURNAMENTS_DELETE_QUERY = "SELECT _id, tn_name || ' (' || (SELECT COUNT(tg_tn_id) FROM tournament_games "
    		+ "WHERE tg_tn_id = tournaments._id) || ')' as tn_name FROM tournaments WHERE tn_delete = 1 AND tn_tm_id=?";
    /***********************************************************************************************/
    
    private static final String DATABASE_NAME = "SoftballStats";
    private static final String DATABASE_TEAMS_TABLE = "team";
    private static final String DATABASE_TOURNAMENTS_TABLE = "tournaments";
    private static final String DATABASE_LEAGUES_TABLE = "leagues";
    private static final String DATABASE_TOURNAMENT_GAMES_TABLE = "tournament_games";
    private static final String DATABASE_LEAGUE_GAMES_TABLE = "league_games";
    private static final String DATABASE_GAMES_TABLE = "games";
    private static final String DATABASE_LOGIN_TABLE = "login";
    private static final int DATABASE_VERSION = 4; //updated 8/25/13 for new version of app

    private final Context mCtx;
	
    private static class DatabaseHelper extends SQLiteOpenHelper {
    	
		DatabaseHelper(Context context) {
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    }
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			createTables(db);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			switch(oldVersion) {
			case 1:
				onCreate(db);
				// we want both updates, so no break statement here...
			case 2:
				db.execSQL(DATABASE_LOGIN_CREATE);
			case 3: //upgrade tables to include new timestamp and delete columns
				String update = "ALTER TABLE " + DATABASE_TEAMS_TABLE + " ADD COLUMN tm_modified_dt real;";
				db.execSQL(update);
				update = "ALTER TABLE " + DATABASE_TEAMS_TABLE + " ADD COLUMN tm_delete int;";
				db.execSQL(update);
				update = "ALTER TABLE " + DATABASE_TEAMS_TABLE + " ADD COLUMN tm_server_id int;";
				db.execSQL(update);
				update = "ALTER TABLE " + DATABASE_TEAMS_TABLE + " ADD COLUMN tm_app_id text;";
				db.execSQL(update);
				
				update = "ALTER TABLE " + DATABASE_TOURNAMENTS_TABLE + " ADD COLUMN tn_modified_dt real;";
				db.execSQL(update);
				update = "ALTER TABLE " + DATABASE_TOURNAMENTS_TABLE + " ADD COLUMN tn_delete int;";
				db.execSQL(update);
				update = "ALTER TABLE " + DATABASE_TOURNAMENTS_TABLE + " ADD COLUMN tn_server_id int;";
				db.execSQL(update);
				update = "ALTER TABLE " + DATABASE_TOURNAMENTS_TABLE + " ADD COLUMN tn_app_id text;";
				db.execSQL(update);
				
				update = "ALTER TABLE " + DATABASE_LEAGUES_TABLE + " ADD COLUMN lg_modified_dt real;";
				db.execSQL(update);
				update = "ALTER TABLE " + DATABASE_LEAGUES_TABLE + " ADD COLUMN lg_delete int;";
				db.execSQL(update);
				update = "ALTER TABLE " + DATABASE_LEAGUES_TABLE + " ADD COLUMN lg_server_id int;";
				db.execSQL(update);
				update = "ALTER TABLE " + DATABASE_LEAGUES_TABLE + " ADD COLUMN lg_app_id text;";
				db.execSQL(update);
				
				update = "ALTER TABLE " + DATABASE_TOURNAMENT_GAMES_TABLE + " ADD COLUMN tg_modified_dt real;";
				db.execSQL(update);
				update = "ALTER TABLE " + DATABASE_TOURNAMENT_GAMES_TABLE + " ADD COLUMN tg_server_id int;";
				db.execSQL(update);
				update = "ALTER TABLE " + DATABASE_TOURNAMENT_GAMES_TABLE + " ADD COLUMN tg_app_id text;";
				db.execSQL(update);
				
				update = "ALTER TABLE " + DATABASE_LEAGUE_GAMES_TABLE + " ADD COLUMN lgg_modified_dt real;";
				db.execSQL(update);
				update = "ALTER TABLE " + DATABASE_LEAGUE_GAMES_TABLE + " ADD COLUMN lgg_server_id int;";
				db.execSQL(update);
				update = "ALTER TABLE " + DATABASE_LEAGUE_GAMES_TABLE + " ADD COLUMN lgg_app_id text;";
				db.execSQL(update);
				
				update = "ALTER TABLE " + DATABASE_GAMES_TABLE + " ADD COLUMN gm_modified_dt real;";
				db.execSQL(update);
				update = "ALTER TABLE " + DATABASE_GAMES_TABLE + " ADD COLUMN gm_delete int;";
				db.execSQL(update);
				update = "ALTER TABLE " + DATABASE_GAMES_TABLE + " ADD COLUMN gm_server_id int;";
				db.execSQL(update);
				update = "ALTER TABLE " + DATABASE_GAMES_TABLE + " ADD COLUMN gm_app_id text;";
				db.execSQL(update);
				
			}
		}
    }
	
	/**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public StatsDbHelper(Context ctx) {
        this.mCtx = ctx;
    }

    public StatsDbHelper open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }
    
    public void addLeague(String asLeagueName, String asLeagueDesc, Long alTeamId) {
    	CURRENT_UTC = System.currentTimeMillis();
    	String lsAppId = UUID.randomUUID().toString();
    	ContentValues initialValues = new ContentValues();
    	
        initialValues.put(KEY_LG_NAME, asLeagueName);
        initialValues.put(KEY_LG_DESCRIPTION, asLeagueDesc);
        initialValues.put(KEY_LG_TM_ID, alTeamId);
        initialValues.put(KEY_LG_MOD_DT, CURRENT_UTC);
        initialValues.put(KEY_LG_DELETE, 0);
        initialValues.put(KEY_LG_APP_ID, lsAppId);
        
    	mDb.insert(DATABASE_LEAGUES_TABLE, null, initialValues);
    }
    
    //add a new team into a database
    public void addTeam(String asTeamName) {
    	Calendar curDate = Calendar.getInstance();
    	Integer liYear = curDate.get(Calendar.YEAR); // - 1900;
    	CURRENT_UTC = System.currentTimeMillis();
    	String lsAppId = UUID.randomUUID().toString();
    	
    	ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TM_NAME, asTeamName);
        initialValues.put(KEY_TM_YEAR, liYear);
        initialValues.put(KEY_TM_MOD_DT, CURRENT_UTC);
        initialValues.put(KEY_TM_DELETE, 0);
        initialValues.put(KEY_TM_APP_ID, lsAppId);
        
    	mDb.insert(DATABASE_TEAMS_TABLE, null, initialValues);
    }
    
    //add a new tournament into a database
    public void addTournament(String asTeamName, String asDate, Long alTeamId) {
    	CURRENT_UTC = System.currentTimeMillis();
    	String lsAppId = UUID.randomUUID().toString();
    	
    	ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TN_NAME, asTeamName);
        initialValues.put(KEY_TN_TM_ID, alTeamId);
        initialValues.put(KEY_TN_DATE, asDate);
        initialValues.put(KEY_TN_MOD_DT, CURRENT_UTC);
        initialValues.put(KEY_TN_DELETE, 0);
        initialValues.put(KEY_TN_APP_ID, lsAppId);
        
    	mDb.insert(DATABASE_TOURNAMENTS_TABLE, null, initialValues);
    }
    
    public static void createTables(SQLiteDatabase db) {
    	//SQLiteDatabase db = mDbHelper.getWritableDatabase();
    	
    	//drop tables if they exist
    	db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TEAMS_TABLE + ";");
    	db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TOURNAMENTS_TABLE + ";");
    	db.execSQL("DROP TABLE IF EXISTS " + DATABASE_LEAGUES_TABLE + ";");
    	db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TOURNAMENT_GAMES_TABLE + ";");
    	db.execSQL("DROP TABLE IF EXISTS " + DATABASE_LEAGUE_GAMES_TABLE + ";");
    	db.execSQL("DROP TABLE IF EXISTS " + DATABASE_GAMES_TABLE + ";");
    	db.execSQL("DROP TABLE IF EXISTS " + DATABASE_LOGIN_TABLE + ";");
    	
    	db.execSQL(DATABASE_TEAM_CREATE);
		db.execSQL(DATABASE_TOURNAMENTS_CREATE);
		db.execSQL(DATABASE_LEAGUES_CREATE);
		db.execSQL(DATABASE_TOURNAMENT_GAMES_CREATE);
		db.execSQL(DATABASE_LEAGUE_GAMES_CREATE);
		db.execSQL(DATABASE_GAMES_CREATE);
		db.execSQL(DATABASE_LOGIN_CREATE);
    }
    
    //remove game from games table and league/tournament table
    public void deleteGame(Long alId, String asType) {
    	Long llId;
    	if(asType.equals("DELETE_LEAGUE")) {
    		//delete all games for league in games table then delete records in league_games table
    		Cursor cur = fetchLeagueGames(alId);
    		if (cur != null) { 
    		    if (cur.moveToFirst()) { 
    		        do {
    		        	llId = cur.getLong(cur.getColumnIndex("games._id"));
    		            deleteGame(llId, "");                  
    		        } while (cur.moveToNext()); 
    		    } 
    		}
    		//mDb.delete(DATABASE_LEAGUE_GAMES_TABLE, KEY_LGG_LG_ID + " = " + alId, null);
    	} else if(asType.equals("DELETE_TOURNAMENT")) {
    		//delete all games for tournament then delete tournament
    		Cursor cur = fetchTournamentGames(alId);
    		if (cur != null) { 
    		    if (cur.moveToFirst()) { 
    		        do {
    		        	llId = cur.getLong(cur.getColumnIndex("games._id"));
    		            deleteGame(llId, "");                  
    		        } while (cur.moveToNext()); 
    		    } 
    		}
    		//mDb.delete(DATABASE_TOURNAMENT_GAMES_TABLE, KEY_TG_TN_ID + " = " + alId, null);
    	} else {
    		/*if(asType.equals("LEAGUE")) {
    			mDb.delete(DATABASE_LEAGUE_GAMES_TABLE, KEY_LGG_GM_ID + " = " + alId, null);
    		} else if(asType.equals("TOURNAMENT")) {
    			mDb.delete(DATABASE_TOURNAMENT_GAMES_TABLE, KEY_TG_GM_ID + " = " + alId, null);
    		}*/
    		//mDb.delete(DATABASE_GAMES_TABLE, KEY_ID + " = " + alId, null);
    		//mark game as to be deleted during synchronization
    		ContentValues gameTable = new ContentValues();
    		CURRENT_UTC = System.currentTimeMillis();
    		
            gameTable.put(KEY_GM_MOD_DT, CURRENT_UTC);
            gameTable.put(KEY_GM_DELETE, 1);
            
            mDb.update(DATABASE_GAMES_TABLE, gameTable, KEY_ID + " = " + alId, null);
    	}
    }
    
    //remove league from leagues table
    public void deleteLeague(Long alId) {
		//mDb.delete(DATABASE_LEAGUES_TABLE, KEY_ID + " = " + alId, null);
    	//mark league as to be deleted during synchronization
		ContentValues leagueTable = new ContentValues();
		CURRENT_UTC = System.currentTimeMillis();
		
		leagueTable.put(KEY_LG_MOD_DT, CURRENT_UTC);
		leagueTable.put(KEY_LG_DELETE, 1);
        
        mDb.update(DATABASE_LEAGUES_TABLE, leagueTable, KEY_ID + " = " + alId, null);
    }
    
    //remove tournament from the application
    public void deleteTeam(Long alId) {
    	Long llId;
    	Cursor cur = fetchLeagues(alId);
		if (cur != null) { 
		    if (cur.moveToFirst()) { 
		        do {
		        	llId = cur.getLong(cur.getColumnIndex("_id"));
		        	deleteGame(llId, "DELETE_LEAGUE");
		        	deleteLeague(llId);                
		        } while (cur.moveToNext()); 
		    } 
		}
		cur = fetchTournaments(alId);
		if (cur != null) { 
		    if (cur.moveToFirst()) { 
		        do {
		        	llId = cur.getLong(cur.getColumnIndex("_id"));
		        	deleteGame(llId, "DELETE_TOURNAMENT");  
		        	deleteTournament(llId);               
		        } while (cur.moveToNext()); 
		    } 
		}
    	deleteTournament(alId);
    	//mDb.delete(DATABASE_TEAMS_TABLE, KEY_ID + " = " + alId, null);
    	//mark tournament as to be deleted during synchronization
		ContentValues teamTable = new ContentValues();
		CURRENT_UTC = System.currentTimeMillis();
    	
		teamTable.put(KEY_TN_MOD_DT, CURRENT_UTC);
		teamTable.put(KEY_TN_DELETE, 1);
        
        mDb.update(DATABASE_GAMES_TABLE, teamTable, KEY_ID + " = " + alId, null);
    }
    
    //remove tournament from tournaments table
    public void deleteTournament(Long alId) {
		//mDb.delete(DATABASE_TOURNAMENTS_TABLE, KEY_ID + " = " + alId, null);
    	//mark tournament as to be deleted during synchronization
		ContentValues tournamentTable = new ContentValues();
		CURRENT_UTC = System.currentTimeMillis();
    	
		tournamentTable.put(KEY_TN_MOD_DT, CURRENT_UTC);
		tournamentTable.put(KEY_TN_DELETE, 1);
        
        mDb.update(DATABASE_TOURNAMENTS_TABLE, tournamentTable, KEY_ID + " = " + alId, null);
    }
    
    public Cursor fetchGame(Long alRowId) {
    	if (alRowId > 0) {
    		return mDb.query(DATABASE_GAMES_TABLE, new String[] {KEY_GM_DATE, KEY_GM_AB, KEY_GM_1B, KEY_GM_2B,
    			KEY_GM_3B, KEY_GM_HR, KEY_GM_R, KEY_GM_RBI, KEY_GM_BB, KEY_GM_SO, KEY_GM_SAC,
    			KEY_GM_ROE, KEY_GM_FC}, KEY_ID + "=" + alRowId, null, null, null, null);
    	} else {
    		return mDb.query(DATABASE_GAMES_TABLE, new String[] {KEY_GM_APP_ID, KEY_GM_DATE, KEY_GM_AB, KEY_GM_1B, KEY_GM_2B,
        			KEY_GM_3B, KEY_GM_HR, KEY_GM_R, KEY_GM_RBI, KEY_GM_BB, KEY_GM_SO, KEY_GM_SAC,
        			KEY_GM_ROE, KEY_GM_FC, KEY_GM_MOD_DT, KEY_GM_DELETE, KEY_GM_SERVER_ID}, "", null, null, null, null);
    	}
    }
    
    //fetch games for given league
    public Cursor fetchLeagueGames(Long alLeagueId) {
    	return mDb.rawQuery(LEAGUE_GAMES_QUERY, new String[]{String.valueOf(alLeagueId)});
    	//return mDb.query(DATABASE_LEAGUE_GAMES_TABLE + " INNER JOIN + " + DATABASE_GAMES_TABLE
    	//		+ " gm ON (lgg_gm_id = gm._id)" , new String[] {KEY_GM_AB, KEY_GM_1B, KEY_GM_2B,
    	//		KEY_GM_3B, KEY_GM_HR, KEY_GM_R, KEY_GM_RBI, KEY_GM_BB, KEY_GM_SO, KEY_GM_SAC,
    	//		KEY_GM_ROE, KEY_GM_FC}, KEY_ID + "=" + alLeagueId, null, null, null, null);
    }
    
    //return a list of leagues for a selected team
    public Cursor fetchLeagues(Long alLeagueId) {
    	if (alLeagueId > 0) {
    		return mDb.rawQuery(LEAGUES_QUERY, new String[]{String.valueOf(alLeagueId)});
    	} else {
    		return mDb.query(true, DATABASE_LEAGUES_TABLE, new String[] {KEY_ID, KEY_LG_APP_ID, KEY_LG_NAME, KEY_LG_DESCRIPTION, KEY_LG_TM_ID, KEY_LG_MOD_DT, KEY_LG_DELETE, KEY_LG_SERVER_ID}, "", null, null, null, KEY_LG_NAME, null);
    	}
    }
    
    //return all stats for a given team
    public Cursor fetchTeamStats(Long alTeamId) {
    	return mDb.rawQuery(TEAM_GAMES_QUERY, new String[]{String.valueOf(alTeamId), String.valueOf(alTeamId)});
    }
    
    //fetch games for given tournament
    public Cursor fetchTournamentGames(Long alTournamentId) {
    	return mDb.rawQuery(TOURNAMENT_GAMES_QUERY, new String[]{String.valueOf(alTournamentId)}); 
    }
    
    //return a list of tournaments for a selected team
    public Cursor fetchTournaments(Long alTeamId) {
    	if (alTeamId > 0) {
    		return mDb.rawQuery(TOURNAMENTS_QUERY, new String[]{String.valueOf(alTeamId)});
    	} else {
    		return mDb.query(true, DATABASE_TOURNAMENTS_TABLE, new String[] {KEY_ID, KEY_TN_APP_ID, KEY_TN_NAME, KEY_TN_TM_ID, KEY_TN_DATE, KEY_TN_MOD_DT, KEY_TN_DELETE, KEY_TN_SERVER_ID}, "", null, null, null, KEY_TN_NAME, null);
    	}
    }
    
    //Return a Cursor over the list of all years in the database
    public Cursor fetchTeams(Integer aiYear) {
    	if (aiYear > 0) {
	    	//get distinct teams for a given year from database
	        return mDb.query(true, DATABASE_TEAMS_TABLE, new String[] {KEY_ID, KEY_TM_NAME}, "tm_year = " + aiYear, null, null, null, KEY_TM_NAME, null);
    	} else {
    		return mDb.query(true, DATABASE_TEAMS_TABLE, new String[] {KEY_ID, KEY_TM_APP_ID, KEY_TM_NAME, KEY_TM_NAME, KEY_TM_YEAR, KEY_TM_MOD_DT, KEY_TM_DELETE, KEY_TM_SERVER_ID}, "", null, null, null, KEY_TM_NAME, null);
    	}
    }
    
    public Cursor fetchUser() {
    	return mDb.query(true, DATABASE_LOGIN_TABLE, new String[] {KEY_ID, KEY_LOGIN_UID}, null, null, null, null, null, null);
    }
    
    //Return a Cursor over the list of all years in the database
    public Cursor fetchYears() {
    	//get distinct years from database
        return mDb.query(true, DATABASE_TEAMS_TABLE, new String[] {KEY_ID, KEY_TM_YEAR}, null, null, null, null, KEY_TM_YEAR, null);
    }
    
    //get number of games for the id and type (league, tournament, all leagues, all tournaments, etc)
    public int getGameCount(Long alId, Integer aiType) {
    	int liGames = 0;
    	switch (aiType) {
	    	case GAMES_COUNT_LEAGUE:
	    		Cursor c = mDb.rawQuery(LEAGUE_GAMES_COUNT, new String[] { aiType.toString() });
	    		c.moveToFirst();
	    		liGames = c.getInt(0);
	    		c.close();
	    		break;
    	}
    	
    	return liGames;
    }
    
    //insert new game into the database
    public void saveNewGame(String asType, Long aId, String asDate, int aiAb, int ai1b, int ai2b, int ai3b, int aiHr, 
    		int aiR, int aiRbi, int aiBb, int aiSo, int aiSac, int aiRoe, int aiFc) {
    	Long llRowId;
    	ContentValues gameTable = new ContentValues();
    	ContentValues leagueGameTable = new ContentValues();
    	ContentValues TournamentGameTable = new ContentValues();
    	String lsAppId = UUID.randomUUID().toString();
    	CURRENT_UTC = System.currentTimeMillis();
    	
    	gameTable.put(KEY_GM_DATE, asDate);
    	gameTable.put(KEY_GM_AB, aiAb);
    	gameTable.put(KEY_GM_1B, ai1b);
        gameTable.put(KEY_GM_2B, ai2b);
        gameTable.put(KEY_GM_3B, ai3b);
        gameTable.put(KEY_GM_HR, aiHr);
        gameTable.put(KEY_GM_R, aiR);
        gameTable.put(KEY_GM_RBI, aiRbi);
        gameTable.put(KEY_GM_BB, aiBb);
        gameTable.put(KEY_GM_SO, aiSo);
        gameTable.put(KEY_GM_SAC, aiSac);
        gameTable.put(KEY_GM_ROE, aiRoe);
        gameTable.put(KEY_GM_FC, aiFc);
        gameTable.put(KEY_GM_MOD_DT, CURRENT_UTC);
        gameTable.put(KEY_GM_DELETE, 0);
        gameTable.put(KEY_GM_APP_ID, lsAppId);
        
        
        //if all transaction are not successful, roll back so that there are not any orphaned rows in database
        mDb.beginTransaction();
        try {
	    	//insert into game table and get game id
	    	llRowId = mDb.insert(DATABASE_GAMES_TABLE, null, gameTable);
	    	CURRENT_UTC = System.currentTimeMillis();
	    	String lsAppIdGm = UUID.randomUUID().toString();
	    	
	    	if (llRowId > -1) {
	    		
	    		if (asType.equals("LEAGUE")) { //insert into league-game link table
					leagueGameTable.put(KEY_LGG_GM_ID, llRowId);
		        	leagueGameTable.put(KEY_LGG_LG_ID, aId);
		        	leagueGameTable.put(KEY_LGG_MOD_DT, CURRENT_UTC);
		        	leagueGameTable.put(KEY_LGG_APP_ID, lsAppIdGm);
		    		if (mDb.insert(DATABASE_LEAGUE_GAMES_TABLE, null, leagueGameTable) >= 0) {
		    			mDb.setTransactionSuccessful();
		    		}
	    		} else if (asType.equals("TOURNAMENT")) { //insert into tournament-game link table
	    			TournamentGameTable.put(KEY_TG_GM_ID, llRowId);
	    			TournamentGameTable.put(KEY_TG_TN_ID, aId);
	    			TournamentGameTable.put(KEY_TG_MOD_DT, CURRENT_UTC);
	    			TournamentGameTable.put(KEY_TG_APP_ID, lsAppIdGm);
	        		if (mDb.insert(DATABASE_TOURNAMENT_GAMES_TABLE, null, TournamentGameTable) >= 0) {
	        			mDb.setTransactionSuccessful();
	        		}
	    		}
	    	}
        } finally {
        	mDb.endTransaction();
        }
    }
    
    public String syncGame(String gmDt, String gmAb, String gm1b, String gm2b, String gm3b, String gmHr, String gmR, String gmRbi, String gmBb, String gmSo, String gmSac, String gmRoe, String gmFc, String gmSyncDt, String gmDel, String gmServerId, String gmAppId) throws java.text.ParseException {
    	ContentValues gameTable = new ContentValues();
    	long timeInMilliseconds = 0;
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	try {
	    	Date mDate = sdf.parse(gmSyncDt);
	        timeInMilliseconds = mDate.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	
    	gameTable.put(KEY_GM_DATE, gmDt);
    	gameTable.put(KEY_GM_AB, gmAb);
    	gameTable.put(KEY_GM_1B, gm1b);
    	gameTable.put(KEY_GM_2B, gm2b);
    	gameTable.put(KEY_GM_3B, gm3b);
    	gameTable.put(KEY_GM_HR, gmHr);
    	gameTable.put(KEY_GM_R, gmR);
    	gameTable.put(KEY_GM_RBI, gmRbi);
    	gameTable.put(KEY_GM_BB, gmBb);
    	gameTable.put(KEY_GM_SO, gmSo);
    	gameTable.put(KEY_GM_SAC, gmSac);
    	gameTable.put(KEY_GM_ROE, gmRoe);
    	gameTable.put(KEY_GM_FC, gmFc);
    	gameTable.put(KEY_GM_MOD_DT, timeInMilliseconds);
    	gameTable.put(KEY_GM_DELETE, gmDel);
    	gameTable.put(KEY_GM_SERVER_ID, gmServerId);
    	
    	if (gmAppId.length() > 0) { 
    		//check if APP_ID exists. If it does, UPDATE otherwise INSERT
    		Cursor mCount= mDb.rawQuery("SELECT COUNT(*) FROM games WHERE gm_app_id ='" + gmAppId +"'", null);
    		mCount.moveToFirst();
    		int count= mCount.getInt(0);
    		mCount.close();
    		if (count > 0) {
    			mDb.update(DATABASE_GAMES_TABLE, gameTable, KEY_GM_APP_ID + " = " + gmAppId, null);
    		} else {
    			gameTable.put(KEY_GM_APP_ID, gmAppId);
    	    	mDb.insert(DATABASE_GAMES_TABLE, null, gameTable);
    		}
	        gmAppId = ""; //set to empty string to indicate this does not need to be updated back to server
    	} else { //game was created on the server, so add it to app
    		gmAppId = UUID.randomUUID().toString();
    		gameTable.put(KEY_GM_APP_ID, gmAppId);
	    	mDb.insert(DATABASE_GAMES_TABLE, null, gameTable);
    	}
    	
    	return gmAppId;
    }
    
    public String syncLeague(String lgTmId, String lgName, String lgDesc, String lgSyncDt, String lgDel, String lgServerId, String lgAppId) throws java.text.ParseException {
    	ContentValues leagueTable = new ContentValues();
    	long timeInMilliseconds = 0;
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	try {
	    	Date mDate = sdf.parse(lgSyncDt);
	        timeInMilliseconds = mDate.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	
    	leagueTable.put(KEY_LG_TM_ID, lgTmId);
    	leagueTable.put(KEY_LG_NAME, lgName);
    	leagueTable.put(KEY_LG_DESCRIPTION, lgDesc);
    	leagueTable.put(KEY_LG_MOD_DT, timeInMilliseconds);
    	leagueTable.put(KEY_LG_DELETE, lgDel);
    	leagueTable.put(KEY_LG_SERVER_ID, lgServerId);
    	
    	if (lgAppId.length() > 0) { 
    		//check if APP_ID exists. If it does, UPDATE otherwise INSERT
    		Cursor mCount= mDb.rawQuery("SELECT COUNT(*) FROM leagues WHERE lg_app_id ='" + lgAppId +"'", null);
    		mCount.moveToFirst();
    		int count= mCount.getInt(0);
    		mCount.close();
    		if (count > 0) {
    			mDb.update(DATABASE_LEAGUES_TABLE, leagueTable, KEY_LG_APP_ID + " = " + lgAppId, null);
    		} else {
    			leagueTable.put(KEY_LG_APP_ID, lgAppId);
    	    	mDb.insert(DATABASE_LEAGUES_TABLE, null, leagueTable); 
    		}
	        lgAppId = ""; //set to 0 to indicate this does not need to be updated back to server
    	} else { //league was created on the server, so add it to app
    		lgAppId = UUID.randomUUID().toString();
    		leagueTable.put(KEY_LG_APP_ID, lgAppId);
	    	mDb.insert(DATABASE_LEAGUES_TABLE, null, leagueTable); 
    	}
    	
    	return lgAppId;
    }
    
    public String syncTeam(String tmServerId, String tmName, String tmYear, String tmSyncDate, String tmDel, String tmAppId) throws java.text.ParseException {
    	ContentValues teamTable = new ContentValues();
    	long timeInMilliseconds = 0;
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	try {
	    	Date mDate = sdf.parse(tmSyncDate);
	        timeInMilliseconds = mDate.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	
    	teamTable.put(KEY_TM_NAME, tmName);
    	teamTable.put(KEY_TM_YEAR, tmYear);
    	teamTable.put(KEY_TM_MOD_DT, timeInMilliseconds);
    	teamTable.put(KEY_TM_SERVER_ID, tmServerId);
    	teamTable.put(KEY_TM_DELETE, 0);
		
		if (tmAppId.length() > 0) {
    		//check if APP_ID exists. If it does, UPDATE otherwise INSERT
    		Cursor mCount= mDb.rawQuery("SELECT COUNT(*) FROM teams WHERE tm_app_id ='" + tmAppId +"'", null);
    		mCount.moveToFirst();
    		int count= mCount.getInt(0);
    		mCount.close();
    		if (count > 0) {
    			mDb.update(DATABASE_TEAMS_TABLE, teamTable, KEY_ID + " = " + tmAppId, null);
    		} else {
    			teamTable.put(KEY_TM_APP_ID, tmAppId);
    	    	mDb.insert(DATABASE_TEAMS_TABLE, null, teamTable);
    		}
	        tmAppId = "";
		} else { //team was created on the server, so add it to app
			tmAppId = UUID.randomUUID().toString();
			teamTable.put(KEY_TM_APP_ID, tmAppId);
	    	mDb.insert(DATABASE_TEAMS_TABLE, null, teamTable);
		}
		
		return tmAppId;
    }
    
    public String syncTournament(String tnTmId, String tnName, String tnDate, String tnSyncDt, String tnDel, String tnServerId, String tnAppId) throws java.text.ParseException {
    	ContentValues tournamentTable = new ContentValues();
    	long syncMilliseconds = 0;
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
    	
    	/**** logging ****/
    	Log.w("DEBUG", "tnAppId=" + tnAppId + ";");
    	Log.w("DEBUG", "tnTmId=" + tnTmId + ";");
    	Log.w("DEBUG", "tnDate=" + tnDate + ";");
    	Log.w("DEBUG", "tnServerId=" + tnServerId + ";");
    	/**** end log ***/
    	
    	try {
	    	Date mSyncDate = sdf.parse(tnSyncDt);
	    	syncMilliseconds = mSyncDate.getTime();
	    	Date mTnDate = sdf.parse(tnDate);
	    	tnDate = dateFormat.format(mTnDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

    	//Log.w("DEBUG", "tnDate(formatted)=" + tnDate + ";");
    	
    	tournamentTable.put(KEY_TN_TM_ID, tnTmId);
		tournamentTable.put(KEY_TN_NAME, tnName);
		tournamentTable.put(KEY_TN_DATE, tnDate);
		tournamentTable.put(KEY_TN_MOD_DT, syncMilliseconds);
		tournamentTable.put(KEY_TN_DELETE, tnDel);
		tournamentTable.put(KEY_TN_SERVER_ID, tnServerId);
		
    	if (tnAppId.length() > 0) {
    		//check if APP_ID exists. If it does, UPDATE otherwise INSERT
    		Cursor mCount= mDb.rawQuery("SELECT COUNT(*) FROM tournaments WHERE tn_app_id ='" + tnAppId +"'", null);
    		mCount.moveToFirst();
    		int count= mCount.getInt(0);
    		mCount.close();
    		if (count > 0) {
    			mDb.update(DATABASE_TOURNAMENTS_TABLE, tournamentTable, KEY_TN_APP_ID + " = " + tnAppId, null);
    		} else {
    			tournamentTable.put(KEY_TN_APP_ID, tnAppId);
        		mDb.insert(DATABASE_TOURNAMENTS_TABLE, null, tournamentTable);
    		}
	        tnAppId = ""; //set to empty string to indicate this does not need to be updated back to server
    	} else { //league was created on the server, so add it to app
    		tnAppId = UUID.randomUUID().toString();
    		tournamentTable.put(KEY_TN_APP_ID, tnAppId);
    		mDb.insert(DATABASE_TOURNAMENTS_TABLE, null, tournamentTable);
    	}
    	
    	Log.w("DEBUG", "tnAppId=" + tnAppId + ";");
    	
        // check for league update response
    	return tnAppId;
    }
    
    public void updateGame(Long aId, String asDate, int aiAb, int ai1b, int ai2b, int ai3b, int aiHr, 
    		int aiR, int aiRbi, int aiBb, int aiSo, int aiSac, int aiRoe, int aiFc) {
    	ContentValues gameTable = new ContentValues();
    	CURRENT_UTC = System.currentTimeMillis();
    	
    	gameTable.put(KEY_GM_DATE, asDate);
    	gameTable.put(KEY_GM_AB, aiAb);
    	gameTable.put(KEY_GM_1B, ai1b);
        gameTable.put(KEY_GM_2B, ai2b);
        gameTable.put(KEY_GM_3B, ai3b);
        gameTable.put(KEY_GM_HR, aiHr);
        gameTable.put(KEY_GM_R, aiR);
        gameTable.put(KEY_GM_RBI, aiRbi);
        gameTable.put(KEY_GM_BB, aiBb);
        gameTable.put(KEY_GM_SO, aiSo);
        gameTable.put(KEY_GM_SAC, aiSac);
        gameTable.put(KEY_GM_ROE, aiRoe);
        gameTable.put(KEY_GM_FC, aiFc);
        gameTable.put(KEY_GM_MOD_DT, CURRENT_UTC);
        
        mDb.update(DATABASE_GAMES_TABLE, gameTable, KEY_ID + " = " + aId, null);
    }
    
    /**
     * Storing user details in database
     * */
    public void addUser(String name, String email, String uid, String created_dt) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_LOGIN_NAME, name); // Name
        values.put(KEY_LOGIN_EMAIL, email); // Email
        values.put(KEY_LOGIN_UID, uid); // Email
        values.put(KEY_LOGIN_CREATED_DT, created_dt); // Created At
 
        // Inserting Row
        db.insert(DATABASE_LOGIN_TABLE, null, values);
        db.close(); // Closing database connection
    }
     
    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String,String> user = new HashMap<String,String>();
        String selectQuery = "SELECT  * FROM " + DATABASE_LOGIN_TABLE;
          
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("uid", cursor.getString(3));
            user.put("created_at", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        return user;
    }
 
    /**
     * Getting user login status
     * return true if rows are there in table
     * */
    public int getRowCount() {
        String countQuery = "select * from " + DATABASE_LOGIN_TABLE;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();
         
        // return row count
        return rowCount;
    }
     
    /**
     * Re create database
     * Delete all tables and create them again
     * */
    public void resetTables(SQLiteDatabase db){
        createTables(db);
    }
}
