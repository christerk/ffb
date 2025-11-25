package com.fumbbl.ffb.skill.bb2020.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillClassWithValue;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.modifiers.TemporaryEnhancements;
import com.fumbbl.ffb.skill.mixed.Loner;

import java.util.Collections;

/**
 * Grak and Crumbleberry must be hired as a pair and count as two Star Players.
 * However, if either Grak or Crumbleberry is removed from play due to suffering a KO'd or Casualty!
 * Result on the Injury table, the other replaces the Loner (4+) trait with the Loner (2+) trait.
 */

@RulesCollection(Rules.BB2020)
public class TwoForOne extends Skill {
	public TwoForOne() {
		super("Two for One", SkillCategory.TRAIT, SkillUsageType.SPECIAL);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.reducesLonerRollIfPartnerIsHurt);
		setEnhancements(new TemporaryEnhancements().withSkills(Collections.singleton(new SkillClassWithValue(Loner.class, "2"))));
	}
}
