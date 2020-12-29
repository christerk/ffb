package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SpecialEffect;
import com.balancedbytes.games.ffb.model.GameOptions;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.specialEffect)
public class SpecialEffectFactory implements INamedObjectFactory {

	public SpecialEffect forName(String pName) {
		for (SpecialEffect effect : SpecialEffect.values()) {
			if (effect.getName().equalsIgnoreCase(pName)) {
				return effect;
			}
		}
		return null;
	}

	@Override
	public void initialize(Rules rules, GameOptions options) {
		// TODO Auto-generated method stub
		
	}

}
