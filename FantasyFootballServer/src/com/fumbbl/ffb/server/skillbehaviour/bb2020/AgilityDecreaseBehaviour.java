package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.AgilityDecrease;

@RulesCollection(Rules.BB2020)
public class AgilityDecreaseBehaviour extends SkillBehaviour<AgilityDecrease> {
	public AgilityDecreaseBehaviour() {
		super();

		registerModifier(player -> player.setAgility(
			Math.min(6, player.getAgility() + 1))
		);
	}
}
