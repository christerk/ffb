package com.balancedbytes.games.ffb.server.skillbehaviour.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.skill.AgilityDecrease;

@RulesCollection(Rules.BB2020)
public class AgilityDecreaseBehaviour extends SkillBehaviour<AgilityDecrease> {
	public AgilityDecreaseBehaviour() {
		super();

		registerModifier(player -> player.setAgility(
			Math.min(6, player.getAgility() + 1))
		);
	}
}