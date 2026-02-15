package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PushbackMode;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.PUSHBACK_MODE)
@RulesCollection(Rules.COMMON)
public class PushbackModeFactory implements INamedObjectFactory {

	public PushbackMode forName(String pName) {
		for (PushbackMode mode : PushbackMode.values()) {
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
