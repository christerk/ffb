package com.fumbbl.ffb.server.skillbehaviour.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.MovementIncrease;

@RulesCollection(Rules.BB2016)
public class MovementIncreaseBehaviour extends SkillBehaviour<MovementIncrease> {
	public MovementIncreaseBehaviour() {
		super();

		registerModifier(player -> player.setMovement(
			Math.min(
				Math.min(10, player.getPosition().getMovement() + 2),
				player.getMovement() + 1)
			)
		);
	}
}
