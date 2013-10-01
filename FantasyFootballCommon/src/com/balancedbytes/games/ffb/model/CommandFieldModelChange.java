package com.balancedbytes.games.ffb.model;



/**
 * 
 * @author Kalimar
 */
public enum CommandFieldModelChange {
  
  REMOVE_PLAYER(1, "removePlayer", ModelAttributeType.STRING, ModelAttributeType.NULL),
  SET_PLAYER_COOORDINATE(2, "setPlayerCoordinate", ModelAttributeType.STRING, ModelAttributeType.FIELD_COORDINATE),
  SET_PLAYER_STATE(3, "setPlayerState", ModelAttributeType.STRING, ModelAttributeType.PLAYER_STATE),
  SET_BALL_COORDINATE(4, "setBallCoordinate", ModelAttributeType.FIELD_COORDINATE, ModelAttributeType.NULL),
  SET_BALL_MOVING(5, "setBallMoving", ModelAttributeType.BOOLEAN, ModelAttributeType.NULL),
  SET_BALL_IN_PLAY(6, "setBallInPlay", ModelAttributeType.BOOLEAN, ModelAttributeType.NULL),
  ADD_BLOOD_SPOT(7, "addBloodSpot", ModelAttributeType.BLOOD_SPOT, ModelAttributeType.NULL),
  ADD_TRACK_NUMBER(8, "addTrackNumber", ModelAttributeType.TRACK_NUMBER, ModelAttributeType.NULL),
  REMOVE_TRACK_NUMBER(9, "removeTrackNumber", ModelAttributeType.TRACK_NUMBER, ModelAttributeType.NULL),
  ADD_PUSHBACK_SQUARE(10, "addPushbackSquare", ModelAttributeType.PUSHBACK_SQUARE, ModelAttributeType.NULL),
  REMOVE_PUSHBACK_SQUARE(11, "removePushbackSquare", ModelAttributeType.PUSHBACK_SQUARE, ModelAttributeType.NULL),
  ADD_MOVE_SQUARE(12, "addMoveSquare", ModelAttributeType.MOVE_SQUARE, ModelAttributeType.NULL),
  REMOVE_MOVE_SQUARE(13, "removeMoveSquare", ModelAttributeType.MOVE_SQUARE, ModelAttributeType.NULL),
  SET_WEATHER(14, "setWeather", ModelAttributeType.WEATHER, ModelAttributeType.NULL),
  SET_RANGE_RULER(15, "setRangeRuler", ModelAttributeType.RANGE_RULER, ModelAttributeType.NULL),
  ADD_DICE_DECORATION(16, "addDiceDecoration", ModelAttributeType.DICE_DECORATION, ModelAttributeType.NULL),
  REMOVE_DICE_DECORATION(17, "removeDiceDecoration", ModelAttributeType.DICE_DECORATION, ModelAttributeType.NULL),
  ADD_FIELD_MARKER(18, "addFieldMarker", ModelAttributeType.FIELD_MARKER, ModelAttributeType.NULL),
  REMOVE_FIELD_MARKER(19, "removeFieldMarker", ModelAttributeType.FIELD_MARKER, ModelAttributeType.NULL),
  ADD_PLAYER_MARKER(20, "addPlayerMarker", ModelAttributeType.PLAYER_MARKER, ModelAttributeType.NULL),
  REMOVE_PLAYER_MARKER(21, "removePlayerMarker", ModelAttributeType.PLAYER_MARKER, ModelAttributeType.NULL),
  SET_BOMB_COORDINATE(22, "setBombCoordinate", ModelAttributeType.FIELD_COORDINATE, ModelAttributeType.NULL),
  SET_BOMB_MOVING(23, "setBombMoving", ModelAttributeType.BOOLEAN, ModelAttributeType.NULL),
  ADD_CARD(24, "addCard", ModelAttributeType.STRING, ModelAttributeType.CARD),
  REMOVE_CARD(25, "removeCard", ModelAttributeType.STRING, ModelAttributeType.CARD);

  private int fId;
  private String fName;
  private ModelAttributeType fAttributeType1;
  private ModelAttributeType fAttributeType2;
  
  private CommandFieldModelChange(int pValue, String pName, ModelAttributeType pAttributeType1, ModelAttributeType pAttributeType2) {
    fId = pValue;
    fName = pName;
    fAttributeType1 = pAttributeType1;
    fAttributeType2 = pAttributeType2;
  }

  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
  public ModelAttributeType getAttributeType1() {
    return fAttributeType1;
  }
  
  public ModelAttributeType getAttributeType2() {
    return fAttributeType2;
  }
  
  public static CommandFieldModelChange fromId(int pId) {
    for (CommandFieldModelChange command : values()) {
      if (command.getId() == pId) {
        return command;
      }
    }
    return null;
  }
    
  public static CommandFieldModelChange fromName(String pName) {
    for (CommandFieldModelChange command : values()) {
      if (command.getName().equalsIgnoreCase(pName)) {
        return command;
      }
    }
    return null;
  }

}
