package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.util.StringTool;

/**
 *
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.SKILL_CATEGORY)
@RulesCollection(Rules.COMMON)
public class SkillCategoryFactory implements INamedObjectFactory<SkillCategory> {

	public SkillCategory forName(String pName) {
		if (StringTool.isProvided(pName)) {
			for (SkillCategory skillCategory : SkillCategory.values()) {
				if (pName.equalsIgnoreCase(skillCategory.getName())) {
					return skillCategory;
				}
			}
		}
		return null;
	}

	@Override
	public void initialize(Game game) {
	}

}
