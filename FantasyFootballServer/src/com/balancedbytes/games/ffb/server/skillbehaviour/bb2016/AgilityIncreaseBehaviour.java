package com.balancedbytes.games.ffb.server.skillbehaviour.bb2016;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.skill.bb2016.AgilityIncrease;

@RulesCollection(Rules.BB2016)
public class AgilityIncreaseBehaviour extends SkillBehaviour<AgilityIncrease> {
	public AgilityIncreaseBehaviour() {
		super();

		registerModifier(player -> player.setAgility(player.getAgility() + 1));
	}
}
