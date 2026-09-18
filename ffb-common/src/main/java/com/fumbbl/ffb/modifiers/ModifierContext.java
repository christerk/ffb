package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;

public interface ModifierContext {
	Game getGame();

	Player<?> getPlayer();

	default boolean isSkillSelected(Skill skill) {
		return false;
	}
}
