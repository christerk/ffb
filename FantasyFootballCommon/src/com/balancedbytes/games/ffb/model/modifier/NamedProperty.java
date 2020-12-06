package com.balancedbytes.games.ffb.model.modifier;

import com.balancedbytes.games.ffb.model.ISkillProperty;

public class NamedProperty implements ISkillProperty {
	private String propertyName;

	public NamedProperty(String name) {
		this.propertyName = name;
	}

	@Override
	public boolean matches(ISkillProperty other) {
		return other instanceof NamedProperty && ((NamedProperty) other).propertyName.equals(propertyName);
	}
}
