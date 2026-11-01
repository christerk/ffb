package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PickupContext implements ModifierContext {
	private final Player<?> player;
	private final Game game;
	private final Set<Skill> selectedSkills;

	public PickupContext(Game game, Player<?> player) {
		this(game, player, Collections.emptySet());
	}

	public PickupContext(Game game, Player<?> player, Set<Skill> selectedSkills) {
		this.player = player;
		this.game = game;
		this.selectedSkills = selectedSkills == null ? Collections.emptySet() : new HashSet<>(selectedSkills);
	}

	@Override
	public boolean isSkillSelected(Skill skill) {
		return selectedSkills.contains(skill);
	}

	@Override
	public Player<?> getPlayer() {
		return player;
	}

	@Override
	public Game getGame() {
		return game;
	}
}
