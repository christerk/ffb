package com.balancedbytes.games.ffb.server.skillbehaviour;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.server.model.SkillBehaviour;
import com.balancedbytes.games.ffb.skill.StrengthIncrease;

@RulesCollection(Rules.COMMON)
public class StrengthIncreaseBehaviour extends SkillBehaviour<StrengthIncrease> {
	public StrengthIncreaseBehaviour() {
		super();

		registerModifier(player -> player.setStrength(player.getStrength() + 1));
	}
}
