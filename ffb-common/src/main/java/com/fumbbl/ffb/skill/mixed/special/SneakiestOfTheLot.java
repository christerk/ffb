package com.fumbbl.ffb.skill.mixed.special;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * If your team includes the Black Gobbo, you may commit two Foul actions per team turn, provided one of your Foul actions is committed by the Black Gobbo himself.
 */

@RulesCollection(Rules.BB2020)
@RulesCollection(RulesCollection.Rules.BB2025)
public class SneakiestOfTheLot extends Skill {
	public SneakiestOfTheLot() {
		super("Sneakiest of the Lot", SkillCategory.TRAIT);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.allowsAdditionalFoul);
	}
}
