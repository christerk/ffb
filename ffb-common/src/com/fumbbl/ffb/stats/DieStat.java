package com.fumbbl.ffb.stats;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;

import java.util.Optional;

public abstract class DieStat<T> {
	private final DieBase base;
	private final TeamMapping mapping;
	private final String id;
	private final T value;

	private final boolean duringGame;

	public DieStat(DieBase base, TeamMapping mapping, String id, T value) {
		this(base, mapping, id, value, true);
	}

	public DieStat(DieBase base, TeamMapping mapping, String id, T value, boolean duringGame) {
		this.base = base;
		this.mapping = mapping;
		this.id = id;
		this.value = value;
		this.duringGame = duringGame;
	}

	public DieBase getBase() {
		return base;
	}

	public T getValue() {
		return value;
	}

	public Team team(Game game) {
		return mapping.team(id, game);
	}

	@SuppressWarnings("unused")
	public boolean isDuringGame() {
		return duringGame;
	}

	@SuppressWarnings("unused")
	public Optional<Player<?>> getPlayer(Game game) {
		if (mapping == TeamMapping.TEAM_FOR_PLAYER || mapping == TeamMapping.OPPONENT_TEAM_FOR_PLAYER) {
			return Optional.ofNullable(game.getPlayerById(id));
		}
		return Optional.empty();
	}
}
