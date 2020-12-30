package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.model.GameOptions;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.SERIOUS_INJURY)
@RulesCollection(Rules.COMMON)
public class SeriousInjuryFactory implements INamedObjectFactory {

	public SeriousInjury forName(String pName) {
		for (SeriousInjury seriousInjury : SeriousInjury.values()) {
			if (seriousInjury.getName().equals(pName)) {
				return seriousInjury;
			}
		}
		return null;
	}

	@Override
	public void initialize(Rules rules, GameOptions options) {
		// TODO Auto-generated method stub
		
	}

}
