package com.fumbbl.ffb.server.skillbehaviour.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.ArmourDecrease;

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
