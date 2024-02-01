package com.fumbbl.ffb.skill.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * Wild Animals are uncontrollable creatures that rarely do exactly what a coach
 * wants of them. In fact, just about all you can really rely on them to do is
 * lash out at opposing players that move too close to them! To represent this,
 * immediately after declaring an Action with a Wild Animal, roll a D6, adding 2
 * to the roll if taking a Block or Blitz Action. On a roll of 1-3, the Wild
 * Animal does not move and roars in rage instead, and the Action is wasted.
 */
@RulesCollection(Rules.BB2016)
public class WildAnimal extends Skill {

	public WildAnimal() {
		super("Wild Animal", SkillCategory.EXTRAORDINARY);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.enableStandUpAndEndBlitzAction);
		registerProperty(NamedProperties.needsToRollForActionButKeepsTacklezone);
	}

	@Override
	public String getConfusionMessage() {
		return "roars in rage";
	}
}
