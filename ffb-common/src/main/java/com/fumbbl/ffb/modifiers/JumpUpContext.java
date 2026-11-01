package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class JumpUpContext implements ModifierContext {
	private final ActingPlayer actingPlayer;
	private final Game game;
	private final Set<Skill> selectedSkills;

	public JumpUpContext(ActingPlayer actingPlayer, Game game) {
		this(actingPlayer, game, Collections.emptySet());
	}

	public JumpUpContext(ActingPlayer actingPlayer, Game game, Set<Skill> selectedSkills) {
		this.actingPlayer = actingPlayer;
		this.game = game;
		this.selectedSkills = selectedSkills == null ? Collections.emptySet() : new HashSet<>(selectedSkills);
	}

	@Override
	public boolean isSkillSelected(Skill skill) {
		return selectedSkills.contains(skill);
	}

	@Override
	public Game getGame() {
		return game;
	}

	@Override
	public Player<?> getPlayer() {
		return actingPlayer.getPlayer();
	}
}
