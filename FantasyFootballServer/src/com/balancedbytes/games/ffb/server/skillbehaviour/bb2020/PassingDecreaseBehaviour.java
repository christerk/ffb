package com.balancedbytes.games.ffb.server.skillbehaviour.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.skill.bb2020.PassingDecrease;

@RulesCollection(Rules.BB2020)
public class PassingDecreaseBehaviour extends SkillBehaviour<PassingDecrease> {
	public PassingDecreaseBehaviour() {
		super();

		registerModifier(player -> player.setPassing(player.getPassing() + 1));
	}
}
