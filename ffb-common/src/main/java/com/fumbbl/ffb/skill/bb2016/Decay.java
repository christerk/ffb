package com.fumbbl.ffb.skill.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.model.property.CancelSkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * Staying on the pitch is difficult when your rotting body is barely held
 * together. When this player suffers a Casualty result on the Injury table,
 * roll twice on the Casualty table (see page 25) and apply both results. The
 * player will only ever miss one future match as a result of his injuries, even
 * if he suffers two results with this effect.
 */
@RulesCollection(Rules.BB2016)
public class Decay extends Skill {

	public Decay() {
		super("Decay", SkillCategory.EXTRAORDINARY);
	}

	@Override
	public void postConstruct() {
		registerProperty(new CancelSkillProperty(NamedProperties.allowsRaisingLineman));

		registerProperty(NamedProperties.requiresSecondCasualtyRoll);
	}

}
