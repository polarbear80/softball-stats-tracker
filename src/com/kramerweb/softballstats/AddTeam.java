package com.kramerweb.softballstats;

import com.google.analytics.tracking.android.EasyTracker;
import com.kramerweb.softballstats.StatsDbHelper;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddTeam extends Activity {
	private EditText mTeamName;
	private StatsDbHelper mDbHelper;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new StatsDbHelper(this);
        mDbHelper.open();

        setContentView(R.layout.add_team);
        setTitle(R.string.add_team);

        mTeamName = (EditText) findViewById(R.id.etTeamName);

        Button saveButton = (Button) findViewById(R.id.bSave);

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
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //saveState();
	}

    @Override
    protected void onPause() {
        super.onPause();
        //saveState();
    }
	
	private void saveState() {
        String teamName = mTeamName.getText().toString();
        
        if(teamName.length() > 0) {
        	mDbHelper.addTeam(teamName);
        }
    }
}
