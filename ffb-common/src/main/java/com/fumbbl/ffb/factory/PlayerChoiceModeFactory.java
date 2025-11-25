package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerChoiceMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;

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
	}

}
