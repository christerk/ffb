package com.balancedbytes.games.ffb.server.skillbehaviour.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.skill.AgilityIncrease;

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