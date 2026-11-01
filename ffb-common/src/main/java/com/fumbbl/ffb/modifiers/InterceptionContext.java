package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class InterceptionContext implements ModifierContext {
	private final Player<?> player;
	private final PassResult passResult;
	private final Game game;
	private final boolean bomb;
	private final Set<Skill> selectedSkills;

	public InterceptionContext(Game game, Player<?> player, PassResult passResult, boolean bomb) {
		this(game, player, passResult, bomb, Collections.emptySet());
	}

	public InterceptionContext(Game game, Player<?> player, PassResult passResult, boolean bomb,
														 Set<Skill> selectedSkills) {
		this.player = player;
		this.passResult = passResult;
		this.game = game;
		this.bomb = bomb;
		this.selectedSkills = selectedSkills == null ? Collections.emptySet() : new HashSet<>(selectedSkills);
	}

	@Override
	public boolean isSkillSelected(Skill skill) {
		return selectedSkills.contains(skill);
	}

	public boolean isBomb() {
		return bomb;
	}

	public Player<?> getPlayer() {
		return player;
	}

	public PassResult getPassResult() {
		return passResult;
	}

	public Game getGame() {
		return game;
	}
}
