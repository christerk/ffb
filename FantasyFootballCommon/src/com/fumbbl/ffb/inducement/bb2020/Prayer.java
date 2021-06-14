package com.fumbbl.ffb.inducement.bb2020;

import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.inducement.InducementDuration;
import com.fumbbl.ffb.model.Game;

import java.util.Objects;

public abstract class Prayer implements INamedObject {
	private final String name, description;
	private final boolean affectsBothTeams;
	private final InducementDuration duration;

	public Prayer(String name, String description) {
		this(name, description, InducementDuration.UNTIL_END_OF_DRIVE);
	}

	public Prayer(String name, String description, InducementDuration duration) {
		this(name, description, duration, false);
	}

	public Prayer(String name, String description, InducementDuration duration, boolean affectsBothTeams) {
		this.name = name;
		this.description = description;
		this.affectsBothTeams = affectsBothTeams;
		this.duration = duration;
	}

	@Override
	public String getName() {
		return name;
	}

	public boolean affectsBothTeams() {
		return affectsBothTeams;
	}

	public String getDescription() {
		return description;
	}

	public InducementDuration getDuration() {
		return duration;
	}

	public abstract void apply(Game game);

	public abstract void undo(Game game);

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Prayer prayer = (Prayer) o;
		return affectsBothTeams == prayer.affectsBothTeams && Objects.equals(name, prayer.name) && Objects.equals(description, prayer.description) && duration == prayer.duration;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, description, affectsBothTeams, duration);
	}
}
