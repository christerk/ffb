package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.ConcedeGameStatus;
import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.GameOptions;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.CONCEDE_GAME_STATUS)
@RulesCollection(Rules.COMMON)
public class ConcedeGameStatusFactory implements INamedObjectFactory {

	public ConcedeGameStatus forName(String pName) {
		for (ConcedeGameStatus status : ConcedeGameStatus.values()) {
			if (status.getName().equalsIgnoreCase(pName)) {
				return status;
			}
		}
		return null;
	}

	@Override
	public void initialize(GameOptions options) {
		// TODO Auto-generated method stub
		
	}

}
