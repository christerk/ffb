package com.fumbbl.ffb.server.skillbehaviour.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.MovementDecrease;

@RulesCollection(Rules.BB2016)
public class MovementDecreaseBehaviour extends SkillBehaviour<MovementDecrease> {
	public MovementDecreaseBehaviour() {
		super();

		registerModifier(player -> player.setMovement(
			Math.max(
				Math.max(1, player.getPosition().getMovement() - 2),
				player.getMovement() - 1)
			)
		);
	}
}
