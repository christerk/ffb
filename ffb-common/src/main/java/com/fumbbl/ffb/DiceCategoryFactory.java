package com.fumbbl.ffb;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;

public class DiceCategoryFactory {
	
	public static DiceCategory forCommandString(String command, Game game, Team team) {
		DiceCategory diceCategory = null;
		if(DiceCategory.isCommandValid(command, game, team))
		{
			diceCategory = new DiceCategory();
			diceCategory.parseCommand(command, game, team);
		}
		else if (DirectionDiceCategory.isCommandValid(command, game, team)) {
			diceCategory = new DirectionDiceCategory();
			diceCategory.parseCommand(command, game, team);
		}
		else if (BlockDiceCategory.isCommandValid(command, game, team)) {
			diceCategory = new BlockDiceCategory();
			diceCategory.parseCommand(command, game, team);
		}

		return diceCategory;
	}
	
	
	public static DiceCategory forDiceSize(int diceSize) {
		return new DiceCategory(diceSize);
	}

}
