package com.fumbbl.ffb.skill.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.GoForItModifier;

@RulesCollection(RulesCollection.Rules.BB2020)
public class Drunkard extends Skill {
	public Drunkard() {
		super("Drunkard", SkillCategory.TRAIT);
	}

	@Override
	public void postConstruct() {
		registerModifier(new GoForItModifier("Drunkard", 1));
	}
}
