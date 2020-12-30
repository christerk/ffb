package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.PlayerGender;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.PLAYER_GENDER)
@RulesCollection(Rules.COMMON)
public class PlayerGenderFactory implements INamedObjectFactory {

	public PlayerGender forName(String pName) {
		if (StringTool.isProvided(pName)) {
			for (PlayerGender gender : PlayerGender.values()) {
				if (pName.equalsIgnoreCase(gender.getName())) {
					return gender;
				}
			}
		}
		return null;
	}

	public PlayerGender forTypeString(String pTypeString) {
		if (StringTool.isProvided(pTypeString)) {
			for (PlayerGender gender : PlayerGender.values()) {
				if (pTypeString.equalsIgnoreCase(gender.getTypeString())) {
					return gender;
				}
			}
		}
		return null;
	}

	@Override
	public void initialize(Game game) {
		// TODO Auto-generated method stub
		
	}

}
