package com.balancedbytes.games.ffb.model.change.old;




/**
 * 
 * @author Kalimar
 */
public enum CommandGameAttributeChange {
  
  SET_ID(1, "setId", ModelAttributeType.LONG),
  SET_STARTED(2, "setStarted", ModelAttributeType.DATE),
  SET_FINISHED(3, "setFinished", ModelAttributeType.DATE),
  SET_HALF(4, "setHalf", ModelAttributeType.BYTE),
  SET_TURN_MODE(5, "setTurnMode", ModelAttributeType.TURN_MODE),
  SET_PASS_COORDINATE(6, "setPassCoordinate", ModelAttributeType.FIELD_COORDINATE),
  SET_HOME_PLAYING(7, "setHomePlaying", ModelAttributeType.BOOLEAN),
  SET_HOME_FIRST_OFFENSE(8, "setHomeFirstOffense", ModelAttributeType.BOOLEAN),
  SET_SETUP_OFFENSE(9, "setSetupOffense", ModelAttributeType.BOOLEAN),
  SET_WAITING_FOR_OPPONENT(10, "setWaitingForOpponent", ModelAttributeType.BOOLEAN),
  SET_DIALOG_PARAMETER(11, "setDialogParameter", ModelAttributeType.DIALOG_PARAMETER),
  SET_DEFENDER_ID(12, "setDefenderId", ModelAttributeType.STRING),
  SET_DEFENDER_ACTION(13, "setDefenderAction", ModelAttributeType.PLAYER_ACTION),
  SET_TIMEOUT_POSSIBLE(14, "setTimeoutPossible", ModelAttributeType.BOOLEAN),
  SET_TIMEOUT_ENFORCED(15, "setTimeoutEnforced", ModelAttributeType.BOOLEAN),
  SET_CONCESSION_POSSIBLE(16, "setConcessionPossible", ModelAttributeType.BOOLEAN),
  SET_TESTING(17, "setTesting", ModelAttributeType.BOOLEAN),
  // 18 obsolete SET_OPTION_OVERTIME
  // 19 obsolete SET_OPTION_TURNTIME
  SET_SCHEDULED(20, "setScheduled", ModelAttributeType.DATE),
  // 21 obsolete SET_OPTION_PETTY_CASH
  // 22 obsolete SET_OPTION_INDUCEMENTS
  // 23 obsolete SET_OPTION_JOURNEYMEN
  // 24 obsolete SET_OPTION_SPIRALLING_EXPENSES
  // 25 obsolete SET_OPTION_SNEAKY_GIT_AS_FOUL_GUARD
  // 26 obsolete SET_OPTION_FOUL_BONUS_OUTSIDE_TACKLEZONE
  // 27 obsolete SET_OPTION_RIGHT_STUFF_CANCELS_TACKLE
  // 28 obsolete SET_OPTION_PILING_ON_WITHOUT_MODIFIER
  // 29 obsolete SET_OPTION_CHECK_OWNERSHIP
  // 30 obsolete SET_OPTION_TEST_MODE
  SET_THROWER_ID(31, "setThrowerId", ModelAttributeType.STRING),
  SET_THROWER_ACTION(32, "setThrowerAction", ModelAttributeType.PLAYER_ACTION),
  ADD_OPTION(33, "addOption", ModelAttributeType.GAME_OPTION);

  private int fId;
  private String fName;
  private ModelAttributeType fAttributeType;
  
  private CommandGameAttributeChange(int pValue, String pName, ModelAttributeType pAttributeType) {
    fId = pValue;
    fName = pName;
    fAttributeType = pAttributeType;
  }

  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
  public ModelAttributeType getAttributeType() {
    return fAttributeType;
  }
  
  public static CommandGameAttributeChange fromId(int pId) {
    for (CommandGameAttributeChange command : values()) {
      if (command.getId() == pId) {
        return command;
      }
    }
    return null;
  }
    
  public static CommandGameAttributeChange fromName(String pName) {
    for (CommandGameAttributeChange command : values()) {
      if (command.getName().equalsIgnoreCase(pName)) {
        return command;
      }
    }
    return null;
  }

}
