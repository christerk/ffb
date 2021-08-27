package com.fumbbl.ffb.server.skillbehaviour.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.StrengthDecrease;

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
