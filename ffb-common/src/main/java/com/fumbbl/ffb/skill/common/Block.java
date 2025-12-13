package com.fumbbl.ffb.skill.common;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * A player with the Block skill is proficient at knocking opponents down. The
 * Block skill, if used, affects the results rolled with the Block dice, as
 * explained in the Blocking rules.
 */
@RulesCollection(Rules.COMMON)
public class Block extends Skill {

	public Block() {
		super("Block", SkillCategory.GENERAL);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.preventFallOnBothDown);
	}

}
