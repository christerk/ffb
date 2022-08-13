package com.fumbbl.ffb.skill.bb2020.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.modifiers.TemporaryEnhancements;

import java.util.Collections;

@RulesCollection(RulesCollection.Rules.BB2020)
public class BalefulHex extends Skill {
	public BalefulHex() {
		super("Baleful Hex", SkillCategory.TRAIT, SkillUsageType.ONCE_PER_GAME);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canMakeOpponentMissTurn);
		setEnhancements(new TemporaryEnhancements().withProperties(Collections.singleton(NamedProperties.hasToMissTurn)));
	}
}
