package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.CardEffect;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.CARD_EFFECT)
@RulesCollection(Rules.COMMON)
public class CardEffectFactory implements INamedObjectFactory {

	public CardEffect forName(String pName) {
		for (CardEffect effect : CardEffect.values()) {
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
