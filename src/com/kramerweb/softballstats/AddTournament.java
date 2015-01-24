package com.kramerweb.softballstats;

import java.util.Calendar;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class AddTournament extends Activity {
	private EditText mTournamentName;
	private TextView mDate;
	private StatsDbHelper mDbHelper;
	private Long mTeamId;
	private int mYear = 2000;
	private int mMonth = 1;
	private int mDay = 1;
	
	static final int DATE_DIALOG_ID = 0;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new StatsDbHelper(this);
        mDbHelper.open();

        setContentView(R.layout.add_tournament);
        setTitle(R.string.add_tournament);

        mTournamentName = (EditText) findViewById(R.id.etTournamentName);
        mDate = (TextView) findViewById(R.id.tvDate);

        Button saveButton = (Button) findViewById(R.id.bSave);
        Button changeDateButton = (Button) findViewById(R.id.bDate);

        mTeamId = (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable(StatsDbHelper.KEY_ID);
		if (mTeamId == null) {
			Bundle extras = getIntent().getExtras();
			mTeamId = extras != null ? extras.getLong(StatsDbHelper.KEY_ID) : null;
		}
        
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }

        });
        
        changeDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	showDialog(DATE_DIALOG_ID);
            }

        });
        
        // get the current date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        // display the current date
        updateDisplay();
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
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case DATE_DIALOG_ID:
				return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
			}
		return null;
	}
	
	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListener =
			new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year,
				int monthOfYear, int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
		}
	};
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
	}

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }
	
    // updates the date we display in the TextView
    private void updateDisplay() {
    	mDate.setText(
    			new StringBuilder()
    			// Month is 0 based so add 1
    			.append(mMonth + 1).append("-")
    			.append(mDay).append("-")
    			.append(mYear).append(" "));
    }
    
	private void saveState() {
        String tournamentName = mTournamentName.getText().toString();
        String date = mDate.getText().toString();

        mDbHelper.addTournament(tournamentName, date, mTeamId);
    }
}
