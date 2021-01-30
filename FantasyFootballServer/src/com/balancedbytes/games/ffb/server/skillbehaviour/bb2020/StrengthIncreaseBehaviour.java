package com.balancedbytes.games.ffb.server.skillbehaviour.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.skill.bb2020.StrengthIncrease;

@RulesCollection(Rules.BB2020)
public class StrengthIncreaseBehaviour extends SkillBehaviour<StrengthIncrease> {
	public StrengthIncreaseBehaviour() {
		super();

		registerModifier(player -> player.setStrength(player.getStrength() + 1));
	}
}
