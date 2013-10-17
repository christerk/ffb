package com.balancedbytes.games.ffb.server.db.old;

/**
 * 
 * @author Kalimar
 */
public interface IDbTablePlayers {
	
	String TABLE_NAME = "ffb_players";
  
  String COLUMN_GAME_STATE_ID = "game_state_id";
  String COLUMN_ID = "id";
  String COLUMN_TEAM_ID = "team_id";
  String COLUMN_POSITION_ID = "position_id";
  String COLUMN_NUMBER = "number";
  String COLUMN_NAME = "name";
  String COLUMN_GENDER = "gender";
  String COLUMN_TYPE = "type";
  String COLUMN_MOVEMENT = "movement";
  String COLUMN_STRENGTH = "strength";
  String COLUMN_AGILITY = "agility";
  String COLUMN_ARMOR = "armor";
  String COLUMN_CURRENT_INJURY = "current_injury";

  int LENGTH_NAME = 100;
  
}