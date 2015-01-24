package com.kramerweb.softballstats;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GamesListAdapter extends ArrayAdapter {
	private final Activity activity;
	private final List games; 
	
	public GamesListAdapter(Activity activity, List objects) { 
		super(activity, R.layout.view_games_row, objects);   
		this.activity = activity;    
		this.games = objects; 
	}     
	
	@Override    
	public View getView(int position, View convertView, ViewGroup parent) {     
		View rowView = convertView;     
		GameDataView sqView = null;     
		if(rowView == null) {           
			// Get a new instance of the row layout view   
			LayoutInflater inflater = activity.getLayoutInflater();       
			rowView = inflater.inflate(R.layout.view_games_row, null);         
			
			// Hold the view objects in an object,         
			// so they don't need to be re-fetched          
			sqView = new GameDataView();        
			sqView.gameDate = (TextView) rowView.findViewById(R.id.tvDate);
			sqView.gameInfo = (TextView) rowView.findViewById(R.id.tvGameData);
			//sqView.gameId = (Integer) rowView.getId;
			
			// Cache the view objects in the tag,           
			// so they can be re-accessed later            
			rowView.setTag(sqView);      
		} else {        
			sqView = (GameDataView) rowView.getTag();     
		}
		
		// Transfer the game data from the data object   
		// to the view objects       
		GamesList currentGame = (GamesList) games.get(position);  
		if(currentGame.getGameDate() != null) {
		//	sqView.gameDate.setVisibility(0); //View.GONE
		//} else {
			sqView.gameDate.setVisibility(0);
			sqView.gameDate.setText(currentGame.getGameDate());
		} else {
			sqView.gameDate.setVisibility(View.GONE);
		}
		sqView.gameInfo.setText(currentGame.getGameInfo());
		sqView.id = currentGame.getGameId();
		return rowView;    
	}   
	
	protected static class GameDataView {   
		protected TextView gameDate;    
		protected TextView gameInfo;
		protected Long id;
	}
}
