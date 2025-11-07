package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SpecialEffect;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;

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
	}

}
