package com.balancedbytes.games.ffb.server.step;

/**
 * Parameters used by Steps.
 * 
 * @author Kalimar
 */
public enum StepParameterKey {

	ADMIN_MODE, // Boolean
	APOTHECARY_MODE, // ApothecaryMode
	ARGUE_THE_CALL_SUCCESSFUL, // Boolean
	BALL_AND_CHAIN_GFI, // Boolean
	BLOCK_DEFENDER_ID, // String
	BLOCK_TARGETS, // List<Target>
	BLOCK_RESULT, // BlockResult
	BLOCK_ROLL, // int[]
	BOMB_EXPLODED, // Boolean
	BOMB_OUT_OF_BOUNDS, // Boolean
	CARD, // Card
	CATCH_SCATTER_THROW_IN_MODE, // CatchScatterThrowinMode
	CATCHER_ID, // String
	CHOOSING_TEAM_ID, // String
	CLIENT_RECEIVE_CHOICE, // Boolean
	CONSUME_PARAMETER, // Set<StepParameterKey>
	COORDINATE_FROM, // FieldCoordinate
	COORDINATE_TO, // FieldCoordinate
	DEFENDER_POSITION, // FieldCoordinate
	DEFENDER_PUSHED, // Boolean
	DICE_INDEX, // Integer
	DISPATCH_PLAYER_ACTION, // PlayerAction
	DODGE_ROLL, // Integer
	DONT_DROP_FUMBLE, // Boolean
	DROP_THROWN_PLAYER, // Boolean
	END_INDUCEMENT_PHASE, // Boolean
	END_PLAYER_ACTION, // Boolean
	END_TURN, // Boolean
	FEEDING_ALLOWED, // Boolean
	FOLLOWUP_CHOICE, // Boolean
	FOUL_DEFENDER_ID, // String
	FOULER_HAS_BALL, // String
	GAZE_VICTIM_ID, // String
	GOTO_LABEL, // String
	GOTO_LABEL_ON_BLITZ, // String
	GOTO_LABEL_ON_DISPATCH, // String
	GOTO_LABEL_ON_DODGE, // String
	GOTO_LABEL_ON_END, // String
	GOTO_LABEL_ON_FAILURE, // String
	GOTO_LABEL_ON_FALL_DOWN, // String
	GOTO_LABEL_ON_HAIL_MARY_PASS, // String
	GOTO_LABEL_ON_HAND_OVER, // String
	GOTO_LABEL_ON_JUGGERNAUT, // String
	GOTO_LABEL_ON_MISSED_PASS, // String
	GOTO_LABEL_ON_PUSHBACK, // String
	GOTO_LABEL_ON_SAVED_FUMBLE,
	GOTO_LABEL_ON_SUCCESS, // String
	HAIL_MARY_PASS, // Boolean
	HANDLE_RECEIVING_TEAM, // Boolean
	HOME_TEAM, // Boolean
	INCREMENT, // int
	INDUCEMENT_GOLD_AWAY, // Integer
	INDUCEMENT_GOLD_HOME, // Integer
	INDUCEMENT_PHASE, // InducementPhase
	INDUCEMENT_USE, // InducementUse
	INJURY_RESULT, // InjuryResult
	INJURY_TYPE, // InjuryType
	INTERCEPTOR_ID, // String
	IS_KICKED_PLAYER, // Boolean
	KICKED_PLAYER_ID, // String
	KICKED_PLAYER_STATE, // PlayerState
	KICKED_PLAYER_HAS_BALL, // Boolean
	KICKED_PLAYER_COORDINATE, // FieldCoordinate
	KICKING_PLAYER_COORDINATE, // FieldCoordinate
	KICKOFF_BOUNDS, // FieldCoordinateBounds
	KICKOFF_RESULT, // KickoffResult
	KICKOFF_START_COORDINATE, // FieldCoordinate
	KTM_MODIFIER, // Integer
	MOVE_STACK, // FieldCoordinate[]
	MOVE_START, // FieldCoordinate
	MULTI_BLOCK_DEFENDER_ID, // String
	NR_OF_DICE, // Integer
	OLD_DEFENDER_STATE, // PlayerState
	PASS_ACCURATE, // Boolean
	PASS_DEVIATES, // Boolean
	PASS_FUMBLE, // Boolean
	PLAYER_ID, // String
	PLAYER_ID_TO_REMOVE, // String
	PLAYER_ID_DAUNTLESS_SUCCESS, // String
	ATTACKER_POISONED, // Boolean
	DEFENDER_POISONED, // Boolean
	RE_ROLL_USED, // Boolean
	ROLL_FOR_EFFECT, // Boolean
	SPECIAL_EFFECT, // SpecialEffect
	STARTING_PUSHBACK_SQUARE, // PushbackSquare
	SUPPRESS_EXTRA_EFFECT_HANDLING, // Boolean
	SHOW_NAME_IN_REPORT, // Boolean
	SUCCESSFUL_DAUNTLESS, // Boolean
	TARGET_COORDINATE, // FieldCoordinate
	THROW_IN_COORDINATE, // FieldCoordinate
	THROWN_PLAYER_COORDINATE, // FieldCoordinate
	THROWN_PLAYER_HAS_BALL, // Boolean
	THROWN_PLAYER_ID, // String
	THROWN_PLAYER_STATE, // PlayerState
	THROW_SCATTER, // Boolean
	TOUCHBACK, // Boolean
	UPDATE_PERSISTENCE, // Boolean
	USING_BREAK_TACKLE, // Boolean
	USING_CHAINSAW, // Boolean
	USING_DIVING_TACKLE, // Boolean
	USING_PILING_ON, // Boolean,
	USING_STAB // Boolean

}
