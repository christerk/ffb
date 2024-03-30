package com.fumbbl.ffb.option;

import com.fumbbl.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
public class UtilGameOption {

	public static boolean isOptionEnabled(Game pGame, GameOptionId pOptionId) {
		if ((pGame == null) || (pOptionId == null)) {
			return false;
		}
		GameOptionBoolean gameOption = (GameOptionBoolean) pGame.getOptions().getOptionWithDefault(pOptionId);
		return gameOption != null && gameOption.isEnabled();
	}

	public static int getIntOption(Game pGame, GameOptionId pOptionId) {
		if ((pGame == null) || (pOptionId == null)) {
			return 0;
		}
		GameOptionInt gameOption = (GameOptionInt) pGame.getOptions().getOptionWithDefault(pOptionId);
		return (gameOption != null) ? gameOption.getValue() : 0;
	}

}
