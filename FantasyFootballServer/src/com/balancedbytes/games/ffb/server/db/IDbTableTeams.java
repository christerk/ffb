package com.balancedbytes.games.ffb.server.db;

/**
 * 
 * @author Kalimar
 */
public interface IDbTableTeams {
  
	String TABLE_NAME = "ffb_teams";

  String COLUMN_GAME_STATE_ID = "game_state_id";
  String COLUMN_ID = "id";
  String COLUMN_ROSTER_ID = "roster_id";
  String COLUMN_NAME = "name";
  String COLUMN_HOME_TEAM = "home_team";
  String COLUMN_RACE = "race";
  String COLUMN_COACH = "coach";
  String COLUMN_RE_ROLLS = "re_rolls";
  String COLUMN_APOTHECARIES = "apothecaries";
  String COLUMN_CHEERLEADERS = "cheerleaders";
  String COLUMN_ASSISTANT_COACHES = "assistant_coaches";
  String COLUMN_FAN_FACTOR = "fan_factor";
  String COLUMN_TEAM_VALUE = "team_value";
  String COLUMN_DIVISION = "division";
  String COLUMN_TREASURY = "treasury";
  String COLUMN_BASE_ICON_PATH = "base_icon_path";
  String COLUMN_LOGO_URL = "logo_url";
  
  int LENGTH_NAME = 100;

}