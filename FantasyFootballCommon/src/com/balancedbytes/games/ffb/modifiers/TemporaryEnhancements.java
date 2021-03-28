package com.balancedbytes.games.ffb.modifiers;

import com.balancedbytes.games.ffb.model.skill.SkillClassWithValue;
import com.balancedbytes.games.ffb.model.property.ISkillProperty;

import java.util.HashSet;
import java.util.Set;

public class TemporaryEnhancements {
	private Set<TemporaryStatModifier> modifiers = new HashSet<>();
	private Set<SkillClassWithValue> skills = new HashSet<>();
	private Set<ISkillProperty> properties = new HashSet<>();

	public Set<TemporaryStatModifier> getModifiers() {
		return modifiers;
	}

	public TemporaryEnhancements withModifiers(Set<TemporaryStatModifier> modifiers) {
		this.modifiers = modifiers;
		return this;
	}

	public Set<SkillClassWithValue> getSkills() {
		return skills;
	}

	public TemporaryEnhancements withSkills(Set<SkillClassWithValue> skills) {
		this.skills = skills;
		return this;
	}

	public Set<ISkillProperty> getProperties() {
		return properties;
	}

	public TemporaryEnhancements withProperties(Set<ISkillProperty> properties) {
		this.properties = properties;
		return this;
	}
}
