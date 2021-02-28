package com.balancedbytes.games.ffb.model.modifier;

import com.balancedbytes.games.ffb.model.ISkillProperty;

import java.util.Objects;

public class NamedProperty implements ISkillProperty {
	private final String propertyName;

	public NamedProperty(String name) {
		propertyName = name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(propertyName);
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof NamedProperty && ((NamedProperty) other).propertyName.equals(propertyName);
	}
}
