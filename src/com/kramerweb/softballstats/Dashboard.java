package com.kramerweb.softballstats;

import java.text.DecimalFormat;
import java.util.Calendar;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.backup.BackupManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.ads.*;

import com.google.analytics.tracking.android.EasyTracker;

public class Dashboard extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	private AdView adView;
	private StatsDbHelper mDbHelper;
	
	private static final int ACTIVITY_ADD_TEAM = 0;
	private static final int ACTIVITY_ADD_LEAGUE = 1;
	private static final int ACTIVITY_ADD_TOURNAMENT = 2;
	private static final int ACTIVITY_VIEW_LEAGUE_GAMES = 3;
	private static final int ACTIVITY_VIEW_TOURNAMENT_GAMES = 4;
	private static final int ACTIVITY_DELETE_LEAGUE_GAMES = 5;
	private static final int ACTIVITY_DELETE_TOURNAMENT_GAMES = 6;

    private static final int ADD_TEAM = Menu.FIRST;
    private static final int ADD_LEAGUE = Menu.FIRST + 1;
    private static final int ADD_TOURNAMENT = Menu.FIRST + 2;
    private static final int VIEW_LEAGUE_GAMES = Menu.FIRST + 3;
    private static final int DELETE_LEAGUE_GAMES = Menu.FIRST + 4;
    private static final int VIEW_TOURNAMENT_GAMES = Menu.FIRST + 5;
    private static final int DELETE_TOURNAMENT_GAMES = Menu.FIRST + 6;
    private static final int DELETE_TEAM = Menu.FIRST + 7;
    
    private int iiYear;
    private long ilTeamId;
    private long ilListItemId;
    private String isListItemName;
    
    Calendar curDate = Calendar.getInstance();
	Integer iiCurrentYear = curDate.get(Calendar.YEAR); // - 1900; 
	
	private TextView mAb;
	private TextView m1b;
	private TextView m2b;
	private TextView m3b;
	private TextView mHr;
	private TextView mR;
	private TextView mRbi;
	private TextView mBb;
	private TextView mSo;
	private TextView mSac;
	private TextView mFc;
	private TextView mRoe;
	private TextView mAvg;
	private BackupManager mBackupManager;
//	private ListView lvLeagues;
	
	// The loader's unique id. Loader ids are specific to the Activity or
    // Fragment in which they reside.
    private static final int LOADER_ID = 1;
 
    // The callbacks through which we will interact with the LoaderManager.
    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
 
    // The adapter that binds our data to the ListView
    private SimpleCursorAdapter mTeamAdapter;
    private SimpleCursorAdapter mLeaguesAdapter;
	
    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "com.kramerweb.softballstats.provider";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "com.kramerweb.softballstats.datasync";
    // The account name
    public static final String ACCOUNT = "softballstats";
    // Instance fields
    Account mAccount;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mDbHelper = new StatsDbHelper(this);
        mDbHelper.open();
        
        ListView leagueList = (ListView)findViewById(R.id.lvLeagues);
        registerForContextMenu(leagueList);
        
        ListView tournamentList = (ListView)findViewById(R.id.lvTournaments);
        registerForContextMenu(tournamentList);
        
        mAb = (TextView)findViewById(R.id.tvAb);
    	m1b = (TextView)findViewById(R.id.tv1b);
    	m2b = (TextView)findViewById(R.id.tv2b);
    	m3b = (TextView)findViewById(R.id.tv3b);
    	mHr = (TextView)findViewById(R.id.tvHr);
    	mR = (TextView)findViewById(R.id.tvR);
    	mRbi = (TextView)findViewById(R.id.tvRbi);
    	mBb = (TextView)findViewById(R.id.tvBb);
    	mSo = (TextView)findViewById(R.id.tvSo);
    	mSac = (TextView)findViewById(R.id.tvSac);
    	mFc = (TextView)findViewById(R.id.tvFc);
    	mRoe = (TextView)findViewById(R.id.tvRoe);
    	mAvg = (TextView)findViewById(R.id.tvAvg);

        updateMainScreen();
        // Initiate a generic request to load it with an ad
        AdRequest adRequest = new AdRequest();
        
        //test mode on EMULATOR
        adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
        
        //test mode on DEVICE (this example code must be replaced with your device uniquq ID)
        adRequest.addTestDevice("9E30A665C78044AC3FF52292BE93E8D1");
 
        adView = (AdView)findViewById(R.id.adView);      
 
        // Initiate a request to load an ad in test mode.
        // You can keep this even when you release your app on the market, because
        // only emulators and your test device will get test ads. The user will receive real ads.
        adView.loadAd(adRequest);
    	
//        ActionBar actionBar = getActionBar();
//        actionBar.setSubtitle("mytest");
//        actionBar.setTitle("vogella.com");
        
//        mBackupManager = new BackupManager(this);
        
//        lvLeagues = (ListView) findViewById(R.id.lvLeagues);
//        lvLeagues.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listview_array));
//        lv.setTextFilterEnabled(true);
//        lv.setOnItemClickListener(new OnItemClickListener()
//        {
//        public void onItemClick(AdapterView<?> arg0, View v, int position, long id)
//        {
//        AlertDialog.Builder adb = new AlertDialog.Builder(
//        ListviewOnclickExample.this);
//        adb.setTitle("ListView OnClick");
//        adb.setMessage("Selected Item is = "
//        + lv.getItemAtPosition(position));
//        adb.setPositiveButton("Ok", null);
//        adb.show();                     
//                                                        }
//                                        });
        
//        mAccount = CreateSyncAccount(this);
        mAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
        ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, true);
        ContentResolver.setIsSyncable(mAccount, AUTHORITY, 1);
        
//        final Account account = new Account(mUsername, getString(R.string.ACCOUNT_TYPE));
//        ContentResolver.setSyncAutomatically(account, getString(R.string.CONTENT_AUTHORITY), true);
//        ContentResolver.setIsSyncable(account, getString(R.string.CONTENT_AUTHORITY), 1);
//        mAccountManager.addAccountExplicitly(account, mPassword, null);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        menu.add(0, ADD_TEAM, 0, R.string.menu_add_team);
//        menu.add(0, ADD_LEAGUE, 0, R.string.menu_add_league);
//        menu.add(0, ADD_TOURNAMENT, 0, R.string.menu_add_tournament);
//        menu.add(0, DELETE_TEAM, 0, R.string.menu_delete_team);
//        return true;
        
     // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
        
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_new_team:
                addTeam();
     		   	//Log.i("SoftballStats", "in onOptionsItemSelected()");
                return true;
            case R.id.action_new_league:
                addLeague();
                return true;
            case R.id.action_new_tourney:
                addTournament();
                return true;
            case R.id.action_sync_data:
            	manualSyncData();
            	return true;
            case DELETE_TEAM:
            	new AlertDialog.Builder(this) 
                .setTitle("Delete Team?") 
                .setMessage("Are you sure you want to delete currently selected team?") 
                .setNegativeButton(R.string.no, new OnClickListener() { 
                    public void onClick(DialogInterface arg0, int arg1) {
                    	return;
                    } 
                }) 
                .setPositiveButton(R.string.yes, new OnClickListener() { 
                    public void onClick(DialogInterface arg0, int arg1) {
                    	mDbHelper.deleteTeam(ilTeamId);
                    	updateMainScreen();
                    } 
                }).create().show();
            	return true;
        }
        return false;
    }

    //create context menu for Leagues and Tournaments
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
//    	// Get the info on which item was selected 
//        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo; 
//        ilListItemId = info.id;
//        isListItemName = ((TextView) info.targetView).getText().toString();
//
//    	switch(v.getId()) {
//	    	case R.id.lvLeagues:
//	    	    menu.setHeaderTitle(isListItemName);
//	    	    menu.add(1, VIEW_LEAGUE_GAMES, 1, R.string.view_games);
//	    	    menu.add(1, DELETE_LEAGUE_GAMES, 2, R.string.delete);
//	    	    break;
//	    	case R.id.lvTournaments:
//	    	    menu.setHeaderTitle(isListItemName);
//	    	    menu.add(2, VIEW_TOURNAMENT_GAMES, 1, R.string.view_games);
//	    	    menu.add(2, DELETE_TOURNAMENT_GAMES, 2, R.string.delete);
//	    	    break;
//    	}
    }
    
//    //process context menu selection on selected Tournament/League
//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        final Intent i = new Intent(this, ViewGames.class);
//        i.putExtra(StatsDbHelper.KEY_ID, ilListItemId);
//        i.putExtra("YEAR", iiYear);
//        i.putExtra("NAME", isListItemName);
//        switch(item.getItemId()) {
//            case VIEW_LEAGUE_GAMES:
//                i.putExtra("TYPE", "LEAGUE");
//                startActivityForResult(i, ACTIVITY_VIEW_LEAGUE_GAMES);
//                return true;
//            case VIEW_TOURNAMENT_GAMES:
//                i.putExtra("TYPE", "TOURNAMENT");
//                startActivityForResult(i, ACTIVITY_VIEW_TOURNAMENT_GAMES);
//                return true;
//            case DELETE_LEAGUE_GAMES:
//            	new AlertDialog.Builder(this) 
//                .setTitle("Delete League?") 
//                .setMessage("Do you wish to delete " + isListItemName + "?") 
//                .setNegativeButton(R.string.no, new OnClickListener() { 
//                    public void onClick(DialogInterface arg0, int arg1) {
//                    	return;
//                    } 
//                }) 
//                .setPositiveButton(R.string.yes, new OnClickListener() { 
//                    public void onClick(DialogInterface arg0, int arg1) {
//                    	i.putExtra("TYPE", "DELETE_LEAGUE");
//                        startActivityForResult(i, ACTIVITY_DELETE_LEAGUE_GAMES);
//                    } 
//                }).create().show();
//            	return true;
//            case DELETE_TOURNAMENT_GAMES:
//            	new AlertDialog.Builder(this) 
//                .setTitle("Delete Tournament?") 
//                .setMessage("Do you wish to delete " + isListItemName + "?") 
//                .setNegativeButton(R.string.no, new OnClickListener() { 
//                    public void onClick(DialogInterface arg0, int arg1) {
//                    	return;
//                    } 
//                }) 
//                .setPositiveButton(R.string.yes, new OnClickListener() { 
//                    public void onClick(DialogInterface arg0, int arg1) {
//                    	i.putExtra("TYPE", "DELETE_TOURNAMENT");
//                        startActivityForResult(i, ACTIVITY_DELETE_TOURNAMENT_GAMES);
//                    } 
//                }).create().show();
//            	return true;
//        }
//        return super.onContextItemSelected(item);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch(requestCode) {
	        case ACTIVITY_ADD_TEAM:
	        	iiYear = iiCurrentYear;
	            populateYearSpinner();
	            break;
	        case ACTIVITY_ADD_LEAGUE:
	        	iiYear = iiCurrentYear;
	        	populateLeagues();
    	        populateStats();
	        	break;
	        case ACTIVITY_ADD_TOURNAMENT:
	        	iiYear = iiCurrentYear;
	        	populateTournaments();
    	        populateStats();
	        	break;
	        case ACTIVITY_DELETE_LEAGUE_GAMES:
	        	populateLeagues();
    	        populateStats();
	        	break;
	        case ACTIVITY_DELETE_TOURNAMENT_GAMES:
	        	populateTournaments();
    	        populateStats();
	        	break;
        }
        
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	
    	EasyTracker.getInstance().activityStart(this);
    	
    	Spinner spnYear = (Spinner) findViewById(R.id.spYear);
    	Spinner spnTeam = (Spinner) findViewById(R.id.spTeam);
    	ListView lvLeagues = (ListView) findViewById(R.id.lvLeagues);
    	ListView lvTournaments = (ListView) findViewById(R.id.lvTournaments);
    	
    	final Intent i = new Intent(this, ViewGames.class);
    	
    	spnYear.setOnItemSelectedListener(new OnItemSelectedListener() {
    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
    	        iiYear = (Integer) parentView.getItemAtPosition(position);
    	        populateTeamSpinner();
    	    } 
    	    
    	    public void onNothingSelected(AdapterView<?> parentView) { 
    	        // your code here 
    	    } 
    	 
    	}); 
    	
    	spnTeam.setOnItemSelectedListener(new OnItemSelectedListener() {
    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
    	    	ilTeamId = id;
    	    	//isTeamName = (String) parentView.getItemAtPosition(position);
    	    	populateLeagues();
    	        populateTournaments();
    	        populateStats();
    	    } 
    	    
    	    public void onNothingSelected(AdapterView<?> parentView) { 
    	        // your code here 
    	    } 
    	 
    	});
    	
    	lvLeagues.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parentView, View selectedItemView, int position, long id) {
    			TextView c = (TextView) selectedItemView.findViewById(R.id.text1);
    		    String leagueName = c.getText().toString();
    			
    	        i.putExtra(StatsDbHelper.KEY_ID, id);
    	        i.putExtra("YEAR", iiYear);
    	        i.putExtra("NAME", leagueName);
    	        i.putExtra("TYPE", "LEAGUE");
                startActivityForResult(i, ACTIVITY_VIEW_LEAGUE_GAMES);
    		}
    	    
    	    public void onNothingSelected(AdapterView<?> parentView) { 
    	        // your code here 
    	    }
    	});
    	
    	lvTournaments.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parentView, View selectedItemView, int position, long id) {
    			TextView c = (TextView) selectedItemView.findViewById(R.id.text1);
    		    String tournamentName = c.getText().toString();
    		    
    	        i.putExtra(StatsDbHelper.KEY_ID, id);
    	        i.putExtra("YEAR", iiYear);
    	        i.putExtra("NAME", tournamentName);
    	        i.putExtra("TYPE", "TOURNAMENT");
                startActivityForResult(i, ACTIVITY_VIEW_LEAGUE_GAMES);
    		}
    	    
    	    public void onNothingSelected(AdapterView<?> parentView) { 
    	        // your code here 
    	    }
    	});
    }
    
    @Override
    public void onStop() {
      super.onStop();
      
      EasyTracker.getInstance().activityStop(this);  // Add this method.
    }
    
    public void addLeague() {
        Intent i = new Intent(this, AddLeague.class);
        i.putExtra(StatsDbHelper.KEY_ID, ilTeamId);
        startActivityForResult(i, ACTIVITY_ADD_LEAGUE);
    }
    
    private void addTeam() {
        Intent i = new Intent(this, AddTeam.class);
        startActivityForResult(i, ACTIVITY_ADD_TEAM);
    }
    
    public void addTournament() {
        Intent i = new Intent(this, AddTournament.class);
        i.putExtra(StatsDbHelper.KEY_ID, ilTeamId);
        startActivityForResult(i, ACTIVITY_ADD_TOURNAMENT);
    }
    
    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        return newAccount;
    }
    
    public void manualSyncData() {
        // Pass the settings flags by inserting them in a bundle
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
        ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);
    }
    
    private void populateLeagues() {
    	int liNumGames = 0;
    	Long llLeagueId;
    	ListView listLeagues = (ListView)findViewById(R.id.lvLeagues);
    	
    	Cursor leaguesCursor = mDbHelper.fetchLeagues(ilTeamId);
        //startManagingCursor(leaguesCursor);
        

        // Create an array to specify the fields we want to display in the list (only LEAGUE)
        String[] from = new String[]{StatsDbHelper.KEY_LG_NAME, StatsDbHelper.KEY_ID};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.text1};

        // Now create a simple cursor adapter and set it to display
        mLeaguesAdapter = new SimpleCursorAdapter(this, R.layout.leagues_row, leaguesCursor, from, to, 0);
        listLeagues.setAdapter(mLeaguesAdapter);
        
//        if(mLeaguesAdapter.moveToFirst()){
//	        do {
//	        	//get game id and remove games
//	        	llLeagueId = mLeaguesAdapter.getLong(mLeaguesAdapter.getColumnIndexOrThrow(StatsDbHelper.KEY_ID));
//	        	
//	        	liNumGames = mDbHelper.getGameCount(llLeagueId, StatsDbHelper.GAMES_COUNT_LEAGUE);
//	        	
//	        	//listLeagues.
//	        } while(mLeaguesAdapter.moveToNext());
//		}
    }
    
    private void populateStats() {
        Integer liTotAb = 0;
        Integer liTot1b = 0;
        Integer liTot2b = 0;
        Integer liTot3b = 0;
        Integer liTotHr = 0;
        Integer liTotR = 0;
        Integer liTotRbi = 0;
        Integer liTotBb = 0;
        Integer liTotSo = 0;
        Integer liTotSac = 0;
        Integer liTotRoe = 0;
        Integer liTotFc = 0;
        Double ldbAvg = 0.0;
        Double ldbFormattedAvg = 1.000;
        DecimalFormat newFormat;
        
		Cursor statsCursor = mDbHelper.fetchTeamStats(ilTeamId);
		//startManagingCursor(statsCursor);
		
        if(statsCursor.moveToFirst()){
	     	//loop through cursor and insert values into adaptor
	        do {
	            //add to totals
	            liTotAb += statsCursor.getInt(statsCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_AB));
	            liTot1b += statsCursor.getInt(statsCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_1B));
	            liTot2b += statsCursor.getInt(statsCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_2B));
	            liTot3b += statsCursor.getInt(statsCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_3B));
	            liTotHr += statsCursor.getInt(statsCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_HR));
	            liTotR += statsCursor.getInt(statsCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_R));
	            liTotRbi += statsCursor.getInt(statsCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_RBI));
	            liTotBb += statsCursor.getInt(statsCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_BB));
	            liTotSo += statsCursor.getInt(statsCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_SO));
	            liTotSac += statsCursor.getInt(statsCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_SAC));
	            liTotRoe += statsCursor.getInt(statsCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_ROE));
	            liTotFc += statsCursor.getInt(statsCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_FC));
	        } while(statsCursor.moveToNext());
	        
	        //compute average
	        ldbAvg = (double) ((liTot1b + liTot2b + liTot3b + liTotHr + 0.0) / liTotAb);
	        
	        newFormat = new DecimalFormat("0.000");
	        ldbFormattedAvg =  Double.valueOf(newFormat.format(ldbAvg));
	        
	        //set totals into stats totals at bottom of the screen
	        mAb.setText(liTotAb.toString());
	    	m1b.setText(liTot1b.toString());
	    	m2b.setText(liTot2b.toString());
	    	m3b.setText(liTot3b.toString());
	    	mHr.setText(liTotHr.toString());
	    	mR.setText(liTotR.toString());
	    	mRbi.setText(liTotRbi.toString());
	    	mBb.setText(liTotBb.toString());
	    	mSo.setText(liTotSo.toString());
	    	mSac.setText(liTotSac.toString());
	    	mFc.setText(liTotFc.toString());
	    	mRoe.setText(liTotRoe.toString());
	    	mAvg.setText(ldbFormattedAvg.toString());
        }
    }
    
    private void populateTeamSpinner() {
    	Cursor teamCursor = mDbHelper.fetchTeams(iiYear);
    	//startManagingCursor(teamCursor);
    	
        Spinner spnTeam = (Spinner) findViewById(R.id.spTeam);
        
        mTeamAdapter = new SimpleCursorAdapter(this,  
        	      android.R.layout.simple_spinner_item, 
        	      teamCursor, 
        	      new String[] {StatsDbHelper.KEY_TM_NAME, StatsDbHelper.KEY_ID},  
        	      new int[] {android.R.id.text1},
        	      0); 
        mTeamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
        spnTeam.setAdapter(mTeamAdapter); 
        
        populateLeagues();
        populateTournaments();
        populateStats();
    }
    
    private void populateTournaments() {
    	ListView listTournaments = (ListView)findViewById(R.id.lvTournaments);
    	
    	Cursor tournamentsCursor = mDbHelper.fetchTournaments(ilTeamId);
        //startManagingCursor(leaguesCursor);

        // Create an array to specify the fields we want to display in the list (only TOURNAMENT)
        String[] from = new String[]{StatsDbHelper.KEY_TN_NAME, StatsDbHelper.KEY_ID};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.text1};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter tournaments = 
            new SimpleCursorAdapter(this, R.layout.tournaments_row, tournamentsCursor, from, to, 0);
        listTournaments.setAdapter(tournaments);
    }
    
    private void populateYearSpinner() {
    	Integer liPos;
        
    	Cursor yearCursor = mDbHelper.fetchYears();
    	//startManagingCursor(yearCursor);
    	
        Spinner spnYear = (Spinner) findViewById(R.id.spYear);
        
        //setup adapter to be used for loading data from cursor and inserting into Spinner
        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item); 
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
        spnYear.setAdapter(yearAdapter); 
     
        Integer liYearIndex = yearCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_TM_YEAR);
        Boolean bFoundYear = false;
        if(yearCursor.moveToFirst()){
        	//loop through cursor and insert values into adaptor
            do{ 
            	//check if current year exists in cursor
            	if (iiCurrentYear == yearCursor.getInt(liYearIndex)) {
            		bFoundYear = true;
            	}
            	//check if year already exists in spinner - add if it does not exist
            	if (yearAdapter.getPosition(yearCursor.getInt(liYearIndex)) == -1) {
            		yearAdapter.add(yearCursor.getInt(liYearIndex));
            	}
            } while(yearCursor.moveToNext()); 
        }

        //if year does not exist, then add
        if (!bFoundYear) {
        	yearAdapter.add(iiCurrentYear);
        }
        
        liPos = yearAdapter.getPosition(iiCurrentYear);
        
        //default to select current year
        spnYear.setSelection(liPos);
        
        iiYear = iiCurrentYear;
    	
    	//populate teams for currently selected year
    	populateTeamSpinner();
    }
    
    private void updateMainScreen() {
    	//setup the year spinner with dates that are saved plus the current year, if not already listed
    	populateYearSpinner();
    	
    }

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		return null;
		

//	    // Create a new CursorLoader with the following query parameters.
//	    return new CursorLoader(SampleListActivity.this, CONTENT_URI,
//	        PROJECTION, null, null, null);
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//		// A switch-case is useful when dealing with multiple Loaders/IDs
//	    switch (loader.getId()) {
//	      case LOADER_ID:
//	        // The asynchronous load is complete and the data
//	        // is now available for use. Only now can we associate
//	        // the queried Cursor with the SimpleCursorAdapter.
//	        mAdapter.swapCursor(cursor);
//	        break;
//	    }
//	    // The listview now displays the queried data.
	}

	public void onLoaderReset(Loader<Cursor> loader) {
//	    // For whatever reason, the Loader's data is now unavailable.
//	    // Remove any references to the old data by replacing it with
//	    // a null Cursor.
//	    mAdapter.swapCursor(null);
	}
}