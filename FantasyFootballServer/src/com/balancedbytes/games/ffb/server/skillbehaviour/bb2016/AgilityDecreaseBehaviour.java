package com.balancedbytes.games.ffb.server.skillbehaviour.bb2016;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.skill.bb2016.AgilityDecrease;

@RulesCollection(Rules.BB2016)
public class AgilityDecreaseBehaviour extends SkillBehaviour<AgilityDecrease> {
	public AgilityDecreaseBehaviour() {
		super();

		registerModifier(player -> player.setAgility(player.getAgility() - 1));
	}
}
