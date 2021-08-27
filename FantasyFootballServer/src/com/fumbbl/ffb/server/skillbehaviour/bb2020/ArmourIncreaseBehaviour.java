package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.bb2020.ArmourIncrease;

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
