package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.CatchScatterThrowInMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.CATCH_SCATTER_THROWIN_MODE)
@RulesCollection(Rules.COMMON)
public class CatchScatterThrowInModeFactory implements INamedObjectFactory {

	public CatchScatterThrowInMode forName(String pName) {
		for (CatchScatterThrowInMode mode : CatchScatterThrowInMode.values()) {
			if (mode.getName().equalsIgnoreCase(pName)) {
				return mode;
			}
		}
		return null;
	}

	@Override
	public void initialize(Game game) {
		// TODO Auto-generated method stub
		
	}

}
