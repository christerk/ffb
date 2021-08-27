package com.fumbbl.ffb.server.skillbehaviour.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.AgilityDecrease;

@RulesCollection(Rules.BB2016)
public class AgilityDecreaseBehaviour extends SkillBehaviour<AgilityDecrease> {
	public AgilityDecreaseBehaviour() {
		super();

		registerModifier(player -> player.setAgility(
			Math.max(
				Math.max(player.getPosition().getAgility() - 2, 1),
				player.getAgility() - 1)
			)
		);
	}
}
