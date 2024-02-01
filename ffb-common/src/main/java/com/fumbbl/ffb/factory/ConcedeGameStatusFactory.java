package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.ConcedeGameStatus;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;

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
	public void initialize(Game game) {
		// TODO Auto-generated method stub
		
	}

}
