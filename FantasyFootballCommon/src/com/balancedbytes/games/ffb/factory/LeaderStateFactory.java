package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.LeaderState;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.GameOptions;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.LEADER_STATE)
@RulesCollection(Rules.COMMON)
public class LeaderStateFactory implements INamedObjectFactory {

	public LeaderState forName(String pName) {
		for (LeaderState state : LeaderState.values()) {
			if (state.getName().equalsIgnoreCase(pName)) {
				return state;
			}
		}
		return null;
	}

	@Override
	public void initialize(GameOptions options) {
		// TODO Auto-generated method stub
		
	}

}
