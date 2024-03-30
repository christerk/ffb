package com.fumbbl.ffb.server.skillbehaviour.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.bb2016.StrengthIncrease;

@RulesCollection(Rules.BB2016)
public class StrengthIncreaseBehaviour extends SkillBehaviour<StrengthIncrease> {
	public StrengthIncreaseBehaviour() {
		super();

		registerModifier(player -> player.setStrength(
			Math.min(
				Math.min(10, player.getPosition().getStrength() + 2),
				player.getStrength() + 1)
			)
		);
	}
}
