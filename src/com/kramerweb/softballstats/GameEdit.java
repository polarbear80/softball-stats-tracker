package com.kramerweb.softballstats;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class GameEdit extends ListActivity {
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
    private Long mRowId;
    private String mType;
    private String mProcess;
    private StatsDbHelper mDbHelper;
    private String mSpinnerArray[];

	private int mYear = 2000;
	private int mMonth = 1;
	private int mDay = 1;
	
	static final int DATE_DIALOG_ID = 0;
	static final int GAME_DELETE_DIALOG_ID = 1;
	
	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this) 
        .setTitle("Save?") 
        .setMessage("Do you wish to save your current game?") 
        .setNegativeButton(R.string.no, new OnClickListener() { 
            public void onClick(DialogInterface arg0, int arg1) {
            	GameEdit.super.onBackPressed();
            } 
        }) 
        .setPositiveButton(R.string.yes, new OnClickListener() { 
            public void onClick(DialogInterface arg0, int arg1) { 
                saveData();
            	GameEdit.super.onBackPressed();
            } 
        }).create().show();
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new StatsDbHelper(this);
        mDbHelper.open();

        setContentView(R.layout.game_edit);
        setTitle(R.string.edit_game);

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

        Button confirmButton = (Button) findViewById(R.id.bSave);
        Button changeDateButton = (Button) findViewById(R.id.bDate);
        
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	saveData();
                setResult(RESULT_OK);
                finish();
            }

        });
		
		changeDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	showDialog(DATE_DIALOG_ID);
            }

        });
        
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
		
        Bundle extras = getIntent().getExtras();
        mRowId = extras != null ? extras.getLong(StatsDbHelper.KEY_ID) : null;
        mProcess = extras != null ? extras.getString("PROCESS") : null;
        mType = extras != null ? extras.getString("TYPE") : null;
        if (mProcess.equals("DELETE")) {
        	deleteGame();
        } else {
        	populateFields();
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
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case DATE_DIALOG_ID:
				return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
			/*case GAME_DELETE_DIALOG_ID:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    	builder.setMessage(R.string.delete_game)
			        .setIcon(android.R.drawable.ic_dialog_alert) 
			        .setTitle(R.string.confirm) 
		    		.setCancelable(false)
		    		.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
		    			public void onClick(DialogInterface dialog, int id) {
		    				mDbHelper.deleteGame(mRowId, mType);
		                    setResult(RESULT_OK);
		                    finish();
						}
					})
		    		.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
		    			public void onClick(DialogInterface dialog, int id) {
		    				dialog.cancel();
						}
					});
		    	AlertDialog alert = builder.create();
		    	alert.show();
		    	return alert;*/
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

    //delete the game
    private void deleteGame() {
    	//Verify the user wishes to delete the game
    	//onCreateDialog(GAME_DELETE_DIALOG_ID);

    	mDbHelper.deleteGame(mRowId, mType);
        setResult(RESULT_OK);
        finish();
    	
    }
    
    //populate game data for editing
    private void populateFields() {
        if (mRowId != null) {
        	String lsDate;
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
        	
            Cursor stats = mDbHelper.fetchGame(mRowId);
            startManagingCursor(stats);
            stats.moveToFirst();
            
            lsDate = stats.getString(stats.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_DATE));
            liAb = (Integer) stats.getInt(stats.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_AB));
            li1b = (Integer) stats.getInt(stats.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_1B));
            li2b = (Integer) stats.getInt(stats.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_2B));
            li3b = (Integer) stats.getInt(stats.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_3B));
            liHr = (Integer) stats.getInt(stats.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_HR));
            liR = (Integer) stats.getInt(stats.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_R));
            liRbi = (Integer) stats.getInt(stats.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_RBI));
            liBb = (Integer) stats.getInt(stats.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_BB));
            liSo = (Integer) stats.getInt(stats.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_SO));
            liSac = (Integer) stats.getInt(stats.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_SAC));
            liRoe = (Integer) stats.getInt(stats.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_ROE));
            liFc = (Integer) stats.getInt(stats.getColumnIndexOrThrow(StatsDbHelper.KEY_GM_FC));
            
            mDate.setText(lsDate);
            ArrayAdapter myAb = (ArrayAdapter) mAb.getAdapter();
            int spinnerPosition = myAb.getPosition(liAb.toString());
            //set the default according to value
            mAb.setSelection(spinnerPosition);

            ArrayAdapter my1b = (ArrayAdapter) m1b.getAdapter();
            spinnerPosition = my1b.getPosition(li1b.toString());
            //set the default according to value
            m1b.setSelection(spinnerPosition);

            ArrayAdapter my2b = (ArrayAdapter) m2b.getAdapter();
            spinnerPosition = my2b.getPosition(li2b.toString());
            //set the default according to value
            m2b.setSelection(spinnerPosition);

            ArrayAdapter my3b = (ArrayAdapter) m3b.getAdapter();
            spinnerPosition = my3b.getPosition(li3b.toString());
            //set the default according to value
            m3b.setSelection(spinnerPosition);

            ArrayAdapter myHr = (ArrayAdapter) mHr.getAdapter();
            spinnerPosition = myHr.getPosition(liHr.toString());
            //set the default according to value
            mHr.setSelection(spinnerPosition);

            ArrayAdapter myR = (ArrayAdapter) mR.getAdapter();
            spinnerPosition = myR.getPosition(liR.toString());
            //set the default according to value
            mR.setSelection(spinnerPosition);

            ArrayAdapter myRbi = (ArrayAdapter) mRbi.getAdapter();
            spinnerPosition = myRbi.getPosition(liRbi.toString());
            //set the default according to value
            mRbi.setSelection(spinnerPosition);

            ArrayAdapter myBb = (ArrayAdapter) mBb.getAdapter();
             spinnerPosition = myBb.getPosition(liBb.toString());
            //set the default according to value
            mBb.setSelection(spinnerPosition);

            ArrayAdapter mySo = (ArrayAdapter) mSo.getAdapter();
             spinnerPosition = mySo.getPosition(liSo.toString());
            //set the default according to value
            mSo.setSelection(spinnerPosition);

            ArrayAdapter mySac = (ArrayAdapter) mSac.getAdapter();
            spinnerPosition = mySac.getPosition(liSac.toString());
            //set the default according to value
            mSac.setSelection(spinnerPosition);

            ArrayAdapter myRoe = (ArrayAdapter) mRoe.getAdapter();
            spinnerPosition = myRoe.getPosition(liRoe.toString());
            //set the default according to value
            mRoe.setSelection(spinnerPosition);

            ArrayAdapter myFc = (ArrayAdapter) mFc.getAdapter();
            spinnerPosition = myFc.getPosition(liFc.toString());
            //set the default according to value
            mFc.setSelection(spinnerPosition);
            
            //mAb.setText(liAb.toString());
            //m1b.setText(li1b.toString());
            //m2b.setText(li2b.toString());
            //m3b.setText(li3b.toString());
            //mHr.setText(liHr.toString());
            //mR.setText(liR.toString());
            //mRbi.setText(liRbi.toString());
            //mBb.setText(liBb.toString());
            //mSo.setText(liSo.toString());
            //mSac.setText(liSac.toString());
            //mRoe.setText(liRoe.toString());
            //mFc.setText(liFc.toString());
            
            SimpleDateFormat stringToDate = new SimpleDateFormat("yyyy-MM-dd");
            Date gameDate = null;
			try {
				gameDate = stringToDate.parse(lsDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            mMonth = gameDate.getMonth();
            mDay = gameDate.getDate();
            mYear = gameDate.getYear() + 1900;
        }
    }
    
    //save game data
    private void saveData() {
    	String lsDate = mDate.getText().toString();
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

        mDbHelper.updateGame(mRowId, lsDate, liAb, li1b, li2b, li3b, liHr, liR, liRbi, liBb, liSo, liSac, liRoe, liFc);
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
