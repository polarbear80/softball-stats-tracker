package com.kramerweb.softballstats;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class ViewGames extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	private AdView adView;
	
	private static final int ACTIVITY_ADD_GAME = 0;
	private static final int ACTIVITY_EDIT_GAME = 1;
	private static final int ACTIVITY_DELETE_GAME = 2;
	
	private static final int ADD_GAME = Menu.FIRST;
    private static final int EDIT_GAME = Menu.FIRST + 1;
    private static final int DELETE_GAME = Menu.FIRST + 2;
    
	private static final String GAME_TYPE_LEAGUE = "LEAGUE";
	private static final String DELETE_LEAGUE = "DELETE_LEAGUE";
	private static final String DELETE_GAME_STRING = "DELETE_GAME";
	//private static final String GAME_TYPE_TOURNAMENT = "TOURNAMENT";
	
	private StatsDbHelper mDbHelper;
	private String mType;
	private String mName;
	private int mYear;
	private Long mId;
	
	private TextView mGameTitle;
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
	
	private Long ilListItemId;
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new StatsDbHelper(this);
        mDbHelper.open();
        
        Bundle extras = getIntent().getExtras();
        
        mType = extras != null ? extras.getString("TYPE") : null;
        mId = extras != null ? extras.getLong(StatsDbHelper.KEY_ID) : null;
        mYear = extras != null ? extras.getInt("YEAR") : null;
        mName = extras != null ? extras.getString("NAME") : null;

        setContentView(R.layout.view_games);
        setTitle("Games");

        ListView gameList = getListView();
        registerForContextMenu(gameList);
    	
        mGameTitle = (TextView)findViewById(R.id.tvViewGameTitle);
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
        
        mGameTitle.setText(new StringBuilder().append(mYear).append(" - ").append(mName));
        
        if(mType.equals("DELETE_LEAGUE")) {
        	deleteGames("LEAGUE");
            setResult(RESULT_OK);
            finish();
        } else if(mType.equals("DELETE_TOURNAMENT")) {
        	deleteGames("TOURNAMENT");
            setResult(RESULT_OK);
            finish();
        } else {
        	showGames();
        }
        
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
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        menu.add(0, ADD_GAME, 0, R.string.menu_add_game);
//        return true;

        // Inflate the menu items for use in the action bar
           MenuInflater inflater = getMenuInflater();
           inflater.inflate(R.menu.view_games_activity_actions, menu);
           return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_new_game:
                addGame();
                return true;
            case R.id.action_delete:
            	if(mType.equals("LEAGUE")) {
            		deleteGames("DELETE_LEAGUE");
            	} else {
            		deleteGames("DELETE_TOURNAMENT");
            	}
            	finish();
        }

        return false;
    }
    
  //create context menu for Leagues and Tournaments
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	// Get the info on which item was selected 
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo; 
        
        ArrayAdapter<Object> adapter = (ArrayAdapter<Object>) getListAdapter();
        
        // Retrieve the item that was clicked on 
        GamesList games = (GamesList) adapter.getItem(info.position);
        
        ilListItemId = games.getGameId();
        
	    menu.add(1, EDIT_GAME, 1, R.string.edit_game);
	    menu.add(1, DELETE_GAME, 2, R.string.delete);
    }
    
    //process context menu selection on selected Game
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final Intent i = new Intent(this, GameEdit.class);
        i.putExtra(StatsDbHelper.KEY_ID, ilListItemId);
        switch(item.getItemId()) {
            case EDIT_GAME:
                i.putExtra("PROCESS", "EDIT");
                i.putExtra("TYPE", mType);
                startActivityForResult(i, ACTIVITY_EDIT_GAME);
                return true;
            case DELETE_GAME:
            	new AlertDialog.Builder(this) 
                .setTitle("Delete?") 
                .setMessage("Do you wish to delete this game?") 
                .setNegativeButton(R.string.no, new OnClickListener() { 
                    public void onClick(DialogInterface arg0, int arg1) {
                    	return;
                    } 
                }) 
                .setPositiveButton(R.string.yes, new OnClickListener() { 
                    public void onClick(DialogInterface arg0, int arg1) { 
                    	i.putExtra("PROCESS", "DELETE");
                        i.putExtra("TYPE", mType);
                        startActivityForResult(i, ACTIVITY_DELETE_GAME);
                    } 
                }).create().show();
                return true;
        }
        return super.onContextItemSelected(item);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        showGames();
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	
//    	final TextView tvGame = (TextView) findViewById(R.id.tvGameData);
//    	
//    	ListView lvGames = (ListView) findViewById(R.id.lvGameData);
//    	
//    	final Intent i = new Intent(this, GameEdit.class);
//    	
//    	tvGame.setOnClickListener(new View.OnClickListener() {
//    		public void onClick(View v) {
//    			//i.putExtra(StatsDbHelper.KEY_ID, id);
//                i.putExtra("PROCESS", "EDIT");
//                i.putExtra("TYPE", mType);
//                //startActivityForResult(i, ACTIVITY_EDIT_GAME);
//    		} 
//    	});
    	
    	
//    	lvGames.setOnItemClickListener(new OnItemClickListener() {
//    		public void onItemClick(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//    			TextView c = (TextView) selectedItemView.findViewById(R.id.text1);
//    			i.putExtra(StatsDbHelper.KEY_ID, id);
//                i.putExtra("PROCESS", "EDIT");
//                i.putExtra("TYPE", mType);
//                startActivityForResult(i, ACTIVITY_EDIT_GAME);
//    		}
//    	    
//    	    public void onNothingSelected(AdapterView<?> parentView) { 
//    	        // your code here 
//    	    }
//    	});
    	
        
        EasyTracker.getInstance().activityStart(this);  // Add this method.
    }

    @Override
    public void onStop() {
      super.onStop();
      
      EasyTracker.getInstance().activityStop(this);  // Add this method.
    }
    
    private void addGame() {
        Intent i = new Intent(this, AddGame.class);
        i.putExtra("TYPE", mType);
        i.putExtra(StatsDbHelper.KEY_ID, mId);
        startActivityForResult(i, ACTIVITY_ADD_GAME);
        
        showGames();
    }
    
//    private void delete() {
//    	String lsTitle = "";
//    	String lsMessage = "";
//    	
//    	if(mType.equals("LEAGUE")) {
//    		lsTitle = "Delete League?";
//    		lsMessage = "Are you sure you want to delete current league?";
//    	} else {
//    		lsTitle = "Delete Tournament?";
//    		lsMessage = "Are you sure you want to delete current tournament?";
//    	}
//    	
//    	new AlertDialog.Builder(this) 
//        .setTitle(lsTitle) 
//        .setMessage(lsMessage) 
//        .setNegativeButton(R.string.no, new OnClickListener() { 
//            public void onClick(DialogInterface arg0, int arg1) {
//            	return;
//            } 
//        }) 
//        .setPositiveButton(R.string.yes, new OnClickListener() { 
//            public void onClick(DialogInterface arg0, int arg1) {
//            	if(mType.equals("LEAGUE")) {
//            		mDbHelper.deleteLeague(mId);
//            	} else {
//            		mDbHelper.deleteTournament(mId);
//            	}
////            	updateMainScreen();
//            } 
//        }).create().show();
//        //mBackupManager.dataChanged();
//    }
    
    private void deleteGames(String asType) {
    	//loop through all games and delete first, then delete row from league/tournament table
    	Cursor gamesCursor;
		Long llGmId;
    	
    	if (mType.equals(DELETE_LEAGUE)) {
			gamesCursor = mDbHelper.fetchLeagueGames(mId);
		} else {
			gamesCursor = mDbHelper.fetchTournamentGames(mId);
		}
		startManagingCursor(gamesCursor);
		
		//loop through all games for league/tournament and delete from game and link tables
		if(gamesCursor.moveToFirst()){
	        do {
	        	//get game id and remove games
	        	if (mType.equals("DELETE_LEAGUE")) {
	        		llGmId = gamesCursor.getLong(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_LGG_GM_ID));
	        	} else {
	        		llGmId = gamesCursor.getLong(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_TG_GM_ID));
	        	}
	        	
	        	mDbHelper.deleteGame(llGmId, mType);
	        } while(gamesCursor.moveToNext());
		}
	    //delete record from the league/tournament table
    	if(asType.equals("LEAGUE")) {
    		mDbHelper.deleteLeague(mId);
    	} else {
    		mDbHelper.deleteTournament(mId);
    	}
    }
    
    public void editGame(View lvGame) {
    	final Intent i = new Intent(this, GameEdit.class);
    	final int id = 1;
    	//final String ids = (String) ((TextView) lvGame.findViewById(R.id.tvGameId)).getText();
    	
    	i.putExtra(StatsDbHelper.KEY_ID, id);
        i.putExtra("PROCESS", "EDIT");
        i.putExtra("TYPE", mType);
        startActivityForResult(i, ACTIVITY_EDIT_GAME);
    }
    
	private void showGames() {
		Cursor gamesCursor;
		
		//ListView listGames = (ListView)findViewById(R.id.lvGames);
		
		String lsDate;
		String lsPrevDate = "";
		String lsGameInfo;
		Long llGmId;
        Integer liAb;
        Integer li1b;
        Integer li2b;
        Integer li3b;
        Integer liHr;
        Integer liR;
        Integer liRbi;
        Integer liBb;
        Integer liSo;
        Integer liSac;
        Integer liRoe;
        Integer liFc;
        Integer liHits;
        
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
        SimpleDateFormat stringToDate;
        SimpleDateFormat dateToString;
        Date gameDate;
        
		
		if (mType.equals(GAME_TYPE_LEAGUE)) {
			gamesCursor = mDbHelper.fetchLeagueGames(mId);
		} else {
			gamesCursor = mDbHelper.fetchTournamentGames(mId);
		}
		startManagingCursor(gamesCursor);
        
        List<GamesList> games = new ArrayList<GamesList>();
        
        if(gamesCursor.moveToFirst()){
	     	//loop through cursor and insert values into adaptor
	        do {
	        	//get game values
	        	if (mType.equals("LEAGUE")) {
	        		llGmId = gamesCursor.getLong(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_LGG_GM_ID));
	        	} else {
	        		llGmId = gamesCursor.getLong(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_TG_GM_ID));
	        	}
	        	lsDate = gamesCursor.getString(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_DATE));
	        	liAb = gamesCursor.getInt(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_AB));
	            li1b = gamesCursor.getInt(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_1B));
	            li2b = gamesCursor.getInt(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_2B));
	            li3b = gamesCursor.getInt(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_3B));
	            liHr = gamesCursor.getInt(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_HR));
	            liR = gamesCursor.getInt(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_R));
	            liRbi = gamesCursor.getInt(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_RBI));
	            liBb = gamesCursor.getInt(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_BB));
	            liSo = gamesCursor.getInt(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_SO));
	            liSac = gamesCursor.getInt(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_SAC));
	            liRoe = gamesCursor.getInt(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_ROE));
	            liFc = gamesCursor.getInt(gamesCursor.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_FC));
	        	
	            liHits = li1b + li2b + li3b + liHr;
	            lsGameInfo = liHits.toString() + "/" + liAb.toString();
	            
	            lsGameInfo += (li2b > 0) ? (li2b > 1) ? ", " + li2b.toString() + " 2B" : ", 2B" : "";
	            lsGameInfo += (li3b > 0) ? (li3b > 1) ? ", " + li3b.toString() + " 3B" : ", 3B" : "";
	            lsGameInfo += (liHr > 0) ? (liHr > 1) ? ", " + liHr.toString() + " HR" : ", HR" : "";
	            lsGameInfo += (liR > 0) ? (liR > 1) ? ", " + liR.toString() + " R" : ", R" : "";
	            lsGameInfo += (liRbi > 0) ? (liRbi > 1) ? ", " + liRbi.toString() + " RBI" : ", RBI" : "";
	            lsGameInfo += (liBb > 0) ? (liBb > 1) ? ", " + liBb.toString() + " BB" : ", BB" : "";
	            lsGameInfo += (liSo > 0) ? (liSo > 1) ? ", " + liSo.toString() + " K" : ", K" : "";
	            lsGameInfo += (liSac > 0) ? (liSac > 1) ? ", " + liSac.toString() + " SAC" : ", SAC" : "";
	            lsGameInfo += (liRoe > 0) ? (liRoe > 1) ? ", " + liRoe.toString() + " ROE" : ", ROE" : "";
	            lsGameInfo += (liFc > 0) ? (liFc > 1) ? ", " + liFc.toString() + " FC" : ", FC" : "";
	            
	            //set date format to MMM dd (ie. Jul 2
	    		stringToDate = new SimpleDateFormat("yyyy-MM-dd");
	            gameDate = null;
	    		try {
	    			gameDate = stringToDate.parse(lsDate);
	    		} catch (ParseException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    		
	    		dateToString = new SimpleDateFormat("MMMM d");
	    		lsDate = dateToString.format(gameDate);
	            
	    		if(lsDate.equals(lsPrevDate)) {
	    			lsDate = null;
	    		} else {
	    			lsPrevDate = lsDate;
	    		}
	    		
	            games.add(new GamesList(lsDate, lsGameInfo, llGmId));
	            
	            //add to totals
	            liTotAb += liAb;
	            liTot1b += li1b;
	            liTot2b += li2b;
	            liTot3b += li3b;
	            liTotHr += liHr;
	            liTotR += liR;
	            liTotRbi += liRbi;
	            liTotBb += liBb;
	            liTotSo += liSo;
	            liTotSac += liSac;
	            liTotFc += liFc;
	            liTotRoe += liRoe;
	        } while(gamesCursor.moveToNext());
	        
	        setListAdapter(new GamesListAdapter(this, games));
	        
	        //compute average
	        ldbAvg = (double) ((liTot1b + liTot2b + liTot3b + liTotHr + 0.0) / liTotAb);
	        
	        /*if(ldbAvg <= 0.0) {
		        newFormat = new DecimalFormat(".000"); 
		        ldbFormattedAvg =  Double.valueOf(newFormat.format(ldbAvg)); 
	        } else {*/
	        	newFormat = new DecimalFormat("0.000");
	        	ldbFormattedAvg =  Double.valueOf(newFormat.format(ldbAvg)); 
	        //}
	        
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

	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// TODO Auto-generated method stub
		
	}

	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}
}
