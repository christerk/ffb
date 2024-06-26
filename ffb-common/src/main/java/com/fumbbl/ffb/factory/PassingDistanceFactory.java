package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PassingDistance;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;

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
	public void initialize(Game game) {
		// TODO Auto-generated method stub
		
	}

}
