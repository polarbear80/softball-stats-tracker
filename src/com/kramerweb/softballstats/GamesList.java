package com.kramerweb.softballstats;

public class GamesList {
	private String gameDate;
	private String gameInfo;
	private Long gameId;
	
	public GamesList(String gameDate, String gameInfo, Long gameId) {
		this.gameDate = gameDate;
		this.gameInfo = gameInfo;
		this.gameId = gameId;
	}
	
	public void setGameDate(String gameDate) {
		this.gameDate = gameDate;
	}
	
	public String getGameDate() {
		return gameDate;
	}
	
	public void setGameInfo(String gameInfo) {
		this.gameInfo = gameInfo;
	}
	
	public String getGameInfo() {
		return gameInfo;
	}
	
	public void setGameId(Long gameId) {
		this.gameId = gameId;
	}
	
	public Long getGameId() {
		return gameId;
	}
}
