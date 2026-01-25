package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.CancelSkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

@RulesCollection(Rules.BB2025)
public class MonstrousMouth extends Skill {

	public MonstrousMouth() {
		super("Monstrous Mouth", SkillCategory.MUTATION);
	}

	@Override
	public void postConstruct() {
		registerProperty(new CancelSkillProperty(NamedProperties.forceOpponentToDropBallOnPushback));
		registerProperty(NamedProperties.canPinPlayers);
		registerProperty(NamedProperties.providesBlockAlternative);
	}

}
