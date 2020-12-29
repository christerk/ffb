package com.balancedbytes.games.ffb.factory;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillUse;
import com.balancedbytes.games.ffb.model.GameOptions;
import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.skillUse)
public class SkillUseFactory implements INamedObjectFactory {

	public SkillUse forName(String pName) {
		if (StringTool.isProvided(pName)) {
			for (SkillUse skillUse : SkillUse.values()) {
				if (pName.equalsIgnoreCase(skillUse.getName())) {
					return skillUse;
				}
			}
		}
		return null;
	}

	@Override
	public void initialize(Rules rules, GameOptions options) {
		// TODO Auto-generated method stub
		
	}

}
