package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;

public class PassContext implements ModifierContext {
	private final PassingDistance distance;
	private final boolean duringThrowTeamMate;
	private final Player<?> player;
	private final Game game;

	public PassContext(Game game, Player<?> player, PassingDistance distance, boolean duringThrowTeamMate) {
		this.game = game;
		this.player = player;
		this.distance = distance;
		this.duringThrowTeamMate = duringThrowTeamMate;
	}

	@Override
	public Game getGame() {
		return game;
	}

	@Override
	public Player<?> getPlayer() {
		return player;
	}

	public PassingDistance getDistance() {
		return distance;
	}

	public boolean isDuringThrowTeamMate() {
		return duringThrowTeamMate;
	}
}
