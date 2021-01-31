package com.balancedbytes.games.ffb.server.skillbehaviour.bb2016;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.skill.MovementDecrease;

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
