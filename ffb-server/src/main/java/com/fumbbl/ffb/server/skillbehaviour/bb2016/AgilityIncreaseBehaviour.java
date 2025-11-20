package com.fumbbl.ffb.server.skillbehaviour.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.common.AgilityIncrease;

@RulesCollection(Rules.BB2016)
public class AgilityIncreaseBehaviour extends SkillBehaviour<AgilityIncrease> {
	public AgilityIncreaseBehaviour() {
		super();

		registerModifier(player -> player.setAgility(
			Math.min(
				Math.min(10, player.getPosition().getAgility() + 2),
				player.getAgility() + 1)
			)
		);
	}
}
