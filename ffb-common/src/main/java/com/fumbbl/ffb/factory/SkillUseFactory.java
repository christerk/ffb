package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.SKILL_USE)
@RulesCollection(Rules.COMMON)
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
	public void initialize(Game game) {
	}

}
