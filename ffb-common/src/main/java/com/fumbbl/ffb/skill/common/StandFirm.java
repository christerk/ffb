package com.fumbbl.ffb.skill.common;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * A player with this skill may choose to not be pushed back as the result of a
 * block. He may choose to ignore being pushed by "Pushed" results, and to have
 * 'Knock-down' results knock the player down in the square where he started. If
 * a player is pushed back into a player with using Stand Firm then neither
 * player moves.
 */
@RulesCollection(Rules.COMMON)
public class StandFirm extends Skill {

	public StandFirm() {
		super("Stand Firm", SkillCategory.STRENGTH);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canRefuseToBePushed);
	}
}
