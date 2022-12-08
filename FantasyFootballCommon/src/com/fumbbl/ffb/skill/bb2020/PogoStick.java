package com.fumbbl.ffb.skill.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.CancelSkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

@RulesCollection(RulesCollection.Rules.BB2020)
public class PogoStick extends Skill {
	public PogoStick() {
		super("Pogo Stick", SkillCategory.TRAIT);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.canLeap);
		registerProperty(NamedProperties.ignoreTacklezonesWhenJumping);
		registerProperty(new CancelSkillProperty(NamedProperties.makesJumpingHarder));
		registerProperty(new CancelSkillProperty(NamedProperties.canAttemptToTackleJumpingPlayer));
	}
}
