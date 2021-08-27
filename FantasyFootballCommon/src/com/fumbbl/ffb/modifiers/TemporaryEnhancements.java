package com.fumbbl.ffb.modifiers;

import java.util.HashSet;
import java.util.Set;

import com.fumbbl.ffb.model.property.ISkillProperty;
import com.fumbbl.ffb.model.skill.SkillClassWithValue;

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
