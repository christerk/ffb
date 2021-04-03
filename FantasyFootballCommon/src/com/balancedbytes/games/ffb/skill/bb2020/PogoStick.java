package com.balancedbytes.games.ffb.skill.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.model.skill.Skill;

@RulesCollection(RulesCollection.Rules.BB2020)
public class PogoStick extends Skill {
	public PogoStick() {
		super("Pogo Stick", SkillCategory.EXTRAORDINARY);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canLeap);
		registerProperty(NamedProperties.ignoreTacklezonesWhenJumping);
	}
}
