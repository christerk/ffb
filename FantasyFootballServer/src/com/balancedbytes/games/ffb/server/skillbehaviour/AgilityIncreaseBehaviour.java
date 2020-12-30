package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.skill.AgilityIncrease;

@RulesCollection(Rules.COMMON)
public class AgilityIncreaseBehaviour extends SkillBehaviour<AgilityIncrease> {
	public AgilityIncreaseBehaviour() {
		super();

		registerModifier(player -> player.setAgility(player.getAgility() + 1));
	}
}
