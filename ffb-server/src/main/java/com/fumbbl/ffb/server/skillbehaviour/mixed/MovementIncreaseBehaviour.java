package com.fumbbl.ffb.server.skillbehaviour.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.common.MovementIncrease;

@RulesCollection(Rules.BB2020)
@RulesCollection(Rules.BB2025)
public class MovementIncreaseBehaviour extends SkillBehaviour<MovementIncrease> {
	public MovementIncreaseBehaviour() {
		super();

		registerModifier(player -> player.setMovement(
			Math.min(
				Math.min(9, player.getPosition().getMovement() + 2),
				player.getMovement() + 1)
			)
		);
	}
}
