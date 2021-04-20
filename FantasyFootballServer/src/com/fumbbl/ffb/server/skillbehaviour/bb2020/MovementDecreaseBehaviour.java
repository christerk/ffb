package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.MovementDecrease;

@RulesCollection(Rules.BB2020)
public class MovementDecreaseBehaviour extends SkillBehaviour<MovementDecrease> {
	public MovementDecreaseBehaviour() {
		super();

		registerModifier(player -> player.setMovement(
			Math.max(1, player.getMovement() - 1))
		);
	}
}
