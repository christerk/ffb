package com.balancedbytes.games.ffb.server.db.old;

/**
 * 
 * @author Kalimar
 */
public interface IDbTableTurnData {
	
  String TABLE_NAME = "ffb_turn_data";

  String COLUMN_GAME_STATE_ID = "game_state_id";
  String COLUMN_HOME_DATA = "home_data";
  String COLUMN_TURN_NR = "turn_nr";
  String COLUMN_FIRST_TURN_AFTER_KICKOFF = "first_turn_after_kickoff";
  String COLUMN_RE_ROLLS = "re_rolls";
  String COLUMN_SCORE = "score";
  String COLUMN_APOTHECARIES = "apothecaries";
  String COLUMN_RE_ROLL_USED = "re_roll_used";
  String COLUMN_BLITZ_USED = "blitz_used";
  String COLUMN_FOUL_USED = "foul_used";
  String COLUMN_HAND_OVER_USED = "hand_over_used";
  String COLUMN_PASS_USED = "pass_used";
  String COLUMN_LEADER_STATE = "leader_state";
  String COLUMN_TURN_STARTED = "turn_started";

}