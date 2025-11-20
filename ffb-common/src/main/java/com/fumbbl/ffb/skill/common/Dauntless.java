package com.fumbbl.ffb.skill.common;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * A player with this skill is capable of psyching themselves up so that they
 * can take on even the very strongest opponent. The skill only works when the
 * player attempts to block an opponent who is stronger than himself. When the
 * skill is used, the coach of the player with the Dauntless skill rolls a D6
 * and adds it to his strength. If the total is equal to or lower than the
 * opponent's Strength, the player must block using his normal Strength. If the
 * total is greater, then the player with the Dauntless skill counts as having a
 * Strength equal to his opponent's when he makes the block. The strength of
 * both players is calculated before any defensive or offensive assists are
 * added but after all other modifiers.
 */
@RulesCollection(Rules.COMMON)
public class Dauntless extends Skill {

	public Dauntless() {
		super("Dauntless", SkillCategory.GENERAL);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canRollToMatchOpponentsStrength);
	}

}
