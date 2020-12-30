package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.skill.MovementDecrease;

@RulesCollection(Rules.COMMON)
public class MovementDecreaseBehaviour extends SkillBehaviour<MovementDecrease> {
	public MovementDecreaseBehaviour() {
		super();

		registerModifier(player -> player.setMovement(player.getMovement() - 1));
	}
}
