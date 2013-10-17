package com.balancedbytes.games.ffb.server.db.old;

/**
 * 
 * @author Kalimar
 */
public interface IDbTableStepStack {
  
	String TABLE_NAME = "ffb_step_stack";
	
  String COLUMN_GAME_STATE_ID = "game_state_id";
  String COLUMN_STACK_INDEX = "stack_index";
  String COLUMN_STEP_BYTES = "step_bytes";
  
	int MAX_BYTES = 1024;
  
}