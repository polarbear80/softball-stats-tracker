package com.kramerweb.softballstats;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddLeague extends Activity {

	private EditText mLeagueName;
	private EditText mDescription;
	private StatsDbHelper mDbHelper;
	private Long mTeamId;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new StatsDbHelper(this);
        mDbHelper.open();

        setContentView(R.layout.add_league);
        setTitle(R.string.add_league);

        mLeagueName = (EditText) findViewById(R.id.etLeagueName);
        mDescription = (EditText) findViewById(R.id.etDescription);

        Button saveButton = (Button) findViewById(R.id.bSave);

        mTeamId = (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable(StatsDbHelper.KEY_ID);
		if (mTeamId == null) {
			Bundle extras = getIntent().getExtras();
			mTeamId = extras != null ? extras.getLong(StatsDbHelper.KEY_ID) : null;
		}
        
        saveButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            	saveState();
                setResult(RESULT_OK);
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
	
	/*@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
	}

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }*/
	
	private void saveState() {
        String leagueName = mLeagueName.getText().toString();
        String leagueDescription = mDescription.getText().toString();

        mDbHelper.addLeague(leagueName, leagueDescription, mTeamId);
    }
}
