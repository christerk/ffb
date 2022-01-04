package com.fumbbl.ffb.skill.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * Players with Unchannelled Fury are uncontrollable creatures that rarely do exactly what a coach
 * wants of them. In fact, just about all you can really rely on them to do is
 * lash out at opposing players that move too close to them! To represent this,
 * immediately after declaring an Action, roll a D6, adding 2
 * to the roll if taking a Block or Blitz Action. On a roll of 1-3, the
 * player does not move and roars in rage instead, and the action is wasted.
 */
@RulesCollection(Rules.BB2020)
public class UnchannelledFury extends Skill {

	public UnchannelledFury() {
		super("Unchannelled Fury", SkillCategory.TRAIT, true);
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
