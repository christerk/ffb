package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.ArmourDecrease;

@RulesCollection(Rules.BB2020)
public class ArmourDecreaseBehaviour extends SkillBehaviour<ArmourDecrease> {
	public ArmourDecreaseBehaviour() {
		super();

		registerModifier(player -> player.setArmour(
			Math.max(3, player.getArmour() - 1))
		);
	}
}
