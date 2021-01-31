package com.balancedbytes.games.ffb.server.skillbehaviour.bb2016;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.skill.StrengthDecrease;

@RulesCollection(Rules.BB2016)
public class StrengthDecreaseBehaviour extends SkillBehaviour<StrengthDecrease> {
	public StrengthDecreaseBehaviour() {
		super();

		registerModifier(player -> player.setStrength(
			Math.max(
				Math.max(1, player.getPosition().getStrength() - 2),
				player.getStrength() - 1))
		);
	}
}
