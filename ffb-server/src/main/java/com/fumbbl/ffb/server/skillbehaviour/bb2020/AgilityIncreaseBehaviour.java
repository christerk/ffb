package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.common.AgilityIncrease;

@RulesCollection(Rules.BB2020)
public class AgilityIncreaseBehaviour extends SkillBehaviour<AgilityIncrease> {
	public AgilityIncreaseBehaviour() {
		super();

		registerModifier(player -> player.setAgility(
			Math.max(
				Math.max(1, player.getPosition().getAgility() - 2),
				player.getAgility() - 1)
			)
		);
	}
}
