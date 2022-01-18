package com.fumbbl.ffb.skill.bb2020.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;

/**
 * Once per half, when Bryce makes the Chainsaw Attack Special action as part of a Blitz action, he may add a +4 to the Armour roll against an opponent rather than a +3
 */

@RulesCollection(Rules.BB2020)
public class GhostlyFlames extends Skill {
	public GhostlyFlames() {
		super("Ghostly Flames", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_HALF);
	}
}