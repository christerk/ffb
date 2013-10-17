package com.balancedbytes.games.ffb.server.db.old;

/**
 * 
 * @author Kalimar
 */
public interface IDbTablePlayerIcons {
  
	String TABLE_NAME = "ffb_player_icons";

  String COLUMN_GAME_STATE_ID = "game_state_id";
  String COLUMN_PLAYER_ID = "player_id";
  String COLUMN_ICON_TYPE = "icon_type";
  String COLUMN_ICON_URL = "icon_url";
  
}