package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.skill.StrengthDecrease;

public class StrengthDecreaseBehaviour extends SkillBehaviour<StrengthDecrease> {
	public StrengthDecreaseBehaviour() {
		super();

		registerModifier(player -> player.setStrength(player.getStrength() - 1));
	}
}
