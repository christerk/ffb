package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.GameStatus;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;

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
	public void initialize(Game game) {
		// TODO Auto-generated method stub
		
	}

}
