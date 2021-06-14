package com.fumbbl.ffb.inducement.bb2020;

import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.model.Game;

import java.util.Objects;

public abstract class Prayer implements INamedObject {
	private final String name;
	private final boolean affectsBothTeams;

	public Prayer(String name) {
		this(name, false);
	}

	public Prayer(String name, boolean affectsBothTeams) {
		this.name = name;
		this.affectsBothTeams = affectsBothTeams;
	}

	@Override
	public String getName() {
		return name;
	}

	public boolean affectsBothTeams() {
		return affectsBothTeams;
	}

	public abstract void apply(Game game);

	public abstract void undo(Game game);

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Prayer prayer = (Prayer) o;
		return affectsBothTeams == prayer.affectsBothTeams && Objects.equals(name, prayer.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, affectsBothTeams);
	}
}
