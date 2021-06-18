package com.fumbbl.ffb.modifiers;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

import java.util.Set;

public class GoForItContext implements ModifierContext {
	private final Game game;
	private final Player<?> player;
	private final Set<String> teamsWithMolesUnderThePitch;

	public GoForItContext(Game game, Player<?> player, Set<String> teamsWithMolesUnderThePitch) {
		this.game = game;
		this.player = player;
		this.teamsWithMolesUnderThePitch = teamsWithMolesUnderThePitch;
	}

	@Override
	public Game getGame() {
		return game;
	}

	@Override
	public Player<?> getPlayer() {
		return player;
	}

	public Set<String> getTeamsWithMolesUnderThePitch() {
		return teamsWithMolesUnderThePitch;
	}
}
