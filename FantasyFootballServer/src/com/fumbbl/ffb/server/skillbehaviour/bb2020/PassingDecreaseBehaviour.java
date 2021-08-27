package com.fumbbl.ffb.server.skillbehaviour.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import com.fumbbl.ffb.skill.bb2020.PassingDecrease;

@RulesCollection(Rules.BB2020)
public class PassingDecreaseBehaviour extends SkillBehaviour<PassingDecrease> {
	public PassingDecreaseBehaviour() {
		super();

		registerModifier(player -> {
			if (player.getPassing() > 0) {
				player.setPassing(
					Math.min(6, player.getPassing() + 1)
				);
			}
		});
	}
}
