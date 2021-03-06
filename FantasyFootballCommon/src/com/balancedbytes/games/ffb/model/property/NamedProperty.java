package com.balancedbytes.games.ffb.model.property;

import java.util.Objects;

public class NamedProperty implements ISkillProperty {
	private final String name;

	public NamedProperty(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof NamedProperty && ((NamedProperty) other).name.equals(name);
	}
}
