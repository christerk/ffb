package com.balancedbytes.games.ffb.factory;

import java.util.ArrayList;
import java.util.List;

import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.GameOptions;
import com.balancedbytes.games.ffb.util.StringTool;

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
		List<SkillCategory> skillCategories = new ArrayList<SkillCategory>();
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
	public void initialize(Rules rules, GameOptions options) {
		// TODO Auto-generated method stub
		
	}

}
