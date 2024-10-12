package com.fumbbl.ffb.client;

import com.fumbbl.ffb.IClientProperty;

/**
 * @author Kalimar
 */
public enum ActionKey {

	PLAYER_MOVE_NORTH(IClientProperty.KEY_PLAYER_MOVE_NORTH),
	PLAYER_MOVE_NORTHEAST(IClientProperty.KEY_PLAYER_MOVE_NORTHEAST),
	PLAYER_MOVE_EAST(IClientProperty.KEY_PLAYER_MOVE_EAST),
	PLAYER_MOVE_SOUTHEAST(IClientProperty.KEY_PLAYER_MOVE_SOUTHEAST),
	PLAYER_MOVE_SOUTH(IClientProperty.KEY_PLAYER_MOVE_SOUTH),
	PLAYER_MOVE_SOUTHWEST(IClientProperty.KEY_PLAYER_MOVE_SOUTHWEST),
	PLAYER_MOVE_WEST(IClientProperty.KEY_PLAYER_MOVE_WEST),
	PLAYER_MOVE_NORTHWEST(IClientProperty.KEY_PLAYER_MOVE_NORTHWEST),

	PLAYER_SELECT(IClientProperty.KEY_PLAYER_SELECT), PLAYER_CYCLE_RIGHT(IClientProperty.KEY_PLAYER_CYCLE_RIGHT),
	PLAYER_CYCLE_LEFT(IClientProperty.KEY_PLAYER_CYCLE_LEFT),

	PLAYER_ACTION_BLOCK(IClientProperty.KEY_PLAYER_ACTION_BLOCK),
	PLAYER_ACTION_BLITZ(IClientProperty.KEY_PLAYER_ACTION_BLITZ),
	PLAYER_ACTION_FOUL(IClientProperty.KEY_PLAYER_ACTION_FOUL),
	PLAYER_ACTION_MOVE(IClientProperty.KEY_PLAYER_ACTION_MOVE),
	PLAYER_ACTION_STAND_UP(IClientProperty.KEY_PLAYER_ACTION_STAND_UP),
	PLAYER_ACTION_HAND_OVER(IClientProperty.KEY_PLAYER_ACTION_HAND_OVER),
	PLAYER_ACTION_PASS(IClientProperty.KEY_PLAYER_ACTION_PASS),
	PLAYER_ACTION_JUMP(IClientProperty.KEY_PLAYER_ACTION_JUMP),
	PLAYER_ACTION_END_MOVE(IClientProperty.KEY_PLAYER_ACTION_END_MOVE),
	PLAYER_ACTION_STAB(IClientProperty.KEY_PLAYER_ACTION_STAB),
	PLAYER_ACTION_CHAINSAW(IClientProperty.KEY_PLAYER_ACTION_CHAINSAW),
	PLAYER_ACTION_GAZE(IClientProperty.KEY_PLAYER_ACTION_GAZE),
	PLAYER_ACTION_GAZE_ZOAT(IClientProperty.KEY_PLAYER_ACTION_GAZE_ZOAT),
	PLAYER_ACTION_FUMBLEROOSKIE(IClientProperty.KEY_PLAYER_ACTION_FUMBLEROOSKIE),
	PLAYER_ACTION_PROJECTILE_VOMIT(IClientProperty.KEY_PLAYER_ACTION_PROJECTILE_VOMIT),
	PLAYER_ACTION_RANGE_GRID(IClientProperty.KEY_PLAYER_ACTION_RANGE_GRID),
	PLAYER_ACTION_HAIL_MARY_PASS(IClientProperty.KEY_PLAYER_ACTION_HAIL_MARY_PASS),
	PLAYER_ACTION_MULTIPLE_BLOCK(IClientProperty.KEY_PLAYER_ACTION_MULTIPLE_BLOCK),
	PLAYER_ACTION_FRENZIED_RUSH(IClientProperty.KEY_PLAYER_ACTION_FRENZIED_RUSH),
	PLAYER_ACTION_SHOT_TO_NOTHING(IClientProperty.KEY_PLAYER_ACTION_SHOT_TO_NOTHING),
	PLAYER_ACTION_SHOT_TO_NOTHING_BOMB(IClientProperty.KEY_PLAYER_ACTION_SHOT_TO_NOTHING_BOMB),
	PLAYER_ACTION_TREACHEROUS(IClientProperty.KEY_PLAYER_ACTION_TREACHEROUS),
	PLAYER_ACTION_WISDOM(IClientProperty.KEY_PLAYER_ACTION_WISDOM),
	PLAYER_ACTION_BEER_BARREL_BASH(IClientProperty.KEY_PLAYER_ACTION_BEER_BARREL_BASH),
	PLAYER_ACTION_RAIDING_PARTY(IClientProperty.KEY_PLAYER_ACTION_RAIDING_PARTY),
	PLAYER_ACTION_LOOK_INTO_MY_EYES(IClientProperty.KEY_PLAYER_ACTION_LOOK_INTO_MY_EYES),
	PLAYER_ACTION_BALEFUL_HEX(IClientProperty.KEY_PLAYER_ACTION_BALEFUL_HEX),
	PLAYER_ACTION_HIT_AND_RUN(IClientProperty.KEY_PLAYER_ACTION_HIT_AND_RUN),
	PLAYER_ACTION_ALL_YOU_CAN_EAT(IClientProperty.KEY_PLAYER_ACTION_ALL_YOU_CAN_EAT),
	PLAYER_ACTION_KICK_EM_BLOCK(IClientProperty.KEY_PLAYER_ACTION_KICK_EM_BLOCK),
	PLAYER_ACTION_KICK_EM_BLITZ(IClientProperty.KEY_PLAYER_ACTION_KICK_EM_BLITZ),
	PLAYER_ACTION_GORED(IClientProperty.KEY_PLAYER_ACTION_GORED),
	PLAYER_ACTION_BLACK_INK(IClientProperty.KEY_PLAYER_ACTION_BLACK_INK),
	PLAYER_ACTION_CATCH_OF_THE_DAY(IClientProperty.KEY_PLAYER_ACTION_CATCH_OF_THE_DAY),
	PLAYER_ACTION_BOUNDING_LEAP(IClientProperty.KEY_PLAYER_ACTION_BOUNDING_LEAP),
	PLAYER_ACTION_BREATHE_FIRE(IClientProperty.KEY_PLAYER_ACTION_BREATHE_FIRE),
	PLAYER_ACITON_THEN_I_STARTED_BLASTIN(IClientProperty.KEY_PLAYER_ACTION_THEN_I_STARTED_BLASTIN),
	TOOLBAR_TURN_END(IClientProperty.KEY_TOOLBAR_TURN_END),
	TOOLBAR_ILLEGAL_PROCEDURE(IClientProperty.KEY_TOOLBAR_ILLEGAL_PROCEDURE),

	RESIZE_LARGER(IClientProperty.KEY_RESIZE_LARGER),
	RESIZE_SMALLER(IClientProperty.KEY_RESIZE_SMALLER),
	RESIZE_RESET(IClientProperty.KEY_RESIZE_RESET),
	RESIZE_SMALLER2(IClientProperty.KEY_RESIZE_SMALLER2),

	MENU_SETUP_LOAD(IClientProperty.KEY_MENU_SETUP_LOAD), MENU_SETUP_SAVE(IClientProperty.KEY_MENU_SETUP_SAVE),
	MENU_REPLAY(IClientProperty.KEY_MENU_REPLAY);

	@SuppressWarnings({"FieldCanBeLocal", "unused"})
	private final String fPropertyName;

	ActionKey(String pPropertyName) {
		fPropertyName = pPropertyName;
	}


}
