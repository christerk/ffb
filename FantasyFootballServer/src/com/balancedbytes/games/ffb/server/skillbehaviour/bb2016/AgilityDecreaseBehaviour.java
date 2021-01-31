package com.balancedbytes.games.ffb.server.skillbehaviour.bb2016;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.skill.AgilityDecrease;

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
