package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Game;

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
