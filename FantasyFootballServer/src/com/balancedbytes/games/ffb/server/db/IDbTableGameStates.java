package com.balancedbytes.games.ffb.server.db;

/**
 * 
 * @author Kalimar
 */
public interface IDbTableGameStates {

  String TABLE_NAME = "ffb_game_states";

  String COLUMN_ID = "id";  // 1
  String COLUMN_SCHEDULED = "scheduled";
  String COLUMN_STARTED = "started";
  String COLUMN_FINISHED = "finished";
  String COLUMN_HALF = "half";
  String COLUMN_TURN_MODE = "turn_mode";
  String COLUMN_HOME_PLAYING = "home_playing";
  String COLUMN_HOME_FIRST_OFFENSE = "home_first_offense";
  String COLUMN_SETUP_OFFENSE = "setup_offense";
  String COLUMN_WAITING_FOR_OPPONENT = "waiting_for_opponent";
  String COLUMN_DEFENDER_ID  = "defender_id";
  String COLUMN_DEFENDER_ACTION  = "defender_action";
  String COLUMN_PASS_COORDINATE_X = "pass_coordinate_x";
  String COLUMN_PASS_COORDINATE_Y = "pass_coordinate_y";
  String COLUMN_TURN_TIME = "turn_time";
  String COLUMN_TIMEOUT_POSSIBLE = "timeout_possible";
  String COLUMN_TIMEOUT_ENFORCED = "timeout_enforced";
  String COLUMN_CONCESSION_POSSIBLE = "concession_possible";
  String COLUMN_TESTING = "testing";
  String COLUMN_STATUS = "status";
  String COLUMN_THROWER_ID = "thrower_id";
  String COLUMN_THROWER_ACTION = "thrower_action";

}