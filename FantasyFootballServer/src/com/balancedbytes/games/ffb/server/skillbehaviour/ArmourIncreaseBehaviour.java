package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.skill.ArmourIncrease;

public class ArmourIncreaseBehaviour extends SkillBehaviour<ArmourIncrease> {
	public ArmourIncreaseBehaviour() {
		super();

		registerModifier(player -> player.setArmour(player.getArmour() + 1));
	}
}
