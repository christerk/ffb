package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerGender;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.util.StringTool;

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
	}

}
