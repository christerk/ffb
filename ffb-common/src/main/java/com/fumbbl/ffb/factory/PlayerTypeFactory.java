package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.util.StringTool;

/**
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.PLAYER_TYPE)
@RulesCollection(Rules.COMMON)
public class PlayerTypeFactory implements INamedObjectFactory<PlayerType> {

	public PlayerType forName(String pName) {
		if (StringTool.isProvided(pName)) {
			for (PlayerType type : PlayerType.values()) {
				if (pName.equalsIgnoreCase(type.getName())) {
					return type;
				}
			}
			if (StringTool.isProvided(pName) && pName.equalsIgnoreCase("Normal")) {
				return PlayerType.REGULAR;
			}
		}
		return null;
	}

	@Override
	public void initialize(Game game) {
	}

}
