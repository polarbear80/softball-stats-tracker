package com.kramerweb.softballstats;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class AddGame extends Activity {
	
	private StatsDbHelper mDbHelper;
	private String mType;
	private Long mId;
	
	private TextView mDate;
	private Spinner mAb;
	private Spinner m1b;
	private Spinner m2b;
	private Spinner m3b;
	private Spinner mHr;
	private Spinner mR;
	private Spinner mRbi;
	private Spinner mBb;
	private Spinner mSo;
	private Spinner mSac;
	private Spinner mRoe;
	private Spinner mFc;
	private int mYear = 2000;
	private int mMonth = 1;
	private int mDay = 1;
    private String mSpinnerArray[];
	
	static final int DATE_DIALOG_ID = 0;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new StatsDbHelper(this);
        mDbHelper.open();

        setContentView(R.layout.game_edit);
        setTitle("Add Game");
        
        mDate = (TextView) findViewById(R.id.tvDate);
        mAb = (Spinner) findViewById(R.id.spAtBats);
    	m1b = (Spinner) findViewById(R.id.spSingles);
    	m2b = (Spinner) findViewById(R.id.spDoubles);
    	m3b = (Spinner) findViewById(R.id.spTriples);
    	mHr = (Spinner) findViewById(R.id.spHomeRuns);
    	mR = (Spinner) findViewById(R.id.spRuns);
    	mRbi = (Spinner) findViewById(R.id.spRBIs);
    	mBb = (Spinner) findViewById(R.id.spWalks);
    	mSo = (Spinner) findViewById(R.id.spStrikeOuts);
    	mSac = (Spinner) findViewById(R.id.spSacs);
    	mRoe = (Spinner) findViewById(R.id.spROE);
    	mFc = (Spinner) findViewById(R.id.spFC);
    	
		mType = (savedInstanceState == null) ? null : (String) savedInstanceState.getSerializable("TYPE");
		if (mType == null) {
			Bundle extras = getIntent().getExtras();
			mType = extras != null ? extras.getString("TYPE") : null;
		}
		
		mId = (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable(StatsDbHelper.KEY_ID);
		if (mId == null) {
			Bundle extras = getIntent().getExtras();
			mId = extras != null ? extras.getLong(StatsDbHelper.KEY_ID) : null;
		}
        
		Button saveButton = (Button) findViewById(R.id.bSave);
		Button changeDateButton = (Button) findViewById(R.id.bDate);
		
		saveButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            	saveState();
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
        

        
		//pre-populate spinners with values
		mSpinnerArray = new String[26];
		for (Integer i = 0; i < 26; i++) {
			mSpinnerArray[i] = i.toString();
		}
		
		Spinner s1 = (Spinner) findViewById(R.id.spAtBats);
		ArrayAdapter adapter1 = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, mSpinnerArray);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s1.setAdapter(adapter1);
		
		Spinner s2 = (Spinner) findViewById(R.id.spSingles);
		ArrayAdapter adapter2 = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, mSpinnerArray);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s2.setAdapter(adapter2);
		
		Spinner s3 = (Spinner) findViewById(R.id.spDoubles);
		ArrayAdapter adapter3 = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, mSpinnerArray);
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s3.setAdapter(adapter3);
		
		Spinner s4 = (Spinner) findViewById(R.id.spTriples);
		ArrayAdapter adapter4 = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, mSpinnerArray);
		adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s4.setAdapter(adapter4);
		
		Spinner s5 = (Spinner) findViewById(R.id.spHomeRuns);
		ArrayAdapter adapter5 = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, mSpinnerArray);
		adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s5.setAdapter(adapter5);
		
		Spinner s6 = (Spinner) findViewById(R.id.spRuns);
		ArrayAdapter adapter6 = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, mSpinnerArray);
		adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s6.setAdapter(adapter6);
		
		Spinner s7 = (Spinner) findViewById(R.id.spRBIs);
		ArrayAdapter adapter7 = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, mSpinnerArray);
		adapter7.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s7.setAdapter(adapter7);
		
		Spinner s8 = (Spinner) findViewById(R.id.spWalks);
		ArrayAdapter adapter8 = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, mSpinnerArray);
		adapter8.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s8.setAdapter(adapter8);
		
		Spinner s9 = (Spinner) findViewById(R.id.spStrikeOuts);
		ArrayAdapter adapter9 = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, mSpinnerArray);
		adapter9.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s9.setAdapter(adapter9);
		
		Spinner s10 = (Spinner) findViewById(R.id.spSacs);
		ArrayAdapter adapter10 = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, mSpinnerArray);
		adapter10.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s10.setAdapter(adapter10);
		
		Spinner s11 = (Spinner) findViewById(R.id.spROE);
		ArrayAdapter adapter11 = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, mSpinnerArray);
		adapter11.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s11.setAdapter(adapter11);
		
		Spinner s12 = (Spinner) findViewById(R.id.spFC);
		ArrayAdapter adapter12 = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, mSpinnerArray);
		adapter12.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s12.setAdapter(adapter12);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case DATE_DIALOG_ID:
				return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
			}
		return null;
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
		String lsDate = mDate.getText().toString();
		
		SimpleDateFormat stringToDate = new SimpleDateFormat("MM-dd-yyyy");
        Date gameDate = null;
		try {
			gameDate = stringToDate.parse(lsDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SimpleDateFormat dateToString = new SimpleDateFormat("yyyy-MM-dd");
		lsDate = dateToString.format(gameDate); 
		
        int liAb = Integer.parseInt(mAb.getSelectedItem().toString());
        int li1b = Integer.parseInt(m1b.getSelectedItem().toString());
        int li2b = Integer.parseInt(m2b.getSelectedItem().toString());
        int li3b = Integer.parseInt(m3b.getSelectedItem().toString());
        int liHr = Integer.parseInt(mHr.getSelectedItem().toString());
        int liR = Integer.parseInt(mR.getSelectedItem().toString());
        int liRbi = Integer.parseInt(mRbi.getSelectedItem().toString());
        int liBb = Integer.parseInt(mBb.getSelectedItem().toString());
        int liSo = Integer.parseInt(mSo.getSelectedItem().toString());
        int liSac = Integer.parseInt(mSac.getSelectedItem().toString());
        int liRoe = Integer.parseInt(mRoe.getSelectedItem().toString());
        int liFc = Integer.parseInt(mFc.getSelectedItem().toString());

        mDbHelper.saveNewGame(mType, mId, lsDate, liAb, li1b, li2b, li3b, liHr, liR, liRbi, liBb, liSo, liSac, liRoe, liFc);
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
}
