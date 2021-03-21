package com.balancedbytes.games.ffb.model.change;

import com.balancedbytes.games.ffb.INamedObject;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.eclipsesource.json.JsonValue;

/**
 * 
 * @author Kalimar
 */
public enum ModelChangeId implements INamedObject {

	ACTING_PLAYER_MARK_SKILL_USED("actingPlayerMarkSkillUsed", ModelChangeDataType.SKILL),
	ACTING_PLAYER_SET_CURRENT_MOVE("actingPlayerSetCurrentMove", ModelChangeDataType.INTEGER),
	ACTING_PLAYER_SET_DODGING("actingPlayerSetDodging", ModelChangeDataType.BOOLEAN),
	ACTING_PLAYER_SET_GOING_FOR_IT("actingPlayerSetGoingForIt", ModelChangeDataType.BOOLEAN),
	ACTING_PLAYER_SET_HAS_BLOCKED("actingPlayerSetHasBlocked", ModelChangeDataType.BOOLEAN),
	ACTING_PLAYER_SET_HAS_FED("actingPlayerSetHasFed", ModelChangeDataType.BOOLEAN),
	ACTING_PLAYER_SET_HAS_FOULED("actingPlayerSetHasFouled", ModelChangeDataType.BOOLEAN),
	ACTING_PLAYER_SET_HAS_MOVED("actingPlayerSetHasMoved", ModelChangeDataType.BOOLEAN),
	ACTING_PLAYER_SET_HAS_PASSED("actingPlayerSetHasPassed", ModelChangeDataType.BOOLEAN),
	ACTING_PLAYER_SET_JUMPING("actingPlayerSetLeaping", ModelChangeDataType.BOOLEAN),
	ACTING_PLAYER_SET_PLAYER_ACTION("actingPlayerSetPlayerAction", ModelChangeDataType.PLAYER_ACTION),
	ACTING_PLAYER_SET_PLAYER_ID("actingPlayerSetPlayerId", ModelChangeDataType.STRING),
	ACTING_PLAYER_SET_STANDING_UP("actingPlayerSetStandingUp", ModelChangeDataType.BOOLEAN),
	ACTING_PLAYER_SET_STRENGTH("actingPlayerSetStrength", ModelChangeDataType.INTEGER),
	ACTING_PLAYER_SET_SUFFERING_ANIMOSITY("actingPlayerSetSufferingAnimosity", ModelChangeDataType.BOOLEAN),
	ACTING_PLAYER_SET_SUFFERING_BLOOD_LUST("actingPlayerSetSufferingBloodLust", ModelChangeDataType.BOOLEAN),

	FIELD_MODEL_ADD_BLOOD_SPOT("fieldModelAddBloodSpot", ModelChangeDataType.BLOOD_SPOT),
	FIELD_MODEL_ADD_CARD("fieldModelAddCard", ModelChangeDataType.CARD),
	FIELD_MODEL_ADD_CARD_EFFECT("fieldModelAddCardEffect", ModelChangeDataType.CARD_EFFECT),
	FIELD_MODEL_ADD_DICE_DECORATION("fieldModelAddDiceDecoration", ModelChangeDataType.DICE_DECORATION),
	FIELD_MODEL_ADD_FIELD_MARKER("fieldModelAddFieldMarker", ModelChangeDataType.FIELD_MARKER),
	FIELD_MODEL_ADD_MOVE_SQUARE("fieldModelAddMoveSquare", ModelChangeDataType.MOVE_SQUARE),
	FIELD_MODEL_ADD_PLAYER_MARKER("fieldModelAddPlayerMarker", ModelChangeDataType.PLAYER_MARKER),
	FIELD_MODEL_ADD_PUSHBACK_SQUARE("fieldModelAddPushbackSquare", ModelChangeDataType.PUSHBACK_SQUARE),
	FIELD_MODEL_ADD_TRACK_NUMBER("fieldModelAddTrackNumber", ModelChangeDataType.TRACK_NUMBER),
	FIELD_MODEL_KEEP_DEACTIVATED_CARD("fieldModelKeepDeactivatedCard", ModelChangeDataType.CARD),
	FIELD_MODEL_REMOVE_CARD("fieldModelRemoveCard", ModelChangeDataType.CARD),
	FIELD_MODEL_REMOVE_CARD_EFFECT("fieldModelRemoveCardEffect", ModelChangeDataType.CARD_EFFECT),
	FIELD_MODEL_REMOVE_DICE_DECORATION("fieldModelRemoveDiceDecoration", ModelChangeDataType.DICE_DECORATION),
	FIELD_MODEL_REMOVE_FIELD_MARKER("fieldModelRemoveFieldMarker", ModelChangeDataType.FIELD_MARKER),
	FIELD_MODEL_REMOVE_MOVE_SQUARE("fieldModelRemoveMoveSquare", ModelChangeDataType.MOVE_SQUARE),
	FIELD_MODEL_REMOVE_PLAYER("fieldModelRemovePlayer", ModelChangeDataType.FIELD_COORDINATE),
	FIELD_MODEL_REMOVE_PLAYER_MARKER("fieldModelRemovePlayerMarker", ModelChangeDataType.PLAYER_MARKER),
	FIELD_MODEL_REMOVE_PUSHBACK_SQUARE("fieldModelRemovePushbackSquare", ModelChangeDataType.PUSHBACK_SQUARE),
	FIELD_MODEL_REMOVE_TRACK_NUMBER("fieldModelRemoveTrackNumber", ModelChangeDataType.TRACK_NUMBER),
	FIELD_MODEL_SET_BALL_COORDINATE("fieldModelSetBallCoordinate", ModelChangeDataType.FIELD_COORDINATE),
	FIELD_MODEL_SET_BALL_IN_PLAY("fieldModelSetBallInPlay", ModelChangeDataType.BOOLEAN),
	FIELD_MODEL_SET_BALL_MOVING("fieldModelSetBallMoving", ModelChangeDataType.BOOLEAN),
	FIELD_MODEL_SET_BLITZ_STATE("fieldModelSetBlitzState", ModelChangeDataType.BLITZ_STATE),
	FIELD_MODEL_SET_BOMB_COORDINATE("fieldModelSetBombCoordinate", ModelChangeDataType.FIELD_COORDINATE),
	FIELD_MODEL_SET_BOMB_MOVING("fieldModelSetBombMoving", ModelChangeDataType.BOOLEAN),
	FIELD_MODEL_SET_PLAYER_COORDINATE("fieldModelSetPlayerCoordinate", ModelChangeDataType.FIELD_COORDINATE),
	FIELD_MODEL_SET_PLAYER_STATE("fieldModelSetPlayerState", ModelChangeDataType.PLAYER_STATE),
	FIELD_MODEL_SET_RANGE_RULER("fieldModelSetRangeRuler", ModelChangeDataType.RANGE_RULER),
	FIELD_MODEL_SET_WEATHER("fieldModelSetWeather", ModelChangeDataType.WEATHER),

	GAME_SET_ADMIN_MODE("gameSetAdminMode", ModelChangeDataType.BOOLEAN),
	GAME_SET_CONCESSION_POSSIBLE("gameSetConcessionPossible", ModelChangeDataType.BOOLEAN),
	GAME_SET_DEFENDER_ACTION("gameSetDefenderAction", ModelChangeDataType.PLAYER_ACTION),
	GAME_SET_DEFENDER_ID("gameSetDefenderId", ModelChangeDataType.STRING),
	GAME_SET_DIALOG_PARAMETER("gameSetDialogParameter", ModelChangeDataType.DIALOG_PARAMETER),
	GAME_SET_FINISHED("gameSetFinished", ModelChangeDataType.DATE),
	GAME_SET_HALF("gameSetHalf", ModelChangeDataType.INTEGER),
	GAME_SET_HOME_FIRST_OFFENSE("gameSetHomeFirstOffense", ModelChangeDataType.BOOLEAN),
	GAME_SET_HOME_PLAYING("gameSetHomePlaying", ModelChangeDataType.BOOLEAN),
	GAME_SET_ID("gameSetId", ModelChangeDataType.LONG),
	GAME_SET_LAST_TURN_MODE("gameSetLastTurnMode", ModelChangeDataType.TURN_MODE),
	GAME_SET_PASS_COORDINATE("gameSetPassCoordinate", ModelChangeDataType.FIELD_COORDINATE),
	GAME_SET_SCHEDULED("gameSetScheduled", ModelChangeDataType.DATE),
	GAME_SET_SETUP_OFFENSE("gameSetSetupOffense", ModelChangeDataType.BOOLEAN),
	GAME_SET_STARTED("gameSetStarted", ModelChangeDataType.DATE),
	GAME_SET_TESTING("gameSetTesting", ModelChangeDataType.BOOLEAN),
	GAME_SET_THROWER_ID("gameSetThrowerId", ModelChangeDataType.STRING),
	GAME_SET_THROWER_ACTION("gameSetThrowerAction", ModelChangeDataType.PLAYER_ACTION),
	GAME_SET_TIMEOUT_ENFORCED("gameSetTimeoutEnforced", ModelChangeDataType.BOOLEAN),
	GAME_SET_TIMEOUT_POSSIBLE("gameSetTimeoutPossible", ModelChangeDataType.BOOLEAN),
	GAME_SET_TURN_MODE("gameSetTurnMode", ModelChangeDataType.TURN_MODE),
	GAME_SET_WAITING_FOR_OPPONENT("gameSetWaitingForOpponent", ModelChangeDataType.BOOLEAN),

	GAME_OPTIONS_ADD_OPTION("gameOptionsAddOption", ModelChangeDataType.GAME_OPTION),

	INDUCEMENT_SET_ACTIVATE_CARD("inducementSetActivateCard", ModelChangeDataType.CARD),
	INDUCEMENT_SET_ADD_AVAILABLE_CARD("inducementSetAddAvailableCard", ModelChangeDataType.CARD),
	INDUCEMENT_SET_ADD_INDUCEMENT("inducementSetAddInducement", ModelChangeDataType.INDUCEMENT),
	INDUCEMENT_SET_CARD_CHOICES("inducementSetCardChoices", ModelChangeDataType.CARD_CHOICES),
	INDUCEMENT_SET_DEACTIVATE_CARD("inducementSetDeactivateCard", ModelChangeDataType.CARD),
	INDUCEMENT_SET_REMOVE_AVAILABLE_CARD("inducementSetRemoveAvailableCard", ModelChangeDataType.CARD),
	INDUCEMENT_SET_REMOVE_INDUCEMENT("inducementSetRemoveInducement", ModelChangeDataType.INDUCEMENT),

	PLAYER_RESULT_SET_BLOCKS("playerResultSetBlocks", ModelChangeDataType.INTEGER),
	PLAYER_RESULT_SET_CASUALTIES("playerResultSetCasualties", ModelChangeDataType.INTEGER),
	PLAYER_RESULT_SET_COMPLETIONS("playerResultSetCompletions", ModelChangeDataType.INTEGER),
	PLAYER_RESULT_SET_CURRENT_SPPS("playerResultSetCurrentSpps", ModelChangeDataType.INTEGER),
	PLAYER_RESULT_SET_DEFECTING("playerResultSetDefecting", ModelChangeDataType.BOOLEAN),
	PLAYER_RESULT_SET_FOULS("playerResultSetFouls", ModelChangeDataType.INTEGER),
	PLAYER_RESULT_SET_HAS_USED_SECRET_WEAPON("playerResultSetHasUsedSecretWeapon", ModelChangeDataType.BOOLEAN),
	PLAYER_RESULT_SET_INTERCEPTIONS("playerResultSetInterceptions", ModelChangeDataType.INTEGER),
	PLAYER_RESULT_SET_DEFLECTIONS("playerResultSetDeflections", ModelChangeDataType.INTEGER),
	PLAYER_RESULT_SET_PASSING("playerResultSetPassing", ModelChangeDataType.INTEGER),
	PLAYER_RESULT_SET_PLAYER_AWARDS("playerResultSetPlayerAwards", ModelChangeDataType.INTEGER),
	PLAYER_RESULT_SET_RUSHING("playerResultSetRushing", ModelChangeDataType.INTEGER),
	PLAYER_RESULT_SET_SEND_TO_BOX_BY_PLAYER_ID("playerResultSetSendToBoxByPlayerId", ModelChangeDataType.STRING),
	PLAYER_RESULT_SET_SEND_TO_BOX_HALF("playerResultSetSendToBoxHalf", ModelChangeDataType.INTEGER),
	PLAYER_RESULT_SET_SEND_TO_BOX_REASON("playerResultSetSendToBoxReason", ModelChangeDataType.SEND_TO_BOX_REASON),
	PLAYER_RESULT_SET_SEND_TO_BOX_TURN("playerResultSetSendToBoxTurn", ModelChangeDataType.INTEGER),
	PLAYER_RESULT_SET_SERIOUS_INJURY("playerResultSetSeriousInjury", ModelChangeDataType.SERIOUS_INJURY),
	PLAYER_RESULT_SET_SERIOUS_INJURY_DECAY("playerResultSetSeriousInjuryDecay", ModelChangeDataType.SERIOUS_INJURY),
	PLAYER_RESULT_SET_TOUCHDOWNS("playerResultSetTouchdowns", ModelChangeDataType.INTEGER),
	PLAYER_RESULT_SET_TURNS_PLAYED("playerResultSetTurnsPlayed", ModelChangeDataType.INTEGER),

	TEAM_RESULT_SET_CONCEDED("teamResultSetConceded", ModelChangeDataType.BOOLEAN),
	TEAM_RESULT_SET_FAME("teamResultSetFame", ModelChangeDataType.INTEGER),
	TEAM_RESULT_SET_BADLY_HURT_SUFFERED("teamResultSetBadlyHurtSuffered", ModelChangeDataType.INTEGER),
	TEAM_RESULT_SET_FAN_FACTOR_MODIFIER("teamResultSetFanFactorModifier", ModelChangeDataType.INTEGER),
	TEAM_RESULT_SET_PETTY_CASH_TRANSFERRED("teamResultSetPettyCashTransferred", ModelChangeDataType.INTEGER),
	TEAM_RESULT_SET_PETTY_CASH_USED("teamResultSetPettyCashUsed", ModelChangeDataType.INTEGER),
	TEAM_RESULT_SET_RAISED_DEAD("teamResultSetRaisedDead", ModelChangeDataType.INTEGER),
	TEAM_RESULT_SET_RIP_SUFFERED("teamResultSetRipSuffered", ModelChangeDataType.INTEGER),
	TEAM_RESULT_SET_SCORE("teamResultSetScore", ModelChangeDataType.INTEGER),
	TEAM_RESULT_SET_SERIOUS_INJURY_SUFFERED("teamResultSetSeriousInjurySuffered", ModelChangeDataType.INTEGER),
	TEAM_RESULT_SET_SPECTATORS("teamResultSetSpectators", ModelChangeDataType.INTEGER),
	TEAM_RESULT_SET_SPIRALLING_EXPENSES("teamResultSetSpirallingExpenses", ModelChangeDataType.INTEGER),
	TEAM_RESULT_SET_TEAM_VALUE("teamResultSetTeamValue", ModelChangeDataType.INTEGER),
	TEAM_RESULT_SET_WINNINGS("teamResultSetWinnings", ModelChangeDataType.INTEGER),

	TURN_DATA_SET_APOTHECARIES("turnDataSetApothecaries", ModelChangeDataType.INTEGER),
	TURN_DATA_SET_BLITZ_USED("turnDataSetBlitzUsed", ModelChangeDataType.BOOLEAN),
	TURN_DATA_SET_FIRST_TURN_AFTER_KICKOFF("turnDataSetFirstTurnAfterKickoff", ModelChangeDataType.BOOLEAN),
	TURN_DATA_SET_FOUL_USED("turnDataSetFoulUsed", ModelChangeDataType.BOOLEAN),
	TURN_DATA_SET_HAND_OVER_USED("turnDataSetHandOverUsed", ModelChangeDataType.BOOLEAN),
	TURN_DATA_SET_LEADER_STATE("turnDataSetLeaderState", ModelChangeDataType.LEADER_STATE),
	TURN_DATA_SET_PASS_USED("turnDataSetPassUsed", ModelChangeDataType.BOOLEAN),
	TURN_DATA_SET_RE_ROLLS("turnDataSetReRolls", ModelChangeDataType.INTEGER),
	TURN_DATA_SET_RE_ROLL_USED("turnDataSetReRollUsed", ModelChangeDataType.BOOLEAN),
	TURN_DATA_SET_TURN_NR("turnDataSetTurnNr", ModelChangeDataType.INTEGER),
	TURN_DATA_SET_TURN_STARTED("turnDataSetTurnStarted", ModelChangeDataType.BOOLEAN),
	TURN_DATA_SET_COACH_BANNED("turnDataSetCoachBanned", ModelChangeDataType.BOOLEAN);

	private String fName;
	private ModelChangeDataType fDataType;

	private ModelChangeId(String pName, ModelChangeDataType pDataType) {
		fName = pName;
		fDataType = pDataType;
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

	public Object fromJsonValue(IFactorySource source, JsonValue pJsonValue) {
		return getDataType().fromJsonValue(source, pJsonValue);
	}

}