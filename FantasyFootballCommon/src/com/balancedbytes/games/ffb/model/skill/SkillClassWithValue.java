package com.balancedbytes.games.ffb.model.skill;

import java.util.Optional;

public class SkillClassWithValue {
	private final Class<? extends Skill> skill;
	private String value;

	public SkillClassWithValue(Class<? extends Skill> skill) {
		this.skill = skill;
	}

	public SkillClassWithValue(Class<? extends Skill> skill, String value) {
		this.skill = skill;
		this.value = value;
	}

	public Class<? extends Skill> getSkill() {
		return skill;
	}

	public Optional<String> getValue() {
		return Optional.ofNullable(value);
	}
}
