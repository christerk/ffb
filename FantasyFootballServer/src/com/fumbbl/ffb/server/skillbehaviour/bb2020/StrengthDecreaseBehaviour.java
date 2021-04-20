package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.StrengthDecrease;

@RulesCollection(Rules.BB2020)
public class StrengthDecreaseBehaviour extends SkillBehaviour<StrengthDecrease> {
	public StrengthDecreaseBehaviour() {
		super();

		registerModifier(player -> player.setStrength(
			Math.max(1, player.getStrength() - 1))
		);
	}
}
