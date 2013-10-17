package com.balancedbytes.games.ffb.server.db.old;

/**
 * 
 * @author Kalimar
 */
public interface IDbTableGameLogs {
	
	String TABLE_NAME = "ffb_game_logs";
  
  String COLUMN_GAME_STATE_ID = "game_state_id";
  String COLUMN_COMMAND_NR = "command_nr";
  String COLUMN_SEQUENCE_NR = "sequence_nr";
  String COLUMN_COMMAND_BYTES = "command_bytes";
	
  int MAX_BYTES = 1024;
  
}