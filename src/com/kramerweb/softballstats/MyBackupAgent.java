package com.kramerweb.softballstats;

import java.io.File;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;
import android.util.Log;

public class MyBackupAgent extends BackupAgentHelper {
	private static final String DB_NAME = "SoftballStats";

	//@Override
	public void onCreate(){
		Log.i("BackupAgent", "in onCreate()");
		FileBackupHelper dbs = new FileBackupHelper(this, DB_NAME);
		addHelper("dbs", dbs);
	}
	
	@Override
	public File getFilesDir(){
		Log.i("BackupAgent", "in getFilesDir()");
	    File path = getDatabasePath(DB_NAME);
	    return path.getParentFile();
	}
}
