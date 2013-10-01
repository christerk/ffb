package com.balancedbytes.games.ffb.server.db;

/**
 * 
 * @author Kalimar
 */
public interface IDbTableGamesInfo {

  String TABLE_NAME = "ffb_games_info";
  
  String COLUMN_ID = "id";
  String COLUMN_SCHEDULED = "scheduled";
  String COLUMN_STARTED = "started";
  String COLUMN_FINISHED = "finished";
  String COLUMN_COACH_HOME = "coach_home";
  String COLUMN_TEAM_HOME_ID = "team_home_id";
  String COLUMN_TEAM_HOME_NAME = "team_home_name";
  String COLUMN_COACH_AWAY = "coach_away";
  String COLUMN_TEAM_AWAY_ID = "team_away_id";
  String COLUMN_TEAM_AWAY_NAME = "team_away_name";
  String COLUMN_HALF = "half";
  String COLUMN_TURN = "turn";
  String COLUMN_HOME_PLAYING = "home_playing";
  String COLUMN_STATUS = "status";
  String COLUMN_TESTING = "testing";

}