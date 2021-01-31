package com.balancedbytes.games.ffb.server.skillbehaviour.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.skill.MovementDecrease;

@RulesCollection(Rules.BB2020)
public class MovementDecreaseBehaviour extends SkillBehaviour<MovementDecrease> {
	public MovementDecreaseBehaviour() {
		super();

		registerModifier(player -> player.setMovement(
			Math.max(1, player.getMovement() - 1))
		);
	}
}
