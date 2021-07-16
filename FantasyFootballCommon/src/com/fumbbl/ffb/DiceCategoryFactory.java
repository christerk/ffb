package com.fumbbl.ffb;

import com.fumbbl.ffb.factory.INamedObjectFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;

public class DiceCategoryFactory implements INamedObjectFactory {

	@Override
	public INamedObject forName(String pName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static DiceCategory forCommandString(String command, Game game, Team team) {
		DiceCategory diceCategory = null;
		if(DiceCategory.IsCommandValid(command, game, team))
		{
			diceCategory = new DiceCategory();
			diceCategory.ParseCommand(command, game, team);
		}
		else if (DirectionDiceCategory.IsCommandValid(command, game, team)) {
			diceCategory = new DirectionDiceCategory();
			diceCategory.ParseCommand(command, game, team);
		}
		else if (BlockDiceCategory.IsCommandValid(command, game, team)) {
			diceCategory = new BlockDiceCategory();
			diceCategory.ParseCommand(command, game, team);
		}

		return diceCategory;
	}
	
	
	public static DiceCategory forDiceSize(int diceSize) {
		return new DiceCategory(diceSize);
	}

	@Override
	public void initialize(Game game) {
		// TODO Auto-generated method stub	
	}
	
}
