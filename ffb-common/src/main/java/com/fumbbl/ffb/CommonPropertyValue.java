package com.fumbbl.ffb;

import java.util.Arrays;
import java.util.List;

/*
	Value must not exceed 40 chars
 */
public interface CommonPropertyValue {
	String SETTING_RE_ROLL_BALL_AND_CHAIN_NEVER = "reRollBallAndChainNever";
	String SETTING_RE_ROLL_BALL_AND_CHAIN_TEAM_MATE = "reRollBallAndChainTeamMate";
	String SETTING_RE_ROLL_BALL_AND_CHAIN_NO_OPPONENT = "reRollBallAndChainNoOpponent";
	String SETTING_RE_ROLL_BALL_AND_CHAIN_ALWAYS = "reRollBallAndChainAlways";
	String SETTING_PLAYER_MARKING_TYPE_MANUAL = "playerMarkingTypeManual";
	String SETTING_PLAYER_MARKING_TYPE_AUTO = "playerMarkingTypeAuto";
	String SETTING_PLAYER_MARKING_TYPE_AUTO_NO_SORT = "playerMarkingTypeAutoNoSort";

	List<String> AUTO_MARKING = Arrays.asList(SETTING_PLAYER_MARKING_TYPE_AUTO,
		SETTING_PLAYER_MARKING_TYPE_AUTO_NO_SORT);
}
