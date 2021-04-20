package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.PLAYER_ACTION)
@RulesCollection(Rules.COMMON)
public class PlayerActionFactory implements INamedObjectFactory {

	public PlayerAction forName(String pName) {
		for (PlayerAction action : PlayerAction.values()) {
			if (action.getName().equalsIgnoreCase(pName)) {
				return action;
			}
		}
		return null;
	}

	@Override
	public void initialize(Game game) {
		// TODO Auto-generated method stub
		
	}

}
