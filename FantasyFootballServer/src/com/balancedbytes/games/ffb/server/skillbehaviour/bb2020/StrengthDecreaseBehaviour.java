package com.balancedbytes.games.ffb.server.skillbehaviour.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.skill.StrengthDecrease;

@RulesCollection(Rules.BB2020)
public class StrengthDecreaseBehaviour extends SkillBehaviour<StrengthDecrease> {
	public StrengthDecreaseBehaviour() {
		super();

		registerModifier(player -> player.setStrength(
			Math.max(1, player.getStrength() - 1))
		);
	}
}
