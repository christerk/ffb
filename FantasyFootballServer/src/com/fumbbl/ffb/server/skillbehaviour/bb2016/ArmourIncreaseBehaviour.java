package com.fumbbl.ffb.server.skillbehaviour.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.bb2016.ArmourIncrease;

@RulesCollection(Rules.BB2016)
public class ArmourIncreaseBehaviour extends SkillBehaviour<ArmourIncrease> {
	public ArmourIncreaseBehaviour() {
		super();

		registerModifier(player -> player.setArmour(
			Math.min(
				Math.min(10, player.getPosition().getArmour() + 2),
				player.getArmour() + 1)
			)
		);
	}
}
