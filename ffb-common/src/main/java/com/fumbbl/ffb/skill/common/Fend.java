package com.fumbbl.ffb.skill.common;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * This player is very skilled at holding off would-be attackers. Opposing
 * players may not follow-up blocks made against this player even if the Fend
 * player is Knocked Down. The opposing player may still continue moving after
 * blocking if he had declared a Blitz Action.
 */
@RulesCollection(Rules.COMMON)
public class Fend extends Skill {

	public Fend() {
		super("Fend", SkillCategory.GENERAL);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.preventOpponentFollowingUp);
	}

}
