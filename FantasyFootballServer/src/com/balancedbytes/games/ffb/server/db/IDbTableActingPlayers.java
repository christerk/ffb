package com.balancedbytes.games.ffb.server.db;

/**
 * 
 * @author Kalimar
 */
public interface IDbTableActingPlayers {

	String TABLE_NAME = "ffb_acting_players";
	
  String COLUMN_GAME_STATE_ID = "game_state_id";
  String COLUMN_PLAYER_ID = "player_id";
  String COLUMN_STRENGTH = "strength";
  String COLUMN_CURRENT_MOVE = "current_move";
  String COLUMN_GOING_FOR_IT = "going_for_it";
  String COLUMN_DODGING = "dodging";
  String COLUMN_LEAPING = "leaping";
  String COLUMN_HAS_BLOCKED = "has_blocked";
  String COLUMN_HAS_FOULED = "has_fouled";
  String COLUMN_HAS_PASSED = "has_passed";
  String COLUMN_HAS_MOVED = "has_moved";
  String COLUMN_PLAYER_ACTION = "player_action";
  String COLUMN_STANDING_UP = "standing_up";
  String COLUMN_SUFFERING_BLOODLUST = "suffering_bloodlust";
  String COLUMN_SUFFERING_ANIMOSITY = "suffering_animosity";
  String COLUMN_USED_SKILL_1 = "used_skill_1";
  String COLUMN_USED_SKILL_2 = "used_skill_2";
  String COLUMN_USED_SKILL_3 = "used_skill_3";
  String COLUMN_USED_SKILL_4 = "used_skill_4";
  String COLUMN_USED_SKILL_5 = "used_skill_5";

}