package com.fumbbl.ffb.stats;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;

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
}
