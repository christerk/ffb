package com.balancedbytes.games.ffb.model.modifier;

import com.balancedbytes.games.ffb.model.ISkillProperty;

import java.util.Collections;
import java.util.Set;

public class NamedProperty implements ISkillProperty {
	private final String propertyName;
	private final Set<ISkillProperty> cancelsProperties;

	public NamedProperty(String name, Set<ISkillProperty> cancelsProperties) {
		this.propertyName = name;
		this.cancelsProperties = cancelsProperties;
	}

	public NamedProperty(String name) {
		this(name, Collections.emptySet());
	}

	@Override
	public boolean matches(ISkillProperty other) {
		return other instanceof NamedProperty && ((NamedProperty) other).propertyName.equals(propertyName);
	}

	@Override
	public Set<ISkillProperty> cancelsProperties() {
		return cancelsProperties;
	}
}
