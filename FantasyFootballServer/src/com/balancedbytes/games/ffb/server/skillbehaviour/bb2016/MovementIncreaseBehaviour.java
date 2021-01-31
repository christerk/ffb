package com.balancedbytes.games.ffb.server.skillbehaviour.bb2016;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.skill.MovementIncrease;

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
