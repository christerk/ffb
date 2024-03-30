package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.APOTHECARY_MODE)
@RulesCollection(Rules.COMMON)
public class ApothecaryModeFactory implements INamedObjectFactory {

	public ApothecaryMode forName(String pName) {
		for (ApothecaryMode mode : ApothecaryMode.values()) {
			if (mode.getName().equalsIgnoreCase(pName)) {
				return mode;
			}
		}
		return null;
	}

	@Override
	public void initialize(Game game) {
	}

}
