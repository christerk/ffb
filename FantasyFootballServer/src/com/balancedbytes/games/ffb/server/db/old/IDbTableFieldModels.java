package com.balancedbytes.games.ffb.server.db.old;

/**
 * 
 * @author Kalimar
 */
public interface IDbTableFieldModels {

	String TABLE_NAME = "ffb_field_models";

  String COLUMN_GAME_STATE_ID = "game_state_id";
  String COLUMN_TYPE = "type";
  String COLUMN_ITEM = "item";
  String COLUMN_COORDINATE_X = "coordinate_x";
  String COLUMN_COORDINATE_Y = "coordinate_y";
  String COLUMN_NUMBER_1 = "number_1";
  String COLUMN_NUMBER_2 = "number_2";
  String COLUMN_FLAG_1 = "flag_1";
  String COLUMN_FLAG_2 = "flag_2";
  String COLUMN_ID_1 = "id_1";
  String COLUMN_TEXT_1 = "text_1";
  String COLUMN_TEXT_2 = "text_2";
  
  int MAX_TEXT_LENGTH = 40;
  
}