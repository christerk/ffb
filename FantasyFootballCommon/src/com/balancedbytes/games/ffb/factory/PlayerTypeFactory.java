package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.PlayerType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.GameOptions;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.PLAYER_TYPE)
@RulesCollection(Rules.COMMON)
public class PlayerTypeFactory implements INamedObjectFactory {

	public PlayerType forName(String pName) {
		if (StringTool.isProvided(pName)) {
			for (PlayerType type : PlayerType.values()) {
				if (pName.equalsIgnoreCase(type.getName())) {
					return type;
				}
			}
			// TODO: clear this up with Christer. should be "Regular" instead
			if (StringTool.isProvided(pName) && pName.equalsIgnoreCase("Normal")) {
				return PlayerType.REGULAR;
			}
		}
		return null;
	}

	@Override
	public void initialize(Rules rules, GameOptions options) {
		// TODO Auto-generated method stub
		
	}

}
