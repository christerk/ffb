package com.fumbbl.ffb.factory;

import java.util.ArrayList;
import java.util.List;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.util.StringTool;

/**
 *
 * @author Kalimar
 */
@FactoryType(FactoryType.Factory.SKILL_CATEGORY)
@RulesCollection(Rules.COMMON)
public class SkillCategoryFactory implements INamedObjectFactory {

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

	public SkillCategory forTypeString(String pTypeString) {
		if (StringTool.isProvided(pTypeString)) {
			for (SkillCategory skillCategory : SkillCategory.values()) {
				if (pTypeString.equalsIgnoreCase(skillCategory.getTypeString())) {
					return skillCategory;
				}
			}
		}
		return null;
	}

	public SkillCategory[] forTypeStrings(String pTypeStrings) {
		List<SkillCategory> skillCategories = new ArrayList<>();
		if (StringTool.isProvided(pTypeStrings)) {
			for (int i = 0; i < pTypeStrings.length(); i++) {
				SkillCategory skillCategory = forTypeString(pTypeStrings.substring(i, i + 1));
				if (skillCategory != null) {
					skillCategories.add(skillCategory);
				}
			}
		}
		return skillCategories.toArray(new SkillCategory[skillCategories.size()]);
	}

	@Override
	public void initialize(Game game) {
		// TODO Auto-generated method stub

	}

}
