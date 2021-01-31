package com.balancedbytes.games.ffb.server.skillbehaviour.bb2016;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.skill.ArmourDecrease;

@RulesCollection(Rules.BB2016)
public class ArmourDecreaseBehaviour extends SkillBehaviour<ArmourDecrease> {
	public ArmourDecreaseBehaviour() {
		super();

		registerModifier(player -> player.setArmour(
			Math.max(
				Math.max(1, player.getPosition().getArmour() - 2),
				player.getArmour() - 1)
			)
		);
	}
}
