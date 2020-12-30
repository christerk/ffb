package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.GameStatus;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.GameOptions;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.GAME_STATUS)
@RulesCollection(Rules.COMMON)
public class GameStatusFactory implements INamedObjectFactory {

	public GameStatus forName(String pName) {
		for (GameStatus status : GameStatus.values()) {
			if (status.getName().equalsIgnoreCase(pName)) {
				return status;
			}
		}
		return null;
	}

	public GameStatus forTypeString(String pTypeString) {
		for (GameStatus status : GameStatus.values()) {
			if (status.getTypeString().equals(pTypeString)) {
				return status;
			}
		}
		return null;
	}

	@Override
	public void initialize(Rules rules, GameOptions options) {
		// TODO Auto-generated method stub
		
	}

}
