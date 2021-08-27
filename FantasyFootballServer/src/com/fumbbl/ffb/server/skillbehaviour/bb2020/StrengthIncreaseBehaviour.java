package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.bb2020.StrengthIncrease;

@RulesCollection(Rules.BB2020)
public class StrengthIncreaseBehaviour extends SkillBehaviour<StrengthIncrease> {
	public StrengthIncreaseBehaviour() {
		super();

		registerModifier(player -> player.setStrength(
			Math.min(
				Math.min(8, player.getPosition().getStrength() + 2),
				player.getStrength() + 1)
			)
		);
	}
}
