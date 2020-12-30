package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.PlayerAction;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Game;

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
