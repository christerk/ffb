package com.balancedbytes.games.ffb.server.skillbehaviour.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.skill.bb2020.ArmourIncrease;

@RulesCollection(Rules.BB2020)
public class ArmourIncreaseBehaviour extends SkillBehaviour<ArmourIncrease> {
	public ArmourIncreaseBehaviour() {
		super();

		registerModifier(player -> player.setArmour(
			Math.min(
				Math.min(11, player.getPosition().getArmour() + 2),
				player.getArmour() + 1)
			)
		);
	}
}
