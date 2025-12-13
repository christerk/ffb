package com.fumbbl.ffb.skill.mixed.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * If Deeproot fumbles a Throw Team-Mate action, the player that was to be thrown will bounce as normal but will automatically land safely
 */

@RulesCollection(Rules.BB2020)
@RulesCollection(Rules.BB2025)
public class Reliable extends Skill {
	public Reliable() {
		super("Reliable", SkillCategory.TRAIT);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.fumbledPlayerLandsSafely);
	}
}
