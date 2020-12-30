package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.CardType;
import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Game;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.CARD_TYPE)
@RulesCollection(Rules.COMMON)
public class CardTypeFactory implements INamedObjectFactory {

	public CardType forName(String pName) {
		for (CardType type : CardType.values()) {
			if (type.getName().equalsIgnoreCase(pName)) {
				return type;
			}
		}
		return null;
	}

	@Override
	public void initialize(Game game) {
		// TODO Auto-generated method stub
		
	}

}
