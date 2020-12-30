package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SpecialEffect;
import com.balancedbytes.games.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.SPECIAL_EFFECT)
@RulesCollection(Rules.COMMON)
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
	public void initialize(Game game) {
		// TODO Auto-generated method stub
		
	}

}
