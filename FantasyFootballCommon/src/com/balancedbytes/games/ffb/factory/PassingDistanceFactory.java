package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.GameOptions;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.PASSING_DISTANCE)
@RulesCollection(Rules.COMMON)
public class PassingDistanceFactory implements INamedObjectFactory {

	public PassingDistance forName(String pName) {
		for (PassingDistance distance : PassingDistance.values()) {
			if (distance.getName().equalsIgnoreCase(pName)) {
				return distance;
			}
		}
		return null;
	}

	public PassingDistance forShortcut(char pShortcut) {
		for (PassingDistance distance : PassingDistance.values()) {
			if (distance.getShortcut() == pShortcut) {
				return distance;
			}
		}
		return null;
	}

	@Override
	public void initialize(Rules rules, GameOptions options) {
		// TODO Auto-generated method stub
		
	}

}
