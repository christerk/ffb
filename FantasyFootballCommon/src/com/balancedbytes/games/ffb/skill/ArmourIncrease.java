package com.balancedbytes.games.ffb.skill;

import com.balancedbytes.games.ffb.SkillCategory;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;

public class ArmourIncrease extends Skill {

	public ArmourIncrease() {
		super("+AV", SkillCategory.STAT_INCREASE);
	}

	@Override
	public int getCost(Player<?> player) {
		return 30000;
	}

}