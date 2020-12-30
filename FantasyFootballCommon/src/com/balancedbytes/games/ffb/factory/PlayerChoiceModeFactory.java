package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.PlayerChoiceMode;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.PLAYER_CHOICE_MODE)
@RulesCollection(Rules.COMMON)
public class PlayerChoiceModeFactory implements INamedObjectFactory {

	public PlayerChoiceMode forName(String pName) {
		for (PlayerChoiceMode type : PlayerChoiceMode.values()) {
			if (type.getName().equalsIgnoreCase(pName)) {
				return type;
			}
		}
		return null;
	}

	@Override
	public void initialize(Game game) {
		// TODO Auto-generated method stub
		
	}

}
