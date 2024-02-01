package com.fumbbl.ffb.skill.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;

// This should be removed but at the moment PilingOnBehavior is used to handle block knockdowns
// (e.g. for BothDown results). So this needs to be untangled first
@RulesCollection(Rules.BB2020)
public class PilingOn extends Skill {

	public PilingOn() {
		super("Piling On", SkillCategory.STRENGTH);
	}

	@Override
	public boolean eligible() {
		return false;
	}
}
