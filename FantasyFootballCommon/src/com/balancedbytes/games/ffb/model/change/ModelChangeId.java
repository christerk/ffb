package com.balancedbytes.games.ffb.model.change;

import com.balancedbytes.games.ffb.IEnumWithId;
import com.balancedbytes.games.ffb.IEnumWithName;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public enum ModelChangeId implements IEnumWithId, IEnumWithName {

	ACTING_PLAYER_MARK_SKILL_USED(1, "actingPlayerMarkSkillUsed", ModelChangeDataType.SKILL),
	ACTING_PLAYER_SET_CURRENT_MOVE(2, "actingPlayerSetCurrentMove", ModelChangeDataType.INTEGER),
	ACTING_PLAYER_SET_DODGING(3, "actingPlayerSetDodging", ModelChangeDataType.BOOLEAN),
	ACTING_PLAYER_SET_GOING_FOR_IT(4, "actingPlayerSetGoingForIt", ModelChangeDataType.BOOLEAN),
	ACTING_PLAYER_SET_HAS_BLOCKED(5, "actingPlayerSetHasBlocked", ModelChangeDataType.BOOLEAN),
	ACTING_PLAYER_SET_HAS_FED(6, "actingPlayerSetHasFed", ModelChangeDataType.BOOLEAN),
	ACTING_PLAYER_SET_HAS_FOULED(7, "actingPlayerSetHasFouled", ModelChangeDataType.BOOLEAN),
	ACTING_PLAYER_SET_HAS_MOVED(8, "actingPlayerSetHasMoved", ModelChangeDataType.BOOLEAN),
	ACTING_PLAYER_SET_HAS_PASSED(9, "actingPlayerSetHasPassed", ModelChangeDataType.BOOLEAN),
	ACTING_PLAYER_SET_LEAPING(10, "actingPlayerSetLeaping", ModelChangeDataType.BOOLEAN),
	ACTING_PLAYER_SET_PLAYER_ACTION(11, "actingPlayerSetPlayerAction", ModelChangeDataType.BOOLEAN),
	ACTING_PLAYER_SET_PLAYER_ID(12, "actingPlayerSetPlayerId", ModelChangeDataType.STRING),
	ACTING_PLAYER_SET_STANDING_UP(13, "actingPlayerSetStandingUp", ModelChangeDataType.BOOLEAN),
	ACTING_PLAYER_SET_STRENGTH(14, "actingPlayerSetStrength", ModelChangeDataType.INTEGER),
	ACTING_PLAYER_SET_SUFFERING_ANIMOSITY(15, "actingPlayerSetSufferingAnimosity", ModelChangeDataType.BOOLEAN),
	ACTING_PLAYER_SET_SUFFERING_BLOOD_LUST(16, "actingPlayerSetSufferingBloodLust", ModelChangeDataType.BOOLEAN),
	
  FIELD_MODEL_ADD_BLOOD_SPOT(17, "fieldModelAddBloodSpot", ModelChangeDataType.BLOOD_SPOT),
  FIELD_MODEL_ADD_CARD(18, "fieldModelAddCard", ModelChangeDataType.CARD),
  FIELD_MODEL_ADD_DICE_DECORATION(19, "fieldModelAddDiceDecoration", ModelChangeDataType.DICE_DECORATION),
  FIELD_MODEL_ADD_FIELD_MARKER(20, "fieldModelAddFieldMarker", ModelChangeDataType.FIELD_MARKER),
  FIELD_MODEL_ADD_MOVE_SQUARE(21, "fieldModelAddMoveSquare", ModelChangeDataType.MOVE_SQUARE),
  FIELD_MODEL_ADD_PLAYER_MARKER(22, "fieldModelAddPlayerMarker", ModelChangeDataType.PLAYER_MARKER),
  FIELD_MODEL_ADD_PUSHBACK_SQUARE(23, "fieldModelAddPushbackSquare", ModelChangeDataType.PUSHBACK_SQUARE),
  FIELD_MODEL_ADD_TRACK_NUMBER(24, "fieldModelAddTrackNumber", ModelChangeDataType.TRACK_NUMBER),
  FIELD_MODEL_REMOVE_CARD(25, "fieldModelRemoveCard", ModelChangeDataType.CARD),
  FIELD_MODEL_REMOVE_DICE_DECORATION(26, "fieldModelRemoveDiceDecoration", ModelChangeDataType.DICE_DECORATION),
  FIELD_MODEL_REMOVE_FIELD_MARKER(27, "fieldModelRemoveFieldMarker", ModelChangeDataType.FIELD_MARKER),
  FIELD_MODEL_REMOVE_MOVE_SQUARE(28, "fieldModelRemoveMoveSquare", ModelChangeDataType.MOVE_SQUARE),
  FIELD_MODEL_REMOVE_PLAYER(29, "fieldModelRemovePlayer", ModelChangeDataType.FIELD_COORDINATE),
  FIELD_MODEL_REMOVE_PLAYER_MARKER(30, "fieldModelRemovePlayerMarker", ModelChangeDataType.PLAYER_MARKER),
  FIELD_MODEL_REMOVE_PUSHBACK_SQUARE(31, "fieldModelRemovePushbackSquare", ModelChangeDataType.PUSHBACK_SQUARE),
  FIELD_MODEL_REMOVE_TRACK_NUMBER(32, "fieldModelRemoveTrackNumber", ModelChangeDataType.TRACK_NUMBER),
  FIELD_MODEL_SET_BALL_COORDINATE(33, "fieldModelSetBallCoordinate", ModelChangeDataType.FIELD_COORDINATE),
  FIELD_MODEL_SET_BALL_IN_PLAY(34, "fieldModelSetBallInPlay", ModelChangeDataType.BOOLEAN),
  FIELD_MODEL_SET_BALL_MOVING(35, "fieldModelSetBallMoving", ModelChangeDataType.BOOLEAN),
  FIELD_MODEL_SET_BOMB_COORDINATE(36, "fieldModelSetBombCoordinate", ModelChangeDataType.FIELD_COORDINATE),
  FIELD_MODEL_SET_BOMB_MOVING(37, "fieldModelSetBombMoving", ModelChangeDataType.BOOLEAN),
  FIELD_MODEL_SET_PLAYER_COORDINATE(38, "fieldModelSetPlayerCoordinate", ModelChangeDataType.FIELD_COORDINATE),
  FIELD_MODEL_SET_PLAYER_STATE(39, "fieldModelSetPlayerState", ModelChangeDataType.PLAYER_STATE),
  FIELD_MODEL_SET_RANGE_RULER(40, "fieldModelSetRangeRuler", ModelChangeDataType.RANGE_RULER),
  FIELD_MODEL_SET_WEATHER(41, "fieldModelSetWeather", ModelChangeDataType.WEATHER),

	GAME_SET_CONCESSION_POSSIBLE(42, "gameSetConcessionPossible", ModelChangeDataType.BOOLEAN),
	GAME_SET_DEFENDER_ACTION(43, "gameSetDefenderAction", ModelChangeDataType.PLAYER_ACTION),
	GAME_SET_DEFENDER_ID(44, "gameSetDefenderId", ModelChangeDataType.STRING),
	GAME_SET_DIALOG_PARAMETER(45, "gameSetDialogParameter", ModelChangeDataType.DIALOG_PARAMETER),
	GAME_SET_FINISHED(46, "gameSetFinished", ModelChangeDataType.DATE),
	GAME_SET_HALF(47, "gameSetHalf", ModelChangeDataType.INTEGER),
	GAME_SET_HOME_FIRST_OFFENSE(48, "gameSetHomeFirstOffense", ModelChangeDataType.BOOLEAN),
	GAME_SET_HOME_PLAYING(49, "gameSetHomePlaying", ModelChangeDataType.BOOLEAN),
	GAME_SET_ID(50, "gameSetId", ModelChangeDataType.LONG),
	GAME_SET_PASS_COORDINATE(51, "gameSetPassCoordinate", ModelChangeDataType.FIELD_COORDINATE),
	GAME_SET_SCHEDULED(52, "gameSetScheduled", ModelChangeDataType.DATE),
	GAME_SET_SETUP_OFFENSE(53, "gameSetSetupOffense", ModelChangeDataType.BOOLEAN),
	GAME_SET_STARTED(54, "gameSetStarted", ModelChangeDataType.DATE),
	GAME_SET_TESTING(55, "gameSetTesting", ModelChangeDataType.BOOLEAN),
	GAME_SET_THROWER_ID(56, "gameSetThrowerId", ModelChangeDataType.STRING),
	GAME_SET_THROWER_ACTION(57, "gameSetThrowerAction", ModelChangeDataType.PLAYER_ACTION),
	GAME_SET_TIMEOUT_ENFORCED(58, "gameSetTimeoutEnforced", ModelChangeDataType.BOOLEAN),
	GAME_SET_TIMEOUT_POSSIBLE(59, "gameSetTimeoutPossible", ModelChangeDataType.BOOLEAN),
	GAME_SET_TURN_MODE(60, "gameSetTurnMode", ModelChangeDataType.TURN_MODE),
	GAME_SET_TURN_TIME(61, "gameSetTurnTime", ModelChangeDataType.LONG),
	GAME_SET_WAITING_FOR_OPPONENT(62, "gameSetWaitingForOpponent", ModelChangeDataType.BOOLEAN),
	
	GAME_OPTIONS_ADD_OPTION(63, "gameOptionsAddOption", ModelChangeDataType.GAME_OPTION),
	
  INDUCEMENT_SET_ACTIVATE_CARD(64, "inducementSetActivateCard", ModelChangeDataType.CARD),
  INDUCEMENT_SET_ADD_AVAILABLE_CARD(65, "inducementSetAddAvailableCard", ModelChangeDataType.CARD),
  INDUCEMENT_SET_ADD_INDUCEMENT(66, "inducementSetAddInducement", ModelChangeDataType.INDUCEMENT),
  INDUCEMENT_SET_DEACTIVATE_CARD(67, "inducementSetDeactivateCard", ModelChangeDataType.CARD),
  INDUCEMENT_SET_REMOVE_AVAILABLE_CARD(68, "inducementSetRemoveAvailableCard", ModelChangeDataType.CARD),
  INDUCEMENT_SET_REMOVE_INDUCEMENT(69, "inducementSetRemoveInducement", ModelChangeDataType.INDUCEMENT),
  
  PLAYER_RESULT_SET_BLOCKS(70, "playerResultSetBlocks", ModelChangeDataType.INTEGER),
  PLAYER_RESULT_SET_CASUALTIES(71, "playerResultSetCasualties", ModelChangeDataType.INTEGER),
  PLAYER_RESULT_SET_COMPLETIONS(72, "playerResultSetCompletions", ModelChangeDataType.INTEGER),
  PLAYER_RESULT_SET_CURRENT_SPPS(73, "playerResultSetCurrentSpps", ModelChangeDataType.INTEGER),
  PLAYER_RESULT_SET_DEFECTING(74, "playerResultSetDefecting", ModelChangeDataType.BOOLEAN),
  PLAYER_RESULT_SET_FOULS(75, "playerResultSetFouls", ModelChangeDataType.INTEGER),
  PLAYER_RESULT_SET_HAS_USED_SECRET_WEAPON(76, "playerResultSetHasUsedSecretWeapon", ModelChangeDataType.BOOLEAN),
  PLAYER_RESULT_SET_INTERCEPTIONS(77, "playerResultSetInterceptions", ModelChangeDataType.INTEGER),
  PLAYER_RESULT_SET_PASSING(78, "playerResultSetPassing", ModelChangeDataType.INTEGER),
  PLAYER_RESULT_SET_PLAYER_AWARDS(79, "playerResultSetPlayerAwards", ModelChangeDataType.INTEGER),
  PLAYER_RESULT_SET_RUSHING(80, "playerResultSetRushing", ModelChangeDataType.INTEGER),
  PLAYER_RESULT_SET_SEND_TO_BOX_BY_PLAYER_ID(81, "playerResultSetSendToBoxByPlayerId", ModelChangeDataType.STRING),
  PLAYER_RESULT_SET_SEND_TO_BOX_HALF(82, "playerResultSetSendToBoxHalf", ModelChangeDataType.INTEGER),
  PLAYER_RESULT_SET_SEND_TO_BOX_REASON(83, "playerResultSetSendToBoxReason", ModelChangeDataType.SEND_TO_BOX_REASON),
  PLAYER_RESULT_SET_SEND_TO_BOX_TURN(84, "playerResultSetSendToBoxTurn", ModelChangeDataType.INTEGER),
  PLAYER_RESULT_SET_SERIOUS_INJURY(85, "playerResultSetSeriousInjury", ModelChangeDataType.SERIOUS_INJURY),
  PLAYER_RESULT_SET_SERIOUS_INJURY_DECAY(86, "playerResultSetSeriousInjuryDecay", ModelChangeDataType.SERIOUS_INJURY),
  PLAYER_RESULT_SET_TOUCHDOWNS(87, "playerResultSetTouchdowns", ModelChangeDataType.INTEGER),
  PLAYER_RESULT_SET_TURNS_PLAYED(88, "playerResultSetTurnsPlayed", ModelChangeDataType.INTEGER),

	TEAM_RESULT_SET_CONCEDED(89, "teamResultSetConceded", ModelChangeDataType.BOOLEAN),
  TEAM_RESULT_SET_FAME(90, "teamResultSetFame", ModelChangeDataType.INTEGER),
  TEAM_RESULT_SET_BADLY_HURT_SUFFERED(91, "teamResultSetBadlyHurtSuffered", ModelChangeDataType.INTEGER),
  TEAM_RESULT_SET_FAN_FACTOR_MODIFIER(92, "teamResultSetFanFactorModifier", ModelChangeDataType.INTEGER),
  TEAM_RESULT_SET_PETTY_CASH_TRANSFERRED(93, "teamResultSetPettyCashTransferred", ModelChangeDataType.INTEGER),
  TEAM_RESULT_SET_PETTY_CASH_USED(94, "teamResultSetPettyCashUsed", ModelChangeDataType.INTEGER),
  TEAM_RESULT_SET_RAISED_DEAD(95, "teamResultSetRaisedDead", ModelChangeDataType.INTEGER),
  TEAM_RESULT_SET_RIP_SUFFERED(96, "teamResultSetRipSuffered", ModelChangeDataType.INTEGER),
  TEAM_RESULT_SET_SCORE(97, "teamResultSetScore", ModelChangeDataType.INTEGER),
  TEAM_RESULT_SET_SERIOUS_INJURY_SUFFERED(98, "teamResultSetSeriousInjurySuffered", ModelChangeDataType.INTEGER),
  TEAM_RESULT_SET_SPECTATORS(99, "teamResultSetSpectators", ModelChangeDataType.INTEGER),
  TEAM_RESULT_SET_SPIRALLING_EXPENSES(100, "teamResultSetSpirallingExpenses", ModelChangeDataType.INTEGER),
  TEAM_RESULT_SET_TEAM_VALUE(101, "teamResultSetTeamValue", ModelChangeDataType.INTEGER),
  TEAM_RESULT_SET_WINNINGS(102, "teamResultSetWinnings", ModelChangeDataType.INTEGER),
  
  TURN_DATA_SET_APOTHECARIES(103, "turnDataSetApothecaries", ModelChangeDataType.INTEGER),
  TURN_DATA_SET_BLITZ_USED(104, "turnDataSetBlitzUsed", ModelChangeDataType.BOOLEAN),
  TURN_DATA_SET_FIRST_TURN_AFTER_KICKOFF(105, "turnDataSetFirstTurnAfterKickoff", ModelChangeDataType.BOOLEAN),
  TURN_DATA_SET_FOUL_USED(106, "turnDataSetFoulUsed", ModelChangeDataType.BOOLEAN),
  TURN_DATA_SET_HAND_OVER_USED(107, "turnDataSetHandOverUsed", ModelChangeDataType.BOOLEAN),
  TURN_DATA_SET_LEADER_STATE(108, "turnDataSetLeaderState", ModelChangeDataType.LEADER_STATE),
  TURN_DATA_SET_PASS_USED(109, "turnDataSetPassUsed", ModelChangeDataType.BOOLEAN),
  TURN_DATA_SET_RE_ROLLS(110, "turnDataSetReRolls", ModelChangeDataType.INTEGER),
  TURN_DATA_SET_RE_ROLL_USED(111, "turnDataSetReRollUsed", ModelChangeDataType.BOOLEAN),
  TURN_DATA_SET_TURN_NR(112, "turnDataSetTurnNr", ModelChangeDataType.INTEGER),
  TURN_DATA_SET_TURN_STARTED(113, "turnDataSetTurnStarted", ModelChangeDataType.BOOLEAN);
	
	private int fId;
	private String fName;
  private ModelChangeDataType fDataType;

  private ModelChangeId(int pId, String pName, ModelChangeDataType pDataType) {
  	fId = pId;
  	fName = pName;
  	fDataType = pDataType;
  }
  
  public int getId() {
	  return fId;
  }
  
  public String getName() {
    return fName;
  }

  public ModelChangeDataType getDataType() {
    return fDataType;
  }
  
  // JSON serialization
  
  public JsonValue toJsonValue(Object pValue) {
    return getDataType().toJsonValue(pValue);
  }
  
  public Object fromJsonValue(JsonValue pJsonValue) {
    return getDataType().fromJsonValue(pJsonValue);
  }
      
}