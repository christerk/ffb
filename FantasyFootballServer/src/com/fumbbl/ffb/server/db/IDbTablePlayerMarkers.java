package com.fumbbl.ffb.server.db;

/**
 * 
 * @author Kalimar
 */
public interface IDbTablePlayerMarkers {

	String TABLE_NAME = "ffb_player_markers";

	String COLUMN_TEAM_ID = "team_id";
	String COLUMN_PLAYER_ID = "player_id";
	String COLUMN_TEXT = "text";

	int MAX_TEXT_LENGTH = 40;

}