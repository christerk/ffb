package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

@RulesCollection(RulesCollection.Rules.BB2025)
public class HitAndRun extends Skill {
	public HitAndRun() {
		super("Hit And Run", SkillCategory.TRAIT);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canMoveAfterBlock);
		registerConflictingProperty(NamedProperties.movesRandomly);
	}
}
