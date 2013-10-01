package com.balancedbytes.games.ffb.server.db;


/**
 * 
 * @author Kalimar
 */
public interface IDbTablePlayerResults {
	
	String TABLE_NAME = "ffb_player_results";
  
  String COLUMN_GAME_STATE_ID = "game_state_id";
  String COLUMN_PLAYER_ID = "player_id";
  String COLUMN_COMPLETIONS = "completions";
  String COLUMN_TOUCHDOWNS = "topuchdowns";
  String COLUMN_INTERCEPTIONS = "interceptions";
  String COLUMN_CASUALTIES = "casualties";
  String COLUMN_PLAYER_AWARDS = "player_awards";
  String COLUMN_PASSING = "passing";
  String COLUMN_RUSHING = "rushing";
  String COLUMN_BLOCKS = "blocks";
  String COLUMN_FOULS = "fouls";
  String COLUMN_OLD_SPPS = "old_spps";
  String COLUMN_SERIOUS_INJURY = "serious_injury";
  String COLUMN_SERIOUS_INJURY_DECAY = "serious_injury_decay";
  String COLUMN_SEND_TO_BOX_REASON = "send_to_box_reason";
  String COLUMN_SEND_TO_BOX_TURN = "send_to_box_turn";
  String COLUMN_SEND_TO_BOX_HALF = "send_to_box_half";
  String COLUMN_SEND_TO_BOX_BY_PLAYER_ID = "send_to_box_by_player_id";
  String COLUMN_TURNS_PLAYED = "turns_played";
  String COLUMN_HAS_USED_SECRET_WEAPON = "has_used_secret_weapon";
  String COLUMN_DEFECTING = "defecting";

}