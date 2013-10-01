package com.balancedbytes.games.ffb.server.db;

/**
 * 
 * @author Kalimar
 */
public interface IDbTableTeamSetups {
	
	int LENGTH_NAME = 40;
	
	String TABLE_NAME = "ffb_team_setups";
	
	String COLUMN_TEAM_ID = "team_id";
	String COLUMN_NAME = "name";

	String COLUMN_PLAYER_NR = "player_nr_$1";
	String COLUMN_COORDINATE_X = "coordinate_$1_x";
	String COLUMN_COORDINATE_Y = "coordinate_$1_y";
	
}