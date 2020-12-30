package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.CardEffect;
import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.GameOptions;

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
	public void initialize(GameOptions options) {
		// TODO Auto-generated method stub
		
	}

}
