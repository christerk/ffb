package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.LeaderState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;

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
	public void initialize(Game game) {
		// TODO Auto-generated method stub
		
	}

}
